/**
 * 
 */
package alvahouse.eatool.test.repository.metamodel;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestMetaRelationship {

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

	/*
	 * Given a new meta relationship when I set key, name & description etc I can retrieve them again
	 */
	@Test
	public void testBasicMetaRelationshipAttributes() {
		UUID key = new UUID();
		String name = "fred";
		String desc = "description of fred";
		
		MetaRelationship mr = new MetaRelationship(key);
		mr.setName(name);
		mr.setDescription(desc);
		
		assertEquals(key, mr.getKey());
		assertEquals(name, mr.getName());
		assertEquals(desc, mr.getDescription());
		
		
	}

	/*
	 * Given a new meta relationship when I set key, name & description etc I can retrieve them again
	 */
	@Test
	public void testMetaRoleAttributes() {
		UUID key = new UUID();
		String name = "Relationship";
		String desc = "description of relationship";
		
		MetaRelationship mr = new MetaRelationship(key);
		mr.setName(name);
		mr.setDescription(desc);
		
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		start.setName("startRole");
		start.setDescription("startDescription");
		start.setMultiplicity(Multiplicity.fromString("zero, one or many"));
		
		finish.setName("finishRole");
		finish.setDescription("finishDescription");
		finish.setMultiplicity(Multiplicity.fromString("many"));
		
		assertEquals(mr.start().getName(),"startRole");
		assertEquals(mr.start().getDescription(),"startDescription");
		assertEquals(mr.start().getMultiplicity().toString(),"zero, one or many");
		
		assertEquals(mr.finish().getName(),"finishRole");
		assertEquals(mr.finish().getDescription(),"finishDescription");
		assertEquals(mr.finish().getMultiplicity().toString(),"many");
	}

	@Test
	public void testMetaRelationshipProperties() throws Exception {
		
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaProperty mp1 = new MetaProperty(new UUID());
		MetaProperty mp2 = new MetaProperty(new UUID());
		
		mr.addMetaProperty(mp1);
		mr.addMetaProperty(mp2);
		
		MetaProperty retrieved1 = mr.getMetaProperty(mp1.getKey());
		MetaProperty retrieved2 = mr.getMetaProperty(mp2.getKey());

		assertEquals(mp1, retrieved1);
		assertEquals(mp2, retrieved2);

	}

	@Test
	public void testMetaRoleProperties() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaProperty mp1 = new MetaProperty(new UUID());
		MetaProperty mp2 = new MetaProperty(new UUID());
		
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		start.addMetaProperty(mp1);
		finish.addMetaProperty(mp2);
		
		MetaProperty retrieved1 = mr.start().getMetaProperty(mp1.getKey());
		MetaProperty retrieved2 = mr.finish().getMetaProperty(mp2.getKey());

		assertEquals(mp1, retrieved1);
		assertEquals(mp2, retrieved2);
		
	}


}
