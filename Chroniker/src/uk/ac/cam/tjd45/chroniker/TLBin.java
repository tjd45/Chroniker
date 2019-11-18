package uk.ac.cam.tjd45.chroniker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TLBin {
	public int id;
	public int sentRaw;
	public int rcvRaw;
	public int sntGRP;
	public int rcvGRP;
	public Map<Integer,Integer> sntRich;
	public Map<Integer,Integer> rcvRich;
	
	
	TLBin(int i, int sent, int rcv){
		id = i;
		sentRaw = sent;
		rcvRaw = rcv;
		
	}
	
	TLBin(int i, int sent, int rcv, int groupSent, int groupRcv){
		id = i;
		sentRaw = sent;
		rcvRaw = rcv;
		sntGRP = groupSent;
		rcvGRP = groupRcv;
		
	}
	
	TLBin(int i, int sent, int rcv, int groupSent, int groupRcv, Map<Integer,Integer> sR, Map<Integer,Integer> rR){
		id = i;
		sentRaw = sent;
		rcvRaw = rcv;
		sntGRP = groupSent;
		rcvGRP = groupRcv;
		sntRich = new HashMap<Integer,Integer>(sR);
		rcvRich = new HashMap<Integer,Integer>(rR);
	
	}
	
	TLBin(int i,prepBin p){
		id = i;
		sentRaw = p.sent;
		rcvRaw = p.rcv;
		sntGRP = p.groupSent;
		rcvGRP = p.groupRcv;
		sntRich = new HashMap<Integer,Integer>(p.richSent);
		rcvRich = new HashMap<Integer,Integer>(p.richReceive);
	}
	
	public void print(){
		 Iterator it = sntRich.entrySet().iterator();
		 System.out.println("Sent");
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    System.out.println("Receive");
		    it = rcvRich.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
	}
}
