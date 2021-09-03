/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.metamodel.MetaEntityDisplayHintDto;
import alvahouse.eatool.repository.dto.metamodel.MetaEntityDto;
import alvahouse.eatool.repository.dto.metamodel.MetaPropertyContainerDto;
import alvahouse.eatool.repository.dto.metamodel.MetaPropertyDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRelationshipRestrictionDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRoleDto;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestMetaModelDao {

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
	void testMetaEntityAsXML() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaEntityDto copy = (MetaEntityDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testMetaEntityAsJson() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaEntityDto copy = (MetaEntityDto) Serialise.unmarshalFromJson(asJson, MetaEntityDto.class);

		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
		
	}

	@Test
	void testMetaEntityWithPropertiesAsXML() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		addProperties(dao);
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaEntityDto copy = (MetaEntityDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
	}

	@Test
	void testMetaEntityWithPropertiesAsJson() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		addProperties(dao);
		
		String asJson = Serialise.marshalToJSON(dao);
		System.out.println(asJson);
		MetaEntityDto copy = (MetaEntityDto) Serialise.unmarshalFromJson(asJson, MetaEntityDto.class);

		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
		
	}

	@Test
	void testMetaRelationshipAsXML() throws Exception{
		
		MetaRelationshipDto dao = createMetaRelationshipDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaRelationshipDto copy = (MetaRelationshipDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dao, "version", "start", "finish", "restriction"));
		assertThat( copy.getStart(), notNullValue());
		assertThat( copy.getFinish(), notNullValue());
		assertThat( copy.getStart(), samePropertyValuesAs(dao.getStart()));
		assertThat( copy.getFinish(), samePropertyValuesAs(dao.getFinish()));
		assertThat( copy.getRestriction(), samePropertyValuesAs(dao.getRestriction()));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testMetaRelationshipAsJson() throws Exception{
		
		MetaRelationshipDto dao = createMetaRelationshipDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaRelationshipDto copy = (MetaRelationshipDto) Serialise.unmarshalFromJson(asJson, MetaRelationshipDto.class);

		assertThat( copy, samePropertyValuesAs(dao, "version", "start", "finish", "restriction"));
		assertThat( copy.getStart(), notNullValue());
		assertThat( copy.getFinish(), notNullValue());
		assertThat( copy.getStart(), samePropertyValuesAs(dao.getStart()));
		assertThat( copy.getFinish(), samePropertyValuesAs(dao.getFinish()));
		assertThat( copy.getRestriction(), samePropertyValuesAs(dao.getRestriction()));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testMetaRelationshipWithPropertiesAsXML() throws Exception{
		
		MetaRelationshipDto dao = createMetaRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaRelationshipDto copy = (MetaRelationshipDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));

		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));

		assertThat( copy.getStart().getProperties(), hasSize(2));
		assertThat( copy.getStart().getProperties().get(0), samePropertyValuesAs(dao.getStart().getProperties().get(0)));
		assertThat( copy.getStart().getProperties().get(1), samePropertyValuesAs(dao.getStart().getProperties().get(1)));

		assertThat( copy.getFinish().getProperties(), hasSize(2));
		assertThat( copy.getFinish().getProperties().get(0), samePropertyValuesAs(dao.getFinish().getProperties().get(0)));
		assertThat( copy.getFinish().getProperties().get(1), samePropertyValuesAs(dao.getFinish().getProperties().get(1)));

	}

	@Test
	void testMetaRelationshipWithPropertiesAsJson() throws Exception{
		
		MetaRelationshipDto dao = createMetaRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asJson = Serialise.marshalToJSON(dao);
		System.out.println(asJson);
		MetaRelationshipDto copy = (MetaRelationshipDto) Serialise.unmarshalFromJson(asJson, MetaRelationshipDto.class);

		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));

		assertThat( copy.getStart().getProperties(), hasSize(2));
		assertThat( copy.getStart().getProperties().get(0), samePropertyValuesAs(dao.getStart().getProperties().get(0)));
		assertThat( copy.getStart().getProperties().get(1), samePropertyValuesAs(dao.getStart().getProperties().get(1)));

		assertThat( copy.getFinish().getProperties(), hasSize(2));
		assertThat( copy.getFinish().getProperties().get(0), samePropertyValuesAs(dao.getFinish().getProperties().get(0)));
		assertThat( copy.getFinish().getProperties().get(1), samePropertyValuesAs(dao.getFinish().getProperties().get(1)));

	}

	@Test
	void testMetaEntityDisplayHintAsJson() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		addProperties(dao);
		MetaEntityDisplayHintDto dhd = new MetaEntityDisplayHintDto();
		List<UUID> propertyKeys = new LinkedList<>();
		
		for(MetaPropertyDto mpd : dao.getProperties()) {
			dhd.getKeys().add(mpd.getKey());
			propertyKeys.add(mpd.getKey());
		}
		dao.setDisplayHint(dhd);

		propertyKeys.forEach( pk -> assertTrue(dhd.getKeys().contains(pk)));
		//assertThat( dhd.getKeys(), containsInAnyOrder(propertyKeys));

		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaEntityDto copy = (MetaEntityDto) Serialise.unmarshalFromJson(asJson, MetaEntityDto.class);

		assertThat( copy.getDisplayHint(), notNullValue());
		propertyKeys.forEach( pk -> assertTrue(copy.getDisplayHint().getKeys().contains(pk)));
		
	}

	@Test
	void testMetaEntityDisplayHintAsXML() throws Exception{
		
		MetaEntityDto dao = createMetaEntityDao();
		addProperties(dao);
		MetaEntityDisplayHintDto dhd = new MetaEntityDisplayHintDto();
		List<UUID> propertyKeys = new LinkedList<>();
		
		for(MetaPropertyDto mpd : dao.getProperties()) {
			dhd.getKeys().add(mpd.getKey());
			propertyKeys.add(mpd.getKey());
		}
		dao.setDisplayHint(dhd);

		propertyKeys.forEach( pk -> assertTrue(dhd.getKeys().contains(pk)));
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaEntityDto copy = (MetaEntityDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy.getDisplayHint(), notNullValue());
		propertyKeys.forEach( pk -> assertTrue(copy.getDisplayHint().getKeys().contains(pk)));
	}

	
	/**
	 * @param dao
	 */
	private void addProperties(MetaPropertyContainerDto dao) {
		MetaPropertyDto prop = new MetaPropertyDto();
		prop.setKey(new UUID());
		prop.setName("value");
		prop.setDescription("A value in the model");
		prop.setTypeKey(MetaPropertyTypes.getBuiltInType("string").getKey());
		prop.setMandatory(true);
		prop.setDefaultValue("fortytwo");
		
		dao.getProperties().add(prop);

		MetaPropertyDto prop2 = new MetaPropertyDto();
		prop2.setKey(new UUID());
		prop2.setName("int value");
		prop2.setDescription("An integer value in the model");
		prop2.setTypeKey(MetaPropertyTypes.getBuiltInType("int").getKey());
		prop2.setMandatory(false);
		prop2.setDefaultValue("42");

		dao.getProperties().add(prop2);

	}

	/**
	 * @return
	 */
	private MetaEntityDto createMetaEntityDao() {
		MetaEntityDto dao = new MetaEntityDto();
		dao.setKey(new UUID());
		dao.setBase(new UUID());
		dao.setName("metaEntity");
		dao.setDescription("metaEntityDescription");
		dao.setAbstract(false);
		
		VersionDto version = createVersion();
		dao.setVersion(version);
		return dao;
	}

	/**
	 * @return
	 */
	private MetaRelationshipDto createMetaRelationshipDao() {
		MetaRelationshipDto dao = new MetaRelationshipDto();
		dao.setKey(new UUID());
		dao.setName("metaRelationship");
		dao.setDescription("metaRelationshipDescription");
		
		MetaRelationshipRestrictionDto restriction = new MetaRelationshipRestrictionDto();
		restriction.setName("NONE");
		dao.setRestriction(restriction);
		
		MetaRoleDto start = new MetaRoleDto();
		start.setKey(new UUID());
		start.setName("start");
		start.setDescription("start description");
		start.setConnects(new UUID());
		start.setMultiplicity("zero one or many");
		dao.setStart(start);

		MetaRoleDto finish = new MetaRoleDto();
		finish.setKey(new UUID());
		finish.setName("finish");
		finish.setDescription("finish description");
		finish.setConnects(new UUID());
		finish.setMultiplicity("zero one or many");
		dao.setFinish(finish);

		VersionDto version = createVersion();
		dao.setVersion(version);
		return dao;
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

}
