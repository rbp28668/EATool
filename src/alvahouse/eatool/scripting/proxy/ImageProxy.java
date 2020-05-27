/*
 * Image.java
 * Project: EATool
 * Created on 13-Aug-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

/**
 * Image allows scripting access to an image.
 * 
 * @author rbp28668
 */
@Scripted(description="An image.")    
public class ImageProxy {

    private alvahouse.eatool.repository.images.Image image;

    /**
     * 
     */
    ImageProxy(alvahouse.eatool.repository.images.Image image) {
        super();
        this.image = image;
    }

    public alvahouse.eatool.repository.images.Image getImage(){
        return image;
    }
}
