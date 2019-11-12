package uk.ac.cam.tjd45.chroniker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LiveTesting{

	static int imageWidth = 800;
	static int imageHeight = 800;

	static boolean indivMess = false;

	static int maxSpoke = 0;
	static Ingestion ingest = new Ingestion();

	static String person = "CharlotteBurrows_mYY_2FtTZA";


	public static void main(String[] args){
		//databaseTestBed dtb = new databaseTestBed();
		//dtb.populateGrid(activated);


		//CharlotteBurrows_mYY_2FtTZA
		//MarkAllingham_TFy7pTAnZg
		//SamCw_wg6_iA4i2Q
		//RoshniAtwal_qPsEhgKTsQ
		//OliviaDadge_4uCQqw44Bg
		//JadeAnyaPallister_MvTFJJazsw
		//LaraHorin_P0QkyDNeQQ
		ingest.basicIngest(person);
		
		
		
		//ingest.dbIngest(person);

		if(indivMess){
			for(int i = 0; i<ingest.hourCounter1.length; i++){
				if((ingest.hourCounter1[i]+ingest.hourCounter2[i])>maxSpoke){
					maxSpoke = ingest.hourCounter1[i]+ ingest.hourCounter2[i];
				}
			}
		}else{
			for(int i = 0; i<ingest.rawHourCounter1.length; i++){
				if((ingest.rawHourCounter1[i]+ingest.rawHourCounter2[i])>maxSpoke){
					maxSpoke = ingest.rawHourCounter1[i]+ ingest.rawHourCounter2[i];
				}
			}
		}

		updateHourCounters(ingest, maxSpoke);

		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				createAndShowGUI();
			}
		});


	}

	private static void updateHourCounters(Ingestion ingest, int maxLength){
		float SF = (float)(imageHeight/2)/(float)maxLength;

		if(indivMess){
			for(int i = 0; i<ingest.hourCounter1.length; i++){
				//SF = (float)(imageHeight/2)/(float)(ingest.hourCounter1[i]+ingest.hourCounter2[i]);
				int newVal = (int) (SF*ingest.hourCounter1[i]);
				ingest.hourCounter1[i]=newVal;
				newVal = (int) (SF*ingest.hourCounter2[i]);
				ingest.hourCounter2[i]=newVal;
			}
		}else{
			for(int i = 0; i<ingest.rawHourCounter1.length; i++){
				///SF = (float)(imageHeight/2)/(float)(ingest.rawHourCounter1[i]+ingest.rawHourCounter2[i]);
				int newVal = (int) (SF*ingest.rawHourCounter1[i]);
				ingest.rawHourCounter1[i]=newVal;
				newVal = (int) (SF*ingest.rawHourCounter2[i]);
				ingest.rawHourCounter2[i]=newVal;
			}
		}

		for(int i = 0; i<ingest.hourCounter1.length;i++){
			System.out.println(i+" "+ingest.rawHourCounter1[i]+","+ingest.rawHourCounter2[i]);
		}

	}

	private static void drawSegment(int startAng, int runAng, int radLength, Graphics g){
		radLength += (imageHeight/2);
		int rad = imageHeight - radLength;
		int imDim = imageHeight - (2*rad);

		g.drawArc(rad, rad, imDim, imDim, startAng, runAng);


	}
	
	private static void fillSegment(int startAng, int runAng, int radLength, Graphics g){
		radLength += (imageHeight/2);
		int rad = imageHeight - radLength;
		int imDim = imageHeight - (2*rad);

		g.fillArc(rad, rad, imDim, imDim, startAng, runAng);


	}








	private static void createAndShowGUI(){
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		final JPanel panel = new JPanel(){

			@Override
			protected void paintComponent(Graphics g){

				Graphics g2 = g.create();
				super.paintComponent(g);
				setBackground(Color.darkGray);

				g2.setColor(Color.black);

				g2.fillOval(0, 0, imageWidth, imageHeight);

				g2.setColor(Color.blue);






				for(int i = 0; i<24; i++){

					int len1;
					int len2;
					if(indivMess){
						len1 = ingest.hourCounter1[i];//other person
						len2 = ingest.hourCounter2[i];//me
					}else{
						len1 = ingest.rawHourCounter1[i];//other person
						len2 = ingest.rawHourCounter2[i];//me
					}

					int halfway = (len1+len2)/2;

					if(len1>len2){
						g2.setColor(new Color(167,183,226));
						fillSegment((-1*(i*15))+90,-15,len1+len2,g2);

						g2.setColor(new Color(189,236,182));
						fillSegment((-1*(i*15))+90,-15,len2,g2);
						
						g2.setColor(Color.black);
					
						drawSegment((-1*(i*15))+86,-7,halfway,g2);
					}else{


						g2.setColor(new Color(189,236,182));
						fillSegment((-1*(i*15))+90,-15,len1+len2,g2);

						g2.setColor(new Color(167,183,226));
						fillSegment((-1*(i*15))+90,-15,len1,g2);
						
						g2.setColor(Color.black);
						drawSegment((-1*(i*15))+86,-7,halfway,g2);
					}

				


				}


			}



		};

		panel.addComponentListener(new ComponentListener(){

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

		});

		panel.addKeyListener(new java.awt.event.KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch( keyCode ) { 
				case KeyEvent.VK_UP:
					System.out.println("UP");

					break;


				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});

		panel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {

				if(evt.getButton()==1){
					System.out.println("Left");
				}else{
					System.out.println("Right");


				}
			}
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				//System.out.println("mouseEntered");
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				//System.out.println("mouseExited");
			}
			public void mousePressed(java.awt.event.MouseEvent evt) {
				//System.out.println("mousePressed");
			}
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				//System.out.println("mouseReleased");
			}
		});

		frame.addComponentListener(new ComponentListener(){

			@Override
			public void componentResized(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {


			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

		});

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);


		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();


		frame.setSize(imageWidth,imageHeight+22);
		frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, gd[0].getDefaultConfiguration().getBounds().y);


		frame.setVisible(true);

		panel.setFocusable(true);
		panel.requestFocusInWindow();
	}
}