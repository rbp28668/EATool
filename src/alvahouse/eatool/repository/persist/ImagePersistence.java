/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.images.ImageDto;
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
	public ImageDto getImage(UUID key) throws Exception;

	/**
	 * adds a new image to the collection.
	 * 
	 * @param s is the image to add.
	 * @return the revision number of the new image record.
	 * @throws IllegalStateException if the image already exists in the model.
	 */
	public String addImage(ImageDto image) throws Exception;

	/**
	 * Updates and existing image in the model.
	 * 
	 * @param s is the image to update.
	 * @return the new revision number of the updated record.
	 * @throws IllegalStateException if the image already exists in the model.
	 */
	public String updateImage(ImageDto image) throws Exception;

	/**
	 * Deletes an image keyed by UUID.
	 * 
	 * @param key is the key of the image to delete.
	 * @param version is the revision number of the record to delete.
	 * @throws IllegalStateException if deleting a image not in the model.
	 */
	public void deleteImage(UUID key, String version) throws Exception;

	/**
	 * Get all the images.
	 */
	public Collection<ImageDto> getImages() throws Exception;

	/**
	 * DEBUG only
	 * 
	 * @return
	 */
	public int getImageCount() throws Exception;

}
