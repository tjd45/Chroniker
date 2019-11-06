package uk.ac.cam.tjd45.chroniker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;



public class Sentenciser {

	private String cleanSentence(String st){
		
		String clean = st.substring(1, st.length()-1);
		
		String[] words = clean.split("\\s+");
		clean = "";
		for(int i = 0; i<words.length-1; i++){
			words[i] = words[i].substring(0, words[i].length() - 1);
			clean+=words[i]+" ";
		}
		clean+=words[words.length-1];
		
		return clean;
	}
	
	public void splitSentence(String message, ArrayList<String> sentences){
		Reader reader = new StringReader(message);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		
		for (List<HasWord> sentence : dp) {
			String cleanSentence = cleanSentence(sentence.toString());
			sentences.add(cleanSentence);
			

		}
	}
	
}