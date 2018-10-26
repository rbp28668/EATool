/*
 * HieararchyLayoutStrategyBase.java
 * Project: EATool
 * Created on 28-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * HieararchyLayoutStrategyBase is an abstract base class for any hiearchy
 * layout.  It builds a tree of HNode and passes it, with the original graph
 * to the layoutHierarchy method which should be all that needs to be 
 * over-ridden.
 * 
 * @author rbp28668
 */
public abstract class HieararchyLayoutStrategyBase implements
        IGraphicalLayoutStrategy {

    private boolean complete = false;

    /**
     * 
     */
    public HieararchyLayoutStrategyBase() {
        super();
    }

    protected void setComplete(boolean complete){
        this.complete = complete;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#layout(alvahouse.eatool.gui.graphical.NodeGraph)
     * Note that arcs are directional - for a hierarchy the start end points to the parent, the finish end to children.
     */
    public void layout(NodeGraph graph) {
		int nodeCount = graph.nodeCount();
		if(nodeCount < 2 || graph.arcCount() == 0){
			setComplete(true);
			return;
		}
		
		
		// Find the root node. It's the one that is connected to the 
		// appropriate type of relationship but only at the "one" end
		// not the "many" end.
		Node root = null;

        for(Iterator<? extends Node> iterNodes = graph.getNodes().iterator(); iterNodes.hasNext() && root == null;){
		    Node node = (Node)iterNodes.next();
		    root = node; // assume this one....
		    for(Arc arc : node.getArcs()){
		    	if(arc.getFinishEnd() == node){
		    		root = null;
		    		break;
		    	}
		    }
		}

        if(root == null){
            throw new IllegalStateException("null hierarchy root (impossible!)");
        }
        
        Set<Node> visited = new HashSet<Node>();
        HNode hierarchy = buildHierarchy(root, visited);
        layoutHierarchy(graph, hierarchy);
        
        setComplete(true);
    }


    /**
     * @param graph
     * @param hierarchy
     */
    protected abstract void layoutHierarchy(NodeGraph graph, HNode hierarchy);

//    /**
//     * Utility method that resets all the arcs on the graph to their most
//     * direct path. 
//     */
//    protected void resetArcs(NodeGraph graph) {
//        for(Iterator<Arc> iter = graph.getArcs(); iter.hasNext();){
//            Arc arc = (Arc)iter.next();
//            arc.normalise();
//        }
//    }

    /**
     * @param root is the root of the part of the hierarchy we are building.
     * @param visited tracks which nodes we've visited - just in case it's
     * not a proper hierarchy!
     */
    private HNode buildHierarchy(Node root, Set<Node>visited) {

        HNode node = new HNode(root);
        visited.add(root);
        
	    for(Arc arc : root.getArcs()){
	    	Node childNode = arc.getOtherEnd(root);
	    	if(!visited.contains(childNode)){
	    		node.addChild(buildHierarchy(childNode, visited));
	    	}
	    }
	    
	    return node;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#reset()
     */
    public void reset() {
        complete = false;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#isComplete()
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Hierarchy node.
     * @author bruce.porteous
     *
     */
    protected static class HNode {
        
        private List<HNode> children = new LinkedList<HNode>();
        private Node node;
        private float compositeWidth;
        private float height;
        
        HNode(Node node){
            this.node = node;
        }
        
        void addChild(HNode child){
            children.add(child);
        }
        
        List<HNode> getChildren(){
            return children;
        }
        
        Node getNode() {
            return node;
        }
        
        
        /**
         * @return Returns the compositeWidth.
         */
        float getCompositeWidth() {
            return compositeWidth;
        }
        /**
         * @param compositeWidth The compositeWidth to set.
         */
        void setCompositeWidth(float compositeWidth) {
            this.compositeWidth = compositeWidth;
        }
        /**
         * @return Returns the height.
         */
        public float getHeight() {
            return height;
        }
        /**
         * @param height The height to set.
         */
        public void setHeight(float height) {
            this.height = height;
        }
    }
    
}
