/*
 * LayoutManager.java
 * Created on 12-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import alvahouse.eatool.repository.graphical.DiagramsChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramsChangeListener;

/**
 * LayoutManager manages the use of layout strategies to layout a diagram.
 * @author Bruce.Porteous
 *
 */
/**
 * @author bruce.porteous
 *
 */
public class LayoutManager implements DiagramsChangeListener{

private NodeGraph model;
private IGraphicalLayoutStrategy layout;
private FlexAnnealLayoutStrategy flex;
private LinkedList<Island> islands = new LinkedList<Island>(); 		// disconnected islands
private Map<Node,Island> islandMap = new HashMap<Node,Island>();	// map nodes to islands
private boolean changedSinceLayout = false;
	
	/**
	 * Creates a layout manager for a given model.
	 * @param model is the node graph that will need to be laid out.
	 */
	public LayoutManager(NodeGraph model) {
		super();
		if(model == null){
			throw new NullPointerException("Null model passed to layout manager");
		}
		this.model = model;
		loadFromModel();
		setDefaultStrategy();
	}

	/**
	 * Sets the default layout strategy for the manager.
	 */
	public final void setDefaultStrategy(){
		CompositeGraphicalLayoutStrategy strat = new CompositeGraphicalLayoutStrategy();
		 strat.add(new GridLayoutStrategy());
//		   strat.add(new GridAnnealLayoutStrategy());
//		   strat.add(new SpringLayoutStrategy());
		 flex = new FlexAnnealLayoutStrategy();
		 strat.add(flex);
		 strat.reset();
		 layout = strat;
	}
	
	/**
	 * Resets the layout information.
	 */
	public void reset(){
		islands.clear();
		islandMap.clear();
		changedSinceLayout = true;		
	}
	
	/**
	 * Loads up the layout manager from the model data.
	 */
	public void loadFromModel(){
		reset();
		for(Node node : model.getNodes()){
			addNode(node);
		}
		for(Arc arc : model.getArcs()){
			addArc(arc);
		}
		changedSinceLayout = true;		
	}
	
	/**
	 * Sets the layout strategy for laying out the gtraph.
	 * @param layout is the new strategy to use.
	 */
	public void setLayout(IGraphicalLayoutStrategy layout){
		if(layout == null){
			throw new NullPointerException("Null strategy passed to layout manager");
		}
		this.layout = layout;
		changedSinceLayout = true;		
	}
	
	/**
	 * Determines whether the manager has changed since the last layout.
	 * @return
	 */
	public boolean hasChanged() {
		return changedSinceLayout;
	}
	
	/**
	 * Layout the graphs to fit within the given dimensions
	 * @param size is the size of the window to scale to.
	 * @return the suggested scale value.
	 */
	public float layoutGraphs(Dimension size) {
		
		if(size == null){
			throw new NullPointerException("Null size passed to layout");
		}
		
		BoxPacker packer = new BoxPacker();
			
		for(Iterator<Island> iterIslands = getIslands(); iterIslands.hasNext();) {
			Island island = (Island)iterIslands.next();				
			//System.out.println("laying out island size " + island.nodeCount() + ", " + island.arcCount());
			layout.reset();
			while(!layout.isComplete()) {
				layout.layout(island); 
			}
			
			for(Arc arc : island.getArcs()){
				arc.normalise();
			}
			
			packer.addIsland(island);	// for final box packing layout
		}

		// Calculate scale so model fits window
		// pack using current window dimensions for aspect ratio
		packer.packInto(size.width, size.height);

		// Now set scaling for viewing.
		Rectangle2D.Float bounds = model.getBounds();
		float scalex = size.width / (2 + bounds.width);
		float scaley = size.height / (2 + bounds.height);
		float scale = 1.0f;
		if(scalex > scaley){
			scale = scaley;
		} else{
			scale = scalex;
		}
		
		changedSinceLayout = false;		
		
		return scale;			
 	}

	public int islandCount() {
		return islands.size();
	}	    
	
	public Iterator<Island> getIslands() {
		return islands.iterator();
	}
	
    
	/** 
	 * Adds a node to the graph.
	 */
	public void addNode(Node node) {
        
		Island island = new Island();
		island.addNode(node);
        
		islands.addLast(island);
		islandMap.put(node,island);
		changedSinceLayout = true;		
	}

	/**
	 * Adds an arc to the graph.  Note that the nodes must already have been added.
	 * @param arc is the arc to add.
	 * @throws IllegalStateException
	 */
	public void addArc(Arc arc) throws IllegalStateException{
    	
    	if(arc == null) {
    		throw new NullPointerException("null arc being added to layout manager");
    	}
    	
		Node nodeStart = arc.getStartEnd();
		Node nodeFinish = arc.getFinishEnd();
		if(nodeStart == null || nodeFinish == null)
			throw new IllegalStateException("Missing node objects on arc");

		// Work out which island the nodes go into.  If they are different
		// then these islands are in fact joined, and need to be coalesced.
		Island islandStart = (Island)islandMap.get(nodeStart);
		Island islandFinish = (Island)islandMap.get(nodeFinish);
		if(islandStart == null || islandFinish == null) {
			throw new IllegalStateException("Arc references unknown node objects");
		}
		if(islandStart != islandFinish) {
			islandStart.merge(islandFinish);
    		
			for(Node node : islandFinish.getNodes()) {
				islandMap.put(node, islandStart);
			}
    		
			islandFinish.reset();
			islands.remove(islandFinish);
		}
		islandStart.addArc(arc);
		changedSinceLayout = true;		
	}
    
	/** deletes a single node and any attached arcs from the graphical model 
	 * @param n is the node to delete
	 */
	protected void deleteNode(Node n) {
    	assert(n != null);
    	assert(islandMap.containsKey(n));
		Island island = (Island)islandMap.get(n);
		island.deleteNode(n);
		if(island.nodeCount() == 0) {
			islands.remove(island);
		}
		islandMap.remove(n);
        
	}
    
	/** deletes a single arc from the graphical model
	 * @param a is the arc to delete
	 */
	protected void deleteArc(Arc a) {
 		assert(a != null);
		// Make sure the arc is disconnected from its nodes
		a.getStartEnd().deleteArc(a);
		a.getFinishEnd().deleteArc(a);
        
		// now make sure the arc is fully removed from the lists
		removeArcFromLists(a);
	}

	/**
	 * Method removeArcFromLists removes an arc from the lists and maps while
	 * ensuring the islands are kept up to date.  This doesn't unlink the
	 * arc from its nodes (use deleteArc for this).
	 * @param a is the arc to be removed
	 */
	private void removeArcFromLists(Arc a) {
		assert(a != null);
		// by definition the end nodes must be in the same island - key by start
		assert(islandMap.containsKey(a.getStartEnd()));
		assert(islandMap.containsKey(a.getFinishEnd()));
		Island island = (Island)islandMap.get(a.getStartEnd());
		// Delete the arc from the island - this may split the island, if it
		// does then we will have a new island. If this happens we need to 
		// update the island map.
		Island newIsland = island.deleteArc(a); // may need to split island
		if(newIsland != null) {
			for(Node node : newIsland.getNodes()) {
				islandMap.put(node, newIsland);
			}
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#typesUpdated(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void typesUpdated(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeAdded(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeAdded(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeChanged(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeChanged(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeDeleted(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeDeleted(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramsUpdated(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramsUpdated(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramAdded(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramAdded(DiagramsChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramChanged(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramChanged(DiagramsChangeEvent e) {
		if(e.getSource() == model){
			changedSinceLayout = true;		
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramDeleted(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramDeleted(DiagramsChangeEvent e) {
	}



}
