/*
 * UnitVector.java
 * Created on 07-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * UnitVector
 * @author Bruce.Porteous
 *
 */
public class UnitVector {

	private float x = 0;
	private float y = 0;
	private float dx = 1;
	private float dy = 0;
	
	/**
	 * Creates a unit vector, starting at the origin, along the x axis.
	 */
	public UnitVector() {
		super();
	}
	
	/**
	 * Creates a unit vector, starting at the given point and taking its
	 * direction from the given line segment.  Note that the start point 
	 * does not have to lie on the line.
	 * @param start provides the start point of the vector.
	 * @param direction provides the direction of the vector.
	 */
	public UnitVector(Point2D start, Line2D direction){
		x = (float)start.getX();
		y = (float)start.getY();
		
		dx = (float)(direction.getP2().getX() - direction.getP1().getX());
		dy = (float)(direction.getP2().getY() - direction.getP1().getY());
		float scale = (float)Math.sqrt(dx * dx + dy * dy);
		dx /= scale;
		dy /= scale;
	}

	/**
	 * Reverses the direction of the unit vector.
	 */
	public void reverseDirection(){
		dx = 0.0f - dx;
		dy = 0.0f - dy;
	}
	
	public Point2D.Float getStart() {
		return new Point2D.Float(x,y);
	}
	
	public Point2D.Float getDirection() {
		return new Point2D.Float(dx,dy);
	}
	
	public float getStartX() {
		return x;
	}
	public float getStartY() {
		return y;
	}
	
	public float getDirectionX(){
		return dx;
	}
	
	public float getDirectionY() {
		return dy;
	}
}
