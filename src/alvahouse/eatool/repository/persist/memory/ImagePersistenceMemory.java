/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ImagePersistenceMemory implements ImagePersistence {

	private Map<UUID, Image> images = new HashMap<>();
	
	/**
	 * 
	 */
	public ImagePersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception{
		images.clear();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Image getImage(UUID key) throws Exception {
		Image image = images.get(key);
		if(image == null) {
			throw new IllegalArgumentException("No image with key " + key + " in repository");
		}
		return (Image) image.clone();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#addImage(alvahouse.eatool.repository.images.Image)
	 */
	@Override
	public void addImage(Image image) throws Exception {
		UUID key = image.getKey();
		if(images.containsKey(key)) {
			throw new IllegalArgumentException("Image with key " + key + " already exists in repository");
		}
		images.put(key, (Image)image.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#updateImage(alvahouse.eatool.repository.images.Image)
	 */
	@Override
	public void updateImage(Image image) throws Exception {
		UUID key = image.getKey();
		if(!images.containsKey(key)) {
			throw new IllegalArgumentException("Image with key " + key + " does not exist in repository");
		}
		images.put(key, (Image)image.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#deleteImage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteImage(UUID key) throws Exception {
		if(!images.containsKey(key)) {
			throw new IllegalArgumentException("Image with key " + key + " does not exist in repository");
		}
		images.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImages()
	 */
	@Override
	public Collection<Image> getImages() throws Exception {
		List<Image> copy = new ArrayList<>(images.size());
		for(Image image : images.values()) {
			copy.add( (Image) image.clone());
		}
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImageCount()
	 */
	@Override
	public int getImageCount() throws Exception{
		return images.size();
	}

}
