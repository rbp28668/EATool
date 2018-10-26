/*
 * Handle.java
 * Created on 16-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Handle is a base class for drawing graphical handles for manipulating
 * shapes in a diagram.
 * @author Bruce.Porteous
 *
 */
public abstract class Handle{
	protected static final int HANDLE_SIZE = 7; 
	
	/**
	 * This gets the base position of the handle x,y such that the
	 * handle will be drawn in x, y, x+getSize(), y+getSize()
	 * @return 2D point with the handle's position.
	 */
	protected abstract Point2D.Float getPosition(float zoom);
	
	/**
	 * Gets the bounds of the handle.
	 * @param zoom is the current scale factor for drawing
	 * @return the rectangular bounds of the handle.
	 */
	public Rectangle2D getBounds(float zoom) {
		Point2D.Float position = getPosition(zoom);
		int px = Math.round(position.x);
		int py = Math.round(position.y);
		Rectangle2D bounds =  new Rectangle2D.Float();
		bounds.setRect(px,py, Handle.HANDLE_SIZE,Handle.HANDLE_SIZE);	
		return bounds;
	}
	
	/**
	 * This draws the handle.
	 * @param g is the graphics context to draw into.
	 * @param scale is the current zoom scale factor.
	 */
	public void draw(Graphics2D g, float scale){
		Point2D.Float position = getPosition(scale);
		int px = Math.round(position.x);
		int py = Math.round(position.y);
		g.setColor(Color.green);
		g.fillRect(px,py, Handle.HANDLE_SIZE,Handle.HANDLE_SIZE);	
		g.setColor(Color.black);
		g.drawRect(px,py, Handle.HANDLE_SIZE,Handle.HANDLE_SIZE);
	}
	
	/**
	 * This drags the handle to the given position.
	 * @param mx is the integer x mouse position to drag to.
	 * @param my isthe integer y mouse position to drag to.
	 * @param scale is the current scale of the drawing.
	 */
	public abstract void dragTo(int mx,int my, float scale);
	
	
	/**
	 * Determines whether a lies within a handle (to initiate drag
	 * for example).
	 * @param mx is the integer x mouse position to test.
	 * @param my is the integer y mouse position to test.
	 * @param scale is the current scale of the drawing.
	 * @return true if the point lies in the handle, false otherwise.
	 */
	public boolean pointInHandle(int mx, int my, float scale){
		float x = mx ;
		float y = my ;
		Point2D.Float handlePos = getPosition(scale);
		return x >= handlePos.x && x <= handlePos.x + Handle.HANDLE_SIZE && 
		y >= handlePos.y && y <= handlePos.y + Handle.HANDLE_SIZE;
	}
	
	/**
	 * Gets the size of the handle in pixels.  
	 * @return the handle size.
	 */
	public static float getSize() {
		return Handle.HANDLE_SIZE;
	}
}