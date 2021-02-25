/*
 * ImagesChangeListener.java
 * Project: EATool
 * Created on 04-Jul-2007
 *
 */
package alvahouse.eatool.repository.images;

/**
 * ImagesChangeListener is a Listener interface to receive information
 * about changes to an Images collection and its contents.
 * 
 * @author rbp28668
 */
public interface ImagesChangeListener {

    /**
     * Signals an image has been added to the collection.
     * @param event references the image.
     * @throws Exception 
     */
    void imageAdded(ImageChangeEvent event) throws Exception;


    /**
     * Signals an image has been removed from the collection.
     * @param event references the image.
     */
    void imageRemoved(ImageChangeEvent event) throws Exception;

    /**
     * Signals an image has been edited.
     * @param event references the image.
     */
    void imageEdited(ImageChangeEvent event) throws Exception;

}
