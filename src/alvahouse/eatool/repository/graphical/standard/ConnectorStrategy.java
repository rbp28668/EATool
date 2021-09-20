/*
 * ConnectorStrategy.java
 * Created on 20-Jan-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.util.XMLWriter;

/**
 * ConnectorStrategy is a base class for strategies for drawing
 * connectors.  This allows connectors to vary their drawing 
 * strategy at runtime. (see Strategy pattern in GOF).
 * @author Bruce.Porteous
 *
 */
public abstract class ConnectorStrategy {

	/** first endpoint */
	protected float x1,y1;
	/** second endpoint */
	protected float x2,y2;
	/** current draw colour */
	private Color colour = Color.black;

	/**
	 * Gets one of the handles position that define where this strategy draws the connector.
	 * This needs to be over-ridden for any strategy more complex than a straight line.
	 * @param idx is the index of the handle to get.
	 * @return 2D point with the handle's position.  null for this default implementation.
	 */
	public Point2D.Float getHandle(int idx) {return null;}
	
	
	/**
	 * This moves one of the handles that define where this strategy draws the connector.
	 * This needs to be over-ridden for any strategy more complex than a straight line.
	 * @param idx is the index of the handle to get.
	 * @param x is the new x coordinate of the handle.
	 * @param y is the new y coordinate of the handle.
	 */
	public void moveHandle(int idx, float x, float y) {}

    /**
     * Set handle creates a new handle with the given index.  Used for de-serialising - 
     * an implementation may need to extend its store of handles.  CompareByName to moveHandle that
     * always moves an existing handle without the need to create others.  The default implementation
     * just calls moveHandle(int,float,float).
     * @param idx is the index of the handle.
     * @param x is the x coordinate of the handle.
     * @param y is the y coordinate of the handle.
     */
    public void setHandle(int idx, float x, float y) {
        moveHandle(idx,x,y);
    }

	/**
	 * Gets the count of the number of handles currently used to define where this strategy 
	 * draws the connector.
	 * @return handle count.
	 */
	public int getHandleCount() {return 0;}
	
	/**
	 * Callback to signal to the connector that one of it's ends (attached to a symbol) has been
	 * moved.  Derived classes may over-ride this - it's a good time to recalculate handle 
	 * positions / control points or whatnot.
	 */
	protected void endMoved() {}
	
	/**
	 * resets the layout between the 2 ends.
	 */
	public void resetLayout(){
        int handleCount = getHandleCount();
        for(int i=0; i<handleCount; ++i){
            float x = (x2 - x1) / (handleCount + 1);
            float y = (y2 - y1) / (handleCount + 1);
            setHandle(i,x,y);
        }
	}
	
	/**
	 * Gets the shape used to draw this connector.
	 * @param zoom is the current zoom factor.
	 * @return the drawing shape.
	 */
	protected Shape getShape(float zoom){ 
		throw new IllegalStateException("ConnectorStrategy.getCurve default implementation called");
	}
	
	
	/**
	 * Draws the connector
	 * @param g is the drawing context to use.
	 * @param zoom is the current scale factor to draw with.
	 * @param selected is a flag to show whether the the shape should
	 * be highlighted to show selection.
	 */
	public void draw(Graphics2D g, float zoom, boolean selected){
		Shape curve = getShape(zoom);
		g.setColor(colour);
		Stroke oldStroke = null;
		if(selected){
			Stroke s = new BasicStroke(3); // wider
			oldStroke = g.getStroke();
			g.setStroke(s);
		}
		g.draw(curve);
		if(oldStroke != null){
			g.setStroke(oldStroke);
		}
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.ConnectorStrategy#setStart(float, float)
	 */
	public void setStart(float x, float y) {
		x1 = x;
		y1 = y;
		endMoved();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.ConnectorStrategy#setFinish(float, float)
	 */
	public void setFinish(float x, float y) {
		x2 = x;
		y2 = y;
		endMoved();
	}

	/**
	 * Gets the current draw colour.
	 * @return the current colour.
	 */
	public Color getColour(){
		return colour;
	}

    /**
     * Sets the current draw colour.
     * @param colour is the new colour.
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

	/**
	 * Gets the basic bounding rectangle of the connector.  This is without
	 * any handles or notation that might extend the bounding box.
	 * @return a rectangular bounding box.
	 */
	public Rectangle2D.Float getBounds(float zoom){
		Rectangle2D bounds = getShape(zoom).getBounds2D();
		return new Rectangle2D.Float(
		(float)bounds.getX(),
		(float)bounds.getY(),
		(float)bounds.getWidth(),
		(float)bounds.getHeight());
	}
	
	/**
	 * Determines whether the given point (in screen coordinates) lies on the
	 * connector.
	 * @param mx is the x mouse coordinate to test.
	 * @param my is the y mouse coordinate to test.
	 * @param zoom is the current zoom factor
	 * @return true if the mouse lies on the curve, false otherwise.
	 */
	public boolean hitTest(int mx, int my, float zoom){
		Shape curve = getShape(zoom);
		Stroke stroke = new BasicStroke(5); // solid, 5 px wide
		curve = stroke.createStrokedShape(curve);
		return curve.contains(mx,my);		
	}
	
	/**
	 * Writes the ConnectorStrategy out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
	    out.startEntity("ConnectorStrategy");
	    out.addAttribute("class",this.getClass().getCanonicalName());
	    FactoryBase.writeColour(out,"ConnectorColour",colour);
	    int handleCount = getHandleCount();
	    for(int i=0; i<handleCount; ++i){
	        Point2D.Float handle = getHandle(i);
	        out.startEntity("Handle");
	        	out.addAttribute("index",i);
	        	out.addAttribute("x",Float.toString(handle.x));
	        	out.addAttribute("y",Float.toString(handle.y));
	        out.stopEntity();
	    }
	    out.stopEntity();
	}


}
