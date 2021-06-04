/*
 * TextualObject.java
 * Project: EATool
 * Created on 18-Jun-2007
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.StringTokenizer;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.Handle;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * TextualObject handles the drawing of any text-based graphical object. This
 * includes symbols and text boxes.
 * 
 * @author rbp28668
 */
public abstract class TextualObject extends RepositoryItem implements GraphicalObject, TextObjectSettings {

	private float x,y;
	private DimensionFloat size = new DimensionFloat(50,20);
	private Color backColour = Color.lightGray;
	private Color textColour = Color.black;
	private Color borderColour = Color.black;
	private Font font = null;
	private boolean selected = false;
	private Handle[] handles = null;
	private Handle resizeHandle = null;	// current handle for resizing
	private float dragOffsetX = 0;
	private float dragOffsetY = 0;
	private boolean mustSizeSymbol = true;

    /**
     * @param key
     */
    public TextualObject(UUID key) {
        super(key);
		font = new Font("SansSerif", Font.PLAIN,10);
		mustSizeSymbol = true;
    }

    public abstract String getText();

	/**
	 * Draws handles round the symbol.
	 * @param g is the Graphics2D to draw into.
	 * @param scale is the current scale factor of the diagram.
	 */
	private void drawHandles(Graphics2D g, float scale){
		if(handles != null) {
			for(int i=0; i<handles.length; ++i) {
				handles[i].draw(g,scale);
			}
		}
	}

	

    /**
     * @param dx
     * @param dy
     */
    protected void move(float dx, float dy){
        x += dx;
        y += dy;
    }
    
	/**
	 * Gets the current width irrespective of any zoom factor.
	 * @return the current width.
	 */
	protected float getWidth() {
	    return size.getWidth();
	}
	
	/**
	 * Gets the current height irrespective of any zoom factor.
	 * @return the current height.
	 */
	protected float getHeight() {
	    return size.getHeight();
	}
	
	/**
	 * Calculates the required size of the symbol.
     * @param g is the Graphics2D to size for.
     */
    protected void sizeSymbolFor(Graphics2D g) throws Exception{
        // Want to size object by breaking string so that we have symbols about 1.5
		// width:height.  Arbitrary but looks ok!
		if(mustSizeSymbol){
			sizeWith(g);
			mustSizeSymbol = false;
		}
    }


	/**
	 * Draws text in the symbol.  This auto-breaks the text using the current size of the
	 * symbol.  Note that locations and dimensions should already be scaled.
	 * @param g is the graphics to draw into.
	 * @param x is the x coordinate of the text box.
	 * @param y is the y coordinate of the text box.
	 * @param width is the width of the text box.
	 * @param height is the height of the text box.
	 * @param scale is the current scaling factor.
	 */
	protected void drawText(Graphics2D g, int x, int y, int width, int height,  float scale) {
		
		String text = getText();
		
		Font localFont = this.font;
		if(scale != 1.0f){
			float scaleSize = font.getSize() * scale;
			localFont = font.deriveFont(scaleSize);
		}
		
		g.setFont(localFont);

		FontMetrics fontMetrics = g.getFontMetrics(localFont);
		
		int borderWidth = fontMetrics.charWidth('m'); // already scaled as font zoomed
		int borderHeight = fontMetrics.getHeight()/2;

		int startx = x + borderWidth;
		int starty = y + borderHeight + fontMetrics.getAscent();

		Shape clip = g.getClip();
		g.clipRect(x,y,width,height);

		double maxx = x + width - borderWidth;

		int spaceWidth = fontMetrics.charWidth(' ');
		StringTokenizer st = new StringTokenizer(text);
		x = startx;
		y = starty;

		g.setColor(textColour);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			Rectangle2D tokBounds = fontMetrics.getStringBounds(tok,g);
			if( (x + tokBounds.getWidth()) > maxx){
				x = startx;
				y += fontMetrics.getHeight();
			}
			g.drawString(tok,x,y);

			x += tokBounds.getWidth() + spaceWidth;
			
			
		}
		g.setClip(clip);
	}

	/**
	 * Works out the size of the text box needed to draw the attached
	 * object's text.
     * @param g is the graphics context for the font metrics
     * @param size is set to the size of the text box.
     */
    protected void sizeTextBox(Graphics2D g, DimensionFloat size) {
        final float widthToHeight = 1.5f;
		
		FontMetrics fontMetrics = g.getFontMetrics(font);
		
		int borderWidth = fontMetrics.charWidth('m'); 
		int borderHeight = fontMetrics.getHeight()/2;
		
		String text = getText();
		Rectangle2D bounds = fontMetrics.getStringBounds(text,g);
		
		// Work out the number of lines we would like to use if we could
		// split the text arbitrarily.
		int nLines = 0;
		float boxWidth = 0;
		float boxHeight = 0;
		do{
			++nLines;
			boxWidth = 2 * borderWidth + (float)bounds.getWidth() / nLines;
			boxHeight = 2 * borderHeight + (float)bounds.getHeight() * nLines;
		}while(boxWidth / boxHeight > widthToHeight);
		
		// Now, based on nLines, work out real size of box.
		
		double breakPosition = bounds.getWidth() / nLines;
		int lineCount = 1;	// track actual lines
		double maxWidth = 0;
		double lineWidth = 0;
		int spaceWidth = fontMetrics.charWidth(' ');
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			Rectangle2D tokBounds = fontMetrics.getStringBounds(tok,g);
			
			double width = tokBounds.getWidth();
			if(width > maxWidth){ // default in case single word never gets as far as breakPosition.
			    maxWidth = width;
			}
			
			if(lineWidth > 0) {
				lineWidth += spaceWidth;
			}
			lineWidth += width;
			
			if(lineWidth > breakPosition){
				if(lineWidth > maxWidth){
					maxWidth = lineWidth;
				}
				if(st.hasMoreTokens()){
					++lineCount; // we'll need another line.
					lineWidth = 0; // so far on new line.
				}
			}
		}
		
		// Now, based on nLines & maxWidth, work out desired size of symbol
		size.setWidth( 2 * borderWidth + (float)maxWidth);
		size.setHeight( 2 * borderHeight + fontMetrics.getHeight() * lineCount);
    }
    
    /**
     * Signals that the object must be resized.
     * @param b
     */
    protected void mustReSize(boolean b) {
        mustSizeSymbol = b;
    }

	/**
	 * Writes the TextObject attributes specific data.
     * @param out is the writer to write to.
	 * @throws IOException
	 */
	protected void writeTextObjectAttributesXML(XMLWriter out) throws IOException {
		out.addAttribute("uuid",getKey().toString());
	    out.addAttribute("x",Float.toString(getX()));
	    out.addAttribute("y",Float.toString(getY()));
		out.addAttribute("width",Float.toString(getWidth()));
 		out.addAttribute("height",Float.toString(getHeight()));
	}

	/**
	 * Writes the TextObjectSettings specific data.
     * @param out is the writer to write to.
	 * @throws IOException
	 */
	protected void writeTextObjectSettingsXML(XMLWriter out) throws IOException {
	    FactoryBase.writeColour(out, "TextColour",getTextColour());
	    FactoryBase.writeColour(out, "BackColour",getBackColour());
	    FactoryBase.writeColour(out,"BorderColour",getBorderColour());
	    FactoryBase.writeFont(out,"Font",getFont());
	}
    
	
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#draw(java.awt.Graphics2D, float)
     */
	public void draw(Graphics2D g, float scale) throws Exception{
		
		sizeSymbolFor(g);
		
		
		int x = (int) Math.round((getX() - getWidth() / 2) * scale);
		int y = (int)Math.round((getY() - getHeight() / 2) * scale);
		int w = (int) Math.round(getWidth() * scale);
		int h = (int) Math.round(getHeight() * scale);
		g.setColor(backColour);
		g.fillRect(x,y,w,h);
		g.setColor(borderColour);
		g.drawRect(x,y,w,h);
		
		drawText(g, x, y, w, h, scale);
			
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#sizeWith(java.awt.Graphics2D)
     */
    public void sizeWith(Graphics2D g) throws Exception{
		sizeTextBox(g , size);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#drawCollateral(java.awt.Graphics2D, float)
     */
    public void drawCollateral(Graphics2D g, float scale) {
		if(selected){
			drawHandles(g,scale);	
		}
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds()
	 */
	public Rectangle2D.Float getBounds() {
		return getBounds(1.0f);
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds(float)
     */
 	public Rectangle2D.Float getBounds(float zoom) {
		return new Rectangle2D.Float(
		(getX() - getWidth()/2) * zoom , 
		(getY() - getHeight()/2) * zoom , 
		getWidth() * zoom, 
		getHeight() * zoom 
		);
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getExtendedBounds(float)
     */
    public Rectangle2D.Float getExtendedBounds(float zoom) {
		float handles = 0;
		if(selected){
			handles = Handle.getSize();
		}
		return new Rectangle2D.Float(
		(getX() - getWidth()/2) * zoom - handles/2, 
		(getY() - getHeight()/2) * zoom - handles/2, 
		getWidth() * zoom + handles, 
		getHeight() * zoom + handles
		);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#hitTest(int, int, float)
     */
    public boolean hitTest(int mx, int my, float zoom) {
		// If we're selected check handles for hits.
		if(selected){
			for(int i=0; i<handles.length; ++i){
				Rectangle2D handleBounds = handles[i].getBounds(zoom);
				if(handleBounds.contains(mx,my)){
					return true;
				}
			}
		}
		
		Rectangle2D bounds = getBounds(zoom);
		return(bounds.contains(mx,my));
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onSelect(int, int, float)
     */
    public void onSelect(int mx, int my, float zoom) {
		if(handles == null){
			handles = new Handle[8];
			handles[0] = new GraphicalObjectHandle(this, GraphicalObjectHandle.TOP_LEFT);
			handles[1] = new GraphicalObjectHandle(this, GraphicalObjectHandle.TOP_CENTER);
			handles[2] = new GraphicalObjectHandle(this, GraphicalObjectHandle.TOP_RIGHT);
			handles[3] = new GraphicalObjectHandle(this, GraphicalObjectHandle.CENTER_LEFT);
			handles[4] = new GraphicalObjectHandle(this, GraphicalObjectHandle.CENTER_RIGHT);
			handles[5] = new GraphicalObjectHandle(this, GraphicalObjectHandle.BOTTOM_LEFT);
			handles[6] = new GraphicalObjectHandle(this, GraphicalObjectHandle.BOTTOM_CENTER);
			handles[7] = new GraphicalObjectHandle(this, GraphicalObjectHandle.BOTTOM_RIGHT);
		}
		
		resizeHandle = null;
		for(int i=0; i<handles.length; ++i) {
			if(handles[i].pointInHandle(mx,my,zoom)){
				resizeHandle = handles[i];
				break;
			}
		}
		
		// Establish relative posisition of mouse to rectangle for drag
		dragOffsetX = (mx / zoom) - getX();
		dragOffsetY = (my / zoom) - getY();
		
		selected = true;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#clearSelect()
     */
    public void clearSelect() {
		selected = false;
		resizeHandle = null;
		handles = null;
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.GraphicalObject#onDrag(int, int, float)
	 */
	public void onDrag(int mx, int my, float zoom){
		float x = mx / zoom;
		float y = my / zoom;
		
		if(resizeHandle != null){
			resizeHandle.dragTo(mx,my,zoom);
		} else {
			setPosition(x - dragOffsetX, y - dragOffsetY);
		}
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setPosition(float, float)
     */
    public void setPosition(float x, float y) {
        this.x = x; 
        this.y = y;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getX()
     */
    public float getX() { 
        return x;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getY()
     */
    public float getY() {
        return y;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setSize(float, float)
     */
    public void setSize(float width, float height) {
		size.set(width,height);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#isSelected()
     */
    public boolean isSelected() {
		return selected;
    }


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getBackColour()
	 */
	public Color getBackColour() {
		return backColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getTextColour()
	 */
	public Color getTextColour() {
		return textColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getBorderColour()
	 */
	public Color getBorderColour() {
		return borderColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setBackColour(java.awt.Color)
	 */
	public void setBackColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		backColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setTextColour(java.awt.Color)
	 */
	public void setTextColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		textColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setBorderColour(java.awt.Color)
	 */
	public void setBorderColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		borderColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getFont()
	 */
	public Font getFont() {
		return font;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		if(font == null) {
			throw new NullPointerException("Setting null font on symbol");
		}
		this.font = font;
	}
    
	protected void cloneTo(TextualObject copy) {
		super.cloneTo(copy);
	    copy.x = x;
	    copy.y = y;
		copy.size = new DimensionFloat(size.getWidth(), size.getHeight());
		copy.textColour = textColour;
		copy.backColour = backColour;
		copy.borderColour = borderColour;
		copy.font = font;
	}
	
}
