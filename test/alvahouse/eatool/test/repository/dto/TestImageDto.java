/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.images.ImageDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestImageDto {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	void testViaXML() throws Exception{
		ImageDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		ImageDto copy = (ImageDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat( copy, samePropertyValuesAs(dto, "version", "image" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertEquals(copy.getImage().getWidth(), dto.getImage().getWidth());
		assertEquals(copy.getImage().getHeight(), dto.getImage().getHeight());
		assertTrue(imagesEqual(copy.getImage(), dto.getImage()));
	}


	@Test
	void testViaJson() throws Exception{
		ImageDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		ImageDto copy = (ImageDto) Serialise.unmarshalFromJson(asJson, ImageDto.class);
		assertThat( copy, samePropertyValuesAs(dto, "version", "image" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertEquals(copy.getImage().getWidth(), dto.getImage().getWidth());
		assertEquals(copy.getImage().getHeight(), dto.getImage().getHeight());
		assertTrue(imagesEqual(copy.getImage(), dto.getImage()));
	}

	/**
	 * @return
	 */
	private ImageDto createDto() {
		ImageDto dto = new ImageDto();
		dto.setKey(new UUID());
		dto.setName("An image");
		dto.setDescription("This is an image");
		dto.setFormat("png");
		dto.setImage(new BufferedImage(30,30,BufferedImage.TYPE_4BYTE_ABGR));
		dto.setVersion(createVersion());
		return dto;
	}

	/**
	 * @return
	 */
	private VersionDto createVersion() {
		VersionDto version = new VersionDto();
		version.setCreateDate(new Date());
		version.setCreateUser("fred");
		version.setOriginalVersion(new UUID().asJsonId());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID().asJsonId());
		return version;
	}

	/**
	 * @param copy
	 * @param source
	 * @return
	 */
	private boolean imagesEqual(BufferedImage copy, BufferedImage source) {
		if(copy.getWidth() != source.getWidth()) return false;
		if(copy.getHeight() != source.getHeight()) return false;
		
		for(int iy=0; iy<copy.getHeight(); ++iy) {
			for(int ix=0; ix<copy.getWidth(); ++ix) {
				if(copy.getRGB(ix, iy) != source.getRGB(ix, iy)) return false;
			}
		}
		return true;
	}

}
