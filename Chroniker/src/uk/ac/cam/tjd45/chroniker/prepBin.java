package uk.ac.cam.tjd45.chroniker;

import java.util.HashMap;
import java.util.Map;

public class prepBin {
	int sent = 0;
	int rcv = 0;
	int groupSent = 0;
	int groupRcv = 0;
	int thisSentVol = 0;
	int thisRcvVol = 0;
	Map<Integer,Integer> richSent = new HashMap<Integer,Integer>();
	Map<Integer,Integer> richReceive = new HashMap<Integer,Integer>();
	
	public void clear(){
		sent = 0;
		rcv = 0;
		groupSent = 0;
		groupRcv = 0;
		thisSentVol = 0;
		thisRcvVol = 0;
		richSent.clear();
		richReceive.clear();
	}
}
