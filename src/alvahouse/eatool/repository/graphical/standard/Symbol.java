/*
 * Symbol.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Shape;
import java.io.IOException;

import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Symbol is a graphical representation of an object for use in diagrams.
 * @author Bruce.Porteous
 *
 */
public interface Symbol extends Node, GraphicalObject, GraphicalProxy, TextObjectSettings, KeyedItem {

	/**
	 * This gets the outline shape of the symbol.  Needed by connectors to know
	 * where to draw their ends.
	 * @return a Shape containing the outline shape of the symbol.
	 */
	public Shape getOutlineShape();
		
	/**
	 * Gets the type of this symbol.
	 * @return the corresponding SymbolType.
	 */
	public SymbolType getType();
	
	/**
	 * Sets the type of this symbol.
	 * @param type is the type to set.
	 */
	public void setType(SymbolType type);
	
	/**
	 * Sets the size of the symbol.  Symbols are free to interpret the width/height
	 * to maintain aspect ratio.
	 * @param width is the new width.
	 * @param height is the new height.
	 */
	public void setSize(float width, float height);
	
	/**
	 * Write the symbol as XML.
	 * @param out is the XMLWriter to write to.
	 * @throws IOException
	 */
	public void writeXML(XMLWriter out) throws IOException;

    /**
     * Gets the unique key for this symbol.
     * @return the UUID that identifies this symbol.
     */
    public UUID getKey();

    /**
     * Determines whether a symbol must be automatically resized.  Typically
     * true when a new symbol is added to a diagram, false if read in 
     * from file.
     * @param b
     */
    public void mustSizeSymbol(boolean b);
    
 	
}
