/*
 * Diagram.java
 * Project: EATool
 * Created on 20-Aug-2006
 *
 */
package alvahouse.eatool.repository.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Diagram
 * 
 * @author rbp28668
 */
public abstract class Diagram extends NamedRepositoryItem {


	private DiagramType type;
	private boolean isDynamic = false;	// True if content script generated so don't save.
	private Color backColour = Color.lightGray;
	private List<DiagramsChangeListener> diagramsListeners = new LinkedList<DiagramsChangeListener>();
	private List<DiagramChangeListener> diagramListeners = new LinkedList<DiagramChangeListener>();
    private EventMap eventMap = new EventMap();
    private boolean mustDoLayout = false;

	
    public final static String ON_DISPLAY_EVENT = "OnDisplay";
    public final static String ON_CLOSE_EVENT= "OnClose";

	public Diagram(DiagramType type, UUID key) {
		super(key);
		this.type = type;
		type.getEventMap().cloneTo(eventMap);
	}
   
	/**
	 * Returns the type.
	 * @return StandardDiagramType
	 */
	public DiagramType getType() {
		return type;
	}
	
	/**
	 * Set or clear the dynamic flag for the diagram.  A dynamic diagram is one where
	 * the contents are set by a script when it is displayed so any connectors or
	 * symbols should not be saved with the diagram.
	 * @param dynamic is the new dynamic state to set.
	 */
	public void setDynamic(boolean dynamic){
	    isDynamic = dynamic;
	}

	/**
	 * Gets the dynamic flag for the diagram - true if script generated and content
	 * shouldn't be saved.
	 * @return the dynamic flag.
	 */
	public boolean isDynamic(){
	    return isDynamic;
	}
	
	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		super.setName(name);
		fireDiagramChanged();
	}
	
    /**
	 * Allows automatic layout to be deferred until the next draw.
	 */
	public void deferLayout(boolean defer){
    	mustDoLayout = defer;
    }
	
	   /**
     * @return
     */
    public boolean isLayoutDeferred() {
        return mustDoLayout;
        
    }

	/**
	 * Gets the event map.
	 * @return the event map.
	 */
	public EventMap getEventMap(){
	    return eventMap;
	}

	/**
	 * Adds a listener that should be notified if the diagram changes.
	 * @param listener is the listener to add.
	 */
	public void addChangeListener(DiagramsChangeListener listener){
		assert(listener != null);
		diagramsListeners.add(listener);
	}
	
	/**
	 * Removes a change listener.
	 * @param listener
	 */
	public void removeChangeListener(DiagramsChangeListener listener){
		assert(listener != null);
		diagramsListeners.remove(listener);
	}
	

	/**
	 * Adds a listener that should be notified if the diagram changes.
	 * @param listener is the listener to add.
	 */
	public void addChangeListener(DiagramChangeListener listener){
		assert(listener != null);
		diagramListeners.add(listener);
	}
	
	/**
	 * Removes a change listener.
	 * @param listener
	 */
	public void removeChangeListener(DiagramChangeListener listener){
		assert(listener != null);
		diagramListeners.remove(listener);
	}

	/**
	 * Notifies any listeners that the diagram has changed.
	 */
	protected void fireDiagramChanged(){
		DiagramsChangeEvent event = new DiagramsChangeEvent(this);
		for(Iterator<DiagramsChangeListener> iter = diagramsListeners.iterator(); iter.hasNext();){
			DiagramsChangeListener listener = iter.next();
			listener.diagramChanged(event);
		}
	}
	
	protected void fireSymbolAdded(GraphicalProxy s){
		DiagramChangeEvent e = new DiagramChangeEvent(s);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.symbolAdded(e);
		}
	}
	
	protected void fireSymbolDeleted(GraphicalProxy s){
		DiagramChangeEvent e = new DiagramChangeEvent(s);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.symbolDeleted(e);
		}
	}
	protected void fireSymbolMoved(GraphicalProxy s){
		DiagramChangeEvent e = new DiagramChangeEvent(s);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.symbolMoved(e);
		}
	}
	
	protected void fireConnectorAdded(GraphicalProxy r){
		DiagramChangeEvent e = new DiagramChangeEvent(r);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.connectorAdded(e);
		}
	}
	protected void fireConnectorDeleted(GraphicalProxy r){
		DiagramChangeEvent e = new DiagramChangeEvent(r);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.connectorDeleted(e);
		}
	}
	protected void fireConnectorMoved(GraphicalProxy r){
		DiagramChangeEvent e = new DiagramChangeEvent(r);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.connectorMoved(e);
		}
	}

	protected void fireMajorDiagramChange(){
		DiagramChangeEvent e = new DiagramChangeEvent(this);
		for(Iterator<DiagramChangeListener> iter = diagramListeners.iterator(); iter.hasNext();){
			DiagramChangeListener listener = iter.next();
			listener.majorDiagramChange(e);
		}
	}

	public Color getBackgroundColour(){
		return backColour;
	}
	
	public void setBackgroundColour(Color colour){
		backColour = colour;
	}
	
	public abstract void draw(Graphics2D g, float zoom);

	/**
	 * Get the bounds for a zoom of 1.0
	 * @return bounds.
	 */
	public Rectangle2D.Float getBounds() {
		return getBounds(1.0f);
	}
	
	/**
	 * Get the bounds for a given zoom factor.
	 * @param zoom is the zoom factor to get bounds for.
	 * @return Rectangle with the bounds
	 */
	public abstract Rectangle2D.Float getBounds(float zoom);

	/**
	 * Removes the content of the diagram and sets any diagram attributes to their defaults. 
	 */
	public abstract void reset();

    /**
     * Sets the colours and any other properties of the diagram to their
     * default values (possibly determined by the diagram type).
     */
    public abstract void resetPropertiesToDefaults();

    /**
     * Signals that a repository item that would typically be attached
     * to a node or symbol (i.e. an entity or meta-entity) has been removed
     * from the model so that its corresponding graphical representation 
     * should be removed from the diagram as well.
     * @param object is the object attached to the node to remove.
     */
    public abstract void removeNodeForObject(KeyedItem object);

    /**
     * Signals that a repository item that would typically be attached
     * to an arc or connector (i.e. a relationship or meta-relationship) has been removed
     * from the model so that its corresponding graphical representation 
     * should be removed from the diagram as well.
     * @param object is the object attached to the arc to remove.
     */
    public abstract void removeArcForObject(KeyedItem object);

    /**
     * Takes 3 objects and sees if the arc corresponding to the connecting
     * object has nodes that correspond to the start and finish objects.  If
     * so, then fine, otherwise the connector on the diagram is now invalid
     * and must go.  Used to signal changes to relationships in the model or
     * meta-model so that diagrams can be updated.
     * @param connects is the object corresponding to an arc.
     * @param start is the object corresponding to the start node of the arc.
     * @param finish is the object corresponding to the finish node of the arc.
     */
    public abstract void validate(KeyedItem connects, KeyedItem start, KeyedItem finish);
    
//    /**
//     * Sets the background colour for symbol for a given object (if any).
//     * @param obj is the object who's symbol we want to set the colour for.
//     * @param colour is the colour to set.
//     */
//    public abstract void setSymbolColourFor(KeyedItem item, Color colour);
//
//	/** Adds a symbol associated with a user object which should be a "thing" type
//	 * rather than a relationship type.
//	 * @param item is the user Object that should be associated with a node
//	 * @throws LogicException
//	 * @throws NullPointerException
//	 */
//	public abstract GraphicalProxy addNodeForObject(KeyedItem item);
//    
//
//	
//	/**
//	 * Adds all possible connectors to this diagram.
//     */
//    public abstract void addConnectors();
//    
//	/**
//	 * Adds all connectors to this diagram that match one of the
//	 * MetaRelationships in the given set.
//	 * @param types is a Set of MetaRelationship that determine which
//	 * types of connector should be added.
//     */
//    public abstract void addConnectors(Set types);
//

	/**
	 * Write out the diagram as XML.
	 * @param out is the XMLWriter to output to.
	 * @throws IOException
	 */
	public abstract void writeXML(XMLWriter out) throws IOException;
	
    /**
     * Writes the diagram to an image file.
     * @param path is the path for the output file.
     * @param format should be png or jpg.
     * @throws IOException
     */
    public void export(File path, String format) throws IOException{
        final int BORDER = 10;
		Rectangle2D.Float bounds = getBounds();
		int width = (int)bounds.width;
		int height = (int)bounds.height;
		width += 2*BORDER;
		height += 2*BORDER;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(getBackgroundColour());
		graphics.fillRect(0,0,width,height);
		
		graphics.translate(BORDER,BORDER);
		draw(graphics,1.0f);
		ImageIO.write(image, format, path);

    }
	
	/**
	 * Writes XML standard to all Diagrams.
	 * @param out is the XMLWriter to write to.
	 * @throws IOException
	 */
	public void startXML(XMLWriter out) throws IOException {
		out.startEntity("Diagram");
		out.addAttribute("name",getName());
		out.addAttribute("uuid",getKey().toString());
		out.addAttribute("ofType",getType().getKey().toString());
		
		eventMap.writeXML(out);
	}

	/**
	 * Called if event mapping scripts have been updated. Children of this may care to
	 * override this and provide custom behaviour such as saving the mapping.
	 * @param scripts
	 */
	public void scriptsUpdated() {
	}

}
