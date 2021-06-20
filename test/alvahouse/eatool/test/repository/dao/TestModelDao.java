/**
 * 
 */
package alvahouse.eatool.test.repository.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dao.Serialise;
import alvahouse.eatool.repository.dao.VersionDao;
import alvahouse.eatool.repository.dao.model.EntityDao;
import alvahouse.eatool.repository.dao.model.PropertyContainerDao;
import alvahouse.eatool.repository.dao.model.PropertyDao;
import alvahouse.eatool.repository.dao.model.RelationshipDao;
import alvahouse.eatool.repository.dao.model.RoleDao;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestModelDao {

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
	void testEntityAsXML() throws Exception{
		
		EntityDao dao = createEntityDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		EntityDao copy = (EntityDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testEntityAsJson() throws Exception{
		
		EntityDao dao = createEntityDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		EntityDao copy = (EntityDao) Serialise.unmarshalFromJson(asJson, EntityDao.class);

		assertThat( copy, samePropertyValuesAs(dao, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
		
	}

	@Test
	void testEntityWithPropertiesAsXML() throws Exception{
		
		EntityDao dao = createEntityDao();
		addProperties(dao);
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		EntityDao copy = (EntityDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
	}

	@Test
	void testEntityWithPropertiesAsJson() throws Exception{
		
		EntityDao dao = createEntityDao();
		addProperties(dao);
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		EntityDao copy = (EntityDao) Serialise.unmarshalFromJson(asJson, EntityDao.class);

		assertThat( copy.getProperties(), hasSize(2));
		assertThat( copy.getProperties().get(0), samePropertyValuesAs(dao.getProperties().get(0)));
		assertThat( copy.getProperties().get(1), samePropertyValuesAs(dao.getProperties().get(1)));
		
	}

	
	@Test
	void testRelationshipAsXML() throws Exception{
		
		RelationshipDao dao = createRelationshipDao();
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		RelationshipDao copy = (RelationshipDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dao, "version", "start", "finish", "restriction"));
		assertThat( copy.getStart(), notNullValue());
		assertThat( copy.getFinish(), notNullValue());
		assertThat( copy.getStart(), samePropertyValuesAs(dao.getStart()));
		assertThat( copy.getFinish(), samePropertyValuesAs(dao.getFinish()));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}


	@Test
	void testRelationshipAsJson() throws Exception{
		
		RelationshipDao dao = createRelationshipDao();
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		RelationshipDao copy = (RelationshipDao) Serialise.unmarshalFromJson(asJson, RelationshipDao.class);

		assertThat( copy, samePropertyValuesAs(dao, "version", "start", "finish", "restriction"));
		assertThat( copy.getStart(), notNullValue());
		assertThat( copy.getFinish(), notNullValue());
		assertThat( copy.getStart(), samePropertyValuesAs(dao.getStart()));
		assertThat( copy.getFinish(), samePropertyValuesAs(dao.getFinish()));
		assertThat( copy.getVersion(), samePropertyValuesAs(dao.getVersion()));
	}

	@Test
	void testRelationshipWithPropertiesAsXML() throws Exception{
		
		RelationshipDao dao = createRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asXml = Serialise.marshalToXML(dao);
		//System.out.println(asXml);
		RelationshipDao copy = (RelationshipDao) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));

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
	void testRelationshipWithPropertiesAsJson() throws Exception{
		
		RelationshipDao dao = createRelationshipDao();
		addProperties(dao);
		addProperties(dao.getStart());
		addProperties(dao.getFinish());
		
		String asJson = Serialise.marshalToJSON(dao);
		//System.out.println(asJson);
		RelationshipDao copy = (RelationshipDao) Serialise.unmarshalFromJson(asJson, RelationshipDao.class);

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

	
	
	/**
	 * @return
	 */
	private EntityDao createEntityDao() {
		EntityDao dao = new EntityDao();
		dao.setKey(new UUID());
		dao.setMetaEntityKey(new UUID());
		dao.setVersion(createVersion()); 
		return dao;
	}
	
	/**
	 * @return
	 */
	private RelationshipDao createRelationshipDao() {
		RelationshipDao dao = new RelationshipDao();
		dao.setKey(new UUID());
		dao.setMetaRelationshipKey(new UUID());
		dao.setVersion(createVersion()); 
		
		RoleDao start = new RoleDao();
		start.setKey(new UUID());
		start.setConnects(new UUID());
		dao.setStart(start);
		
		RoleDao finish = new RoleDao();
		finish.setKey(new UUID());
		finish.setConnects(new UUID());
		dao.setFinish(finish);
		
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

	private void addProperties(PropertyContainerDao container) {
		PropertyDao p1 = new PropertyDao();
		p1.setKey(new UUID());
		p1.setMetaPropertyKey(new UUID());
		p1.setValue("p1-value");
		container.getProperties().add(p1);

		PropertyDao p2 = new PropertyDao();
		p2.setKey(new UUID());
		p2.setMetaPropertyKey(new UUID());
		p2.setValue("p2-value");
		container.getProperties().add(p2);

	}
}
