package alvahouse.eatool.repository.graphical.standard;

import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;

import alvahouse.eatool.gui.graphical.standard.UnitVector;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * A ConnectorEnd decorates the end of a connector where
 * it joins a symbol.  Used for things such as cardinality
 * and optinality display.
 * @author bruce.porteous
 *
 */
public abstract class ConnectorEnd {

	/** Font used for basic unit of size so that independent of device
	 * resolution, things scale properly.
	 */
	protected static Font scaleFont = new Font("SansSerif", Font.PLAIN,10);

	/**
	 * NullConnectorEnd implements the null object pattern to make
	 * it easier for connectors to use the null end rather than
	 * have an explicit null.
	 * @author Bruce.Porteous
	 *
	 */
	private static class NullConnectorEnd extends ConnectorEnd{
		NullConnectorEnd(){
		}
		
		public void draw(Graphics2D g, UnitVector pos, float zoom){
			// NOP.
		}

	}
	
	private static ConnectorEnd nullObject = new NullConnectorEnd();
	
	public static ConnectorEnd getNullObject() {
		return nullObject;
	}
	
	private UUID uuid;
	/**
	 * Constructor for ConnectorEnd.
	 */
	public ConnectorEnd() {
		super();
		uuid = new UUID();
	}

	/**
	 * Gets the unique key for this connector end.
	 * @return UUID representing this connector end.
	 */
	public UUID getKey() {
		return uuid;
	}
	
	/**
	 * Implementation of draw that draws the decoration of the connector end.  The derived class must
	 * over-ride getBaseShape()
	 * @param g
	 * @param pos
	 * @param zoom
	 */
	public abstract void draw(Graphics2D g, UnitVector pos, float zoom);
	
	/**
	 * Writes the ConnectorEnd out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("ConnectorEnd");
		out.addAttribute("uuid",getKey().toString());
		
		out.stopEntity();
	}

}
