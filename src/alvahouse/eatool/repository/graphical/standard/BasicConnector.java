/*
 * BasicConnector.java
 * Created on 20-Jan-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.util.UUID;

/**
 * BasicConnector provides a basic connector class for drawing diagrams.
 * 
 * @author Bruce.Porteous
 *
 */
public class BasicConnector extends AbstractConnector {

	
	/**
	 * Default constructor to allow connector to be created 
	 * dynamically from XML.
	 */
	public BasicConnector(){
		super(new UUID(),null,null);
	}
	
	/**
	 * Constructor for when a user is creating a connector.
	 * @param key is the ID of the connector.
	 * @param userObject is the object this connector represents.
	 * @param type is the ConnectorType that this connector is an instance of.
	 */
	public BasicConnector(UUID key, KeyedItem item, ConnectorType type) {
		super(key, item,type);
	}
	

	/**
	 * Protected constructor that allows base classes to define the ConnectorEnds.
	 * @param key is the ID of the connector.
	 * @param userObject is the object this connector represents.
	 * @param start is the ConnectorEnd to use at the start of the connector.
	 * @param finish is the ConnectorEnd to use at the finish of the connector.
	 * @param type is the ConnectorType that this connector is an instance of.
	 */
	protected BasicConnector(
		UUID key,	
		KeyedItem item,
		ConnectorEnd start,
		ConnectorEnd finish,
		ConnectorType type) {
		super(key, item, start, finish, type);
		
	}

	public Object clone() {
		BasicConnector copy = new BasicConnector(getKey(), getItem(), getType());
		cloneTo(copy);
		return copy;
	}

}
