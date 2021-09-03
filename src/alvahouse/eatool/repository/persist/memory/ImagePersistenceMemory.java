/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.dto.images.ImageDto;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.repository.persist.StaleDataException;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ImagePersistenceMemory implements ImagePersistence {

	private Map<UUID, ImageDto> images = new HashMap<>();
	
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
	public ImageDto getImage(UUID key) throws Exception {
		ImageDto image = images.get(key);
		if(image == null) {
			throw new IllegalArgumentException("No image with key " + key + " in repository");
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#addImage(alvahouse.eatool.repository.images.Image)
	 */
	@Override
	public String addImage(ImageDto image) throws Exception {
		UUID key = image.getKey();
		if(images.containsKey(key)) {
			throw new IllegalArgumentException("Image with key " + key + " already exists in repository");
		}
		
		String version = image.getVersion().update(new UUID().asJsonId());
		images.put(key, image);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#updateImage(alvahouse.eatool.repository.images.Image)
	 */
	@Override
	public String updateImage(ImageDto image) throws Exception {
		UUID key = image.getKey();
		ImageDto original = images.get(key);
		if(original == null) {
			throw new IllegalArgumentException("Image with key " + key + " does not exist in repository");
		}
		if(!image.getVersion().sameVersionAs(original.getVersion())) {
			throw new StaleDataException("Unable to update image due to stale data");
		}
		
		String version = image.getVersion().update(new UUID().asJsonId());
		images.put(key, image);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#deleteImage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteImage(UUID key, String version) throws Exception {
		ImageDto image = images.get(key);
		if(image == null) {
			throw new IllegalArgumentException("Image with key " + key + " does not exist in repository");
		}

		if(!image.getVersion().getVersion().equals(version)) {
			throw new StaleDataException("Unable to delete image due to stale data");
		}

		images.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImages()
	 */
	@Override
	public Collection<ImageDto> getImages() throws Exception {
		List<ImageDto> copy = new ArrayList<>(images.size());
		copy.addAll(images.values());
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
