package uk.ac.cam.tjd45.chroniker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class databaseInteracter {

	public void populateDB(JSONArray messages, String title){
		ArrayList<String> Sentences = new ArrayList<String>();
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String strDrop = "drop table if exists "+title;
			stmt.execute(strDrop);

			String strCreate = "create table "+title+" (timestamp BIGINT, sender varchar(50), sentid INT, sentence varchar(10000))";
			stmt.execute(strCreate);

			System.out.println("Table created");

			int wordCount =0;
			int sentCount = 0;
			
			for(Object o: messages){
	            
			    
                if ( o instanceof JSONObject ) {
                    String name = (String) ((JSONObject) o).get("sender_name");
              
                	String content = (String) ((JSONObject) o).get("content");
                
                	Long timestamp = (Long) ((JSONObject) o).get("timestamp_ms");
                	
                	Date date = new Date(timestamp);
                	DateFormat justHour = new SimpleDateFormat("HH");
                	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss.SSS");
                	formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                	justHour.setTimeZone(TimeZone.getTimeZone("UTC"));
                	

                	String dateFormatted = formatter.format(date);
                
                	int hour = Integer.parseInt(justHour.format(date));
                	
                	if(content!=null){
                		Sentences.clear();

                		Sentenciser s = new Sentenciser();
                		s.splitSentence(content,Sentences);
                		int sentNum = 0;
                		for(String st : Sentences){
                			
                			String checker = st.trim();
                			String[] words = checker.split("\\s+");
                			String safeChecker = checker.replaceAll("'","''");
                			
                			String sqlInsert = "INSERT into "+title+" values("+timestamp+","+"'"+name+"'"+","+sentNum+","+"'"+safeChecker+"'"+")";
                			System.out.println(sqlInsert);
                			stmt.executeUpdate(sqlInsert);
                			
                			//                			for(String st2 : words){
//                				System.out.println(dateFormatted+","+hour+","+sentNum+","+st2);
//                			}
                			
                			wordCount+=words.length;
                			sentNum++;
                		}
                		sentCount++;
                	}
                	
                	
      
                	
                }
               
            }



		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void printOut(String title){
		ArrayList<String> Sentences = new ArrayList<String>();
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

		
			
			
			String query = "select * from "+ title +" ORDER BY timestamp";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				System.out.println(rs.getString("timestamp")+":"+rs.getString("sender")+"-"+rs.getString("sentence").length());
			}



		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void populateList(String title){
		ArrayList<String> Sentences = new ArrayList<String>();
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

		
			
			
			String query = "select * from "+ title +" ORDER BY timestamp";
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				System.out.println(rs.getString("timestamp")+":"+rs.getString("sender")+"-"+rs.getString("sentence").length());
			}



		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
}
