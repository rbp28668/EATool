/*
 * ConnectivityLayoutStrategy.java
 * Created on 21-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ConnectivityLayoutStrategy
 * @author Bruce.Porteous
 *
 */
public class ConnectivityLayoutStrategy implements IGraphicalLayoutStrategy {

	/**
	 * NodeDetails tracks extra information (such as connectivity
	 * score) for a node.
	 * @author Bruce.Porteous
	 *
	 */
	private static class NodeDetails implements Comparable<NodeDetails>{
		Node node;
		float score;
		
		/**
		 * Constructs NodeDetails for a given node and score.
		 * @param node is the node to track.
		 * @param score is its connectivity score.
		 */
		NodeDetails(Node node, float score){
			this.node = node;
			this.score = score;
		}
		
		/** 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * Compares the nodes using the connectivity score.
		 */
		public int compareTo(NodeDetails other) {
			if(score < other.score) {
				return -1;
			}
			if(score > other.score){
				return 1;
			}
			return 0;
		}
	}

	private static class NodeGroup{
		List<Node> nodes = new LinkedList<Node>();
		
		void add(Node n){
			nodes.add(n);
		}
	}
	
	private boolean complete = false;
	private int xSpacing;
	private int ySpacing;
	private NodeDetails[] nodeDetails;
	private Map<Node,NodeDetails> detailLookup; // of NodeDetails keyed by Node.
	
	/**
	 * 
	 */
	public ConnectivityLayoutStrategy() {
		super();
	}
	
	

	/**
	 * @return the xSpacing
	 */
	public int getxSpacing() {
		return xSpacing;
	}



	/**
	 * @return the ySpacing
	 */
	public int getySpacing() {
		return ySpacing;
	}



	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy#layout(alvahouse.eatool.gui.graphical.NodeGraph)
	 */
	public void layout(NodeGraph graph) {
		int nodeCount = graph.nodeCount();
		if(nodeCount < 2 || graph.arcCount() == 0){
			complete = true;
			return;
		}
		
		setNodeSpacing(graph);
		
		// Score each node by its local connectivity
		nodeDetails = new NodeDetails[nodeCount];
		detailLookup = new HashMap<Node,NodeDetails>();
		int idx = 0;
		for(Node node : graph.getNodes()) {
			float score = hopScore(node,1.0f,2);
			nodeDetails[idx] = new NodeDetails(node,score);
			detailLookup.put(node,nodeDetails[idx]);
			++idx;
		}
		
		Arrays.sort(nodeDetails);
		
		List<NodeGroup> groups = new LinkedList<NodeGroup>(); 	// of NodeGroup
		Map<Node,NodeGroup> groupLookup = new HashMap<Node,NodeGroup>();	// of NodeGroup by Node
		// Work down through sorted list of nodes starting with the most connected.  Add each node to the
		// group, if the node directly connects to another node already in a group, then add it to that group
		// otherwise start a new group. If a node belongs to 2 or more groups, merge the groups.
		
		List<NodeGroup> groupsToLink = new LinkedList<NodeGroup>();
		for(int i=nodeDetails.length-1; i>=0; --i){
			Node node = nodeDetails[i].node;
			groupsToLink.clear();
			for(Arc arc : node.getArcs()){
				Node other = arc.getOtherEnd(node);
				NodeGroup group = groupLookup.get(other);
				if(group != null){
					groupsToLink.add(group);
				}
			}
			if(groupsToLink.isEmpty()){
				NodeGroup group = new NodeGroup();
				group.add(node);
				groups.add(group);
				groupLookup.put(node,group);
			} else if (groupsToLink.size() == 1){
				// just add the node to the group.
				NodeGroup group = (NodeGroup)groupsToLink.get(0);
				group.add(node);
			} else { // groupsToLink has multiple (>1) groups.
			}
		}
		
		complete = true;
	}

	/**
	 * hopScore builds up a node's local connectivity score. It 
	 * creates a weighted sum of the arcs and the hop-scores of 
	 * each of the connected nodes.  Note that recursion is terminated
	 * by a hop-count reaching zero.  There is no visited concept and
	 * in a highly connected area nodes will be visited multiple times.
	 * @param n is the Node to calculate the score for.
	 * @param weight is the weight to apply to further away nodes.
	 * @param hopCount is the number of hops to recurse before terminating.
	 * @return the score.
	 */
	private float hopScore(Node n, float weight, int hopCount){
		if(hopCount == 0){
			return 0;
		}
		
		float score = 0;
		for(Arc arc : n.getArcs()){
			Node far = arc.getOtherEnd(n);
			score  += 1.0f + weight * hopScore(far, weight * 0.5f, hopCount-1);
		}
		
		return score;
	}

	/**
	 * Calculates the xSpacing and ySpacing values using the sizes
	 * of the symbols on the graph.
	 * @param graph is the graph to calculate the spacing for.
	 */
	private void setNodeSpacing(NodeGraph graph){
		// Set default spacing based on symbol size.
		float xs = 0;
		float ys = 0;
        
        
		for(Node node : graph.getNodes()) {
			Rectangle2D.Float bounds = node.getBounds();
			if(bounds.width > xs){
				xs = (int)bounds.width;
			}
			if(bounds.height > ys){
				ys = (int)bounds.height;
			}
		}
	
		xSpacing = (int)(xs * 1.5f);
		ySpacing = (int)(ys * 1.5f);
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

}
