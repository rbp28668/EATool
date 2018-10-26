/*
 * ConnectorArrow.java
 * Created on 07-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.util.UUID;

/**
 * ConnectorArrow is a connector with an arrow head on the finish.
 * @author Bruce.Porteous
 *
 */
public class ConnectorArrow extends BasicConnector {


    /**
     * Default constructor to allow reconstruction from XML. 
     */
    public ConnectorArrow(){
        this(new UUID(),null,null);
    }
    
	/**
	 * @param userObject
	 * @param start
	 * @param finish
	 */
	public ConnectorArrow(
		UUID key,
		KeyedItem item,
		ConnectorType type) {
		super(key,item, ConnectorEnd.getNullObject(), new ConnectorEndArrowHead(),type);
	}

}
