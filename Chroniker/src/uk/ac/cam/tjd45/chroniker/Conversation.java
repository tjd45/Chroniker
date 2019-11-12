package uk.ac.cam.tjd45.chroniker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Conversation {

	public int convid;
	public boolean group;
	public String participants;
	public int length;
	public int numParts;
	public String partIds;
	
	Conversation(int c, boolean g, String p){
		convid = c;
		group = g;
		participants = p;
		length = 0;
		
		numParts = p.split(":").length;
		
	}
	
	public void updatePartIds(ArrayList<String> people){
		String[] parts = participants.split(":");
		partIds = ":";
		
		for(String st : parts){
			partIds += people.indexOf(st)+":";
		}
	}
	
	
	
}
