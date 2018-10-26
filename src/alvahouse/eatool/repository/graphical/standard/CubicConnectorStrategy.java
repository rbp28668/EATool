/*
 * CubicConnector.java
 * Created on 17-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;



/**
 * CubicConnectorStrategy draws a connector using a cubic.
 * @author bruce.porteous
 *
 */
public class CubicConnectorStrategy extends ConnectorStrategy {
	private Point2D.Float[] handles = new Point2D.Float[2];
	private CubicCurve2D.Float curve = new CubicCurve2D.Float();
	private boolean hasStart = false;
	private boolean hasFinish = false;
	
	
	
	/**
	 */
	public CubicConnectorStrategy() {
		handles[0] = new Point2D.Float();
		handles[1] = new Point2D.Float();
	}

	protected void endMoved() {
		if(!(hasStart && hasFinish)){
			resetLayout();
		}
	}

	public void resetLayout(){
		float dx = x2 - x1;
		float dy = y2 - y1;
		
		handles[0].x = x1 + dx / 3;
		handles[0].y = y1 + dy / 3;
		handles[1].x = x1 + 2 * dx / 3;
		handles[1].y = y1 + 2 * dy / 3;
		
	}
	private void moveEnd(float x, float y, boolean isFirst){
		float mx = (x1 + x2)/2;
		float my = (y1 + y2)/2;
	
		float[] deltasX = new float[handles.length];
		float[] deltasY = new float[handles.length];
	
		for(int i=0; i<handles.length; ++i){
			deltasX[i] = handles[i].x - mx;
			deltasY[i] = handles[i].y - my;
		}
	
		if(isFirst){
			x1 = x;
			y1 = y;
		} else {
			x2 = x;
			y2 = y;
		}
				
		mx = (x1 + x2)/2;
		my = (y1 + y2)/2;
	
		for(int i=0; i<handles.length; ++i){
			handles[i].x = mx + deltasX[i];
			handles[i].y = my + deltasY[i];
		}
	}
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.ConnectorStrategy#setStart(float, float)
	 */
	public void setStart(float x, float y) {
		if(hasStart && hasFinish){
			moveEnd(x,y,true);
		} else {
			x1 = x;
			y1 = y;
			endMoved();
		}
		hasStart = true;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.ConnectorStrategy#setFinish(float, float)
	 */
	public void setFinish(float x, float y) {
		if(hasStart && hasFinish){
			moveEnd(x,y,false);
		} else{
			x2 = x;
			y2 = y;
			endMoved();
		}
		hasFinish = true;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandle(int)
	 */
	public Point2D.Float getHandle(int idx) {
		return handles[idx];
	}

	public void moveHandle(int idx, float x, float y) {
		assert(idx >=0 && idx <handles.length);
		handles[idx].x = x;
		handles[idx].y = y;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandleCount()
	 */
	public int getHandleCount() {
		return handles.length;
	}

	public Shape getShape( float zoom) {
		curve.setCurve(
		x1 * zoom,
		y1 * zoom,
		handles[0].x * zoom,
		handles[0].y * zoom,
		handles[1].x * zoom,
		handles[1].y * zoom,
		x2 * zoom,
		y2 * zoom
		);
		return curve;
	}
	



}
