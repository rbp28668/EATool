/*
 * NullConnectorEnd.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Graphics2D;

import alvahouse.eatool.gui.graphical.standard.UnitVector;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndNullDto;

/**
 * NullConnectorEnd is a ConnectorEnd that doesn't do any drawing.
 * NullConnectorEnd implements the null object pattern to make
 * it easier for connectors to use the null end rather than
 * have an explicit null - i.e. there's always a connector end
 * even if it doesn't do anything.
 * @author bruce.porteous
 *
 */
public class NullConnectorEnd extends ConnectorEnd {

	/**
	 * 
	 */
	public NullConnectorEnd() {
		super();
	}
	public void draw(Graphics2D g, UnitVector pos, float zoom){
		// NOP.
	}
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.standard.ConnectorEnd#toDto()
	 */
	@Override
	public ConnectorEndDto toDto() {
		return new ConnectorEndNullDto();
	}

}
