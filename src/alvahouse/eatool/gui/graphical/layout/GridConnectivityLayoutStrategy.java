/*
 * GridConnectivityLayoutStrategy.java
 * Created on 14-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * GridConnectivityLayoutStrategy is an auto-layout that, using a grid,
 * attempts to group the most connected nodes together.
 * <p>
 * The algorithm is as follows:<br/>
 * All the nodes are given a connectivity score by recursively 
 * following arcs from the node to a limited range.<br/>
 * Arcs are given scores by summing the connectivity of their ends.<br/>
 * The list of arcs is pruned to coalesce arcs between the same nodes.<br/>
 * The list of arcs is sorted and arcs' nodes are added to the grid
 * starting with the arc with the highest connectivity score.<br/>
 * The nodes are added to the grid by keeping a list of positions
 * that border positions have been used.  If a node is placed that
 * hasn't been connected to a node that is already placed it uses the 
 * border cell on the front of the list.  Otherwise, the border cell 
 * nearest to its connected partner is used.  When a cell is used its
 * neighbours are placed on the border queue if they have not already
 * been queued.
 * </p>
 * @author Bruce.Porteous
 *
 */
public class GridConnectivityLayoutStrategy
	implements IGraphicalLayoutStrategy {

	private int xSpacing;
	private int ySpacing;
	private Random rand = new Random(42); // fix seed at arbitrary number to make process reproducable
	private boolean complete = false;
	private boolean[][] board;	
	private List<Point> border;
	private ArcDetails[] arcDetails;
	private NodeDetails[] nodeDetails;
	private Map<Node,NodeDetails> detailLookup; // of NodeDetails keyed by Node.

	/**
	 * NodeDetails tracks extra information (such as connectivity
	 * score) for a node.
	 * @author Bruce.Porteous
	 *
	 */
	private class NodeDetails implements Comparable<NodeDetails>{
		//Node node;
		float score;
		boolean allocated = false;
		int gridX = 0;
		int gridY = 0;
		
		/**
		 * Constructs NodeDetails for a given node and score.
		 * @param node is the node to track.
		 * @param score is its connectivity score.
		 */
		NodeDetails(Node node, float score){
			//this.node = node;
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
		
		/**
		 * Sets the grid location the associated node is placed at.
		 * @param x is the x coordinate of the grid.
		 * @param y is the y coordinate of the grid.
		 */
		void setGrid(int x, int y){
			gridX = x;
			gridY = y;
		}
		
		/**
		 * Gets the x coordinate of the node's grid position.
		 * @return x coordinate.
		 */
		int getGridX(){
			return gridX;
		}
		
		/**
		 * Gets the y coordinate of the node's grid position.
		 * @return y coordinate.
		 */
		int getGridY(){
			return gridY;
		}
	}
	
	/**
	 * ArcDetails tracks extra information (such as connectivity
	 * score) for an arc.
	 * @author Bruce.Porteous
	 *
	 */
	private class ArcDetails implements Comparable<ArcDetails> {
		Arc arc;
		float score;
		int arcHash;
		
		/**
		 * Creates ArcDetails for a given arc and score.
		 * @param arc is the associated Arc.
		 * @param score is the connectivity score for that arc.
		 */
		ArcDetails(Arc arc, float score){
			this.arc = arc;
			this.score = score;
			arcHash = arc.hashCode();
		}
		
		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * Note that as well as comparing by score, if the scores
		 * are the same but the nodes refer to different arcs, 
		 * then the comparator uses hash codes to give an arbitrary
		 * but repreatable difference.  This means that the only 
		 * arcs judged to be equal are those that join the same 2
		 * nodes.
		 */
		public int compareTo(ArcDetails other) {
			if(score < other.score) {
				return -1;
			}
			if(score > other.score){
				return 1;
			}
			
			// If the arc joins the same 2 nodes (either way
			// round) then treat purely as the same.  Score must
			// be the same so we only check for equality here.
			if( (arc.getStartEnd() == other.arc.getStartEnd() )
				&& (arc.getFinishEnd() == other.arc.getFinishEnd())
				|| (arc.getStartEnd() == other.arc.getFinishEnd() )
				&& (arc.getFinishEnd() == other.arc.getStartEnd())){
				return 0;	
			}
			
			// If they're not the same arc, provide an arbitrary
			// (but repeatable!) difference.
			long dif = (long)arcHash - (long)other.arcHash;	
			return (dif > 0) ? 1 : -1;
		}
	}
	
	/**
	 * Creates the layout strategy.
	 */
	public GridConnectivityLayoutStrategy() {
		super();
	}

	/** 
	 * Lays out the graph - see class description for an overview
	 * of the algorithm.
	 * @see uk.co.alvagem.graphical.layout.IGraphicalLayoutStrategy#layout(alvahouse.eatool.gui.graphical.NodeGraph)
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

		// Now convert node scores to arc scores.  Will do 
		// layout by the highest scoring arcs.
		arcDetails = new ArcDetails[graph.arcCount()];
		idx = 0;
		for(Arc arc : graph.getArcs()){
			NodeDetails first = (NodeDetails)detailLookup.get(arc.getStartEnd());
			NodeDetails second = (NodeDetails)detailLookup.get(arc.getFinishEnd());
			float score = first.score + second.score;
			
			arcDetails[idx++] = new ArcDetails(arc,score);
		}
		
		// Now sort arc details into ascending order.
		Arrays.sort(arcDetails);
		
		// Prune the list of arc details.  Note - if 2 arcs
		// apply to the same 2 nodes then they must have the 
		// same score.
		// Note, that when arcs are effectively merged, their
		// scores are added. 
		int nArcs = 0;
		for(int head = 1; head < arcDetails.length; ++head){
			if(arcDetails[nArcs].compareTo(arcDetails[head]) == 0){
				arcDetails[nArcs].score += arcDetails[head].score;
			} else { // just compact
				arcDetails[++nArcs] = arcDetails[head];
			}
		}
		++nArcs;
		
		// Resort the pruned list as coalesced arcs have higher scores
		Arrays.sort(arcDetails,0,nArcs);
		
		// Now we've got a list of arcs, sorted by strength of
		// local connectivity, lay them out. 
		int width = (int)Math.round(Math.sqrt(nodeCount));
		
		initialiseBoard(width);
		
		border = new LinkedList<Point>();	// Store border cells.
		border.add(new Point(width,width)); // initial seed.
		for(int i=nArcs -1; i>=0; --i){
			Arc arc = arcDetails[i].arc;
			placeNode(arc.getStartEnd(), arc.getFinishEnd());
			placeNode(arc.getFinishEnd(), arc.getStartEnd());
		}
		
		// Allow GC to reclaim before next layout (maybe a long time coming...
		border = null;
		board = null;
		nodeDetails = null;
		arcDetails = null;
		
		complete = true; // single pass.
	}
	
	/**
	 * Creates and initialises (to all unused) the grid used to 
	 * track which cells are (potentially) used.
	 * @param width is the half-width to make the grid - normally
	 * the square root of the number of nodes.
	 */
	private void initialiseBoard(int width) {
		board = new boolean[2 * width][ 2 * width];
		for(int iy = 0; iy < (2*width); ++iy){
			for(int ix = 0; ix < (2*width); ++ix){
				board[ix][iy] = false;
			}
		}
	}

	/**
	 * This places a node on the grid.
	 * @param n is the node to place.
	 * @param relatedNode is the related node from the other end 
	 * which we would like to place Node n close to.
	 */
	private void placeNode(Node n, Node relatedNode){
		NodeDetails nodeDetails = (NodeDetails)detailLookup.get(n);
		if(nodeDetails.allocated){
			return;
		}
		
		Point place = findPosition(relatedNode);

		n.setPosition(place.x * xSpacing, place.y * ySpacing);
		board[place.x][place.y] = true;			// this position now taken.
		nodeDetails.allocated = true;			// by this node.
		nodeDetails.setGrid(place.x, place.y);	// here.
		
		addFreeBorders(place);
	}
	
	/**
	 * Adds any cells that are the neighbours of the given cell to
	 * the border list providing that they lie on the grid and 
	 * haven't previously been added to the list.  Note that the
	 * borders are ultimately added in random order to balance
	 * the growth of the diagram.
	 * @param place is the Point for which the neighbours should
	 * be added.
	 */
	private void addFreeBorders(Point place) {
		// Now add the neighbours of this point to the border list
		Point[] neighbours = new Point[4];
		int idx = 0;
		if(place.x > 0 ){
			if(board[place.x-1][place.y] == false){
				neighbours[idx++] = new Point(place.x-1, place.y);
				board[place.x-1][place.y] = true;
			}
		}
		if(place.x < board.length-1){
			if(board[place.x+1][place.y] == false){
				neighbours[idx++] = new Point(place.x+1, place.y);
				board[place.x+1][place.y] = true;
			}
		}
		if(place.y > 0){
			if(board[place.x][place.y-1] == false){
				neighbours[idx++] = new Point(place.x, place.y-1);
				board[place.x][place.y-1] = true;
			}
		}
		if(place.y < board.length-1){
			if(board[place.x][place.y+1] == false){
				neighbours[idx++] = new Point(place.x, place.y+1);
				board[place.x][place.y+1] = true;
			}
		}
		
		// Add the valid neighbours to the start of the border
		// list in random order.
		while(idx > 0){
			int i = rand.nextInt(idx);
			if(border.contains(neighbours[i])){
				System.out.println("Already contains point");
			}
			border.add(0,neighbours[i]);
			neighbours[i] = neighbours[idx-1];
			--idx;
		}
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
	 * This finds the position for a node from the borders list.  If
	 * the node linked to the one being placed has already been allocated
	 * this finds the border entry closest to that node. Otherwise it just
	 * takes the front of the border list.
	 * @param linked is a node joined to the one we want to place.
	 * @return Point containing the grid coordinates for the node.
	 */
	private Point findPosition(Node linked){
		NodeDetails linkedDetails = (NodeDetails)detailLookup.get(linked);
		Point place = null;
		if(linkedDetails.allocated){
			// Find border closest to linked node
			double linkedX = linkedDetails.getGridX();
			double linkedY = linkedDetails.getGridY();
			double dist = Double.MAX_VALUE;
			for(Point cell : border){
				double ds = cell.distanceSq(linkedX,linkedY);
				if(ds < dist){
					place = cell;
					dist = ds;
				}
			}
		} else {
			place = (Point)border.get(0); // Simple find position
		}
		border.remove(place);
		
		return place;
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
