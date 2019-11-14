package uk.ac.cam.tjd45.chroniker;

public class Console {
	
	public static void main(String[] args){
		Timeline tl = new Timeline("MINUTE");
		
		tl.generateBins();
		
		tl.printBins();
		
	}

}
