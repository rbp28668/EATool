/*
 * NullConnectorEnd.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Graphics2D;

import alvahouse.eatool.gui.graphical.standard.UnitVector;

/**
 * NullConnectorEnd is a ConnectorEnd that doesn't 
 * do any drawing.
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

}
