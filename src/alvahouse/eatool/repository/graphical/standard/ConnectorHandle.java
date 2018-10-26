/*
 * ConnectorHandle.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.geom.Point2D;

import alvahouse.eatool.repository.graphical.Handle;


class ConnectorHandle extends Handle{
	private Connector connector;
	private int handleIndex;

	public ConnectorHandle(Connector c, int idx){
		assert(c != null);
		assert(idx >= 0);
		assert(idx < c.getHandleCount());
		
		connector = c;
		handleIndex = idx;
	}
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Handle#getPosition()
	 */
	protected Point2D.Float getPosition(float zoom) {
		Point2D.Float p = connector.getHandlePosition(handleIndex);
		float s2 = Handle.HANDLE_SIZE/2;
		return new Point2D.Float(p.x * zoom - s2, p.y * zoom - s2);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Handle#dragTo(int, int, float)
	 */
	public void dragTo(int mx, int my, float scale) {
		float x = mx / scale;
		float y = my / scale;
		connector.moveHandle(handleIndex, x,y);
	}
}