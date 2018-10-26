/**
 * @author rbp28668
 * File Island.java
 * Created on 13-Nov-02
  */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
* Class to handle a "graphical island" which is a set of linked nodes (i.e.
* all nodes in an island must be reachable from any node)
*/
public class Island extends AbstractNodeGraph{
	
	// The nodes and arcs unique to this island.  
    private LinkedList<Node> nodes = new LinkedList<Node>();
    private LinkedList<Arc> arcs = new LinkedList<Arc>();
    
    float xPosition = 0;	// nominal position within complete layout
    float yPosition = 0;

	private static final float border = 1.0f;


    /** gets a count of the number of nodes in the island
     * @return the node count
     */
	@Override
    public int nodeCount() {
        return nodes.size();
    }
    
    /** gets a count of the number of arcs in the island
     * @return the arc count
     */
	@Override
    public int arcCount() {
        return arcs.size();
    }
    
	public void addNode(Node node) {
		nodes.addLast(node);
	}    
    
	/* (non-Javadoc)
	 * @see uk.co.alvagem.graphical.layout.AbstractNodeGraph#getNodes()
	 */
	@Override
    public Collection<? extends Node> getNodes() {
        return Collections.unmodifiableCollection(nodes);
    }
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.graphical.layout.AbstractNodeGraph#nodesAsArray()
     */
	@Override
    public Node[] nodesAsArray() {
        return (Node[])nodes.toArray(new Node[nodes.size()]);
    }

	/**
	 * @param arc
	 */
	public void addArc(Arc arc) {
		arcs.addLast(arc);
	}
	
	@Override
    public Collection<? extends Arc> getArcs() {
        return Collections.unmodifiableCollection(arcs);
    }

    /** deletes the contents of the model */
    public void reset() {
        nodes.clear();
        arcs.clear();
    }

	public void setPosition(float x, float y) {
		xPosition = x;
		yPosition = y;
		
		Rectangle2D.Float bounds = getBounds();
		x -= bounds.x;
		y -= bounds.y;
		offsetModel(x+border,y+border);
	}
	
  
    /** merges the contents of a second island to this one
     * @param second is the island to be merged
     */
	public void merge(Island second) {
		nodes.addAll(second.nodes);
		arcs.addAll(second.arcs);
	}
    
    

    /** deletes a single node and any attached arcs from the graphical model 
     * @param n is the node to delete
     */
    public void deleteNode(Node n) {
        nodes.remove(n);
    }
    
    /** deletes a single arc from the graphical model. Note that this needs
     * to check that the island is still fully connected.  If it isn't then
     * there are 2 islands so create and return a new island.
     * @param a is the arc to delete
     * @return a new island if one is needed, otherwise null if deleting
     * this arc doesn't split the island.
     */
    public Island deleteArc(Arc a) {
        Node nodeStart = a.getStartEnd();
        Node nodeFinish = a.getFinishEnd();

		Island newIsland = null;
		
		// See if by removing this arc, we've split the island
		if((nodeStart != nodeFinish) && !nodeConnects(nodeStart, nodeFinish, new HashSet<Node>())) {
			// Island now split - we'll keep all the nodes
			// connected to nodeStart in the current island 
			// and move all the nodes connected to nodeFinish
			// to a new island.
			newIsland = new Island();
			moveNodes(nodeFinish, newIsland, new HashSet<Node>());
		}

        arcs.remove(a);
        
        return newIsland;
    }
    
	/**
	 * Method nodeConnects sees if the finish node connects directly or indirectly
	 * to the start node.
	 * @param start is one node to test for connection
	 * @param finish is the other node to test for connection - the search 
	 * fans out from this node
	 * @param visited is a set used to track which nodes in the graph have
	 * been visited.  It should be empty before the first recursive call
	 * to this method.
	 * @return boolean if the 2 nodes connect.
	 */
    private boolean nodeConnects(Node start, Node finish, Set<Node> visited) {
    	if(start == finish)
    		return true;				// we've come back to the original node.

		if(visited.contains(finish))  	// ignore - been here before
			return false;
			    		
		visited.add(finish);			// mark as visited

		// See if any of the arcs out of the current test node link to 
		// the start node.				    		
		for(Arc a : finish.getArcs() ) {
			if(nodeConnects(start, a.getOtherEnd(finish), visited))
				return true;
		}    	
		return false;
    }

	/**
	 * Method moveNodes moves all the nodes connected to a start node to
	 * a new island
	 * @param start is the start node to move from
	 * @param newIsland is the new island to add the nodes to
	 * @param visited is a set used to track the visited nodes.
	 */
	private void moveNodes(Node start, Island newIsland, Set<Node> visited ) {
		if(visited.contains(start)) // already done
			return;
		
		visited.add(start);			// mark as done
		
		// Move nodes across
		nodes.remove(start);
		newIsland.nodes.addLast(start);
		
		// Now move arcs across and recurse for all nodes/arcs
		for(Arc a : start.getArcs() ) {
			arcs.remove(a);
			newIsland.arcs.addLast(a);
			
			moveNodes(a.getOtherEnd(start), newIsland, visited);
		}
	}    
    
    public void draw(Graphics2D g, float scale){
        Rectangle2D.Float r = getBounds();
        r.x -= border;
        r.y -= border;
        r.width += 2 * border;
        r.height += 2 * border;
        
        g.setColor(Color.red);
        g.drawRect((int)(r.x * scale), 
        			(int)(r.y * scale),
        			(int)(r.width * scale), 
        			(int)(r.height * scale));
    	g.setColor(Color.black);
    }
    

}
