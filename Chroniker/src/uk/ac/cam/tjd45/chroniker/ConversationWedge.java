package uk.ac.cam.tjd45.chroniker;

import java.awt.geom.Point2D;

public class ConversationWedge {
	
	double angle;
	int diam;
	Point2D location;
	Conversation conv;

	ConversationWedge(double a, int d, Point2D loc, Conversation c){
		angle = a;
		diam = d;
		location = loc;
		conv = c;
	}
}
