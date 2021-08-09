package alvahouse.eatool.repository.graphical.standard;

import java.awt.geom.Point2D;
import java.io.IOException;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.dto.graphical.ConnectorDto;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;


/**
 * A connector joins 2 symbols on a diagram.  The types of connector end are specific to the type
 * of connector - the sub-class constructors should create the appropriate connector ends.
 * The end positions of a connector are fixed by the Symbols it attaches to.  The path between the
 * 2 symbols may vary and this path must be completely defined by draggable handles. Exactly how
 * this path is defined depends on which ConnectorStrategy the connector is using to draw itself.
 * @author bruce.porteous
 *
 */
public interface Connector extends Arc, GraphicalObject, GraphicalProxy, KeyedItem{

	/**
	 * Signals to the connector that one of its ends has been moved.
	 * @param s is the symbol that's been moved.
	 */
	public abstract void endMoved(Symbol s);
	
	/**
	 * Gets a count of the number of handles used to manipulate the connector.
	 * @return a count of handles.
	 */
	public int getHandleCount();
	
	/**
	 * Gets the position of a given handle.
	 * @param idx is the index of the handle.
	 * @return a Point2D.Float with the position of the handle.
	 */
	public Point2D.Float getHandlePosition(int idx);
	
	/**
	 * Moves a given handle to a new position.
	 * @param idx is the index of the handle to move.
	 * @param x is the new x-coordinate of the handle.
	 * @param y is the new y-coordinate of the handle.
	 */
	public void moveHandle(int idx, float x, float y);
	
	/**
	 * Writes out the Connector as XML.
	 * @param out is the XMLWriter to write to.
	 * @throws IOException if something bad happens.
	 */
	public void writeXML(XMLWriter out) throws IOException;
	
	/**
	 * Gets the ConnectorType associated with this Connector.
	 * @return the associated ConnectorType.
	 */
	public ConnectorType getType();
	
	/**
	 * Sets the ConnectorType associated with this Connector.
	 * @param type is the ConnectorType to set.
	 */
	public void setType(ConnectorType type);
    
	/**
	 * Set the ConnectorStrategy that will be used to draw the connector.
	 * Note that the connector must be connected to symbols before calling this
	 * as the connector strategy needs to have its ends set.
     * @param connectorStrategy is the new strategy.
     */
    public abstract void setStrategy(ConnectorStrategy connectorStrategy);


    /**
     * Get the key that uniquely identifies this connector.
     * @return
     */
    public abstract UUID getKey();
	
    
    public Object clone();

	/**
	 * @return
	 */
	public abstract ConnectorDto toDto();
}
