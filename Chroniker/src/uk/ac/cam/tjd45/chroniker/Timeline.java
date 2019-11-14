package uk.ac.cam.tjd45.chroniker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Timeline {
	public long binSize;
	public boolean aggregated;
	
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

		aggregated = true;

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
		
		long start = sDate;
		long end = sDate + binSize;
		
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
				//sent += m.volume;
				sent++;
			}else{
				//rcv += m.volume;
				rcv++;
			}
			
			
		}
		
		
	}
	
	void printBins() {
		for(TLBin b : bins) {
			System.out.println(b.id+","+b.sentRaw+","+b.rcvRaw);
		}
	}


}
