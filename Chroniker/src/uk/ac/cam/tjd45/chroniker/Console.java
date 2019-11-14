package uk.ac.cam.tjd45.chroniker;

public class Console {
	
	public static void main(String[] args){
		Timeline tl = new Timeline("YEAR");
		
		tl.generateBins();
		
		tl.printGRPBins();
		
	}

}
