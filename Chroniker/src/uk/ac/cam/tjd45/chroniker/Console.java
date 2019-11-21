package uk.ac.cam.tjd45.chroniker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Console {

	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	final static long END_OF_TIME = 1573045513470L;

	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public static String outputLoc = "/Users/ThomasDavidson/Documents/Program Output/Chroniker/";

	public static void main(String[] args){
//					Timeline tl = new Timeline("YEAR","","");	
//						tl.generateBins();
//						tl.print("RICH","MED");

//				Calendar cal = new GregorianCalendar(2010,9,1);
//				ConversationMap cm = new ConversationMap("01-10-2017","01-10-2018");
//				cm.generateNodes();
//				cm.print(outputLoc, "MEDLO", "gsc");
//
		Clock clk = new Clock("","");
		clk.generateHours(1017);
		clk.print(outputLoc, "","xn");


	}



	public void gifGeneratorLoop(){
		//		boolean loop = true;
		//		while(loop){
		//
		//			ConversationMap cm = new ConversationMap("01-08-2015",sdf.format(cal.getTime()));
		//
		//			cm.generateNodes();
		//
		//			cm.print(outputLoc,"MEDLO","gsc");
		//
		//			if(cal.getTime().getTime()<END_OF_TIME){
		//				cal.add(Calendar.WEEK_OF_YEAR, 1);
		//			}else{
		//				loop = false;
		//			}
		//		}
	}

}
