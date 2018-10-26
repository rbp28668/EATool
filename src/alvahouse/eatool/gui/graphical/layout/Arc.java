/*
 * Arc.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;



/**
 * Arc abstracts a connector to an arc in a graph. This allows auto-layouts
 * to ignore the underlying type of graphical item.
 * @author Bruce.Porteous
 *
 */
public interface Arc {
	
	
	/** This sets the 2 end nodes for an arc.
	 * @param start is the start node of the arc.
	 * @param finish is the end node of the arc.
	 */
	public abstract void setEnds(Node start, Node finish);
	
	/** gets the start node of the arc
	 * @return the arc's start node.
	 */
	public abstract Node getStartEnd();
	
	/** gets the finish node of the arc
	 * @return the arc's finish node.
	 */
	public abstract Node getFinishEnd();
	
	/** Given the node on one end of the arc, this gets the node on the other
	 * end. Used for traversing the graph.
	 * @param thisEnd is the node to start from.
	 * @return the node at the other end from thisEnd.
	 */
	public abstract Node getOtherEnd(Node thisEnd);
	
	/**
	 * Resets any extra information such as spline points to default values.  Typically
	 * this would make the arc a straight line.
	 */
	public abstract void normalise();
}