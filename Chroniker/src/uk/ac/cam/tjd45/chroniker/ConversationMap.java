package uk.ac.cam.tjd45.chroniker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ConversationMap {
	static Map<Integer,Conversation> conversations = new LinkedHashMap<Integer,Conversation>();
	static ArrayList<Message> messages = new ArrayList<Message>(); 
	private databaseInteracter dbi = new databaseInteracter();

	final long START_OF_TIME = 1241369631000L;
	final long END_OF_TIME = 1573045513470L;

	final int maxWidth = 25600;
	final int maxHeight = 10800;

	public String sStart,sEnd;
	
	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public long sDate;
	public long eDate;

	final boolean defaultIndiv = false;
	public boolean individual;

	public String outputLoc = "/Users/ThomasDavidson/Documents/Program Output/Chroniker/";

	public int maxLength = 0;

	public int imWidth;
	public int imHeight;

	public int spokeLength;

	public int centreX;
	public int centreY;

	public int pNodeDiam;
	public int aNodeDiam;
	public int maxWedgeDiam;

	public int wedgeAngle;

	public int strokeWidth;

	ConversationMap(String start, String end){
		
		sStart = (start.length()>0) ? start : "Not Specified";
		sEnd = (end.length()>0) ? end : "Not Specified";

		System.out.println("Creating new Conversation Map for date period "+sStart+" to "+eDate);
		
		individual = defaultIndiv;

		sDate = START_OF_TIME;
		eDate = END_OF_TIME;

		if(start.length()>0)
			sDate = parseDate(start).getTime();
		if(end.length()>0)
			eDate = parseDate(end).getTime();

		System.out.println("Conversation Map created");

	}

	public void generateNodes(){
		System.out.println("Populating Nodes");
		
		System.out.println("Getting data from database");
		messages = dbi.getMess(sDate, eDate);
		System.out.println("Data fetched");
		
		int thisMessLength = 0;
		Conversation thisConv;

		System.out.println("Populating conversation hashmap");
		for(Message m : messages){

			thisMessLength = individual ? 1 : m.volume;



			if(conversations.containsKey(m.messID)){
				thisConv = conversations.get(m.messID);
				thisConv.length+=thisMessLength;

				if(m.sendId==0){
					thisConv.sntLength+=thisMessLength;
				}else{
					thisConv.rcvLength+=thisMessLength;
				}

				if((!thisConv.intPartIds.contains(m.sendId))||(m.sendId<0)){
					thisConv.intPartIds.add(m.sendId);
					thisConv.numParts++;
				}

			}else{
				boolean isGroup = dbi.isGroup(m.messID);
				thisConv = new Conversation(m.messID, isGroup , m.sendId, thisMessLength);

				thisConv.firstMessage = m.timestamp;

				if(m.sendId==0){
					thisConv.sntLength = thisMessLength;
					thisConv.rcvLength = 0;
				}else{
					thisConv.sntLength = 0;
					thisConv.rcvLength = thisMessLength;
				}

			}

			if(thisConv.length>maxLength){
				maxLength = thisConv.length;
			}

			conversations.put(m.messID, thisConv);

		}
		
		System.out.println("Hashmap populated");



	}

	public void printNodes(){
		for (Map.Entry<Integer, Conversation> entry : conversations.entrySet()) {
			Conversation c = entry.getValue();
			String printout = c.convid+", NP "+c.numParts+", L "+c.length+" - ";
			for(Integer i : c.intPartIds){
				printout+= i+",";
			}
			System.out.println(printout);
		}



	}

	public void setDimensions(String resolution){
		double sf = 1.0;

		double maxArea = 600000000.0;
		double vhiArea = 300000000.0;
		double hiArea = 150000000.0;
		double medArea = 50000000.0;
		double medloArea = 10000000.0;
		double loArea = 1000000.0;
		double vloArea = 500000.0;
		double xloArea = 10000.0;

		if(resolution.equals("MAX")){
			sf = Math.sqrt(maxArea);
		}else if(resolution.equals("VHI")){
			sf = Math.sqrt(vhiArea);
		}else if(resolution.equals("HI")){
			sf = Math.sqrt(hiArea);
		}else if(resolution.equals("MED")){
			sf = Math.sqrt(medArea);
		}else if(resolution.equals("MEDLO")){
			sf = Math.sqrt(medloArea);
		}else if(resolution.equals("LO")){
			sf = Math.sqrt(loArea);
		}else if(resolution.equals("VLO")){
			sf = Math.sqrt(vloArea);
		}else if(resolution.equals("XLO")){
			sf = Math.sqrt(xloArea);
		}else{
			sf = 100;
		}

		imWidth=(int)sf;
		imHeight=(int)sf;

		strokeWidth = (int)sf/500;

		spokeLength = (int)(imHeight*0.45);

		centreX = imWidth/2;
		centreY = imHeight/2;

		pNodeDiam = (int)sf/70;
		aNodeDiam = pNodeDiam/3;
		maxWedgeDiam = pNodeDiam*17;

		wedgeAngle = 150;
	}

	public void print(String resolution, String method){

		System.out.println("Printing Conversation Map with resolution "+resolution+" and method "+method);

		setDimensions(resolution);


		// Constructs a BufferedImage of one of the predefined image types.
		BufferedImage bufferedImage = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_RGB);

		// Create a graphics which can be used to draw into the buffered image
		Graphics2D g2d = bufferedImage.createGraphics();

		// fill all the image with black
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, imWidth, imHeight);

		int origX = centreX;
		int origY = ((imHeight/2)-spokeLength);

		Point2D newRP = new Point2D.Double();
		Point2D newTP = new Point2D.Double();

		int numOfNodes = conversations.size();

		Double angle = (2*(Math.PI))/numOfNodes;

		int i = 0;

		for (Map.Entry<Integer, Conversation> entry : conversations.entrySet()) {
			Conversation c = entry.getValue();



			double len = (double)c.length/maxLength;
			int aWedgeDiam = (int) (len * maxWedgeDiam);


			g2d.setColor(Color.white);
			newRP = rotate(origX,origY,angle*i,centreX,centreY);
			newTP = transform(newRP.getX(),newRP.getY(),len,angle*i,centreX,centreY);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawLine(centreX, centreY, (int)newTP.getX(), (int)newTP.getY());

			paintWedge(g2d, c, angle*i, aWedgeDiam, newTP, method);

			i++;
		}



		g2d.setColor(Color.darkGray);

		g2d.fillOval(centreX-(pNodeDiam/2), centreY-(pNodeDiam/2), pNodeDiam, pNodeDiam);

		g2d.setColor(Color.white);


		g2d.dispose();

		System.out.println("Image printed");
		
		// Save as JPEG
		String filename = "conversationMap"+resolution+method+sStart+":"+sEnd+".jpg";

		System.out.println("Saving to file");
		
		File file = new File(outputLoc+filename);
		try {
			ImageIO.write(bufferedImage, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Saved to file "+filename);



	}

	void paintWedge(Graphics2D g2, Conversation c, double angle, int aWedgeDiam, Point2D OP, String method){
		boolean split = (method.contains("s")) ? true : false;
		boolean groupHighlight = (method.contains("g")) ? true : false;
		boolean text = (method.contains("t")) ? true : false;

		int aWedgeX = (int)OP.getX()-(aWedgeDiam/2);
		int aWedgeY = (int)OP.getY()-(aWedgeDiam/2);

		int r,g,b;
		r = 255;
		g = 0;
		b = 120;
		
		Color sendColour = new Color(r,g,b);
		Color rcvColour = new Color(0,195,225);

//		if(c.convid==49){
//			sendColour = new Color(255,255,255);
//			rcvColour = sendColour;
//		}
		
		if(groupHighlight){
			if(c.numParts>2){
				rcvColour = new Color(130,255,158);
			}
		}

		int sentAng = (int) Math.round((double)c.sntLength/c.length * wedgeAngle);
		int rcvAng = (int) Math.round((double)c.rcvLength/c.length * wedgeAngle);

		int startDegSnt = (450+wedgeAngle/2)-(int) (Math.round((Math.toDegrees(angle))))%360;
		int startDegRcv = startDegSnt - sentAng;


		if(split){
			g2.setColor(sendColour);
			g2.fillArc(aWedgeX, aWedgeY, aWedgeDiam, aWedgeDiam, startDegSnt, -sentAng);

			g2.setColor(rcvColour);
			g2.fillArc(aWedgeX, aWedgeY, aWedgeDiam, aWedgeDiam, startDegRcv, -rcvAng);
		}else{
			g2.setColor(rcvColour);
			g2.fillArc(aWedgeX, aWedgeY, aWedgeDiam, aWedgeDiam, startDegSnt, -wedgeAngle);

		}

		g2.setStroke(new BasicStroke(strokeWidth));
		g2.setColor(new Color(216,215,110));

		if(c.numParts>2){
			g2.drawArc(aWedgeX, aWedgeY, aWedgeDiam, aWedgeDiam, startDegSnt, -wedgeAngle);
		}


		if(text){

			Point2D newTP = transform(OP.getX(),OP.getY(),1.1,angle,centreX,centreY);
			g2.setColor(Color.green);

			g2.setFont(new Font("TimesRoman", Font.PLAIN, 100));

			g2.drawString(Integer.toString(c.convid), (int)newTP.getX(), (int)newTP.getY());
		}


	}

	Point2D rotate(int oX,int oY,double angle,int rX, int rY)
	{
		Point2D newP = new Point2D.Double();

		int xPrime = (int) (((oX-rX)*Math.cos(angle))-((oY-rY)*Math.sin(angle))+rX);
		int yPrime = (int) (((oX-rX)*Math.sin(angle))+((oY-rY)*Math.cos(angle))+rY);

		newP.setLocation(xPrime, yPrime);
		return newP;


	}

	Point2D transform(double oX,double oY,double l,double angle,int rX, int rY)
	{
		Point2D newP = new Point2D.Double();

		int xPrime =  (int) (((oX-rX) + Math.cos(angle))*l)+rX;
		int yPrime = (int) (((oY-rY) + Math.sin(angle))*l)+rY;;


		newP.setLocation(xPrime, yPrime);
		return newP;


	}
}



