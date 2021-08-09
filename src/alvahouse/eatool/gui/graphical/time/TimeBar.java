/*
 * TimeBar.java
 * Project: EATool
 * Created on 02-Jan-2007
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.Date;
import java.util.Vector;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.repository.graphical.standard.DimensionFloat;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Property;

/**
 * TimeBar
 * 
 * @author rbp28668
 */
public class TimeBar implements GraphicalObject, GraphicalProxy{

    private Property property;
    private TimeDiagramType.TypeEntry type;
    private float x;
    private float y;
	private DimensionFloat size = new DimensionFloat(50,20);
    private Date[] dates;
    private transient boolean isSelected = false;
    
    /**
     * 
     */
    public TimeBar(Property property, TimeDiagramType.TypeEntry type) {
        super();
        this.type = type;
        setItem(property);
    }
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.GraphicalObject#setSize(float, float)
	 */
	public void setSize(float width, float height){
		size.set(width,height);
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setPosition(float, float)
     */
    public void setPosition(float px, float py) {
        x = px;
        y = py;
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
    
    /**
     * @return
     */
    public Date getStartDate(){
        return dates[0];
    }
    
    /**
     * @return
     */
    public Date getFinishDate(){
        return dates[dates.length-1];
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalProxy#setItem(alvahouse.eatool.repository.base.KeyedItem)
     */
    public void setItem(KeyedItem item) {
        property = (Property)item;
        dates = type.getTimeLine().getEvents(property.getValue());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalProxy#getItem()
     */
    public KeyedItem getItem() {
        return property;
    }

    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds()
     */
    public Float getBounds() {
        Rectangle2D.Float bounds = new Rectangle2D.Float();
        bounds.height = size.getHeight();
        bounds.width = size.getWidth();
        bounds.y = y - bounds.height/2;
        bounds.x = x - bounds.width/2;
        return bounds;
    }

 
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#draw(java.awt.Graphics2D, float)
     */
    public void draw(Graphics2D g, float scale) {
        float width = size.getWidth();
        float height = size.getHeight();
        Date start = getStartDate();
        Date finish = getFinishDate();
        
        long range = finish.getTime() - start.getTime();
        float px = x - width / 2;
        float py = y - height/2;
        
        //System.out.println("Draw bar " + x + ',' + y + ',' + width + ',' + height);
        float xScale = width / (float)range;
        float currX = px;
        
        Vector colours = type.getColours();
        for(int i=0; i<dates.length-1; ++i){
            int x0 = (int)(px + xScale * (dates[i].getTime() - start.getTime()));
            int y0 = (int)py;
            
            float fw = xScale * (dates[i+1].getTime() - dates[i].getTime());
            currX += fw;

            int w = (int)fw;
            int h = (int)(height);
            g.setColor((Color)colours.get(i));
            g.fillRect(x0,y0,w,h);
        }
        if(true || isSelected){
            int x0 = (int)px;
            int y0 = (int)py;
            int x1 = (int)(width);
            int y1 = (int)(height);
            g.setColor(Color.black);
            g.drawRect(x0,y0,x1,y1);
            
        }
    }

    /**
     * @param g
     * @param scale
     */
    public void drawCaption(MetaModel mm, Graphics2D g, Font font, float scale) throws Exception {
        float height = size.getHeight();
        
        float px = 0;
        float py = y + height/2;
        
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(getCaption(mm),px,py);
    }

    /**
     * Gets the caption that will be used to describe the attached property.
     * @return the caption String.
     */
    public String getCaption(MetaModel mm) throws Exception {
        Entity e = (Entity)property.getContainer();
        return e.toString()+ " (" + type.getTargetProperty(mm).getName() +")";
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#sizeWith(java.awt.Graphics2D)
     */
    public void sizeWith(Graphics2D g) {
        // Leave as a NOP as Time Diagram sizes the bars
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#drawCollateral(java.awt.Graphics2D, float)
     */
    public void drawCollateral(Graphics2D g, float scale) {
        // NOP - no handles.
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds(float)
     */
    public Float getBounds(float zoom) {
        Rectangle2D.Float zoomedBounds = new Rectangle2D.Float();

        zoomedBounds.height = zoom * size.getHeight();
        zoomedBounds.width = zoom * size.getWidth();
        zoomedBounds.y = zoom * (y - zoomedBounds.height/2);
        zoomedBounds.x = zoom * (x - zoomedBounds.width/2);

        return zoomedBounds;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getExtendedBounds(float)
     */
    public Float getExtendedBounds(float zoom) {
        return getBounds(zoom);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#hitTest(int, int, float)
     */
    public boolean hitTest(int mx, int my, float zoom) {
        Rectangle2D.Float bounds = getBounds(zoom);
        return bounds.contains(mx,my);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onSelect(int, int, float)
     */
    public void onSelect(int mx, int my, float zoom) {
        isSelected = true;
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
        // NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#isSelected()
     */
    public boolean isSelected() {
        return isSelected;
    }


    /**
     * @return Returns the property.
     */
    public Property getProperty() {
        return property;
    }
}
