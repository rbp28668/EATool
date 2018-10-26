package alvahouse.eatool.gui.graphical.layout;

/**
 * @author rbp28668
 * File FlexAnnealLayoutStrategy.java
 * Created on 18-Nov-02
 * 
  */
import java.util.Iterator;
import java.util.Random;


/**
 * FlexAnnealLayoutStrategy uses simulated annealing to layout the graph.
 * This randomly perturbs nodes and uses simulated annealing to decide 
 * whether to accept the pertubation. The energy function uses a combination
 * of repulsive forces between the nodes and attractive forces along the
 * arcs.
 * 
 * @author Bruce.Porteous
 *
 */
public class FlexAnnealLayoutStrategy
	extends SimulatedAnnealingLayoutStrategy {

	private float getSpread(){
		Iterator<? extends Node> iter = getGraph().getNodes().iterator();
		Node start = iter.next();
		float xMin = start.getX();
		float xMax = start.getX();
		float yMin = start.getY();
		float yMax = start.getY();
		for(;iter.hasNext();){
			Node n = iter.next();
			xMin = Math.min(xMin, n.getX());
			xMax = Math.max(xMax, n.getY());
			yMin = Math.min(yMin, n.getY());
			yMax = Math.max(yMax, n.getY());
		}
		float dx = xMax - xMin;
		float dy = yMax - yMin;
		double len = Math.sqrt(dx * dx + dy * dy);
		if(len == 0){
			len = 1.0;
		}
		return (float)(len/2);
	}
	
	private float getEnergyForNodeOffset(Node node, float offsetX, float offsetY) {

		float forceX = 0;
		float forceY = 0;
		
        for(Node nodeOther : getGraph().getNodes()){
           
            if(nodeOther != node){
            
                float dx = nodeOther.getX() - node.getX();
                float dy = nodeOther.getY() - node.getY();

				dx += offsetX;
				dy += offsetY;
				
                float length = (float)Math.sqrt(dx * dx + dy * dy);

                dx /= length;
                dy /= length;
                // dx,dy is now normalised unit vector -  gives direction 

                float force = -1 / (length * length);
                forceX += force * dx;
                forceY += force * dy;
            }            
        }

//        float f =  Math.sqrt(forceX * forceX + forceY * forceY);
//		forceX = 0;
//		forceY = 0;
		
        for( Arc arc : node.getArcs() ){
            
            Node nodeOther = arc.getOtherEnd(node);
            if(nodeOther != node) {

                float dx = nodeOther.getX() - node.getX();
                float dy = nodeOther.getY() - node.getY();

				dx += offsetX;
				dy += offsetY;
				
                float length = (float)Math.sqrt(dx * dx + dy * dy);

                dx /= length;	// normalise dx,dy to direction only
                dy /= length;

                float force = length;
                forceX += force * dx;
                forceY += force * dy;
            }
            
        }
        
        return (float)Math.sqrt(forceX * forceX + forceY * forceY);
		
	}

	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#change()
	 */
	protected float change() {
		if(getGraph().nodeCount() > 2) {
				
			if(nodeIter == null) { // first time through
				nodeIter = getGraph().getNodes().iterator();
				spread = getSpread();
				initialTemp = getTemperature();
			} else {
				// If we've been through all the nodes, start at the beginning again
				// and drop the spread factor to reflect the current temperature.
				//
				if(!nodeIter.hasNext()) {
					nodeIter = getGraph().getNodes().iterator();
					spread *= getAnnealSchedule();

				}
			}
			
            testNode = (Node)nodeIter.next();

            testX = (float)random.nextGaussian() * spread;
            testY = (float)random.nextGaussian() * spread;

			float e1 = getEnergyForNodeOffset(testNode, 0,0);
			float e2 = getEnergyForNodeOffset(testNode, testX, testY);            
            
            
            //System.out.print("Jump (" + testX + "," + testY + ") energy " + e1 + " -> " + e2);
            
            return +(e2 - e1);
        }
        return 0;
	}

	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#acceptChange()
	 */
	protected void acceptChange() {
//		System.out.println(" accepted");
		if(testNode != null) {
			float px = testNode.getX() + testX;
			float py = testNode.getY() + testY;
			testNode.setPosition(px, py);
		}		
	}

	/**
	 * @see alvahouse.eatool.GUI.Graphical.SimulatedAnnealingLayoutStrategy#rejectChange()
	 */
	protected void rejectChange() {
//			System.out.println(" rejected");
	}


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.layout.SimulatedAnnealingLayoutStrategy#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		nodeIter = null;
	}


	/** the amount we scale the gaussian amount to move a node by */
    float spread = 0.0f;	
	private Iterator<? extends Node> nodeIter = null;
	private Node testNode = null;
	private float testX;
	private float testY;
	private Random random = new Random();
	private float initialTemp;
	
}
