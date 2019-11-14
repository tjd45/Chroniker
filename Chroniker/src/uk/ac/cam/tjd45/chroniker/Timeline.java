package uk.ac.cam.tjd45.chroniker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

public class Timeline {
	public long binSize;
	public boolean individual;
	
	public long numOfBins;

	final long START_OF_TIME = 1241369631000L;
	final long END_OF_TIME = 1573045513470L;

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


	Timeline(String bSize){

		individual = true;

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

		}

		sDate = START_OF_TIME;
		//sDate = 1572440713470L;
		eDate = END_OF_TIME;
		//eDate = 1572440713470L;

		numOfBins = (eDate-sDate)/binSize;
	
	}


	void generateBins(){
		bins = new ArrayList<TLBin>();
		
		
		int sent = 0;
		int rcv = 0;
		
		allMess = dbi.getMess(sDate, eDate);
		
		long binNum;
		int binChecker = 0;
		
		for(Message m : allMess){
			binNum = (m.timestamp - sDate)/binSize;
			if(binNum!=binChecker){
				bins.add(new TLBin(binChecker, sent, rcv));
				binChecker = (int) binNum;
				sent = 0;
				rcv = 0;
			}
			
			if(m.sendId == 0){
				sent += individual ? 1 : m.volume;
			
			}else{
				rcv += individual ? 1 : m.volume;
				
			}
			
			
		}
		
		
	}
	
	void printBins() {
		for(TLBin b : bins) {
			System.out.println(b.id+","+b.sentRaw+","+b.rcvRaw);
		}
		
		 int width = 250;
	        int height = 250;
	 
	        // Constructs a BufferedImage of one of the predefined image types.
	        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	 
	        // Create a graphics which can be used to draw into the buffered image
	        Graphics2D g2d = bufferedImage.createGraphics();
	 
	        // fill all the image with white
	        g2d.setColor(Color.white);
	        g2d.fillRect(0, 0, width, height);
	 
	        // create a circle with black
	        g2d.setColor(Color.black);
	        g2d.fillOval(0, 0, width, height);
	 
	        // create a string with yellow
	        g2d.setColor(Color.yellow);
	        g2d.drawString("Java Code Geeks", 50, 120);
	 
	        // Disposes of this graphics context and releases any system resources that it is using. 
	        g2d.dispose();
	 
	        // Save as JPEG
	        File file = new File("/Users/ThomasDavidson/Documents/Program Output/myimage.jpg");
	        try {
				ImageIO.write(bufferedImage, "jpg", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
	}


}
