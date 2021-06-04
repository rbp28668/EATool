package alvahouse.eatool.repository.graphical.standard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.gui.graphical.layout.NodeGraph;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * StandardDiagram is a graphical display of entities & their relationships
 * that displays part of the model. This is a concrete implementation 
 * of AbstractNodeGraph where Symbols and Connectors form the Nodes and Arcs.
 * @author bruce.porteous
 *
 */
public class StandardDiagram extends Diagram implements NodeGraph{

	private List<Symbol> symbols = new LinkedList<Symbol>();
	private List<Connector> connectors = new LinkedList<Connector>();
	private List<TextBox> textBoxes = new LinkedList<TextBox>();
	private List<ImageDisplay> images = new LinkedList<ImageDisplay>();
	
	private Map<Object,Symbol> nodeMap = new HashMap<Object,Symbol>();			// map data object to graphical nodes
	private Map<Object,Connector> arcMap = new HashMap<Object,Connector>();			// and relationship objects to graphical arcs
	private Map<UUID,Symbol> symbolsByUUID = new HashMap<UUID,Symbol>();
	private Map<UUID,Connector> connectorsByUUID = new HashMap<UUID,Connector>();
 	

	/**
	 * Constructor for StandardDiagram.
	 */
	public StandardDiagram(DiagramType type, UUID key) {
		super(type,key);
	}

	/**
	 * Gets the symbols in this diagram.
	 * @return an unmodifiable collection of symbols.
	 */
	public Collection<Symbol> getSymbols() {
		return Collections.unmodifiableCollection(symbols);
	}
	
	/**
	 * Gets the connectors in this diagram.
	 * @return an unmodifiable collection of connectors.
	 */
	public Collection<Connector> getConnectors() {
		return Collections.unmodifiableCollection(connectors);
	}
	/**
	 * @see alvahouse.eatool.GUI.Graphical.AbstractNodeGraph#nodeCount()
	 */
    public int nodeCount(){
    	return symbols.size();
    }
        
    /** gets a collection of the complete set of nodes
     * in the graphical model
     * @return a node iterator.
     */
    public Collection<? extends Node> getNodes(){
    	return symbols;
    }
    
    public Node[] nodesAsArray(){
    	return (Node[])symbols.toArray();
    }
    /** gets a count of the number of arc in the graphical model
     * @return the arc count
     */
    public int arcCount(){
    	return connectors.size();
    }
    
    /** gets an iterator that will iterate over the complete set of arcs
     * in the graphical model
     * @return an arc iterator.
     */
    public Collection<? extends Arc> getArcs() {
    	return connectors;
    }
	
    /**
     * Gets the diagram type as a StandardDiagramType.  Convenience method.
     * @return the diagram type - must be a StandardDiagramType.
     */
    private StandardDiagramType getStandardType(){
        return(StandardDiagramType)getType();
    }
    
	/**
	 * Adds a Symbol to the diagram.
	 * @param s is the symbol to add.
	 */
	public void addSymbol(Symbol s)  throws Exception{
		if(s == null) {
			throw new NullPointerException("Adding null symbol to diagram");
		}
		symbols.add(s);
		nodeMap.put(s.getItem(), s);   // allow node lookup by user object
		symbolsByUUID.put(s.getKey(),s);
		fireDiagramChanged();
		fireSymbolAdded(s);
	}
	
	/**
	 * Adds a connector to the diagram.
	 * @param c is the connector to add.
	 */
	public void addConnector(Connector c) throws Exception{
		if(c == null) {
			throw new NullPointerException("Adding null connector to diagram");
		}
		connectors.add(c);
		arcMap.put(c.getItem(),c);
		connectorsByUUID.put(c.getKey(),c);
		fireDiagramChanged();
		fireConnectorAdded(c);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Diagram#reset()
	 */
	public void reset()  throws Exception{
		symbols.clear();
		connectors.clear();	
		nodeMap.clear();
		arcMap.clear();
		symbolsByUUID.clear();
		connectorsByUUID.clear();
		textBoxes.clear();
		images.clear();
		fireMajorDiagramChange();
	}
	
	
	/**
	 * @param g
	 */
	public void sizeWith(Graphics2D g) throws Exception{
		for(Connector c : connectors){
			c.sizeWith(g);
		}
		
		for(Symbol s : symbols){
			s.sizeWith(g);
		}
		
		for( TextBox box:  textBoxes){
		    box.sizeWith(g);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Diagram#draw(java.awt.Graphics2D, float)
	 */
	public void draw(Graphics2D g, float zoom) throws Exception {
		g.setBackground(getBackgroundColour());
		
		for(TextBox box : textBoxes){
		    box.draw(g,zoom);
		}

		for(ImageDisplay img : images){
			img.draw(g, zoom);
		}

		for(Connector c : connectors){
			c.draw(g, zoom);
		}

		for(Symbol s : symbols){
			s.draw(g, zoom);
		}

		
        for(TextBox box : textBoxes){
		    box.drawCollateral(g,zoom);
		}
       
        for(ImageDisplay img : images){
			img.drawCollateral(g, zoom);
		}
		
        for(Connector c : connectors){
			c.drawCollateral(g, zoom);
		}

        for(Symbol s : symbols){
			s.drawCollateral(g, zoom);
		}
		
	}




	/**
	 * Looks for a connector at a given location
	 * @param mx is the x mouse coordinate.
	 * @param my is the y mouse coordinate.
	 * @param zoom is the current scale factor the diagram is being drawn at.
	 * @return true if there's a connector at the given point.
	 */
	public Connector getConnectorAt(int mx, int my, float zoom) {
		Connector selected = null;
		for(Connector c : getConnectors()){
			if(c.hitTest(mx,my,zoom)){
				selected = c;
				break;
			}
		}
		return selected;
	}

	/**
	 * Looks for a symbol at a given location
	 * @param mx is the x mouse coordinate.
	 * @param my is the y mouse coordinate.
	 * @param zoom is the current scale factor the diagram is being drawn at.
	 * @return the symbol at the given point of null if none there.
	 */
	public Symbol getSymbolAt(int mx, int my, float zoom) {
		Symbol selected = null;
		for(Symbol s : getSymbols()){
			if(s.hitTest(mx,my,zoom)){
				selected = s;
				break;
			}
		}
		return selected;
	}


	/**
	 * Gets the bounds of the diagram at a given zoom factor.
	 * @param zoom is the zoom factor to use.
	 * @return the diagram's bounds.
	 */
	public Rectangle2D.Float getBounds(float zoom){
		
		Rectangle2D.Float bounds = new Rectangle2D.Float();
		for(GraphicalObject obj : getSymbols()){
			bounds.add(obj.getBounds(zoom));
		}
		for(GraphicalObject obj : getTextBoxes()){
			bounds.add(obj.getBounds(zoom));
		}
		for(GraphicalObject obj : getImages()){
			bounds.add(obj.getBounds(zoom));
		}
		for(GraphicalObject obj : getConnectors()){
			bounds.add(obj.getBounds(zoom));
		}
		
		return bounds;
	}
	
	/**
	 * Gets the bounds with any handles or other collateral.
	 * @param zoom is the current zoom factor.
	 * @return The extended bounds of the diagram.
	 */
	public Rectangle2D.Float getExtendedBounds(float zoom){
		
		Rectangle2D.Float bounds = new Rectangle2D.Float();
        for(GraphicalObject obj : getSymbols()){
			bounds.add(obj.getExtendedBounds(zoom));
		}
        for(GraphicalObject obj : getTextBoxes()){
			bounds.add(obj.getExtendedBounds(zoom));
		}
        for(GraphicalObject obj : getImages()){
			bounds.add(obj.getExtendedBounds(zoom));
		}
        for(GraphicalObject obj : getConnectors()){
			bounds.add(obj.getExtendedBounds(zoom));
		}
		
		return bounds;
	}
	

	/**
	 * Writes the StandardDiagram out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		
	    startXML(out);
		
		if(!isDynamic()){
			for(Symbol symbol : getSymbols()){
				symbol.writeXML(out);
			}
			for( Connector connector : getConnectors()){
				connector.writeXML(out);
			}
			for(TextBox box : getTextBoxes()){
				box.writeXML(out);
			}
			for(ImageDisplay image : getImages()){
				image.writeXML(out);
			}
			
		}
		
		out.stopEntity();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString().
	 * Returns the digram name for use in lists, trees etc.
	 */
	public String toString() {
		return getName();
	}

	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.NodeGraph#offsetModel(float, float)
	 */
	public void offsetModel(float dx, float dy) {
		for(Node node : symbols){
			float x = node.getX();
			float y = node.getY();
			node.setPosition(x + dx, y + dy);
		}
	}


	/** Gets a symbol associated with a given object.
	 * @param key is the object associated with the node
	 * @return the associated node.
	 */
	public Symbol lookupNode(KeyedItem key) {
		return (Symbol)nodeMap.get(key);
	}
    
	/** Gets an connector associated with a given object.
	 * @param key is the object associated with the arc.
	 * @return the associated arc.
	 */
	public Connector lookupArc(KeyedItem key) {
		return (Connector)arcMap.get(key);
	}

	/** Adds a node associated with a user object.
	 * @param item is the user Object that should be associated with a node
	 * @throws LogicException
	 * @throws NullPointerException
	 */
	public GraphicalProxy addNodeForObject(KeyedItem item){
	    
	    Symbol node = null;
	    try {
            MetaEntity me = null;
            if(item instanceof Entity){
                me = ((Entity)item).getMeta();
            }
            SymbolType st = getStandardType().getSymbolTypeFor(me);
            if(st != null) {
            	node = st.newSymbol(item,0.0f, 0.0f);
            	addSymbol(node);
            }
        } catch (Exception e) {
            node = null;
        }
		return node;
        
	}

	/** Adds an arc associated with a user object to connect existing nodes identified
	 * by 2 other user objects
	 * @param item is the user object to be associated with the arc
	 * @param firstEnd is the user object that identifies the node at one end of the arc
	 * @param secondEnd is the user object that identifies the node at the other end of the arc
	 * @throws LogicException
	 * @throw IllegalStateException if the end nodes aren't known to the model
	 */
	public Connector addArcForObject(KeyedItem item, KeyedItem firstEnd, KeyedItem secondEnd)  throws Exception{
    	
		Connector arc = null; //new BasicConnector(new UUID(),arcObject,null);
		MetaRelationship mr = null;
		
		try {
            if(item instanceof Relationship){
                mr = ((Relationship)item).getMeta();
            }
            ConnectorType ct = getStandardType().getConnectorTypeFor(mr);
            if(ct != null){
                arc = ct.newConnector(new UUID());
                arc.setItem(item);
            	Node nodeStart = (Node)nodeMap.get(firstEnd);
            	Node nodeFinish = (Node)nodeMap.get(secondEnd);
            	if(nodeStart == null || nodeFinish == null)
            		throw new IllegalStateException("node objects not recognised when adding arc to graphical model");

            	arc.setEnds(nodeStart,nodeFinish);
            	nodeStart.addArc(arc);
            	nodeFinish.addArc(arc);
                
            	addConnector(arc);
            }
        } catch (LogicException e) {
            arc = null; // abandon anything half baked.
        }
		
		return arc;
	}
    
	/** deletes a single node and any attached arcs from the graphical model 
	 * @param n is the node to delete
	 */
	public void deleteNode(Node n) {
		List<Arc> arcs = new LinkedList<Arc>(n.getArcs());
		for(Iterator<Arc> iter = arcs.iterator();iter.hasNext();) {
			Connector a = (Connector)iter.next();
			deleteArc(a);
		}
		
		Symbol s = (Symbol)n;
		symbols.remove(s);
		nodeMap.remove(s.getItem());
		symbolsByUUID.remove(s.getKey());
	}
    
	/** deletes a single arc from the graphical model
	 * @param a is the conenctor to delete
	 */
	public void deleteArc(Connector a) {
 		
		// Make sure the arc is disconnected from its nodes
		a.getStartEnd().deleteArc(a);
		a.getFinishEnd().deleteArc(a);

		Connector c = (Connector)a;
		connectors.remove(c);
		arcMap.remove(c.getItem());
		connectorsByUUID.remove(c.getKey());
	}

	/**
	 * Looks up a Symbol given its UUID.
	 * @param symbolKey is the UUID that identifies the symbol.
	 * @return the correspondig symbol.
	 */
	public Symbol getSymbol(UUID symbolKey){
	    return (Symbol)symbolsByUUID.get(symbolKey);
	}
	
	/**
	 * Looks up a Connector given its UUID.
	 * @param connectorKey is the UUID that identifies the connector.
	 * @return the corresponding connector.
	 */
	public Connector getConnector(UUID connectorKey){
	    return (Connector)connectorsByUUID.get(connectorKey);
	}

    /**
     * Removes a node attached to a given object.
     * @param item is the object attached to the node to remove.
     */
    public void removeNodeForObject(KeyedItem item) {
        Node node = (Node)nodeMap.get(item);
        if(node != null){
            deleteNode(node);
        }
        
    }

    /**
     * Deletes the arc attached to a given object.
     * @param object is the item attached to the arc to remove.
     */
    public void removeArcForObject(KeyedItem item) {
        Connector arc = (Connector)arcMap.get(item);
        if(arc != null){
            deleteArc(arc);
        }
    }

    /**
     * Takes 3 objects and sees if the arc corresponding to the connecting
     * object has nodes that correspond to the start and finish objects.  If
     * so, then fine, otherwise the connector on the diagram is now invalid
     * and must go.
     * @param connects is the object corresponding to an arc.
     * @param start is the object corresponding to the start node of the arc.
     * @param finish is the object corresponding to the finish node of the arc.
     */
    public void validate(KeyedItem connects, KeyedItem start, KeyedItem finish) {
        Connector arc = (Connector)arcMap.get(connects);
        if(arc != null){
            Symbol startSymbol = (Symbol) arc.getStartEnd();
            Symbol finishSymbol = (Symbol)arc.getFinishEnd();
            if( !(startSymbol.getItem().equals(start) 
                    && finishSymbol.getItem().equals(finish))){
                deleteArc(arc);
            }
        }
        
    }

	/**
	 * Adds all possible connectors to this diagram.
     */
    public void addConnectors()  throws Exception{
        
        for(Iterator<? extends Node> iter = getNodes().iterator(); iter.hasNext();){
        	Symbol symbol = (Symbol)iter.next();
        	
        	Entity entity = (Entity)symbol.getItem();
        	Set<Relationship> connected = entity.getConnectedRelationships();
        	
        	for(Relationship r : connected ){
        		
        		Entity startEntity = r.start().connectsTo();
        		Entity finishEntity = r.finish().connectsTo();
        		
        		Node start = lookupNode(startEntity);
        		Node finish = lookupNode(finishEntity);
        		
        		// Need both ends to do anything with and connector type must be supported
        		if(start != null && finish != null 
        		    && getStandardType().hasConnectorTypeFor(r.getMeta())){ 
        			if(lookupArc(r) == null){
        				addArcForObject(r,startEntity,finishEntity);
        			}
        		}
        	}
        	
        }
    }

	/**
	 * Adds all connectors to this diagram that match one of the
	 * MetaRelationships in the given set.
	 * @param types is a Set of MetaRelationship that determine which
	 * types of connector should be added.
     */
    public void addConnectors(Set<MetaRelationship> types)  throws Exception{
        
        for(Iterator<? extends Node> iter = getNodes().iterator(); iter.hasNext();){
        	Symbol symbol = (Symbol)iter.next();
        	
        	Entity entity = (Entity)symbol.getItem();
        	Set<Relationship> connected = entity.getConnectedRelationships();
        	
        	for(Relationship r : connected ){
        		if(types.contains(r.getMeta())){
        		
	        		Entity startEntity = r.start().connectsTo();
	        		Entity finishEntity = r.finish().connectsTo();
	        		
	        		Node start = lookupNode(startEntity);
	        		Node finish = lookupNode(finishEntity);
	        		
	        		// Need both ends to do anything with and connector type must be supported
	        		if(start != null && finish != null 
	        		    && getStandardType().hasConnectorTypeFor(r.getMeta())){ 
	        			if(lookupArc(r) == null){
	        				addArcForObject(r,startEntity,finishEntity);
	        			}
	        		}
        		}
        	}
        	
        }
    }

    /**
     * Sets the default colours for all the symbols.
     */
    public void resetPropertiesToDefaults() {
		for(Symbol s : getSymbols()){
			
			MetaEntity me = null;
			if(s.getItem() instanceof Entity){
			    me = ((Entity)s.getItem()).getMeta();
			}
			
			SymbolType st = getStandardType().getSymbolTypeFor(me);
			if(st != null) {
				s.setTextColour(st.getTextColour());
				s.setBackColour(st.getBackColour());
				s.setBorderColour(st.getBorderColour());
			}
		}
    }
    
    /**
     * Sets the background colour for symbol for a given object (if any).
     * @param obj is the object who's symbol we want to set the colour for.
     * @param colour is the colour to set.
     */
    public void setSymbolColourFor(KeyedItem obj, Color colour){
        Symbol symbol = (Symbol)nodeMap.get(obj);
        if(symbol != null){
            symbol.setBackColour(colour);
        }
        
    }

    /**
     * Adds a text box to the diagram.
     * @param box
     */
    public void addTextBox(TextBox box) {
        if(box == null){
            throw new NullPointerException("Can't add null TextBox to diagram");
        }
        textBoxes.add(box);
    }

    /**
     * Gets the collection of TextBox.
     * @return an unmodifiable collection of TextBox
     */
    public Collection<TextBox> getTextBoxes() {
        return Collections.unmodifiableCollection(textBoxes);
    }

    /**
     * Deletes a text box from the diagram.
     * @param box is the TextBox to delete.
     */
    public void deleteTextBox(TextBox box){
        textBoxes.remove(box);
    }
    
    /**
     * Adds an ImageDisplay to the diagram.
     * @param image is the ImageDisplay to add.
     */
    public void addImage(ImageDisplay image){
        if(image == null){
            throw new NullPointerException("Can't add null ImageDisplay to diagram");
        }
        images.add(image);
    }
    
    /**
     * Gets the collection of ImageDisplay.
     * @return the collection of ImageDisplay in the diagram.
     */
    public Collection<ImageDisplay> getImages() {
        return Collections.unmodifiableCollection(images);
    }
    
    /**
     * Deletes the image from the diagram.
     * @param image is the ImageDisplay to delete.
     */
    public void deleteImage(ImageDisplay image){
        images.remove(image);
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.Diagram#clone()
	 */
	@Override
	public Object clone() {
		StandardDiagram copy = new StandardDiagram(getType(),getKey());
		cloneTo(copy);
		return copy;
	}
	
	protected void cloneTo(StandardDiagram copy) {
		super.cloneTo(copy);
		for(Symbol s : symbols) {
			s = (Symbol)s.clone();
			copy.symbols.add(s);
			copy.nodeMap.put(s.getItem(), s);   // allow node lookup by user object
			copy.symbolsByUUID.put(s.getKey(),s);
		}
		for(Connector c : connectors) {
			c = (Connector) c.clone();
			copy.connectors.add(c);
			copy.arcMap.put(c.getItem(),c);
			copy.connectorsByUUID.put(c.getKey(),c);
		}
		for(TextBox t : textBoxes) {
			copy.textBoxes.add((TextBox) t.clone());
		}
		for(ImageDisplay id : images) {
			copy.images.add((ImageDisplay) id.clone());
		}
		
	}

     
}
