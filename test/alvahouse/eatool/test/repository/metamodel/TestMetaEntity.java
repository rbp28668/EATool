/**
 * 
 */
package alvahouse.eatool.test.repository.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class TestMetaEntity {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Given a new meta entity when I set key, name & description etc I can retrieve them again
	 */
	@Test
	public void testBasicMetaEntityAttributes() {
		UUID key = new UUID();
		String name = "fred";
		String desc = "description of fred";
		
		MetaEntity me = new MetaEntity(key);
		me.setName(name);
		me.setDescription(desc);
		
		assertEquals(key, me.getKey());
		assertEquals(name, me.getName());
		assertEquals(desc, me.getDescription());
		
		assertEquals(false, me.isAbstract());
		me.setAbstract(true);
		assertEquals(true,me.isAbstract());
		
	}
	
	/*
	 * Given a meta entity, when I add meta properties then I can retrieve them again
	 */
	@Test
	public void testMetaProperties() throws Exception {
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		MetaProperty mp1 = new MetaProperty(new UUID());
		MetaProperty mp2 = new MetaProperty(new UUID());
		
		me.addMetaProperty(mp1);
		me.addMetaProperty(mp2);
		
		MetaProperty retrieved1 = me.getMetaProperty(mp1.getKey());
		MetaProperty retrieved2 = me.getMetaProperty(mp2.getKey());

		assertEquals(mp1, retrieved1);
		assertEquals(mp2, retrieved2);
	}
	
	/*
	 * Given a meta entity, when I add a base meta entity, I can retrieve the properties
	 * if the base as if they belonged to this meta entity.
	 */
	@Test
	public void testUseBaseClass() throws Exception{
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		MetaProperty mp = new MetaProperty(new UUID());
		me.addMetaProperty(mp);
		
		UUID keyBase = new UUID();
		MetaEntity base = new MetaEntity(keyBase);
		MetaProperty mpBase = new MetaProperty(new UUID());
		base.addMetaProperty(mpBase);
		
		me.setBase(base);
		assertEquals(base, me.getBase());
		assertEquals(me.getBaseKey(), keyBase);
		
		MetaProperty mpRetrievedBase = me.getMetaProperty(mpBase.getKey());
		assertEquals(mpRetrievedBase, mpBase);

		assertTrue(me.getMetaProperties().contains(mp));
		assertTrue(me.getMetaProperties().contains(mpBase));
	}

	/*
	 * Given a meta entity, when I add a base meta entity, I can retrieve only the 
	 * properties declared in this meta entity
	 */
	@Test
	public void testGetDeclaredProperties() throws Exception{
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		MetaProperty mp = new MetaProperty(new UUID());
		me.addMetaProperty(mp);
		
		UUID keyBase = new UUID();
		MetaEntity base = new MetaEntity(keyBase);
		MetaProperty mpBase = new MetaProperty(new UUID());
		base.addMetaProperty(mpBase);

		assertTrue(me.getDeclaredMetaProperties().contains(mp));
		assertFalse(me.getDeclaredMetaProperties().contains(mpBase));
	}

	/*
	 * Given a meta entity, when I set a null base key, the meta entity
	 * knows it has a null base and will return the NULL UUID as the base key.
	 */
	@Test
	public void testNullBaseKey() throws Exception{
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		me.setBaseKey(null);;
		
		assertFalse(me.hasBase());
	}

}
