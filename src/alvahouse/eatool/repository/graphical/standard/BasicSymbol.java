package alvahouse.eatool.repository.graphical.standard;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.util.UUID;


/**
 * BasicSymbol is the graphical representation of an entity.
 * @author bruce.porteous
 *
 */
public class BasicSymbol extends AbstractSymbol {

	public BasicSymbol(UUID key,KeyedItem item, SymbolType type) {
		super(key,item,type);
	}

}
