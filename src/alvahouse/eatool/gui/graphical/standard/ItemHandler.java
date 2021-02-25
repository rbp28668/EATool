/*
 * ItemHandler.java
 * Project: EATool
 * Created on 02-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.Component;

import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.Symbol;

/**
 * ItemHandler is an interface for handling the editing of items that are
 * attached to symbols or connectors in a diagram.  Exactly what will be edited
 * will depend on the diagram type, typically Entities/Relationships for models
 * MetaEntity/MetaRelationship for meta-models.
 * 
 * @author rbp28668
 */
public interface ItemHandler {

    /**
     * Edits an item attached to a symbol.
     * @param parent is the parent component for any dialog.
     * @param item is the item to edit.
     * @return true if edited, false if not.
     * @throws Exception 
     */
    public boolean editSymbolItem(Component parent, Object item) throws Exception;
    
    /**
     * Potentially creates a new symbol at the given location.
     * @param parent is the parent Component for any user dialog.  
     * @param x is the x-coordinate where the symbol is to be added.
     * @param y is the y-coordinate where the symbol is to be added.
     * @return an array of new Symbols or null if the user stops the operation.
     */
    public Symbol[] addSymbolsAt(Component parent, int x, int y) throws LogicException;

    /**
     * Potentially creates a new symbol at the given location attached
     * to a new item in the underlying model/metamodel.
     * @param parent is the parent Component for any user dialog.  
     * @param x is the x-coordinate where the symbol is to be added.
     * @param y is the y-coordinate where the symbol is to be added.
     * @return an array of new Symbols or null if the user stops the operation.
     */
    public Symbol addSymbolNewItem(Component parent, int x, int y) throws LogicException;

	/**
	 * Attempts to join 2 symbols on the diagram.
     * @param parent is the parent Component for any user dialog.  
	 * @param first is the first Symbol to be connected.
	 * @param second is the second Symbol to be connected.
	 * @return a new Connector or null if none is created.
	 * @throws LogicException - in the event of an error.
	 */
	public Connector addConnector(Component parent, Symbol first, Symbol second) 
	throws LogicException;

	public PositionalPopup getPopupFor(StandardDiagramViewer viewer, Class<?> targetClass);
	
	public PositionalPopup getBackgroundPopup(StandardDiagramViewer viewer);
}
