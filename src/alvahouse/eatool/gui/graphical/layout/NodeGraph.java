/*
 * NodeGraph.java
 * Created on 12-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * NodeGraph
 * @author Bruce.Porteous
 *
 */
public interface NodeGraph {
	
	/** gets a count of the number of nodes in the island
	 * @return the node count
	 */
	public abstract int nodeCount();
	
	/** gets a collection with the complete set of nodes
	 * in the graphical model
	 * @return a node iterator.
	 */
	public abstract Collection<? extends Node> getNodes();
	
	public abstract Node[] nodesAsArray();
	
	/** gets a count of the number of arc in the graphical model
	 * @return the arc count
	 */
	public abstract int arcCount();
	
	/** gets a collection with the complete set of arcs
	 * in the graphical model
	 * @return an arc iterator.
	 */
	public abstract Collection<? extends Arc> getArcs();
	
	/** Moves all the nodes by the given amount in the x & y directions.  Used
	 * to re-centre the model during auto-layout
	 * @param dx is the distance to move in the x direction
	 * @param dy is the distance to move in the y direction
	 */
	public abstract void offsetModel(float dx, float dy);
	
	/** gets the bounding box of all the node's coordinates. 
	 * @return a bounding box
	 */
	public abstract Rectangle2D.Float getBounds();
}