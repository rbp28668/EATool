/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface ImagePersistence {

	/**
	 * Clears down the model.
	 */
	public void dispose() throws Exception;

	/**
	 * Looks up a image by UUID.
	 * 
	 * @param key is the key of the image to get
	 * @return the corresponding image, or null if not in the model
	 */
	public Image getImage(UUID key) throws Exception;

	/**
	 * adds a new image to the collection.
	 * 
	 * @param s is the image to add.
	 * @throws IllegalStateException if the image already exists in the model.
	 */
	public void addImage(Image image) throws Exception;

	/**
	 * Updates and existing image in the model.
	 * 
	 * @param s is the image to update.
	 * @throws IllegalStateException if the image already exists in the model.
	 */
	public void updateImage(Image image) throws Exception;

	/**
	 * Deletes an image keyed by UUID.
	 * 
	 * @param key is the key of the image to delete.
	 * @throws IllegalStateException if deleting a image not in the model.
	 */
	public void deleteImage(UUID key) throws Exception;

	/**
	 * Gets an iterator that iterates though all the entites.
	 * 
	 * @return an iterator for the entities.
	 */
	public Collection<Image> getImages() throws Exception;

	/**
	 * DEBUG only
	 * 
	 * @return
	 */
	public int getImageCount() throws Exception;

}
