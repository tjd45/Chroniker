package uk.ac.cam.tjd45.chroniker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Clock {
	ArrayList<Hour> hours;
	boolean defaultIndiv = false;
	boolean individual;

	public long sDate;
	public long eDate;

	final long START_OF_TIME = 1241369631000L;
	final long END_OF_TIME = 1573045513470L;

	public ArrayList<Message> allMess;

	private databaseInteracter dbi = new databaseInteracter();

	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private ArrayList<Integer> groupConv = new ArrayList<>();
	private ArrayList<Integer> notGroupConv = new ArrayList<>();

	int imWidth;
	static int imHeight;
	int imBinWidth;
	int imCentre;
	int maxSpoke;

	public Map<Integer,Color> colourList = new HashMap<Integer,Color>();

	public int topXSent = 5;

	int spokeLength;

	Color sendColour = new Color(255,0,120);
	Color rcvColour = new Color(0,195,225);

	String sStart,sEnd;

	Clock(String start, String end){

		sStart = (start.length()>0) ? start : "Not Specified";
		sEnd = (end.length()>0) ? end : "Not Specified";

		individual = defaultIndiv;
		sDate = START_OF_TIME;
		eDate = END_OF_TIME;


		if(start.length()>0)
			sDate = parseDate(start).getTime();
		if(end.length()>0)
			eDate = parseDate(end).getTime();

		hours = new ArrayList<Hour>();

		for(int i = 0; i < 24; i++){
			hours.add(new Hour(i));
		}



	}

	void generateHours(){
		generateHours(-1);
	}

	void generateHours(int convID){

		System.out.println("Retrieving messages from database");
		if(convID<0){
			allMess = dbi.getMess(sDate, eDate);
		}
		else{
			allMess = dbi.getConvMess(convID, sDate, eDate);
		}
		System.out.println("Messages retrieved");

		int totalMessages = allMess.size();
		int Percent = totalMessages/3;
		int messCounter = 0;

		System.out.println("Generating hours for "+totalMessages+" total messages");

		Calendar cal = Calendar.getInstance();

		int thisSentVol;
		int thisRcvVol;

		for(Message m : allMess){

			Date date = new Date(m.timestamp);

			cal.setTime(date);

			int hour = cal.get(Calendar.HOUR_OF_DAY);

			boolean group = isGroupMessage(m.messID);

			Hour thisHour = hours.get(hour);

			if(m.sendId == 0){

				thisSentVol = individual ? 1 : m.volume;
				thisHour.sentRaw+=thisSentVol;

				if(thisHour.richSent.containsKey(m.messID)){
					thisHour.richSent.put(m.messID, thisHour.richSent.get(m.messID)+thisSentVol);
				}else{
					thisHour.richSent.put(m.messID, thisSentVol);
				}


				if(group){
					thisHour.sntGRP += thisSentVol;
				}

			}else{

				thisRcvVol = individual ? 1 : m.volume;
				thisHour.rcvRaw += thisRcvVol;


				if(thisHour.richReceive.containsKey(m.messID)){
					thisHour.richReceive.put(m.messID, thisHour.richReceive.get(m.messID)+thisRcvVol);
				}else{
					thisHour.richReceive.put(m.messID, thisRcvVol);
				}


				if(group){
					thisHour.rcvGRP += thisRcvVol;
				}




			}


			hours.set(hour, thisHour);
		}

		for(Hour h : hours){
			h.sort();
		}


		generateColourList();
		System.out.println("Hours Generated");

	}

	boolean isGroupMessage(int messID){
		boolean checker = false;

		if(groupConv.contains(messID)){
			return true;
		}else if(notGroupConv.contains(messID)){
			return false;
		}else{
			checker = dbi.isGroup(messID);
			if(checker){
				groupConv.add(messID);
				return true;
			}else{
				notGroupConv.add(messID);
				return false;
			}	
		}
	}

	public void printHours(){
		for(int i = 0; i<24;i++){
			System.out.println(i+":"+hours.get(i).sentRaw+","+hours.get(i).rcvRaw);

		}
	}

	private static void drawSegment(int startAng, int runAng, int radLength, Graphics2D g){
		radLength += (imHeight/2);
		int rad = imHeight - radLength;
		int imDim = imHeight - (2*rad);

		g.drawArc(rad, rad, imDim, imDim, startAng, runAng);


	}

	private static void fillSegment(int startAng, int runAng, int radLength, Graphics2D g){
		radLength += (imHeight/2);
		int rad = imHeight - radLength;
		int imDim = imHeight - (2*rad);

		g.fillArc(rad, rad, imDim, imDim, startAng, runAng);


	}

	private int getMax(String method){
		int current = 0;

		for(int i = 0; i< 24; i++){
			Hour h = hours.get(i);

			if(method.contains("x")){
				if((h.rcvRaw+h.sentRaw-h.rcvGRP-h.sntGRP)>current){
					current = h.rcvRaw+h.sentRaw-h.rcvGRP-h.sntGRP;
				}
			}


		}

		return current;
	}

	void generateColourList(){

		colourList.clear();

		int counter;

		for(Hour h : hours){
			counter = 0;
			for (Map.Entry<Integer, Integer> entry : h.richSent.entrySet()) {
				Integer key = entry.getKey();
				Integer value = entry.getValue();




				if(counter<topXSent){

					
					Random rand = new Random();
					float r = rand.nextFloat();
					float g = rand.nextFloat();
					float b = rand.nextFloat();

					colourList.put(key, new Color(r,g,b));


				}else{
					Random rand = new Random();
					float r = rand.nextFloat();

					if(!colourList.containsKey(key)){
						colourList.put(key, new Color(r,r,r));
					}

				}
				counter++;



			}
		}

	}

	public void bipartitePaint(Hour h, String method, Graphics2D g){
		int rcv;
		int sent;

		if(method.contains("x")){
			sent = h.sentRaw - h.sntGRP;
			rcv = h.rcvRaw - h.rcvGRP;
		}else{
			sent = h.sentRaw;
			rcv = h.rcvRaw;
		}

		if(rcv>sent){
			g.setColor(rcvColour);
		}else{
			g.setColor(sendColour);
		}

		int total = rcv+sent;

		float scale = 1;
		if(!method.contains("n")){
			scale = (float)total/maxSpoke;
		}

		int spoke=(int) (scale*spokeLength);

		fillSegment((-1*(h.id*15))+90,-15,spoke,g);

		if(rcv>sent){
			g.setColor(sendColour);

			spoke = (int) (scale*spokeLength*((float)sent/total));
		}else{

			g.setColor(rcvColour);

			spoke = (int) (scale*spokeLength*((float)rcv/total));
		}


		fillSegment((-1*(h.id*15))+90,-15,spoke,g);
		
		g.setStroke(new BasicStroke(10));
		g.setColor(Color.black);
		
		int halfway = (int) (scale*spokeLength*0.5);
		
		drawSegment((-1*(h.id*15))+86,-7,halfway,g);
		g.setStroke(new BasicStroke(1));
	}

	public void deepPaint(Hour h, String method, Graphics2D g){
		int rcv;
		int sent;

		if(method.contains("x")){
			sent = h.sentRaw - h.sntGRP;
			rcv = h.rcvRaw - h.rcvGRP;
		}else{
			sent = h.sentRaw;
			rcv = h.rcvRaw;
		}

		if(rcv>sent){
			g.setColor(rcvColour);
		}else{
			g.setColor(sendColour);
		}

		int total = rcv+sent;

		float scale = 1;
		if(!method.contains("n")){
			scale = (float)total/maxSpoke;	
		}
		

		int spoke=(int) (scale*spokeLength);

		int runTotal = 0;
		for (Map.Entry<Integer, Integer> entry : h.richSent.entrySet()) {
			int convid = entry.getKey();
			int quant = entry.getValue();


			spoke = (int) (scale*spokeLength*((float)(total-runTotal)/total));

			if(h.richReceive.containsKey(convid)){
				runTotal += quant+h.richReceive.get(convid);
			}else
				runTotal += quant;


			g.setColor(colourList.get(convid));

			fillSegment((-1*(h.id*15))+90,-15,spoke,g);
			//System.out.println(runTotal);
		}


	}

	public void print(String output, String resolution, String method){
		imWidth = 5000;
		imHeight = 5000;

		spokeLength = imWidth/2;

		//setDimensions(resolution);
		maxSpoke = getMax(method);

		// Constructs a BufferedImage of one of the predefined image types.
		BufferedImage bufferedImage = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_RGB);

		// Create a graphics which can be used to draw into the buffered image
		Graphics2D g2d = bufferedImage.createGraphics();

		// fill all the image with black
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, imWidth, imHeight);
		g2d.setColor(Color.white);
		g2d.fillOval(0, 0, imWidth, imHeight);

		for(int i = 0; i<24; i++){

			Hour thisH = hours.get(i);

			if(method.contains("r")){
				deepPaint(thisH, method, g2d);
			}else{
				bipartitePaint(thisH, method, g2d);
			}



		}
		g2d.dispose();

		System.out.println("Image printed");

		// Save as JPEG
		String filename = "clock"+resolution+method+sStart+":"+sEnd+".jpg";

		System.out.println("Saving to file");

		File file = new File(output+filename);
		try {
			ImageIO.write(bufferedImage, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}






	}
}
