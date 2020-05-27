/*
 * ImageDisplay.java
 * Project: EATool
 * Created on 13-Aug-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

/**
 * ImageDisplay manipulates an image on a diagram.
 * 
 * @author rbp28668
 */
@Scripted(description="Returned when an image is added to a diagram this allows you to"
		+ " query size and position and to reposition the image.")
public class ImageDisplayProxy {

    private alvahouse.eatool.repository.graphical.standard.ImageDisplay display;
    
    /**
     * 
     */
    ImageDisplayProxy(alvahouse.eatool.repository.graphical.standard.ImageDisplay display) {
        super();
        this.display = display;
    }

    /**
     * Sets the position of the image.
     * @param x is the x coordinate (horizontal position)
     * @param y is the y coordinate (vertical position)
     */
    @Scripted(description="Sets the position of the image to the given x,y coordinates.")    
    public void setPosition( float x, float y){
        display.setPosition(x,y);
    }
    
    /**
     * Get the width of the image.
     * @return the image width.
     */
    @Scripted(description="Get the width of the image.")    
    public float getWidth(){
        return display.getBounds().width;
    }
    
    /**
     * Get the height of the image.
     * @return the image height.
     */
    @Scripted(description="Get the height of the image.")    
    public float getHeight(){
        return display.getBounds().height;
    }
    
    /**
     * Gets the X coordinate of the centre of the image.
     * @return the image x coordinate.
     */
    @Scripted(description="Gets the X coordinate of the centre of the image.")    
    public float getX(){
        return display.getX();
    }
    
    /**
     * Gets the Y coordinate of the centre of the image.
     * @return the image y coordinate.
     */
    @Scripted(description="Gets the Y coordinate of the centre of the image.")    
    public float getY(){
        return display.getY();
    }
    
}
