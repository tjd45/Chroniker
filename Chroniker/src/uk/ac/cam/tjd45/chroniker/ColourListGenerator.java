package uk.ac.cam.tjd45.chroniker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ColourListGenerator {
	List<Color> discernibleColours = new ArrayList<Color>();
	databaseInteracter dbi = new databaseInteracter();
	boolean randomise = false;
	
	public ColourListGenerator(){
		discernibleColours.clear();
		
		discernibleColours.add(new Color(70,240,240));
		discernibleColours.add(new Color(60,180,75));
		discernibleColours.add(new Color(255,255,25));
		discernibleColours.add(new Color(230,25,75));
		discernibleColours.add(new Color(240,50,230));
		discernibleColours.add(new Color(210,245,60));
		discernibleColours.add(new Color(0,130,200));
		discernibleColours.add(new Color(245,130,48));
		discernibleColours.add(new Color(145,30,180));
		discernibleColours.add(new Color(0,128,128));
		discernibleColours.add(new Color(170,110,40));
		discernibleColours.add(new Color(0,0,128));
		discernibleColours.add(new Color(128,128,0));
		discernibleColours.add(new Color(128,0,0));
		discernibleColours.add(new Color(250,190,190));
		discernibleColours.add(new Color(255,250,200));
		discernibleColours.add(new Color(255,215,180));
		discernibleColours.add(new Color(170,255,195));
		discernibleColours.add(new Color(230,190,255));
		
	}
	
	Map<Integer, Color> generate(long sDate, long eDate, int topX){
		HashMap<Integer, Color> colList = new HashMap<Integer,Color>();
		
		ArrayList<Integer> convList = dbi.getTopConvs(sDate, eDate, topX);
		
		for(int i = 0; i < topX; i++){
			colList.put(convList.get(i), discernibleColours.get(i));
		}
		
		for(int i=topX; i<convList.size(); i++){
			float scaler;
			if(randomise){
				Random rand = new Random();
				scaler = rand.nextFloat();
			}else{
				scaler = (float)(1-((float)(i-topX)/convList.size()));
				scaler*=0.9;
			}
			colList.put(convList.get(i), new Color(scaler,scaler,scaler));
		}
		
		return colList;
	}
	
	
}
