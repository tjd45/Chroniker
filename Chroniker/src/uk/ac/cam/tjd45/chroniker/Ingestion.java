package uk.ac.cam.tjd45.chroniker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Ingestion {
	
	static ArrayList<String> Sentences = new ArrayList<String>();
	
	
	protected static ArrayList<String> people = new ArrayList<String>();
	private static ArrayList<Integer> msgCount = new ArrayList<Integer>();
	private static ArrayList<Integer> msgCountA = new ArrayList<Integer>();
	private static ArrayList<Integer> msgCountHours = new ArrayList<Integer>();
	
	protected static int[] hourCounter1 = new int[24];
	protected static int[] hourCounter2 = new int[24];
	protected static int[] rawHourCounter1 = new int[24];
	protected static int[] rawHourCounter2 = new int[24];
	
	protected static databaseInteracter dbi = new databaseInteracter();
	
	protected static void basicIngest(String person){
		JSONParser jsonParser = new JSONParser();
        msgCount.add(0, 0);
        msgCount.add(1, 0);
        msgCountA.add(0, 0);
        msgCountA.add(1, 0);
        msgCountHours.add(0, 0);
        msgCountHours.add(1, 0);
        
         
        try (FileReader reader = new FileReader("/Users/ThomasDavidson/Documents/messages/inbox/"+person+"/message_1.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject rawJson = (JSONObject) obj;
             

            JSONArray participants = (JSONArray) rawJson.get("participants");
            
            JSONArray messages = (JSONArray) rawJson.get("messages");
            
            for(Object o: participants){
                if ( o instanceof JSONObject ) {
                
                	String name = (String) ((JSONObject) o).get("name");
                	people.add(name);
                }
            }
            
            int counter = 0;
            int counter1 = 0;
            int counter1A = 0;
            int id = 0;
    
            for(Object o: messages){
            	counter++;
            	
    
                if ( o instanceof JSONObject ) {
                    String name = (String) ((JSONObject) o).get("sender_name");
                    id = people.indexOf(name);
                    counter1 = msgCount.get(id);
          
                    counter1++;
                	msgCount.set(id,counter1);
                	
                	String content = (String) ((JSONObject) o).get("content");
                	int contLen = 0;
                	
                	counter1A = msgCountA.get(id);
                	
                	if(content!=null){
                		counter1A+=content.length();
                		contLen = content.length();
                	}
                	
                	msgCountA.set(people.indexOf(name),counter1A);
                	
                	Long timestamp = (Long) ((JSONObject) o).get("timestamp_ms");
                	
                	Date date = new Date(timestamp);
                	DateFormat justHour = new SimpleDateFormat("HH");
                	DateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy-HH:mm:ss.SSS");
                	formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                	justHour.setTimeZone(TimeZone.getTimeZone("UTC"));
                	String dateFormatted = formatter.format(date);
                	
                	int currHours = msgCountHours.get(id);
                	int hour = Integer.parseInt(justHour.format(date));
                	msgCountHours.set(id, currHours+hour);
                	
                	if(id == 0){
                		hourCounter1[hour]++;
                		rawHourCounter1[hour]+=contLen;
                	}else{
                		hourCounter2[hour]++;
                		rawHourCounter2[hour]+=contLen;
                	}
        
                }
            }
         
            float avTime1 = (float)msgCountHours.get(0)/(float)msgCount.get(0);
            float avTime2 = (float)msgCountHours.get(1)/(float)msgCount.get(1);
            
       
            
            for(String p : people){
            	System.out.println(people.indexOf(p)+": "+p);
            }
            System.out.println("Total Messages: "+counter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	protected static void dbIngest(String person){
		JSONParser jsonParser = new JSONParser();
    
        
         
        try (FileReader reader = new FileReader("/Users/ThomasDavidson/Documents/messages/inbox/"+person+"/message_1.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject rawJson = (JSONObject) obj;
             

            JSONArray participants = (JSONArray) rawJson.get("participants");
            
            JSONArray messages = (JSONArray) rawJson.get("messages");
            
            for(Object o: participants){
                if ( o instanceof JSONObject ) {
                
                	String name = (String) ((JSONObject) o).get("name");
                	people.add(name);
                }
            }
            
           int wordCount = 0;
           int sentCount = 0;
           
//            for(Object o: messages){
//            
//    
//                if ( o instanceof JSONObject ) {
//                    String name = (String) ((JSONObject) o).get("sender_name");
//              
//                	String content = (String) ((JSONObject) o).get("content");
//                
//                	Long timestamp = (Long) ((JSONObject) o).get("timestamp_ms");
//                	
//                	Date date = new Date(timestamp);
//                	DateFormat justHour = new SimpleDateFormat("HH");
//                	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss.SSS");
//                	formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//                	justHour.setTimeZone(TimeZone.getTimeZone("UTC"));
//                	
//
//                	String dateFormatted = formatter.format(date);
//                
//                	int hour = Integer.parseInt(justHour.format(date));
//                	
//                	if(content!=null){
//                		Sentences.clear();
//
//                		Sentenciser s = new Sentenciser();
//                		s.splitSentence(content,Sentences);
//                		int sentNum = 0;
//                		for(String st : Sentences){
//                			
//                			String checker = st.trim();
//                			String[] words = checker.split("\\s+");
//                			
//                			System.out.println(dateFormatted+","+name+","+hour+","+sentNum+","+checker);
//                			
////                			for(String st2 : words){
////                				System.out.println(dateFormatted+","+hour+","+sentNum+","+st2);
////                			}
//                			
//                			wordCount+=words.length;
//                			sentNum++;
//                		}
//                		sentCount++;
//                	}
//                	
//                	
//      
//                	
//                }
//               
//            }


        	
        	dbi.populateDB(messages, person);
            
            System.out.println("Total sentences: "+sentCount);
            System.out.println("Total words: "+wordCount);
         
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	
}
