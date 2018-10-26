/*
 * GridAnnealLayoutStrategy.java
 *
 * Created on 05 March 2002, 20:28
 */

package alvahouse.eatool.gui.graphical.layout;

import java.util.Random;

/**
 * GridAnnealLayoutStrategy uses simulated annealing on a grid of nodes
 * to minimise arc length and (to a greater extent) arc crossings.
 * @author  rbp28668
 */
public class GridAnnealLayoutStrategy extends SimulatedAnnealingLayoutStrategy {

    /** Creates new GridAnnealLayoutStrategy */
    public GridAnnealLayoutStrategy() {
    }

    public void layout(NodeGraph model) {
    	nodes = model.nodesAsArray();
    	super.layout(model);
    }

	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#acceptChange()
	 */
    protected void acceptChange() {
        // nop - just leave modified model as it is.
    }
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#change()
	 */
    protected float change() {
          if(nodes.length > 1) {
            // pick n0, n1 at random & swap 
            n0 = nodes[random.nextInt(nodes.length)];
            n1 = n0;
            while(n1 == n0 && nodes.length > 0)
                n1 = nodes[random.nextInt(nodes.length)];
            
            float e1 = getArcEnergy(n0) + getArcEnergy(n1);
            swapNodes();
            float e2 = getArcEnergy(n0) + getArcEnergy(n1);
            
            return e2 - e1;
        }
        return 0;
    }
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#rejectChange()
	 */
    protected void rejectChange() {
        if(nodes.length > 1)
            swapNodes(); // back again
    }
    
	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#getEnergy()
	 */
    protected float getEnergy() {
        float energy = 0;
        NodeGraph graph = getGraph();
        for(Arc arc : graph.getArcs()) {
            energy += LayoutUtils.getLength(arc);
            
            for(Arc otherArc : graph.getArcs()) {
                if(LayoutUtils.intersects(arc, otherArc))
                    energy += 5.0; // arbitrary higher: favour long lines & reduced crossings
            }
        }
        return energy;
    }
    
    private float getArcEnergy(Node n) {
        float energy = 0;
        for(Arc arc : n.getArcs()) {
            energy += LayoutUtils.getLength(arc);

            for(Arc otherArc : getGraph().getArcs()) {
                if(LayoutUtils.intersects(arc, otherArc))
                    energy += 5.0; // arbitrary higher: favour long lines & reduced crossings
            }
        }
        return energy;
    }
    
	/**
	 * Method swapNodes.
	 */
    private void swapNodes() {
        float tx = n0.getX();
        float ty = n0.getY();
        n0.setPosition(n1.getX(),n1.getY());
        n1.setPosition(tx,ty);
    }
    
    private Node[] nodes = null;
    private Node n0,n1;  // nodes being swapped
    private Random random = new Random();
}
