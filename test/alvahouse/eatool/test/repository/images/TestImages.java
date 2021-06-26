/**
 * 
 */
package alvahouse.eatool.test.repository.images;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.image.BufferedImage;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.ImageChangeEvent;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.images.ImagesChangeListener;
import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.repository.persist.memory.ImagePersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestImages {
	
	private ImagePersistence persistence;
	private Images images;
	private ImagesChangeListener listener;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new ImagePersistenceMemory();
		images = new Images(persistence);
		listener = mock(ImagesChangeListener.class);
		images.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	private Image createImage(String name, String desc, String format) {
		UUID key = new UUID();
		Image image = new Image(key);
		image.setName(name);
		image.setDescription(desc);
		image.setFormat(format);
		BufferedImage bi = new BufferedImage(30,20,BufferedImage.TYPE_4BYTE_ABGR);
		image.setImage(bi);
		return image;
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#add(alvahouse.eatool.repository.imageing.Image)}.
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#_add(alvahouse.eatool.repository.imageing.Image)}.
	 */
	@Test
	void testAddAndGet() throws Exception {
		
		Image image = createImage("Test Image","A test image","jpg");
		UUID key = image.getKey();
		
		images.addImage(image);
		
		Image retrieved = images.lookupImage(key);
		assertThat( retrieved, samePropertyValuesAs(image, "version", "image"));

		verify(listener, times(1)).imageAdded(any(ImageChangeEvent.class));

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#update(alvahouse.eatool.repository.imageing.Image)}.
	 */
	@Test
	void testUpdate() throws Exception{
		Image image = createImage("Test Image","A test image","jpg");
		UUID key = image.getKey();
		
		images.addImage(image);

		Image retrieved = images.lookupImage(key);
		assertThat( retrieved, samePropertyValuesAs(image, "version", "image"));

		image.setName("Modified image");
		image.setDescription("A modified image");
		
		images.updateImage(image);
		
		retrieved = images.lookupImage(key);
		assertThat( retrieved, samePropertyValuesAs(image, "version", "image"));
		verify(listener, times(1)).imageEdited(any(ImageChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#delete(alvahouse.eatool.repository.imageing.Image)}.
	 */
	@Test
	void testDelete() throws Exception{
		Image image = createImage("Test Image","A test image","jpg");
		
		images.addImage(image);
		assertEquals(1, images.getImageCount());
		
		images.removeImage(image);
		verify(listener, times(1)).imageRemoved(any(ImageChangeEvent.class));
		assertEquals(0, images.getImageCount());


	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#getImages()}.
	 */
	@Test
	void testGetImages() throws Exception {
		Image image1 = createImage("Test Image","A test image","jpg");
		Image image2 = createImage("Test Image2","A test image","jpg");
		Image image3 = createImage("Test Image3","A test image","jpg");

		images.addImage(image1);
		images.addImage(image2);
		images.addImage(image3);
		
		Collection<Image> retrieved = images.getImages();
		assertEquals(3,retrieved.size());
		assertTrue(retrieved.contains(image1));
		assertTrue(retrieved.contains(image2));
		assertTrue(retrieved.contains(image3));
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception {
		Image image1 = createImage("Test Image","A test image","jpg");
		Image image2 = createImage("Test Image2","A test image","jpg");
		Image image3 = createImage("Test Image3","A test image","jpg");

		images.addImage(image1);
		images.addImage(image2);
		images.addImage(image3);
		assertEquals(3, images.getImageCount());

		images.reset();
		
		assertEquals(0, images.getImageCount());

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#addChangeListener(alvahouse.eatool.repository.imageing.ImagesChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		ImagesChangeListener l = mock(ImagesChangeListener.class);
		
		assertFalse(images.isActive(l));
		images.addChangeListener(l);
		assertTrue(images.isActive(l));
		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#removeChangeListener(alvahouse.eatool.repository.imageing.ImagesChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		ImagesChangeListener l = mock(ImagesChangeListener.class);
		
		assertFalse(images.isActive(l));
		images.addChangeListener(l);
		assertTrue(images.isActive(l));
		images.removeChangeListener(l);
		assertFalse(images.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.imageing.Images#getImageCount()}.
	 */
	@Test
	void testGetImageCount() throws Exception {
		Image image1 = createImage("Test Image","A test image","jpg");
		Image image2 = createImage("Test Image2","A test image","jpg");
		Image image3 = createImage("Test Image3","A test image","jpg");

		assertEquals(0, images.getImageCount());
		images.addImage(image1);
		assertEquals(1, images.getImageCount());
		images.addImage(image2);
		assertEquals(2, images.getImageCount());
		images.addImage(image3);
		assertEquals(3, images.getImageCount());
	}


}
