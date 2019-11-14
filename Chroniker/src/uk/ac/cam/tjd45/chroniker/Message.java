package uk.ac.cam.tjd45.chroniker;

import java.util.Calendar;
import java.util.TimeZone;

public class Message {
	
	public Long timestamp;
	public String sender;
	public String content;
	public int sendId;
	public int year;
	public int month;
	public int day;
	public int messID;
	public int volume;
	
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	
	Message(Long ts, String n, String c, int cId){
		timestamp = ts;
		sender = n;
		content = c;
		volume = (c==null) ? 0 : c.length();
		
		java.util.Date time=new java.util.Date(timestamp);
		
		
		cal.setTime(time);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		
		
		messID=cId;
		
		
	}
	
	Message(Long ts, String n, int sId, String c, int cId){
		timestamp = ts;
		sender = n;
		content = c;
		volume = (c==null) ? 0 : c.length();
		
		java.util.Date time=new java.util.Date(timestamp);
		
		
		cal.setTime(time);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		
		sendId = sId;
		messID=cId;
		
		
	}
	
}
