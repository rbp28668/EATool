/**
 * 
 */
package alvahouse.eatool.test.repository.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dao.Serialise;
import alvahouse.eatool.repository.dao.VersionDao;
import alvahouse.eatool.repository.dao.metamodel.MetaEntityDao;
import alvahouse.eatool.repository.dao.metamodel.MetaEntityDisplayHintDao;
import alvahouse.eatool.repository.dao.metamodel.MetaPropertyContainerDao;
import alvahouse.eatool.repository.dao.metamodel.MetaPropertyDao;
import alvahouse.eatool.repository.dao.metamodel.MetaRelationshipDao;
import alvahouse.eatool.repository.dao.metamodel.MetaRelationshipRestrictionDao;
import alvahouse.eatool.repository.dao.metamodel.MetaRoleDao;
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
		
		MetaEntityDao dao = createMetaEntityDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaEntityDao copy = (MetaEntityDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testMetaEntityAsJson() throws Exception{
		
		MetaEntityDao dao = createMetaEntityDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaEntityDao copy = (MetaEntityDao) Serialise.unmarshalFromJson(asJson, MetaEntityDao.class);

		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
		
	}

	@Test
	void testMetaEntityWithPropertiesAsXML() throws Exception{
		
		MetaEntityDao dao = createMetaEntityDao();
		addProperties(dao);
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaEntityDao copy = (MetaEntityDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
	}

	@Test
	void testMetaEntityWithPropertiesAsJson() throws Exception{
		
		MetaEntityDao dao = createMetaEntityDao();
		addProperties(dao);
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaEntityDao copy = (MetaEntityDao) Serialise.unmarshalFromJson(asJson, MetaEntityDao.class);

		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
		
	}

	@Test
	void testMetaRelationshipAsXML() throws Exception{
		
		MetaRelationshipDao dao = createMetaRelationshipDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaRelationshipDao copy = (MetaRelationshipDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
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
		
		MetaRelationshipDao dao = createMetaRelationshipDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaRelationshipDao copy = (MetaRelationshipDao) Serialise.unmarshalFromJson(asJson, MetaRelationshipDao.class);

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
		
		MetaRelationshipDao dao = createMetaRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		MetaRelationshipDao copy = (MetaRelationshipDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));

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
		
		MetaRelationshipDao dao = createMetaRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		MetaRelationshipDao copy = (MetaRelationshipDao) Serialise.unmarshalFromJson(asJson, MetaRelationshipDao.class);

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
		
		MetaEntityDao dao = createMetaEntityDao();
		addProperties(dao);
		MetaEntityDisplayHintDao dhd = new MetaEntityDisplayHintDao();
		List<UUID> propertyKeys = new LinkedList<>();
		
		for(MetaPropertyDao mpd : dao.getProperties()) {
			dhd.getKeys().add(mpd.getKey());
			propertyKeys.add(mpd.getKey());
		}
		dao.setDisplayHint(dhd);

		propertyKeys.forEach( pk -> assertTrue(dhd.getKeys().contains(pk)));
		//assertThat( dhd.getKeys(), containsInAnyOrder(propertyKeys));

		String asJson = Serialise.marshalToJSON(dao);
		System.out.println(asJson);
		MetaEntityDao copy = (MetaEntityDao) Serialise.unmarshalFromJson(asJson, MetaEntityDao.class);

		assertThat( copy.getDisplayHint(), notNullValue());
		propertyKeys.forEach( pk -> assertTrue(copy.getDisplayHint().getKeys().contains(pk)));
		
	}

	@Test
	void testMetaEntityDisplayHintAsXML() throws Exception{
		
		MetaEntityDao dao = createMetaEntityDao();
		addProperties(dao);
		MetaEntityDisplayHintDao dhd = new MetaEntityDisplayHintDao();
		List<UUID> propertyKeys = new LinkedList<>();
		
		for(MetaPropertyDao mpd : dao.getProperties()) {
			dhd.getKeys().add(mpd.getKey());
			propertyKeys.add(mpd.getKey());
		}
		dao.setDisplayHint(dhd);

		propertyKeys.forEach( pk -> assertTrue(dhd.getKeys().contains(pk)));
		
		String asXml = Serialise.marshalToXML(dao);
		System.out.println(asXml);
		MetaEntityDao copy = (MetaEntityDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy.getDisplayHint(), notNullValue());
		propertyKeys.forEach( pk -> assertTrue(copy.getDisplayHint().getKeys().contains(pk)));
	}

	
	/**
	 * @param dao
	 */
	private void addProperties(MetaPropertyContainerDao dao) {
		MetaPropertyDao prop = new MetaPropertyDao();
		prop.setKey(new UUID());
		prop.setName("value");
		prop.setDescription("A value in the model");
		prop.setTypeKey(MetaPropertyTypes.getBuiltInType("string").getKey());
		prop.setMandatory(true);
		prop.setDefaultValue("fortytwo");
		
		dao.getProperties().add(prop);

		MetaPropertyDao prop2 = new MetaPropertyDao();
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
	private MetaEntityDao createMetaEntityDao() {
		MetaEntityDao dao = new MetaEntityDao();
		dao.setKey(new UUID());
		dao.setBase(new UUID());
		dao.setName("metaEntity");
		dao.setDescription("metaEntityDescription");
		dao.setAbstract(false);
		
		VersionDao version = createVersion();
		dao.setVersion(version);
		return dao;
	}

	/**
	 * @return
	 */
	private MetaRelationshipDao createMetaRelationshipDao() {
		MetaRelationshipDao dao = new MetaRelationshipDao();
		dao.setKey(new UUID());
		dao.setName("metaRelationship");
		dao.setDescription("metaRelationshipDescription");
		
		MetaRelationshipRestrictionDao restriction = new MetaRelationshipRestrictionDao();
		restriction.setName("NONE");
		dao.setRestriction(restriction);
		
		MetaRoleDao start = new MetaRoleDao();
		start.setKey(new UUID());
		start.setName("start");
		start.setDescription("start description");
		start.setConnects(new UUID());
		start.setMultiplicity("zero one or many");
		dao.setStart(start);

		MetaRoleDao finish = new MetaRoleDao();
		finish.setKey(new UUID());
		finish.setName("finish");
		finish.setDescription("finish description");
		finish.setConnects(new UUID());
		finish.setMultiplicity("zero one or many");
		dao.setFinish(finish);

		VersionDao version = createVersion();
		dao.setVersion(version);
		return dao;
	}

	/**
	 * @return
	 */
	private VersionDao createVersion() {
		VersionDao version = new VersionDao();
		version.setCreateDate(new Date());
		version.setCreateUser("fred");
		version.setOriginalVersion(new UUID());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID());
		return version;
	}

}
