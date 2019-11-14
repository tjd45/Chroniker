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
		eDate = END_OF_TIME;


		numOfBins = (eDate-sDate)/binSize;
	}


	void generateBins(){
		bins = new ArrayList<TLBin>();
		
		
		int sent = 0;
		int rcv = 0;
		
		long start = sDate;
		long end = sDate + binSize;
		
		
		for(int i = 0; i<numOfBins; i++){
			sent = dbi.getSent(start,end);
			rcv = dbi.getRcv(start,end);
			

			start += binSize;
			end += binSize;
			
			bins.add(new TLBin(i, sent, rcv));
		}
		
		
		
	}
	
	void printBins() {
		for(TLBin b : bins) {
			System.out.println(b.id+","+b.sentRaw+","+b.rcvRaw);
		}
	}


}
