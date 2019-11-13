package uk.ac.cam.tjd45.chroniker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Unpacker {
	static JSONParser jsonParser = new JSONParser();
	protected static databaseInteracter dbi = new databaseInteracter();
	static ArrayList<Message> allMess = new ArrayList<Message>();
	static ArrayList<Conversation> allConvs = new ArrayList<Conversation>();
	static ArrayList<String> uPeople = new ArrayList<String>();
	static ArrayList<Person> allPeople = new ArrayList<Person>();

	protected static void basicIngest(String person){



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
					System.out.println(name);
				}
			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void extract(String dirPath){
		File dir = new File(dirPath);
		File[] directoryListing = dir.listFiles();


		int count = 0;
		int messCount = 0;
		int thisMessCount = 0;
		int longestMess = 0;
		int conversationID = 0;

		Conversation thisConversation = null;



		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(child.isDirectory()){

					thisMessCount = 0;

					File[] childListing = child.listFiles();

					for(File grandChild : childListing){


						if(grandChild.getName().equals("message_1.json")){
							JSONArray parts = getParticipants(grandChild);
							String pNames = "";

							int membs = 0;
							for(Object o: parts){
								JSONObject p = (JSONObject) o;

								pNames += (String) p.get("name") + ":";

								membs++;
							}
							boolean grp = (membs>2) ? true : false;

							thisConversation = new Conversation(conversationID,grp,pNames);
						}

						if(grandChild.getName().startsWith("message_")){



							JSONArray mess = getMessages(grandChild);


							count++;
							String name = "";
							String content = "";
							Long timestamp = 0L;

							for(Object o: mess){
								JSONObject m = (JSONObject) o;

								timestamp = (Long) m.get("timestamp_ms");
								name = (String) m.get("sender_name");
								content = (String) m.get("content");

								allMess.add(new Message(timestamp, name, content, conversationID));


								messCount++;
								thisMessCount++;

								if(messCount%1000==0){
									System.out.println(messCount+" extracted");
								}

							}



						}

					}

					thisConversation.length = thisMessCount;

					allConvs.add(thisConversation);

					conversationID++;

				}




			}
		} 


	}

	protected static JSONArray getParticipants(File conversation){
		JSONArray participants = null;

		try(FileReader reader = new FileReader(conversation)){
			Object obj = jsonParser.parse(reader);

			JSONObject rawJson = (JSONObject) obj;


			participants = (JSONArray) rawJson.get("participants");


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return participants;
	}

	protected static JSONArray getMessages(File conversation){
		JSONArray messages = null;

		try(FileReader reader = new FileReader(conversation)){
			Object obj = jsonParser.parse(reader);

			JSONObject rawJson = (JSONObject) obj;


			messages = (JSONArray) rawJson.get("messages");


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return messages;
	}

	protected static void getUniquePeople(ArrayList<Conversation> convs){
		uPeople.add("Tom Davidson");
		for(Conversation c : convs){
			String [] people = c.participants.split(":");

			for(String p : people){
				if(!uPeople.contains(p)){
					uPeople.add(p);
				}
			}

		}

		int i = 0;
		for(String p : uPeople){
			allPeople.add(new Person(i, p));
			i++;
		}


	}

	protected static void addParticipantIds(){
		for(Conversation c : allConvs){
			c.updatePartIds(uPeople);
		}
	}



	public static void ingest(){
		extract("/Users/ThomasDavidson/Documents/messages/inbox/");

		getUniquePeople(allConvs);
		addParticipantIds();


		dbi.resetMessages();
		dbi.massMessageInput(allMess);
		dbi.updateMessageSenderIDs(allPeople);

		dbi.resetConversations();
		dbi.massConversationInput(allConvs);

		dbi.resetPeople();
		dbi.massPersonInput(allPeople);
	}







}
