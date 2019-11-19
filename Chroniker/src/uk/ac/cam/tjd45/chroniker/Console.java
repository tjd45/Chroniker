package uk.ac.cam.tjd45.chroniker;

public class Console {
	
	public static void main(String[] args){
		Timeline tl = new Timeline("MONTH","01-01-2009","");
		
		tl.generateBins();
		
		tl.print("RICH","MED");
		
	}

}
