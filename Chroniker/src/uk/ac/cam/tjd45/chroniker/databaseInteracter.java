package uk.ac.cam.tjd45.chroniker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

	public void putMessage(Message message){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "insert into messages (timestamp, year, month, day, sender, sendid, content, volume, convid) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setLong(1, message.timestamp);
			ps.setInt(2, message.year);
			ps.setInt(3, message.month);
			ps.setInt(4, message.day);
			ps.setString(5, message.sender);
			ps.setInt(6, -1);
			ps.setString(7, message.content);
			ps.setInt(8, message.volume);
			ps.setInt(9, message.messID);
			ps.addBatch();
			
			ps.executeBatch();
			ps.close();
			
			System.out.println("Inserted 1 record");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resetMessages(){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {
			
			String sqlDrop = "DROP TABLE IF EXISTS messages";
			stmt.execute(sqlDrop);
			System.out.println("Table Dropped");
			
			String sqlCreate = "CREATE TABLE messages (timestamp BIGINT, year INT, month INT, day INT, sender VARCHAR(200), sendid INT, content TEXT, volume INT, convid INT)";
			stmt.execute(sqlCreate);
			System.out.println("Table Created");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetConversations(){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {
			
			String sqlDrop = "DROP TABLE IF EXISTS conversations";
			stmt.execute(sqlDrop);
			System.out.println("Table Dropped");
			
			String sqlCreate = "CREATE TABLE conversations (convid INT, grp BIT,numparts INT, participants TEXT, partids VARCHAR(1000), length INT, PRIMARY KEY (convid))";
			stmt.execute(sqlCreate);
			System.out.println("Table Created");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetPeople(){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {
			
			String sqlDrop = "DROP TABLE IF EXISTS people";
			stmt.execute(sqlDrop);
			System.out.println("Table Dropped");
			
			String sqlCreate = "CREATE TABLE people (id INT, name VARCHAR(200), PRIMARY KEY (id))";
			stmt.execute(sqlCreate);
			System.out.println("Table Created");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void massPersonInput(ArrayList<Person> people){

		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "insert into people (id, name) values (?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			final int batchSize = 1000;
			int count = 0;

			for (Person p: people) {
				
				ps.setInt(1, p.id);
				ps.setString(2, p.name);
				ps.addBatch();

				if(++count % batchSize == 0) {
					ps.executeBatch();
					System.out.println(count+" inserted into People");
				}
			}
			ps.executeBatch(); // insert remaining records
			ps.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void massConversationInput(ArrayList<Conversation> conversations){

		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "insert into conversations (convid, grp, numparts, participants, partids, length) values (?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			final int batchSize = 1000;
			int count = 0;

			for (Conversation conv: conversations) {
				int group = (conv.group) ? 1 : 0;
				
				ps.setInt(1, conv.convid);
				ps.setInt(2, group);
				ps.setInt(3, conv.numParts);
				ps.setString(4, conv.participants);
				ps.setString(5, conv.partIds);
				ps.setInt(6, conv.length);
				ps.addBatch();

				if(++count % batchSize == 0) {
					ps.executeBatch();
					System.out.println(count+" inserted into Conversations");
				}
			}
			ps.executeBatch(); // insert remaining records
			ps.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void massMessageInput(ArrayList<Message> messages){

		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "insert into messages (timestamp, year, month, day, sender, sendid, content, volume, convid) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			final int batchSize = 1000;
			int count = 0;

			for (Message message: messages) {

			

				ps.setLong(1, message.timestamp);
				ps.setInt(2, message.year);
				ps.setInt(3, message.month);
				ps.setInt(4, message.day);
				ps.setString(5, message.sender);
				ps.setInt(6, -1);
				ps.setString(7, message.content);
				ps.setInt(8, message.volume);
				ps.setInt(9, message.messID);
				ps.addBatch();

				if(++count % batchSize == 0) {
					ps.executeBatch();
					System.out.println(count+" inserted into Messages");
				}
			}
			ps.executeBatch(); // insert remaining records
			ps.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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

	public void updateMessageSenderIDs(ArrayList<Person> people){

		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "";

			
			final int batchSize = 50;
			int count = 0;

			for (Person p: people) {
				String cleanName = p.name.replace("'", "''");
				
				
				sql = "UPDATE messages SET sendid ="+p.id+" where sender = '"+cleanName+"'";

			
				stmt.addBatch(sql);

				if(++count % batchSize == 0) {
					System.out.println("Executing batch");
					stmt.executeBatch();
					System.out.println(count+" message records updated");
				}
			
			}
			
			System.out.println("Executing final batch");
			stmt.executeBatch();
			System.out.println(count+" message records updated");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void populateAllPeople(ArrayList<Person> people){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			String sql = "SELECT * from people";
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()){
				people.add(new Person(rs.getInt("id"),rs.getString("name")));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public ArrayList<Message> getMess(Long start, Long end){
		ArrayList<Message> messages = new ArrayList<Message>();
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			
			
			String sql = "SELECT * from messages WHERE timestamp >= "+start+" AND timestamp < "+end+" ORDER BY timestamp";
			
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				messages.add(new Message(rs.getLong("timestamp"), rs.getString("sender"), rs.getInt("sendid"), rs.getString("content"), rs.getInt("convid")));
			}
			
			rs.first();
			
			return messages;
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;

	}
	
	public ArrayList<Message> getConvMess(int convid, Long start, Long end){
		ArrayList<Message> messages = new ArrayList<Message>();
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			
			
			String sql = "SELECT * from messages WHERE convid = "+convid+" AND timestamp >= "+start+" AND timestamp < "+end+" ORDER BY timestamp";
			
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				messages.add(new Message(rs.getLong("timestamp"), rs.getString("sender"), rs.getInt("sendid"), rs.getString("content"), rs.getInt("convid")));
			}
			
			rs.first();
			
			return messages;
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;

	}
	
	public boolean isGroup(int convid){
		try (
				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/CHRONIKER?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						"myuser", "password");   // For MySQL
				// The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

				// Step 2: Allocate a 'Statement' object in the Connection
				Statement stmt = conn.createStatement();
				) {

			
			
			String sql = "SELECT grp from conversations WHERE convid = "+convid;
			
			
			ResultSet rs = stmt.executeQuery(sql);
		
			rs.first();
			
			return rs.getBoolean("grp");
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	
}
