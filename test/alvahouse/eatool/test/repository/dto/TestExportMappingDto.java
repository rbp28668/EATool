/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.mapping.ExportMappingDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestExportMappingDto {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
	}

	
	/**
	 * @return
	 */
	private ExportMappingDto createDto() {
		ExportMappingDto dto = new ExportMappingDto();
		dto.setKey(new UUID());
		dto.setName("test export");
		dto.setDescription("Test export mapping");
		dto.setComponents(42);
		dto.setTransformPath("C:\\home\\fred\\wombats.xslt");
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
		version.setOriginalVersion(new UUID());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID());
		return version;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testViaXML() throws Exception{
		ExportMappingDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		ExportMappingDto copy = (ExportMappingDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat( copy, samePropertyValuesAs(dto, "version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	@Test
	void testViaJson() throws Exception{
		ExportMappingDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		ExportMappingDto copy = (ExportMappingDto) Serialise.unmarshalFromJson(asJson, ExportMappingDto.class);
		assertThat( copy, samePropertyValuesAs(dto, "version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

}
