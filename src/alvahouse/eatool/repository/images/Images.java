/*
 * Images.java
 * Project: EATool
 * Created on 04-Jul-2007
 *
 */
package alvahouse.eatool.repository.images;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Images contains a collection of Image along with listeners that need
 * to be notified of any changes to that collection or the Images contained
 * therein.
 * 
 * @author rbp28668
 */
public class Images {

	private ImagePersistence persistence;
//    private LinkedList<Image> images = new LinkedList<Image>();
//    private Map<UUID,Image> imageLookup = new HashMap<UUID,Image>();
    private LinkedList<ImagesChangeListener> listeners = new LinkedList<ImagesChangeListener>();

    /**
     * Creates a new, empty collection of images. 
     * @param imagePersistence 
     */
    public Images(ImagePersistence imagePersistence) {
        super();
        this.persistence = imagePersistence;
    }

    /**
     * Adds an image to the collection.
     * @param image is the Image to add.
     */
    public void addImage(Image image) throws Exception{
        if(image == null){
            throw new NullPointerException("Can't add null Image");
        }
 		String user = System.getProperty("user.name");
		image.getVersion().createBy(user);
		persistence.addImage(image);
        fireImageAdded(image);
    }
    
    /**
     * Internal Add an image to the collection.
     * @param image is the Image to add.
     */
    public void _add(Image image) throws Exception{
        if(image == null){
            throw new NullPointerException("Can't add null Image");
        }
		persistence.addImage(image);
    }

    /**
     * Updates an image in the repository.
     * @param image is the image to be updated.
     */
    public void updateImage(Image image) throws Exception {
 		String user = System.getProperty("user.name");
		image.getVersion().modifyBy(user);
		persistence.updateImage(image);
        fireImageChanged(image);
    }

    /**
     * Removes an image from the collection.
     * @param image is the image to remove.
     */
    public void removeImage(Image image) throws Exception{
        persistence.deleteImage(image.getKey());
        fireImageRemoved(image);
    }
    
    /**
     * Gets an unmodifiable version of the underlying collection.
     * @return an unmodifiable collection.
     */
    public Collection<Image> getImages() throws Exception{
        return persistence.getImages();
    }
    
    /**
     * Removes all images from the collection. 
     */
    public void reset() throws Exception{
        persistence.dispose();
    }
    
    /**
     * Adds a listener to receive change events to the images.
     * @param listener is the ImagesChangeListener to add.
     */
    public void addChangeListener(ImagesChangeListener listener){
        if(listener == null){
            throw new NullPointerException("Can't add null ImagesChangeListener");
        }
        listeners.add(listener);
    }
    
    /**
     * Removes a listener which will no longer receive chane events.
     * @param listener is the ImagesChangeListener to remove.
     */
    public void removeChangeListener(ImagesChangeListener listener){
        listeners.remove(listener);
    }
    
    /**
     * Signals that an image has been added to the collection.
     * @param image is the image that has been added.
     */
    private void fireImageAdded(Image image) throws Exception{
        ImageChangeEvent event = new ImageChangeEvent(image);
        for(ImagesChangeListener listener : listeners){
            listener.imageAdded(event);
        }
    }
 
    /**
     * Signals that an image has been removed from the collection.
     * @param image is the image that has been removed.
     */
    private void fireImageRemoved(Image image) throws Exception{
        ImageChangeEvent event = new ImageChangeEvent(image);
        for(ImagesChangeListener listener : listeners){
            listener.imageRemoved(event);
        }
    }

    /**
     * Signals that an image has been edited.
     * @param image is the image that has been edited.
     */
    private void fireImageChanged(Image image) throws Exception{
        ImageChangeEvent event = new ImageChangeEvent(image);
        for(ImagesChangeListener listener : listeners){
            listener.imageEdited(event);
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return "Images";
    }

    
    /**
     * Writes the HTMLProxy pages out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Images");
        
        try {
	        for(Image image : getImages()){
	            image.writeXML(out);
	        }
        } catch (Exception e) {
        	throw new IOException("Unable to write images to XML",e);
        }
        out.stopEntity();
    }

    /**
     * Looks up an image by its key.
     * @param imageKey is the key to use for image lookup.
     * @return the corresponding image or null if not found.
     */
    public Image lookupImage(UUID imageKey) throws Exception{
        return persistence.getImage(imageKey);
    }

}
