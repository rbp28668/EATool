/*
 * DiamondSymbol.java
 * Project: EATool
 * Created on 20-Feb-2006
 *
 */
package alvahouse.eatool.repository.graphical.symbols;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.standard.AbstractSymbol;
import alvahouse.eatool.repository.graphical.standard.DimensionFloat;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.util.UUID;

/**
 * DiamondSymbol
 * 
 * @author rbp28668
 */
public class DiamondSymbol extends AbstractSymbol {

    private float textWidth;
    private float textHeight;

	/**
	 * Default constructor to allow dynamic instantiation.
	 */
	public DiamondSymbol(){
		super(new UUID(),null,null);
	}


    /**
     * @param key
     * @param o
     * @param type
     */
    public DiamondSymbol(UUID key, KeyedItem item, SymbolType type) {
        super(key, item, type);
    }
    
	/**
	 * Gets the outline of the symbol.
	 * @return Shape containing the outline symbol.
	 */
	public Shape getOutlineShape() {
		GeneralPath diamond = new GeneralPath();
		diamond.moveTo(getX() - getWidth()/2, getY());
		diamond.lineTo(getX(), getY() + getHeight()/2);
		diamond.lineTo(getX() + getWidth()/2, getY());
		diamond.lineTo(getX(), getY() - getHeight()/2);
		diamond.lineTo(getX() - getWidth()/2, getY());
		return diamond;
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
		
		float width = textWidth * 2;
		float height = textHeight * 2;
		
		setSize(width,height);
	}
    

}
