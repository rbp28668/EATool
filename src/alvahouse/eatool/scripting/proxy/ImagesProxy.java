/*
 * Images.java
 * Project: EATool
 * Created on 13-Aug-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.util.UUID;

/**
 * Images provides scripting access to the Images collection.
 * 
 * @author rbp28668
 */
@Scripted(description="The collection of images in the repository")    
public class ImagesProxy {

    private alvahouse.eatool.repository.images.Images images;
    
    /**
     * 
     */
    public ImagesProxy(alvahouse.eatool.repository.images.Images images) {
        super();
        this.images = images;
    }

    @Scripted(description="Look up a given image by key.")    
    public ImageProxy lookupImage(String key){
        UUID uuidKey = new UUID(key);
        alvahouse.eatool.repository.images.Image image = images.lookupImage(uuidKey);
        if(image == null){
            throw new IllegalArgumentException("No image with key " + key);
        }
        return new ImageProxy(image);
    }
 }
