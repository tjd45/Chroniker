package uk.ac.cam.tjd45.chroniker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
	
	public void sort(){
		Map<Integer,Integer> sortedSent = sortHashMapByValues(richSent);

		richSent = new LinkedHashMap<Integer,Integer>(sortedSent);
		
		Map<Integer,Integer> sortedRcv = sortHashMapByValues(richReceive);

		richReceive = new LinkedHashMap<Integer,Integer>(sortedRcv);
	}
	
	public LinkedHashMap<Integer, Integer> sortHashMapByValues(Map<Integer, Integer> richSent2) {
	    List<Integer> mapKeys = new ArrayList<>(richSent2.keySet());
	    List<Integer> mapValues = new ArrayList<>(richSent2.values());
	    
	    Collections.sort(mapValues, Collections.reverseOrder());
	    Collections.sort(mapKeys);

	    LinkedHashMap<Integer, Integer> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Integer val = valueIt.next();
	        Iterator<Integer> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            Integer key = keyIt.next();
	            Integer comp1 = richSent2.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
}
