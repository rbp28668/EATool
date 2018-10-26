 /*
 * CircularSymbol.java
 * Project: EATool
 * Created on 20-Feb-2006
 *
 */
package alvahouse.eatool.repository.graphical.symbols;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.standard.AbstractSymbol;
import alvahouse.eatool.repository.graphical.standard.DimensionFloat;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.util.UUID;

/**
 * CircularSymbol
 * 
 * @author rbp28668
 */
public class CircularSymbol extends AbstractSymbol {

    private float textWidth;
    private float textHeight;
    
	/**
	 * Default constructor to allow dynamic instantiation.
	 */
	public CircularSymbol(){
		super(new UUID(),null,null);
	}

    /**
     * @param key
     * @param o
     * @param type
     */
    public CircularSymbol(UUID key, KeyedItem item, SymbolType type) {
        super(key, item, type);
    }

	/**
	 * Gets the outline of the symbol.
	 * @return Shape containing the outline symbol.
	 */
	public Shape getOutlineShape() {
		return new Arc2D.Float(
		(getX() - getWidth()/2), 
		(getY() - getHeight()/2), 
		getWidth(), 
		getHeight(),
		0, 360, Arc2D.CHORD
		);
	}
	
	/**
	 * @param g
	 * @param scale
	 */
	public void draw(Graphics2D g, float scale) {
		
		sizeSymbolFor(g);
		// Possible that still may need to init extra settings
		// if symbol read in.
		if(hasText() && textWidth == 0){
	        DimensionFloat size = new DimensionFloat();
	        sizeTextBox(g , size);
			textWidth = size.getWidth();
			textHeight = size.getHeight();
		}

	
		AffineTransform transform = g.getTransform();
		g.scale(scale,scale);
		Shape outline = getOutlineShape();
		g.setColor(getBackColour());
		g.fill(outline);
		g.setColor(getBorderColour());
		g.draw(outline);
		g.setTransform(transform);
		
		if(hasText()){
			int x = (int) Math.round((getX() - textWidth / 2) * scale);
			int y = (int)Math.round((getY() - textHeight / 2) * scale);
			int w = (int) Math.round(textWidth * scale);
			int h = (int) Math.round(textHeight * scale);
			drawText(g, x, y, w, h, scale);
		}
		
	}
	
	public void sizeWith(Graphics2D g) {
	    if(hasText()){
	        DimensionFloat size = new DimensionFloat();
	        sizeTextBox(g , size);
			textWidth = size.getWidth();
			textHeight = size.getHeight();
	    } else {
	        textWidth = textHeight = Math.max(getUnitHeight(g), getUnitWidth(g));
	    }
		
		// A bit of trig to calculate bounding box for the oval that
		// has the same aspect ratio as, and minimally circumscribes
		// the text box.
		double theta = Math.atan2(textHeight, textWidth);
		float width = (float)(textWidth / Math.cos(theta));
		float height = width * textHeight/textWidth;
		
		setSize(width,height);
	}

	
}
