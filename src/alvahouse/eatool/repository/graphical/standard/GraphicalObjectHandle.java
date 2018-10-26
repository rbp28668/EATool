/*
 * GraphicalObjectHandle.java
 * Created on 11-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.Handle;


class GraphicalObjectHandle extends Handle{
	public static final int TOP_LEFT = 0;
	public static final int TOP_CENTER = 1;		
	public static final int TOP_RIGHT = 2;
	public static final int CENTER_LEFT = 3;
	public static final int CENTER_RIGHT = 4;		
	public static final int BOTTOM_LEFT = 5;
	public static final int BOTTOM_CENTER = 6;		
	public static final int BOTTOM_RIGHT = 7;


	private int handleType = 0;
	private GraphicalObject parent;

	protected Point2D.Float getPosition(float zoom) {
		Rectangle2D.Float bounds = parent.getBounds(zoom);
		float x0 = bounds.x;
		float x1 = bounds.x + bounds.width;
		float y0 = bounds.y;
		float y1 = bounds.y + bounds.height;

		float px = 0;
		float py = 0;
				
		switch(handleType){
			case GraphicalObjectHandle.TOP_LEFT: 		px = x0; py = y0; break;
			case GraphicalObjectHandle.TOP_CENTER:	px = (x0 + x1)/2; py = y0; break;
			case GraphicalObjectHandle.TOP_RIGHT:		px = x1; py = y0; break;
			case GraphicalObjectHandle.CENTER_LEFT:	px = x0; py = (y0 + y1)/2; break;
			case GraphicalObjectHandle.CENTER_RIGHT:	px = x1; py = (y0 + y1)/2; break;
			case GraphicalObjectHandle.BOTTOM_LEFT:	px = x0; py = y1; break;
			case GraphicalObjectHandle.BOTTOM_CENTER:	px = (x0 + x1)/2; py = y1; break;
			case GraphicalObjectHandle.BOTTOM_RIGHT:	px = x1; py = y1; break;
		}
	
		px -= Handle.HANDLE_SIZE/2;
		py -= Handle.HANDLE_SIZE/2;
	
		return new Point2D.Float(px,py);
	}
	
	public GraphicalObjectHandle(GraphicalObject parent, int handleType){
		super();
		this.parent  = parent;
		this.handleType = handleType;
	}
	
	public void dragTo(int mx,int my, float scale){
	
		float x = mx / scale;
		float y = my / scale;
		Rectangle2D.Float bounds = parent.getBounds(1.0f);
		float x0 = bounds.x;
		float x1 = bounds.x + bounds.width;
		float y0 = bounds.y;
		float y1 = bounds.y + bounds.height;

		float oldX0 = x0;
		float oldX1 = x1;
		float oldY0 = y0;
		float oldY1 = y1;
	
		switch(handleType){
			case GraphicalObjectHandle.TOP_LEFT:
				if(x <= x1) x0 = x; 
				if(y <= y1) y0 = y; 
				break;
			case GraphicalObjectHandle.TOP_CENTER:
				if(y <= y1) y0 = y;
				break;		
			case GraphicalObjectHandle.TOP_RIGHT:
				if(x >= x0) x1 = x;
				if(y <= y1) y0 = y;
				break;
			case GraphicalObjectHandle.CENTER_LEFT:
				if(x <= x1) x0 = x;
				break;
			case GraphicalObjectHandle.CENTER_RIGHT:
				if(x >= x0) x1 = x;
				break;	
			case GraphicalObjectHandle.BOTTOM_LEFT:
				if(x <= x1) x0 = x;
				if(y >= y0) y1 = y;
				break;
			case GraphicalObjectHandle.BOTTOM_CENTER:		
				if(y >= y0) y1 = y;
				break;
			case GraphicalObjectHandle.BOTTOM_RIGHT:
				if(x >= x0) x1 = x;
				if(y >= y0) y1 = y;
				break;
		}
	
		parent.setPosition((x0 + x1)/2,(y0 + y1)/2);
		parent.setSize(x1-x0, y1-y0);
	}
	
	

}