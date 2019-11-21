package uk.ac.cam.tjd45.chroniker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.Random;

import javax.imageio.ImageIO;

public class Timeline {
	public long binSize;
	public boolean individual;

	public long numOfBins;

	final long START_OF_TIME = 1241369631000L;
	final long END_OF_TIME = 1573045513470L;

	Color sendColour = new Color(255,0,120);
	Color rcvColour = new Color(0,195,225);
	
	Color grpSendColour = new Color(200,0,100);
	Color grpRcvColour = new Color(0,160,200);
	
	final int maxWidth = 25600;
	final int maxHeight = 10800;

	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public long sDate;
	public long eDate;

	public ArrayList<TLBin> bins;
	public ArrayList<Message> allMess;

	private databaseInteracter dbi = new databaseInteracter();

	private ArrayList<Integer> groupConv = new ArrayList<>();
	private ArrayList<Integer> notGroupConv = new ArrayList<>();

	public Map<Integer,Color> colourList = new HashMap<Integer,Color>();

	public String outputLoc = "/Users/ThomasDavidson/Documents/Program Output/Chroniker/";

	static boolean defaultIndiv = false;

	int imWidth,imHeight,imBinWidth,imCentre;
	double unitHeight;

	double ratio = 1;
	double borderFactor = 0.1;

	double sf = 1.0;
	int topX = 19;
	int topXSent = 1;
	int topXRcv = 1;
	
	ColourListGenerator clg = new ColourListGenerator();

	Timeline(){
		this("YEAR","","",defaultIndiv);
	}

	Timeline(String binSize){
		this(binSize,"","",defaultIndiv);
	}

	Timeline(String binSize, String start, String end){
		this(binSize,start,end,defaultIndiv);
	}

	Timeline(String bSize, String start, String end, boolean indiv){

		String sStart = (start.length()>0) ? start : "Not Specified";
		String sEnd = (end.length()>0) ? end : "Not Specified";
		System.out.println("Creating new timeline with Bin Size: "+bSize+", Start Date: "+sStart+", End Date: "+sEnd+", Individual Messages: "+indiv);

		individual = indiv;
		sDate = START_OF_TIME;
		eDate = END_OF_TIME;

		switch(bSize) {
		case "MILLISECOND": binSize = 1L;
		break;
		case "SECOND": binSize = 1000L;
		break;
		case "MINUTE": binSize = 60000L;
		break;
		case "HOUR": binSize = 3600000L;
		break;
		case "DAY": binSize = 86400000L;
		break;
		case "WEEK": binSize = 604800000L;
		break;
		case "MONTH": binSize = 2592000000L;
		break;
		case "YEAR": binSize = 31536000000L;
		break;
		default : binSize = 2592000000L;
		}

		if(start.length()>0)
			sDate = parseDate(start).getTime();
		if(end.length()>0)
			eDate = parseDate(end).getTime();

		numOfBins = (long) Math.ceil((float)(eDate-sDate)/binSize);

		System.out.println("Timeline created with "+numOfBins+" bins");

	}

	void generateBins(){
		generateBins(-1);
	}

	void generateBins(int convID){
		bins = new ArrayList<TLBin>();

		prepBin prep = new prepBin();

		System.out.println("Retrieving messages from database");
		if(convID<0){
			allMess = dbi.getMess(sDate, eDate);
		}
		else{
			allMess = dbi.getConvMess(convID, sDate, eDate);
		}
		System.out.println("Messages retrieved");

		long binNum;
		int binChecker = 0;
		int thisSentVol = 0;
		int thisRcvVol = 0;

		int totalMessages = allMess.size();
		int Percent = totalMessages/3;
		int messCounter = 0;

		System.out.println("Generating bins for "+totalMessages+" total messages");

		for(Message m : allMess){

			binNum = (m.timestamp - sDate)/binSize;

			if(binNum!=binChecker){
				prep.sort();
				bins.add(new TLBin(binChecker, prep));
				prep.clear();
				thisSentVol = 0;
				thisRcvVol = 0;
				binChecker = (int) binNum;
			}

			boolean group = isGroupMessage(m.messID);

			if(m.sendId == 0){

				thisSentVol = individual ? 1 : m.volume;
				prep.sent += thisSentVol;

				if(prep.richSent.containsKey(m.messID)){
					prep.richSent.put(m.messID, prep.richSent.get(m.messID)+thisSentVol);
				}else{
					prep.richSent.put(m.messID, thisSentVol);
				}


				if(group){
					prep.groupSent += thisSentVol;
				}

			}else{

				thisRcvVol = individual ? 1 : m.volume;
				prep.rcv += thisRcvVol;


				if(prep.richReceive.containsKey(m.messID)){
					prep.richReceive.put(m.messID, prep.richReceive.get(m.messID)+thisRcvVol);
				}else{
					prep.richReceive.put(m.messID, thisRcvVol);
				}


				if(group){
					prep.groupRcv += thisRcvVol;
				}




			}

			messCounter++;

			if(messCounter%Percent==0){
				System.out.println(Math.ceil(((float)messCounter/totalMessages)*100)+"% Completed");
			}

		}

		prep.sort();
		bins.add(new TLBin(binChecker, prep));

		System.out.println("Bins Generated");

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

	void setDimensions(String resolution){
		int maxSent = 0;
		int maxRcv = 0;
		int minSoR = 100;

		unitHeight = 0.0;
		imBinWidth = 0;


		for(TLBin b : bins) {
			if(b.sentRaw>maxSent)
				maxSent = b.sentRaw;
			if(b.rcvRaw>maxRcv)
				maxRcv = b.rcvRaw;
			if(b.sentRaw<minSoR&&b.sentRaw>0){
				minSoR = b.sentRaw;
			}if(b.rcvRaw<minSoR&&b.rcvRaw>0){
				minSoR = b.rcvRaw;
			}
		}

		int range = Math.max(maxSent,maxRcv)*2;


		int width = (int)numOfBins;
		int height = (int)(range+(range*borderFactor));

		

		double normaliser;
		if(range>numOfBins){
			normaliser = (height*ratio)/numOfBins;
			width = (int) Math.ceil(width*normaliser);
		}else if(numOfBins>range){
			normaliser = (width*ratio)/range;
			height = (int)Math.ceil(height*normaliser);
		}

		
		
		width = (int) Math.ceil((float)width/minSoR);
		height = (int) Math.ceil((float)height/minSoR);

		

		double maxArea = 600000000.0;
		double vhiArea = 300000000.0;
		double hiArea = 150000000.0;
		double medArea = 50000000.0;
		double loArea = 10000000.0;
		
		
		long area = (long)width*(long)height;

		
		sf = 1.0;

		if(resolution.equals("MAX")){
			sf = Math.sqrt((maxArea/(area)));
		}else if(resolution.equals("VHI")){
			sf = Math.sqrt((vhiArea/(area)));
		}else if(resolution.equals("HI")){
			sf = Math.sqrt((hiArea/(area)));
		}else if(resolution.equals("MED")){
			sf = Math.sqrt((medArea/(area)));
		}else if(resolution.equals("LO")){
			sf = Math.sqrt((loArea/(area)));
		}

		
		
		width*=sf;
		height*=sf;

		
		imBinWidth = (int) (width/(numOfBins));
		width = (int) (imBinWidth*(numOfBins));

		unitHeight = (double)(height/(double)((range+range*borderFactor)));

		height = (int) Math.ceil(unitHeight*(range+range*borderFactor));

		int centre = height/2;

		imWidth = width;
		imHeight = height;
		imCentre = centre;


	}


	void generateColourList(){

		colourList.clear();

		int counter;

		for(TLBin bin : bins){
			counter = 0;
			for (Map.Entry<Integer, Integer> entry : bin.sntRich.entrySet()) {
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
			counter = 0;
			for (Map.Entry<Integer, Integer> entry : bin.rcvRich.entrySet()) {
				Integer key = entry.getKey();

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
						colourList.put(key, new Color(100,100,100));
					}
				}
				counter++;

			}
		}



	}


	void print(String opt, String res) {

		System.out.println("Setting Dimensions with Ratio : "+ratio+" for "+res+" resolution");
		setDimensions(res);
		System.out.println("Dimensions Set: Image Width = "+imWidth+" Image Height = "+imHeight+" Image Bin Width = "+imBinWidth+" Unit Height = "+unitHeight);

		// Constructs a BufferedImage of one of the predefined image types.
		BufferedImage bufferedImage = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_RGB);

		// Create a graphics which can be used to draw into the buffered image
		Graphics2D g2d = bufferedImage.createGraphics();

		// fill all the image with black
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, imWidth, imHeight);

		System.out.print("Printing to File - ");

		switch(opt){
		case "RAW" : System.out.println("RAW");rawPrint(g2d);
		break;
		case "GRP" : System.out.println("GRP");grpPrint(g2d);
		break;
		case "RICH" : System.out.println("RICH");colourList=clg.generate(sDate, eDate, topX);richPrint(g2d);
		break;
		case "CONSOLE" : System.out.println("CONSOLE");consolePrint();
		break;
		default : rawPrint(g2d);
		}

		System.out.println("Printing Complete, saving to file...");
		// Disposes of this graphics context and releases any system resources that it is using. 
		g2d.dispose();

		// Save as JPEG
		String filename = "myimage.jpg";

		File file = new File(outputLoc+filename);
		try {
			ImageIO.write(bufferedImage, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Saved to File: "+filename);

	}

	void rawPrint(Graphics2D g){
		for(TLBin b : bins) {
			g.setColor(rcvColour);
			int rcvHeight = (int) (b.rcvRaw*unitHeight);
			g.fillRect(b.id*imBinWidth, imCentre, imBinWidth, rcvHeight);
			g.setColor(sendColour);
			int sentHeight = (int) (b.sentRaw*unitHeight);
			g.fillRect(b.id*imBinWidth, imCentre-sentHeight, imBinWidth, sentHeight);
		}

		g.setColor(Color.white);

	}

	void grpPrint(Graphics2D g){
		for(TLBin b : bins) {
			g.setColor(rcvColour);
			int rcvHeight = (int) (b.rcvRaw*unitHeight);
			g.fillRect(b.id*imBinWidth, imCentre, imBinWidth, rcvHeight);
			int rcvGRPHeight = (int) (b.rcvGRP*unitHeight);
			g.setColor(grpRcvColour);
			g.fillRect(b.id*imBinWidth, imCentre+(rcvHeight-rcvGRPHeight), imBinWidth, rcvGRPHeight);

			g.setColor(sendColour);
			int sentHeight = (int) (b.sentRaw*unitHeight);
			g.fillRect(b.id*imBinWidth, imCentre-sentHeight, imBinWidth, sentHeight);
			int sentGRPHeight = (int) (b.sntGRP*unitHeight);
			g.setColor(grpSendColour);
			g.fillRect(b.id*imBinWidth, imCentre-sentHeight, imBinWidth, sentGRPHeight);
		}
	}

	void richPrint(Graphics2D g){
		int RrHeight = 0;
		int RsHeight = 0;
		int lastHeight = 0;

		for(TLBin b : bins) {

			lastHeight = 0;

			Iterator it = b.rcvRich.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				g.setColor(colourList.get(pair.getKey()));

				RrHeight = (int) ((int)pair.getValue()*unitHeight);

				g.fillRect(b.id*imBinWidth, imCentre+lastHeight, imBinWidth, RrHeight);

				lastHeight +=RrHeight;


				it.remove(); // avoids a ConcurrentModificationException
			}
			lastHeight = 0;
			it = b.sntRich.entrySet().iterator();
			while (it.hasNext()) {


				Map.Entry pair = (Map.Entry)it.next();

				g.setColor(colourList.get(pair.getKey()));

				RsHeight = (int) ((int)pair.getValue()*unitHeight);

				g.fillRect(b.id*imBinWidth, imCentre-lastHeight-RsHeight, imBinWidth, RsHeight);

				lastHeight +=RsHeight;




				it.remove(); // avoids a ConcurrentModificationException
			}



		}



		g.setColor(Color.white);
		g.setStroke(new BasicStroke ((int)sf));
		g.drawLine(0, imCentre, imWidth, imCentre);
	}

	void consolePrint(){
		for(TLBin b : bins){
			System.out.println("Bin "+b.id);
			System.out.println("Quant Sent: "+b.sentRaw+" Quant Received: "+b.rcvRaw);
			b.print();
		}


	}


}
