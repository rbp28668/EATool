/*
 * Node.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Collection;


/**
 * Node is the basic interface for a graphical item that is linked into a directed
 * graph of Node and Arc.  Each node has an attached object which is the "thing" that
 * the Node represents.  A node also includes position delta handling for algorithms
 * that set up a move for the nodes but want to defer the move until new positions
 * for all the nodes have been calculated.  
 * @author Bruce.Porteous
 *
 */
public interface Node {
    
	
	/**
	 * Adds a connecting arc to this node.
	 * @param a is the arc to add.
	 */
	public abstract void addArc(Arc a);
	
	/**
	 * Deletes a connecting arc from this node.
	 * @param a is the arc to delete.
	 */
	public abstract void deleteArc(Arc a);
	
	/**
	 * Sets the position of this node in 2d space.
	 * @param px is the x position of the node.
	 * @param py is the y position of the node.
	 */
	public abstract void setPosition(float px, float py);
	
	/**
	 * Gets the current x position of this node.
	 * @return the current x position.
	 */
	public abstract float getX();
	
	/**
	 * Gets the current y position of this node.
	 * @return the current y position.
	 */
	public abstract float getY();
	
	/** Hook for algorithms to identify this node by integer index */
	public abstract int getIndex();
	
	/** Hook for algorithms to set an integer index to identify this node.  This
	 * allows algorithms to effectively add extra information to a node - typically
	 * this will be an array index for the extra information.
	 * @param index
	 * @return
	 */
	public abstract int setIndex(int index);
	
//	/**
//	 * Sets the deltas for this node for a future position move.
//	 * @param deltaX is the x amount the node will be moved by.
//	 * @param deltaY is the y amount the node will be moved by.
//	 */
//	public abstract void setDeltas(float deltaX, float deltaY);
//	
//	/**
//	 * Modifies the deltas that describe a future position move. The
//	 * increments are added to the deltas - this helps algorithms such as
//	 * force-directed layout where there may be forces from a number of 
//	 * neighbouring nodes.
//	 * @param incX is the increment to add to the X delta.
//	 * @param incY is the increment to add to the Y delta.
//	 */
//	public abstract void addToDeltas(float incX, float incY);
//	
//	/**
//	 * This moves the node by the amount set up in the deltas. 
//	 */
//	public abstract void applyDeltas();
//	
//	/**
//	 * Gets the current X delta.
//	 * @return the current X delta.
//	 */
//	public abstract float getDeltaX();
//	
//	/**
//	 * Gets the current Y delta.
//	 * @return the current Y delta.
//	 */
//	public abstract float getDeltaY();
	
	/**
	 * Gets a Collection of attached arcs.
	 * @return Collection of attached arcs, may be empty, never null.
	 */
	public abstract Collection<? extends Arc> getArcs();
	
	/**
	 * Gets the number of attached arcs.
	 * @return the number of attached arcs.
	 */
	public abstract int arcCount();
	
	/**
	 * Gets the basic bounding rectangle when the node is rendered.
	 * @return the bounding rectangle.
	 */
	public abstract Rectangle2D.Float getBounds();
}