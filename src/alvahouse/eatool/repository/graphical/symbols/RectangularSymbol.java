/*
 * RectangularSymbol.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.symbols;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.standard.AbstractSymbol;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.util.UUID;


/**
 * RectangularSymbol is a simple rectangular symbol for use on diagrams.
 * @author bruce.porteous
 *
 */
public class RectangularSymbol extends AbstractSymbol {

	/**
	 * Default constructor to allow runtime instantiation.
	 */
	public RectangularSymbol(){
		super(new UUID(),null,null);
	}

	public RectangularSymbol(UUID key,KeyedItem item, SymbolType type) {
		super(key, item, type);
	}
	
}
