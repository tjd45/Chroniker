package uk.ac.cam.tjd45.chroniker;

public class Console {
	
	public static void main(String[] args){
//		Timeline tl = new Timeline("MONTH","01-01-2009","");
//		
//		tl.generateBins();
//		
//		tl.print("RICH","MED");
				
		ConversationMap cm = new ConversationMap("01-10-2015","01-10-2019");
		
		cm.generateNodes();
		
		cm.print("MEDLO","gs");
	}

}
