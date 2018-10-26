/*
 * SpringLayoutStrategy.java
 *
 * Created on 12 February 2002, 20:21
 */

package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;


/**
 * Simple graphical layout strategy that models nodes as repelling each
 * other balancing against the arcs which pull connected nodes together.
 * @author  rbp28668
 */
public class SpringLayoutStrategy implements IGraphicalLayoutStrategy {

    private int count = 0;
    private boolean completed = false;
    private float scaleFactor = 0.0f;
    private float damping = 0.8f;
    private float mass = 1.0f;
    private float vmin = 0.001f;
    private float fmin = 0.001f;
    private State[] nodes;
    
    /** Creates new SpringLayoutStrategy */
    public SpringLayoutStrategy() {
    }

    public void layout(NodeGraph model) {

		++count; // to guarantee termination.
		
		System.out.println("Pass " + count);
		// Initialise on first pass.
		if(count == 1) {
	        Iterator<? extends Node> iter = model.getNodes().iterator();
	        int cNodes = model.nodeCount();
	        nodes = new State[cNodes];
	        for(int i=0; i<cNodes; ++i) {
	        	Node n = iter.next();
	        	n.setIndex(i);
	            nodes[i] = new State(n);
	        }
		    calculateScaleFactor(nodes);
		} else {
			for(State node : nodes){
				node.fx = 0;
				node.fy = 0;
			}
		}

        
        // Run through all the nodes calculating "repulsive" force using
        // inverse square law.
        for(int i = 0; i<nodes.length; ++i) {
            State n1 = nodes[i];
            
            for(int j = i+1; j<nodes.length; ++j) {
                State n2 = nodes[j];
                
                float dx = n2.x - n1.x;
                float dy = n2.y - n1.y;

                float length = 1.0f + (float)Math.sqrt(dx * dx + dy * dy);

                dx /= (length);
                dy /= (length);
                // dx,dy is now normalised unit vector
                
                // Now scale length to target of 1.
                length /= scaleFactor;

                // inverse square law.
                float force = 1.0f / (length * length);
                
                if(Float.isNaN(force)){
                	force = 0.0f;
                }
                
                dx *= force;
                dy *= force;
                
                n1.fx -= dx;
                n1.fy -= dy;
                n2.fx += dx;
                n2.fy += dy;
            }
        }
        
        // Now add in "attractive" force using relationships
        for(Arc arc : model.getArcs()) {
        	Node n1 = arc.getStartEnd();
        	Node n2 = arc.getFinishEnd();
        	
            if(n1 != n2) {
                float dx = n2.getX() - n1.getX();
                float dy = n2.getY() - n1.getY();

                float length = 1.0f + (float)Math.sqrt(dx * dx + dy * dy);
                dx /= (length);
                dy /= (length);

                float force = length;
                force /= scaleFactor;

                State s1 = nodes[n1.getIndex()];
                State s2 = nodes[n2.getIndex()];
                
                s1.fx += force * dx;
                s1.fy += force * dy;
                s2.fx -= force * dx;
                s2.fy -= force * dy;
               
            }
        }
        
        for(State state : nodes){
        	//System.out.println("Force " + state.fx + "," + state.fy);
        	state.vx = state.vx * damping + state.fx/mass;
        	state.vy = state.vy * damping + state.fy/mass;
        	
        	if(Float.isNaN(state.vx)) state.vx = 0;
        	if(Float.isNaN(state.vy)) state.vy = 0;
        	
        	state.x += state.vx;
        	state.y += state.vy;
        	
        	if(Math.abs(state.vx) < vmin  &&
        			Math.abs(state.vy) < vmin && 
        			Math.abs(state.fx) < fmin && 
        			Math.abs(state.fx) < fmin){
        		System.out.println("Terminating on " + count);
        		completed = true;
        	}
        }

        
        float minx = 0;
        float miny = 0;
        
        if(nodes.length > 0){
            minx = nodes[0].x;
            miny = nodes[0].y;
        }
        
        for(int i=1; i<nodes.length; ++i) {
            State state = nodes[i];
            if(state.x < minx) minx = state.x;
            if(state.y < miny) miny = state.y;
        }
        
        // Now offset the model so all (connected) nodes are shifted across.
//        model.offsetModel(1 - bounds.x, 1 - bounds.y);
        for(State state : nodes){
        	state.x -= minx;
        	state.y -= miny;
            state.node.setPosition(state.x,state.y);
        }
    }
    
    /**
     * Get scale factor to set nominal spacing between nodes.  Aim for twice average node
     * size.
     */
    private void calculateScaleFactor(State[] nodes) {
        float total = 0.0f;
        for(State state : nodes){
            Rectangle2D.Float bounds = state.node.getBounds();
            float dist = bounds.width * bounds.width + bounds.height * bounds.height;
            dist = (float)Math.sqrt(dist);
            total += dist;
        }
        
        total = total/nodes.length;	// average dist.
        
        scaleFactor = 1.5f * total;  // magic number
        
        System.out.println("Scale factor " + scaleFactor);
        
    }

    public void reset() {
    	count = 0;
    	completed = false;
    }
    
    public boolean isComplete() {
    	return completed || count > 100;	// arbitrary
        //return false; // never completes
    }
    
    private static class State {
    	float x,y;		// scratchpad position
    	float fx,fy;  // force
    	float vx,vy;  // velocity
    	Node node;
    	
    	State(Node node){
    		assert(node != null);
    		this.node = node;
    		this.x = node.getX();
    		this.y = node.getY();
    		this.fx = 0;
    		this.fy = 0;
    		this.vx = 0;
    		this.vy = 0;
    	}
    }
}
