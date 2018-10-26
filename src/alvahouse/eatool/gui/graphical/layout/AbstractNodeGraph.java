package alvahouse.eatool.gui.graphical.layout;

/**
 * @author rbp28668
 * File AbstractNodeGraph.java
 * Created on 15-Nov-02
 * 
  */

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;


/**
 * AbstractNodeGraph is a basic graph consisting of nodes and arcs. This is
 * an abstract class that allows an underlying diagram to be abstracted
 * as nodes and arcs irrespective of the true implementation.  Hence
 * auto-layout code can be generic and independent of the underlying
 * graph type. 
 * It also provides some helper methods that operate on a generic graph.
 * @author bruce.porteous
 *
 */
public abstract class AbstractNodeGraph implements NodeGraph {
	
   /** gets a count of the number of nodes in the island
     * @return the node count
     */
    public abstract int nodeCount();
        
    /** gets  the complete set of nodes.  Do not rely on the collection being modifiable.
     * in the graphical model
     * @return a collection of nodes.
     */
    public abstract Collection<? extends Node> getNodes();
    
    public abstract Node[] nodesAsArray();
	
    /** gets a count of the number of arc in the graphical model
     * @return the arc count
     */
    public abstract int arcCount();
    
    
    /** gets the complete set of arcs. Do not rely on the collection being modifiable.
     * in the graphical model
     * @return a collection of arcs.
     */
    public abstract Collection<? extends Arc> getArcs();

    /** Moves all the nodes by the given amount in the x & y directions.  Used
     * to re-centre the model during auto-layout
     * @param dx is the distance to move in the x direction
     * @param dy is the distance to move in the y direction
     */
    public void offsetModel(float dx, float dy) {
        for(Node node : getNodes()) {
            float px = node.getX() + dx;
            float py = node.getY() + dy;
            node.setPosition(px, py);
        }
    }
    
    /** gets the bounding box of all the node's coordinates. 
     * @return a bounding box
     */
    public Rectangle2D.Float getBounds() {
        float xmin = 0;
		float ymin = 0;
		float xmax = 0;
		float ymax = 0;
        
        Iterator<? extends Node> iter = getNodes().iterator();
        if(iter.hasNext()) {
            Node node = (Node)iter.next();
            Rectangle2D.Float bounds = node.getBounds();
            xmin = bounds.x; 
            xmax = bounds.x + bounds.width;
            ymin = bounds.y; 
            ymax = bounds.y + bounds.height;
        }

        while(iter.hasNext()) {
            Node node = (Node)iter.next();
            
			Rectangle2D.Float bounds = node.getBounds();
			
            float x = bounds.x;
            if(x < xmin) xmin = x;
            
            x = bounds.x + bounds.width;
            if(x > xmax) xmax = x;
            
            float y = bounds.y;
            if(y < ymin) ymin = y;
            
            y = bounds.y + bounds.height;
            if(y > ymax) ymax = y;
        }
        bounds.setRect(xmin, ymin, xmax - xmin, ymax - ymin);
        return bounds;
    }

    private Rectangle2D.Float bounds = new Rectangle2D.Float();

}
