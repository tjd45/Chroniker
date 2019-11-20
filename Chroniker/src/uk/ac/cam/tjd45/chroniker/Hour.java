package uk.ac.cam.tjd45.chroniker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hour {
	public int id;
	public int sentRaw;
	public int rcvRaw;
	public int sntGRP;
	public int rcvGRP;
	public LinkedHashMap<Integer,Integer> richSent;
	public LinkedHashMap<Integer,Integer> richReceive;
	
	public Hour(int i){
		id = i;
		sentRaw = 0;
		rcvRaw = 0;
		sntGRP = 0;
		rcvGRP = 0;
		richSent = new LinkedHashMap<Integer,Integer>();
		richReceive = new LinkedHashMap<Integer,Integer>();
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
