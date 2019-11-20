package uk.ac.cam.tjd45.chroniker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Conversation {

	public int convid;
	public boolean group;
	public String participants;
	public int length;
	public int sntLength;
	public int rcvLength;
	public int numParts;
	public String partIds;
	public ArrayList<Integer> intPartIds;
	public long firstMessage;
	
	Conversation(int c, boolean g, String p){
		convid = c;
		group = g;
		participants = p;
		length = 0;
		
		numParts = p.split(":").length;
		
	}
	
	Conversation(int c, boolean g, int pID, int l){
		intPartIds = new ArrayList<Integer>();
		convid = c;
		group = g;
		
		length = l;
		intPartIds.add(pID);
		
		numParts = intPartIds.size();
		
	}
	
	public void updatePartIds(ArrayList<String> people){
		String[] parts = participants.split(":");
		partIds = ":";
		
		for(String st : parts){
			partIds += people.indexOf(st)+":";
		}
	}
	
	
	
}
