/*
 * AbstractConnector.java
 *
 * Created on 12 February 2002, 16:45
 */

package alvahouse.eatool.repository.graphical.standard;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.gui.graphical.standard.UnitVector;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.dto.graphical.ConnectorDto;
import alvahouse.eatool.repository.graphical.Handle;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * An arc links 2 nodes in a graphical model.  This abstraction allows arbitrary
 * graphs of "thing - relationship - thing" to be manipulated as generic nodes
 * and arcs.  The arc also contains a link to the object (some kind of
 * relationship) that the arc models.  Note that the type of connector ends depends
 * entirely on the class of connector - it's up to sub-classes to set the connector
 * ends in their constructors as needed.
 * @author  rbp28668
 */
public abstract class AbstractConnector extends RepositoryItem implements Connector{

	private KeyedItem item;
	private ConnectorType type;
	private float length = 0;
	private Symbol start = null;
	private Symbol finish = null;
	protected ConnectorEnd startEnd = ConnectorEnd.getNullObject();
	protected ConnectorEnd finishEnd = ConnectorEnd.getNullObject();

	/** The drawing strategy currently used to draw this connector */
	private ConnectorStrategy drawingStrategy;
	/** flag to show whether this connector has been selected */
	private boolean selected = false;
	/** Array of handles for manipulating this connector */
	private Handle[] handles = null;
	/** Handle currently being dragged, null if no drag in progress */
	private Handle dragHandle = null;

    /** Creates new AbstractConnector 
     * @param userObject is the relationship object associated with this arc
     */
    public AbstractConnector(UUID key,KeyedItem item, ConnectorType type) {
    	super(key);
        this.item = item;
        this.type = type;
		drawingStrategy = new CubicConnectorStrategy();
    }
    
	/**
	 * Constructor for Connector.
	 */
	public AbstractConnector(UUID key, KeyedItem item, ConnectorEnd start, ConnectorEnd finish, ConnectorType type) {
		super(key);
		this.item = item;
		this.startEnd = start;
		this.finishEnd = finish;
		this.type = type;
		drawingStrategy = new CubicConnectorStrategy();
	}
	
	/**
	 * @param dto
	 */
	public AbstractConnector(KeyedItem item, ConnectorType type, Symbol start, Symbol finish, ConnectorDto dto) {
		super(dto);
		this.type = type;
		this.item = item;
		this.startEnd = ConnectorEndFactory.fromDto(dto.getStartEnd());
		this.finishEnd = ConnectorEndFactory.fromDto(dto.getFinishEnd());
		this.start = start;
		this.finish = finish;
		this.drawingStrategy = ConnectorStrategyFactory.fromDto(dto.getDrawingStrategy());
	}

	public void initialise(KeyedItem item, ConnectorEnd start, ConnectorEnd finish) {
		setItem(item);
		this.startEnd = start;
		this.finishEnd = finish;
	}

    /** Gets the user object associated with this arc.
     * @return the associated user object
     */
    public KeyedItem getItem() {
        return item;
    }
    
    /**
     * This sets the relationship object that this arc represents.
	 * @param o is the object to tie to this arc.
	 */
	public void setItem(KeyedItem item){
    	this.item = item;
    }
    
    /** This sets the 2 end nodes for an arc.
     * @param start is the start node of the arc.
     * @param finish is the end node of the arc.
     */
    public void setEnds(Node start, Node finish) {
        this.start = (Symbol)start;
        this.finish = (Symbol)finish;
		endMoved(this.start);
		endMoved(this.finish);
   
    }

    /** This calculates the length of the arc assuming the
     * arc is a straight line.
     * @return the arc length.
     */
    public float getLength() {
        float dx = finish.getX() - start.getX();
        float dy = finish.getY() - start.getY();
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
    
    /** gets the start node of the arc
     * @return the arc's start node.
     */
    public Node getStartEnd() {
        return start;
    }
    
    /** gets the finish node of the arc
     * @return the arc's finish node.
     */
    public Node getFinishEnd() {
        return finish;
    }
    
    /** Given the node on one end of the arc, this gets the node on the other
     * end. Used for traversing the graph.
     * @param thisEnd is the node to start from.
     * @return the node at the other end from thisEnd.
     */
    public Node getOtherEnd(Node thisEnd) {
        if(thisEnd == start)
            return finish;
        else
            return start;
    }
    
    /**
     * Draws the arc on a given Graphics output.
	 * @param g is the graphics to draw on.
	 * @param scale is the scale to draw the arc at.
	 */
	public void draw(Graphics g, float scale){
        //System.out.println("Drawing arc ");

        g.drawLine((int)Math.round(start.getX() * scale),
                   (int)Math.round(start.getY() * scale),
                   (int)Math.round(finish.getX() * scale),
                   (int)Math.round(finish.getY() * scale));
    }
    

    /** Determines whether 2 arcs, modelled as straight lines, intersect.
     * @param other is the arc to check for intersection
     * @return true if the arcs intersect, false otherwise.
     */
    public boolean intersects(Arc other) {
        // First line (x0 + u.dx0, y0 + u.dy0)
        float x0 = start.getX();
        float y0 = start.getY();
        float dx0 = finish.getX() - x0;
        float dy0 = finish.getY() - y0;
        
        // second line
        float x1 = other.getStartEnd().getX();
        float y1 = other.getStartEnd().getY();
        float dx1 = other.getFinishEnd().getX() - x1;
        float dy1 = other.getFinishEnd().getY() - y1;
        
        // This must be satisfied at point of intersection.
        // x0 + u.dx0 = x1 + v.dx1
        // y0 + u.dy0 = y1 + v.dy1
        // Hence solve simultaneous equations:
        // u.dx0 = x1 + v.dx1 - x0
        // u.dy0 = y1 + v.dy1 - y0
        // ==>
        // u = (x1 + v.dx1 - x0)/dx0
        // u = (y1 + v.dy1 - y0)/dy0
        // ==> (x1 + v.dx1 - x0)/dx0 = (y1 + v.dy1 - y0)/dy0
        // ==> (x1 + v.dx1 - x0).dy0 = (y1 + v.dy1 - y0).dx0
        // ==> x1.dy0 + v.dx1.dy0 - x0.dy0 = y1.dx0 + v.dy1.dx0 - y0.dx0
        // ==> v.dx1.dy0 - v.dy1.dx0 = y1.dx0 - y0.dx0 - x1.dy0 + x0.dy0
        // ==> v.(dx1.dy0 - dy1.dx0) = dx0.(y1 - y0) + dy0.(x0 - x1)
        // ==> v = (dx0.(y1 - y0) + dy0.(x0 - x1))/(dx1.dy0 - dy1.dx0)
        
        float denom = (dx1*dy0 - dy1*dx0);
        if(Math.abs(denom) < 0.000001) // arbitrarily small....
            return false;   // (effectively) parallel.
        
        // find one of the parameters.  Has to be within range 0..1 to
        // make lines cross within their lengths.
        float v = (dx0*(y1 - y0) + dy0*(x0 - x1))/denom;
        return v >= 0 && v <= 1.0;
    }
    

	public Rectangle2D.Float getBounds(){
		return getBounds(1.0f);
	}
	
	public ConnectorEnd getStartConnectorEnd(){
		return startEnd;
	}

	public ConnectorEnd getFinishConnectorEnd(){
		return finishEnd;
	}

	/**
	 * Writes the Connector out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("Connector");
		out.addAttribute("uuid",getKey().toString());
		out.addAttribute("type",getType().getKey().toString());
		KeyedItem represents = getItem();
		out.addAttribute("represents",represents.getKey().toString());
		
		out.addAttribute("start",((KeyedItem)start).getKey().toString());
		out.addAttribute("finish",((KeyedItem)finish).getKey().toString());
		
		drawingStrategy.writeXML(out);
		
		out.stopEntity();
	}
	
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Connector#getType()
     */
    public ConnectorType getType() {
        return type;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Connector#setType(alvahouse.eatool.gui.graphical.ConnectorType)
     */
    public void setType(ConnectorType type){
        this.type = type;
    }
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandle(int)
	 */
	public Point2D.Float getHandlePosition(int idx) {
		return drawingStrategy.getHandle(idx);
	}

	public void moveHandle(int idx, float x, float y){
		drawingStrategy.moveHandle(idx, x, y);
	}
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#getHandleCount()
	 */
	public int getHandleCount() {
		return drawingStrategy.getHandleCount();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#draw(java.awt.Graphics2D)
	 */
	public void draw(Graphics2D g, float zoom) {
		drawingStrategy.draw(g, zoom, selected);
		drawEnds(g,zoom,selected);
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.GraphicalObject#sizeWith(java.awt.Graphics2D)
	 */
	public void sizeWith(Graphics2D g){
		Node end = getStartEnd();
		drawingStrategy.setStart(end.getX(), end.getY());
		end = getFinishEnd();
		drawingStrategy.setFinish(end.getX(), end.getY());
		
		drawingStrategy.resetLayout();
	}

	private void drawEnds(Graphics2D g, float zoom, boolean selected){
		Shape curve = drawingStrategy.getShape(1.0f);
		List<Line2D.Float> curveSegments = shapeToLineSegments(curve);

		Symbol startEnd = (Symbol)getStartEnd();
		Shape startShape = startEnd.getOutlineShape();
		UnitVector startEndPos = intersect(curveSegments, startShape);

		Symbol finishEnd = (Symbol)getFinishEnd();
		Shape finishShape = finishEnd.getOutlineShape();
		UnitVector finishEndPos = intersect(curveSegments, finishShape);

		if(startEndPos != null){
			ConnectorEnd start = getStartConnectorEnd();
			start.draw(g, startEndPos, zoom );
//			debugDrawEndVector(g, startEndPos);
		}
		
		if(finishEndPos != null){	
			ConnectorEnd finish = getFinishConnectorEnd();
			finish.draw(g, finishEndPos, zoom );
//			debugDrawEndVector(g, finishEndPos);
		}
			
	}

//	private void debugDrawEndVector(Graphics2D g, UnitVector pos) {
//		int x = (int) pos.getStartX();
//		int y = (int) pos.getStartY();
//		g.setColor(Color.red);
//
//		g.drawLine(
//			x,
//			y,
//			x + (int) (pos.getDirectionX() * 20),
//			y + (int) (pos.getDirectionY() * 20));
//	}

	private UnitVector intersect(List<Line2D.Float> curve, Shape symbolShape){
		List<Line2D.Float> shapeSegments = shapeToLineSegments(symbolShape);
	
		// one end or other of this curve should lie in the symbol.
		Point2D start = ((Line2D.Float)curve.get(0)).getP1();
		boolean forward = symbolShape.contains(start);
		
		Line2D.Float intersectingSegment = null;
		int nSegs = curve.size();

		if(forward){
			for(int i=0; i<nSegs; ++i){
				Line2D.Float segment = curve.get(i);
				if(symbolShape.contains(segment.getP1()) && !symbolShape.contains(segment.getP2())){
					intersectingSegment = segment;
					break;
				}
			}
		} else { // start at other end.
			for(int i=nSegs-1; i>=0; --i){
				Line2D.Float segment = curve.get(i);
				if(symbolShape.contains(segment.getP2()) && !symbolShape.contains(segment.getP1())){
					intersectingSegment = segment;
					break;
				}
			}
		}
		
		if(intersectingSegment == null){
			return null;
		}
		
		UnitVector position = null;
		nSegs = shapeSegments.size();
		for(int i=0; i<nSegs; ++i){
			Line2D.Float segment = (Line2D.Float)shapeSegments.get(i);
			if(segment.intersectsLine(intersectingSegment)){
				Point2D.Float intersection = getIntersection(segment,intersectingSegment);
				if(intersection == null){
					return null; // algorithm fails.
				}
				position = new UnitVector(intersection, intersectingSegment);
				if(!forward && position != null){
					position.reverseDirection();
				}
			}
		}
		
		return position;
	}
	
	private Point2D.Float getIntersection(Line2D.Float first,Line2D.Float second){
		
		// First line (x0 + u.dx0, y0 + u.dy0)
		float x0 = (float)first.getP1().getX();
		float y0 = (float)first.getP1().getY();
		float dx0 = (float)first.getP2().getX() - x0;
		float dy0 = (float)first.getP2().getY() - y0;
        
		// second line
		float x1 = (float)second.getP1().getX();
		float y1 = (float)second.getP1().getY();
		float dx1 = (float)second.getP2().getX() - x1;
		float dy1 = (float)second.getP2().getY() - y1;
        
		// This must be satisfied at point of intersection.
		// x0 + u.dx0 = x1 + v.dx1
		// y0 + u.dy0 = y1 + v.dy1
		// Hence solve simultaneous equations in u or v (pick v):
		// u.dx0 = x1 + v.dx1 - x0
		// u.dy0 = y1 + v.dy1 - y0
		// ==>
		// u = (x1 + v.dx1 - x0)/dx0
		// u = (y1 + v.dy1 - y0)/dy0
		// ==> (x1 + v.dx1 - x0)/dx0 = (y1 + v.dy1 - y0)/dy0
		// ==> (x1 + v.dx1 - x0).dy0 = (y1 + v.dy1 - y0).dx0
		// ==> x1.dy0 + v.dx1.dy0 - x0.dy0 = y1.dx0 + v.dy1.dx0 - y0.dx0
		// ==> v.dx1.dy0 - v.dy1.dx0 = y1.dx0 - y0.dx0 - x1.dy0 + x0.dy0
		// ==> v.(dx1.dy0 - dy1.dx0) = dx0.(y1 - y0) + dy0.(x0 - x1)
		// ==> v = (dx0.(y1 - y0) + dy0.(x0 - x1))/(dx1.dy0 - dy1.dx0)
        
		float denom = (dx1*dy0 - dy1*dx0);
		if(Math.abs(denom) < 0.000001) // arbitrarily small....
			return null;   // (effectively) parallel.
        
		// find one of the parameters.  Has to be within range 0..1 to
		// make lines cross within their lengths.
		float v = (dx0*(y1 - y0) + dy0*(x0 - x1))/denom;
		return new Point2D.Float(x1 + v * dx1, y1 + v * dy1);
	}
		
	private ArrayList<Line2D.Float> shapeToLineSegments(Shape curve) {

		float[] coords = new float[6]; // 6 - see PathIterator docs.
		float currX = 0, currY = 0;
		float moveX = 0, moveY = 0;
		Line2D.Float segment;
		ArrayList<Line2D.Float> segments = new ArrayList<>(32); // 32 covered all cubics in tests
		for (PathIterator pathCurve = curve.getPathIterator(null, 1.0);
			!pathCurve.isDone();
			pathCurve.next()) {
			int segmentType = pathCurve.currentSegment(coords);
			switch (segmentType) {
				case PathIterator.SEG_MOVETO :
					currX = moveX = coords[0];
					currY = moveY = coords[1];
					break;

				case PathIterator.SEG_LINETO :
					float newX = coords[0];
					float newY = coords[1];

					// line is (currX,currY) -> (newX, newY)
					segment = new Line2D.Float(currX, currY, newX, newY);
					segments.add(segment);
					currX = newX;
					currY = newY;
					break;

				case PathIterator.SEG_CLOSE :
					// Line from (currX, currY) -> (moveX, moveY)
					segment = new Line2D.Float(currX, currY, moveX, moveY);
					segments.add(segment);
					currX = moveX;
					currY = moveY;
					break;

				default :
					assert(false);
					break;

			}
		}
		//System.out.println("Curve has " + segments.size() + " segements");
		return segments;
	}
	
	/**
	 * Draws any selection tools for the object.
	 * @param g is the Graphics2D to draw into.
	 * @param scale is the current diagram scale.
	 */
	public void drawCollateral(Graphics2D g, float scale){
		if(selected){
			int handleCount = drawingStrategy.getHandleCount();
			for(int i=0; i<handleCount; ++i) {
				handles[i].draw(g,scale);
			}
		}
	}


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#endMoved(alvahouse.eatool.gui.graphical.Symbol)
	 */
	public void endMoved(Symbol s){
		assert(s != null);
		if(s == getStartEnd()) {
			drawingStrategy.setStart(s.getX(), s.getY());
		} 
		if(s == getFinishEnd()){ 
			drawingStrategy.setFinish(s.getX(), s.getY());
		}
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#normalise()
	 */
	public void normalise(){
	    drawingStrategy.resetLayout();
	}
	
	public void onSelect(int mx, int my, float zoom){
		
		// Create handles for this connector
		if(handles == null){
			int handleCount = getHandleCount();
			handles = new Handle[handleCount];
			for(int i=0; i<handleCount; ++i){
				handles[i] = new ConnectorHandle(this,i);
			}
		}
		
		// See if initial point lies in any of them
		dragHandle = null;
		for(int i=0; i<handles.length; ++i) {
			if(handles[i].pointInHandle(mx,my,zoom)){
				dragHandle = handles[i];
				break;
			}
		}
		
		selected = true;
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#onDrag(int, int, float)
	 */
	public void onDrag(int x, int y, float zoom) {
		if(dragHandle != null) {
			dragHandle.dragTo(x,y,zoom);
		}
	}
	
	public void clearSelect(){
		//System.out.println("Unselecting");
		selected = false;
		dragHandle = null;
		handles = null;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.Connector#isSelected()
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Gets the basic bounding rectangle of the connector.  This is without
	 * any handles or notation that might extend the bounding box.
	 * @return a rectangular bounding box.
	 */
	public Rectangle2D.Float getBounds(float zoom){
		return drawingStrategy.getBounds(zoom);
	}
	
	/**
	 * Gets the extended bounding rectangle of the connector.  This includes
	 * any handles or notation that might extend the bounding box.
	 * @return a rectangular bounding box.
	 */
	public Rectangle2D.Float getExtendedBounds(float zoom){
		Rectangle2D.Float bounds = new Rectangle2D.Float();
		bounds.setRect(getBounds(zoom));
		if(handles != null){
			for(int i=0; i<handles.length; ++i){
				Rectangle2D handleBounds = handles[i].getBounds(zoom);
				Rectangle2D.union(handleBounds,bounds,bounds);
			}
		}
		return bounds;
	}

	public boolean hitTest(int mx, int my, float zoom){

		// If we're selected check handles for hits.
		if(selected){
			for(int i=0; i<handles.length; ++i){
				Rectangle2D handleBounds = handles[i].getBounds(zoom);
				if(handleBounds.contains(mx,my)){
					return true;
				}
			}
		}
		
		// And if we're within the bounds of the curve then do a more
		// detailed hit test.
		Rectangle2D bounds = getBounds(zoom);
		if(bounds.contains(mx,my)){
			return drawingStrategy.hitTest(mx,my,zoom);
		}
		
		
		return false;
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Connector#setStrategy(alvahouse.eatool.gui.graphical.ConnectorStrategy)
     */
    public void setStrategy(ConnectorStrategy connectorStrategy) {
//        if(start == null || finish == null){
//            throw new IllegalStateException("Cannot set drawing strategy if ends not set");
//        }
//	    connectorStrategy.setStart(start.getX(), start.getY());
//	    connectorStrategy.setFinish(finish.getX(), finish.getY());
	    drawingStrategy = connectorStrategy;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setPosition(float, float)
     */
    public void setPosition(float x, float y) {
        // Nop for connectors as position set by ends.
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setSize(float, float)
     */
    public void setSize(float width, float height) {
        // Nop for connectors as position set by ends.
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getX()
     * Just returns the midpoint of the connector.
     */
    public float getX() {
        return (start.getX() + finish.getX())/2;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getY()
     * Just returns the midpoint of the connector.
    */
    public float getY() {
        return (start.getY() + finish.getY())/2;
    }
    
    public abstract Object clone();
    
    protected void cloneTo(AbstractConnector copy) {
    	super.cloneTo(copy);
    	copy.type = type;
    	copy.item = item;
    	copy.startEnd = startEnd;
    	copy.finishEnd = finishEnd;
    }
    
    protected void copyTo(ConnectorDto dto) {
    	super.copyTo(dto);
    	dto.setConnectorTypeKey(type.getKey());
    	dto.setReferencedItemKey(item.getKey());
    	dto.setStartSymbolKey(start.getKey());
    	dto.setFinishSymbolKey(finish.getKey());
    	dto.setStartEnd(startEnd.toDto());
    	dto.setFinishEnd(finishEnd.toDto());
    }
}
