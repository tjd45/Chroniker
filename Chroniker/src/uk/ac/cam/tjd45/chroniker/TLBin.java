package uk.ac.cam.tjd45.chroniker;

public class TLBin {
	public int id;
	public int sentRaw;
	public int rcvRaw;
	public int sntGRP;
	public int rcvGRP;
	
	
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
}
