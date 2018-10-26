/*
 * StraightConnector.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Shape;
import java.awt.geom.Line2D;


/**
 * StraightConnectorStrategy is a connector strategy that draws the
 * connector using a straight line.
 * @author bruce.porteous
 *
 */
public class StraightConnectorStrategy extends ConnectorStrategy {
	private Line2D.Float curve = new Line2D.Float();
	/**
	 * @param userObject
	 */
	public StraightConnectorStrategy() {
	}

	protected Shape getShape( float zoom) {
		curve.x1 = x1 * zoom;
		curve.y1 = y1 * zoom;
		curve.x2 = x2 * zoom;
		curve.y2 = y2 * zoom;
		return curve;
	}


}
