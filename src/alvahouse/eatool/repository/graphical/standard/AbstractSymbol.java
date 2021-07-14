/*
 * AbstractSymbol.java
 *
 * Created on 12 February 2002, 16:40
 */

package alvahouse.eatool.repository.graphical.standard;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.dto.graphical.SymbolDto;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;


/**
 * AbstractSymbol is the basic element of a graph.  Nodes are
 * joined by Arcs.
 * @author  rbp28668
 */
public class AbstractSymbol extends TextualObject implements Symbol  {

	private int index;
	private KeyedItem item;
	private LinkedList<Connector> connectors = new LinkedList<>();
	private SymbolType type;


    /** Creates new AbstractSymbol */
    public AbstractSymbol(UUID key,KeyedItem item, SymbolType type) {
    	super(key);
        this.item = item;
        this.type = type;
		
	}
    
    /**
	 * @param rsd
	 */
	public AbstractSymbol(KeyedItem item, SymbolType type, SymbolDto dto) {
		super(dto);
        this.item = item;
        this.type = type;
	}

	public String getText(){
		String text = getItem().toString();
		return text;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#setItem(java.lang.Object)
     */
    @Override
    public void setItem(KeyedItem item) {
    	this.item = item;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#getItem()
     */
    @Override
    public KeyedItem getItem() {
        return item;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#addArc(alvahouse.eatool.gui.graphical.Arc)
     */
    @Override
    public void addArc(Arc a) {
    	if(!(a instanceof Connector)) {
    		throw new IllegalArgumentException("Arc must be a connector");
    	}
        connectors.addLast((Connector) a );
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#deleteArc(alvahouse.eatool.gui.graphical.Arc)
     */
    @Override
    public void deleteArc(Arc a) {
        connectors.remove(a);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#setPosition(float, float)
     */
    @Override
    public void setPosition(float px, float py) {
        super.setPosition(px,py);
		updateConnectorPositions();
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#getX()
     */
    @Override
    public float getX() { 
        return super.getX();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#getY()
     */
    @Override
    public float getY() {
        return super.getY();
    }
    

    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#getArcs()
     */
    @Override
    public Collection<? extends Arc> getArcs() {
        return Collections.unmodifiableCollection(connectors);
    }
    
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Node#arcCount()
     */
    @Override
    public int arcCount() {
        return connectors.size();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Symbol#getType()
     */
    @Override
    public SymbolType getType(){
        return type;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Symbol#setType(alvahouse.eatool.gui.graphical.SymbolType)
     */
    @Override
    public void setType(SymbolType type){
        this.type = type;
    }
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.standard.Symbol#getConnectors()
	 */
	@Override
	public Collection<Connector> getConnectors() {
		return Collections.unmodifiableCollection(connectors);
	}

	
	/**
	 * Updates the end positions of all connected connectors.  This
	 * should be called whenever the symbol is moved.
	 */
	private void updateConnectorPositions(){
		for(Connector c : connectors){
			c.endMoved(this);
		}
	}



	/**
	 * Gets the unit width for sizing shapes.  This is defined as
	 * the width of the letter m in the current font.
	 * @param g is the graphics context to use for sizing.
	 * @return the unit size.
	 */
	protected int getUnitWidth(Graphics2D g){
		FontMetrics fontMetrics = g.getFontMetrics(getFont());
		return fontMetrics.charWidth('m');
	}

	/**
	 * Gets the unit height for sizing shapes.  This is defined as
	 * the height of the current font.
	 * @param g is the graphics context to use for sizing.
	 * @return the unit height.
	 */
	protected int getUnitHeight(Graphics2D g){
		FontMetrics fontMetrics = g.getFontMetrics(getFont());
		return fontMetrics.getHeight();
	}

	/**
	 * Indicator whether the item to display has sensible text to display.
	 * Assume there is text (i.e. toString is useful), but if there is an
	 * Entity without any display hint, then don't display text.
	 * @return true if should display text.
	 */
	protected boolean hasText() throws Exception {
	    boolean hasText = true;
	    if(item instanceof Entity){
	        hasText = ((Entity)item).getMeta().getDisplayHint() != null;
	    }
	    return hasText;
	}
	
	/**
	 * Gets the outline of the symbol.
	 * @return Shape containing the outline symbol.
	 */
	public Shape getOutlineShape() {
		return new Rectangle2D.Float(
		(getX() - getWidth()/2), 
		(getY() - getHeight()/2), 
		getWidth(), 
		getHeight() 
		);
		
	}
	
	
	/**
	 * Writes the BasicSymbol out as XML
	 * @param out is the XMLWriter to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("Symbol");
		writeTextObjectAttributesXML(out);

 		out.addAttribute("type",getType().getKey().toString());
		KeyedItem represents = (KeyedItem)getItem();
		out.addAttribute("represents",represents.getKey().toString());
		
		writeTextObjectSettingsXML(out);
 		writeExtraData(out);
		out.stopEntity();
	}

	/**
	 * Allows a derived class to write any extra data needed.  Most symbols
	 * won't need this but if the shape is complex or contains some complex
	 * data then over-ride this method.  Note that the data should be written
	 * in an entity named Detail. 
     * @param out is the writer to write to.
     */
    protected void writeExtraData(XMLWriter out) throws IOException {
        // NOP for most symbol types, over-ride as necessary to write
        // out a Detail entity.
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.standard.Symbol#mustSizeSymbol(boolean)
     */
    @Override
    public void mustSizeSymbol(boolean b) {
        super.mustReSize(b);
    }


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.layout.Node#getIndex()
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.layout.Node#setIndex(int)
	 */
	@Override
	public int setIndex(int index) {
		this.index = index;
		return index;
	}

	public Object clone() {
		AbstractSymbol copy = new AbstractSymbol(getKey(), getItem(), getType());
		cloneTo(copy);
		return copy;
	}
	
	protected void cloneTo(AbstractSymbol copy) {
		super.cloneTo(copy);
		copy.index = index;
		copy.connectors.addAll(connectors);
	}


}
