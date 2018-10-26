/*
 * QuadraticConnector.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;



/**
 * QuadraticConnectorStrategy is a connector drawing strategy that
 * uses a quadratic to draw the connector.
 * @author bruce.porteous
 *
 */
public class QuadraticConnectorStrategy extends ConnectorStrategy {

	private Point2D.Float handle;
	private QuadCurve2D.Float curve = new QuadCurve2D.Float();
	/**
	 * @param userObject
	 */
	public QuadraticConnectorStrategy() {
	}

	protected void endMoved() {
		float dx = x2 - x1;
		float dy = y2 - y1;
		
		handle.x = x1 + dx / 2;
		handle.y = y1 + dy / 2;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandle(int)
	 */
	public Point2D.Float getHandle(int idx) {
		if(idx != 0) {
			throw new IllegalArgumentException("handle index out of range");
		}
		return handle;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandleCount()
	 */
	public int getHandleCount() {
		return 1;
	}

	public Shape getShape( float zoom) {
		curve.setCurve(
		x1 * zoom,
		y1 * zoom,
		handle.x * zoom,
		handle.y * zoom,
		x2 * zoom,
		y2 * zoom
		);
		return curve;
	}



}
