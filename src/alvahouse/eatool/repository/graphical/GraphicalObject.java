/*
 * GraphicalObject.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * GraphicalObject is the interface for a general graphical object that deals with
 * the drawing and manipulation of that object.
 * @author Bruce.Porteous
 *
 */
public interface GraphicalObject {
	
    /**
     * Draw the object.
	 * @param g is the graphics surface to draw it on.
	 * @param scale is the current zoom factor to draw it at.
	 */
	public abstract void draw(Graphics2D g, float scale) throws Exception;
	
	/**
	 * Allows the graphical object to automatically resize itself. It
	 * is passed a Graphics2D so that it can use a standard font size
	 * or similar to resize itself according to any text in a fashion
	 * that is independent from any device resolution.
	 * @param g is a Graphics2D to use for resizing.
	 */
	public abstract void sizeWith(Graphics2D g) throws Exception;
	
	/**
	 * Draws any attached handles, borders etc.
	 * @param g is the graphics surface to draw it on.
	 * @param scale is the current zoom factor to draw it at.
	 */
	public abstract void drawCollateral(Graphics2D g, float scale) throws Exception;
	
	/**
	 * Gets the bounds of the object at zoom 1.
	 * @return Rectangle with the objects bounds.
	 */
	public abstract Rectangle2D.Float getBounds();
	
	/**
	 * Gets the bounds of the object at the given zoom.
	 * @param zoom is the current zoom factor.
	 * @return Rectangle with the object's bounds.
	 */
	public abstract Rectangle2D.Float getBounds(float zoom);
	
	/**
	 * Gets the bounds of the object at the given zoom including any
	 * expansion due to drawing selection border/handles.
	 * @param zoom is the current zoom factor.
	 * @return Rectangle with the object's extended bounds.
	 */
	public abstract Rectangle2D.Float getExtendedBounds(float zoom);
	
	/**
	 * Tests for a hit, for a given point, within the object.
	 * @param mx is the x mouse coordinate to be tested.
	 * @param my is the y mouse coordinate to be tested.
	 * @param zoom is the current zoom factor.
	 * @return true if mx,my lies within the object, false otherwise.
	 */
	public abstract boolean hitTest(int mx, int my, float zoom);
	
	/**
	 * Marks a selection point on the object.  May start a drag.
	 * @param mx is the x mouse coordinate.
	 * @param my is the y mouse coordinate.
	 * @param zoom is the current zoom factor.
	 */
	public abstract void onSelect(int mx, int my, float zoom);
	
	/**
	 * Un-selectes this object.
	 */
	public abstract void clearSelect();
	
	/**
	 * implements a drag of the object.
	 * @param mx is the x mouse coordinate.
	 * @param my is the y mouse coordinate.
	 * @param zoom is the current zoom factor.
	 */
	public abstract void onDrag(int mx, int my, float zoom);
	
	/**
	 * Sets the position of the object.
	 * @param x is the x-coordinate of the centre of the object.
	 * @param y is the y-coordinate of the centre of the object.
	 */
	public abstract void setPosition(float x, float y);
	
	/**
	 * Gets the X coordinate (as set by setPosition(float,float).
	 * @return the x coordinate.
	 */
	public float getX();

	/**
	 * Gets the Y coordinate (as set by setPosition(float,float).
	 * @return the y coordinate.
	 */
	public float getY();
	
	/**
	 * Sets the size of the object.
	 * @param width is the new width of the object.
	 * @param height is the new height of the object.
	 */
	public abstract void setSize(float width, float height);
	
	/**
	 * Determines whether the object is currently selected.
	 * @return true if selected.
	 */
	public abstract boolean isSelected();
}