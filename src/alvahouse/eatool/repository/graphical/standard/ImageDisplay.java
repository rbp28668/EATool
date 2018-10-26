/*
 * ImageDisplay.java
 * Project: EATool
 * Created on 19-Jun-2007
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * ImageDisplay is the graphical entity used to display an image on a diagram.
 * 
 * @author rbp28668
 */
public class ImageDisplay extends RepositoryItem implements GraphicalObject {

    private Image image;
    private float x = 0;
    private float y = 0;
    private float width = 0;
    private float height = 0;
    private transient boolean isSelected = false;
    
    private transient float dragStartx = 0;
    private transient float dragStarty = 0;
    private transient float x0;
    private transient float y0;
    
    /**
     * @param key
     */
    public ImageDisplay(UUID key) {
        super(key);
    }
   
    public void setImage(Image image){
        this.image = image;
        int width = image.getImage().getWidth(null);
        int height = image.getImage().getHeight(null);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#draw(java.awt.Graphics2D, float)
     */
    public void draw(Graphics2D g, float scale) {
        java.awt.Image toDraw = image.getImage();
        AffineTransform transform = AffineTransform.getScaleInstance(scale,scale);
        transform.translate(x,y);
        g.drawImage(toDraw, transform, null);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#sizeWith(java.awt.Graphics2D)
     */
    public void sizeWith(Graphics2D g) {
        // NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#drawCollateral(java.awt.Graphics2D, float)
     */
    public void drawCollateral(Graphics2D g, float scale) {
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds()
     */
    public Rectangle.Float getBounds() {
        return new Rectangle.Float(x - width/2.0f, y - height/2.0f, width, height);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds(float)
     */
    public Rectangle2D.Float getBounds(float zoom) {
        java.awt.Image toDraw = image.getImage();
        int width = toDraw.getWidth(null);
        int height = toDraw.getHeight(null);
        
        return new Rectangle.Float(zoom * (x - width/2.0f), zoom * (y - height/2.0f) , zoom * width, zoom * height);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getExtendedBounds(float)
     */
    public Rectangle2D.Float getExtendedBounds(float zoom) {
        return getBounds(zoom);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#hitTest(int, int, float)
     */
    public boolean hitTest(int mx, int my, float zoom) {
        Rectangle.Float bounds = getBounds(zoom);
        return bounds.contains(mx,my);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onSelect(int, int, float)
     */
    public void onSelect(int mx, int my, float zoom) {
        isSelected = true;
        x0 = x;
        y0 = y;
        dragStartx = mx / zoom;
        dragStarty = my / zoom;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#clearSelect()
     */
    public void clearSelect() {
        isSelected = false;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onDrag(int, int, float)
     */
    public void onDrag(int mx, int my, float zoom) {
        float dragx = mx / zoom;
        float dragy = my / zoom;
        
        x = x0 + (dragx - dragStartx);
        y = y0 + (dragy - dragStarty);
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
        this.width = width;
        this.height = height;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#isSelected()
     */
    public boolean isSelected() {
        return isSelected();
    }

    /**
     * Writes the ImageDisplay as XML.
     * @param out is the XMLWriter to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("ImageDisplay");
		out.addAttribute("uuid",getKey().toString());
	    out.addAttribute("x",Float.toString(x));
	    out.addAttribute("y",Float.toString(y));
		out.addAttribute("width",Float.toString(width));
 		out.addAttribute("height",Float.toString(height));
 		out.addAttribute("displays",image.getKey().toString());
        out.stopEntity();
    }

}
