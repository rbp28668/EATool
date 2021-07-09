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
import alvahouse.eatool.repository.dto.mapping.EntityTranslationDto;
import alvahouse.eatool.repository.dto.mapping.ImportMappingDto;
import alvahouse.eatool.repository.dto.mapping.PropertyTranslationDto;
import alvahouse.eatool.repository.dto.mapping.RelationshipTranslationDto;
import alvahouse.eatool.repository.dto.mapping.RoleTranslationDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestImportMappingDto {

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
	private ImportMappingDto createDto() {
		ImportMappingDto dto = new ImportMappingDto();
		dto.setKey(new UUID());
		dto.setName("test import");
		dto.setDescription("Test import mapping");
		dto.setTransformPath("C:\\home\\fred\\wombats.xslt");
		
		EntityTranslationDto et = new EntityTranslationDto();
		et.setMetaEntityKey(new UUID());
		et.setType("an_entity");
		
		PropertyTranslationDto pt1 = new PropertyTranslationDto();
		pt1.setMetaPropertyKey(new UUID());
		pt1.setType("property1");
		pt1.setKey(false);
		et.getProps().add(pt1);
		
		dto.getEntityTranslations().add(et);
		
		RelationshipTranslationDto rt = new RelationshipTranslationDto();
		rt.setMetaRelationshipKey(new UUID());
		rt.setType("relationship");
		RoleTranslationDto start = new RoleTranslationDto();
		start.setMetaRoleKey(new UUID());
		start.setType("start");
		RoleTranslationDto finish = new RoleTranslationDto();
		finish.setMetaRoleKey(new UUID());
		finish.setType("finish");
		rt.setStartRoleTranslation(start);
		rt.setFinishRoleTranslation(finish);

		PropertyTranslationDto pt2 = new PropertyTranslationDto();
		pt2.setMetaPropertyKey(new UUID());
		pt2.setType("property2");
		pt2.setKey(true);
		rt.getProps().add(pt2);

		dto.getRelationshipTranslations().add(rt);
		
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
		ImportMappingDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		ImportMappingDto copy = (ImportMappingDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat( copy, samePropertyValuesAs(dto, "version","entityTranslations", "relationshipTranslations" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	@Test
	void testViaJson() throws Exception{
		ImportMappingDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		ImportMappingDto copy = (ImportMappingDto) Serialise.unmarshalFromJson(asJson, ImportMappingDto.class);
		assertThat( copy, samePropertyValuesAs(dto, "version","entityTranslations", "relationshipTranslations" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

}
