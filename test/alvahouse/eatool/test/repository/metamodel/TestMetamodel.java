/**
 * 
 */
package alvahouse.eatool.test.repository.metamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.memory.MetaModelPersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class TestMetamodel {

	Repository repository;
	MetaPropertyTypes types;
	MetaModelPersistence persistence;
	MetaModel meta;
	UUID meKey1;
	UUID meKey2;
	UUID mrKey;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		UUID.initialise(UUID.findMACAddress(), null);

		persistence = new MetaModelPersistenceMemory();
		ExtensibleTypes extensibleTypes = new ExtensibleTypes(persistence);
		MetaPropertyTypes types = new MetaPropertyTypes(extensibleTypes);
		meta = new MetaModel(persistence, types);
		
		repository = mock(Repository.class);
		when(repository.getTypes()).thenReturn(types);
		when(repository.getMetaModel()).thenReturn(meta);
		
		meKey1 = new UUID();
		meKey2 = new UUID();
		mrKey = new UUID();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Given that I have a meta model when I create and save a meta entity I get one back with basic properties intact.
	 *  
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntity(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testGetMetaEntity() throws Exception{
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		me.setName("fred");
		me.setDescription("fredDescription");
		meta.addMetaEntity(me);
		
		MetaEntity retrieved = meta.getMetaEntity(key);
		assertEquals(me.getName(),retrieved.getName());
		assertEquals(me.getDescription(),retrieved.getDescription());
	}

	/**
	 * Given that I have a meta model when I create and save a meta entity with meta properties I get one back 
	 * with its meta-properties intact.
	 *  
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntity(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testGetMetaEntityWithProperties() throws Exception{
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		
		UUID mpKey = new UUID();
		MetaProperty mp = new MetaProperty(mpKey);
		mp.setName("Fred");
		mp.setDescription("Fred description");
		mp.setDefaultValue("fortyTwo");
		mp.setMetaPropertyType(MetaPropertyTypes.getBuiltInType("string"));
		me.addMetaProperty(mp);
		
		meta.addMetaEntity(me);
		
		MetaEntity retrieved = meta.getMetaEntity(key);
		MetaProperty mpRetrieved = retrieved.getMetaProperty(mpKey);
		assertEquals(mpRetrieved.getName(),"Fred");
		assertEquals(mpRetrieved.getDescription(),"Fred description");
		assertEquals(mpRetrieved.getDefaultValue(),"fortyTwo");
		assertEquals(mpRetrieved.getMetaPropertyType().getName(), "string");
	}


	
	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntities()}.
	 */
	@Test
	public void testGetMetaEntities() throws Exception {
		UUID key1 = new UUID();
		UUID key2 = new UUID();
		MetaEntity me1 = new MetaEntity(key1);
		MetaEntity me2 = new MetaEntity(key2);
		meta.addMetaEntity(me1);
		meta.addMetaEntity(me2);
		
		Collection<MetaEntity> retrieved = meta.getMetaEntities();
		assertEquals(2, retrieved.size());
		assertTrue(retrieved.contains(me1));
		assertTrue(retrieved.contains(me2));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntityCount()}.
	 */
	@Test
	public void testGetMetaEntityCount() throws Exception{
		UUID key1 = new UUID();
		UUID key2 = new UUID();
		MetaEntity me1 = new MetaEntity(key1);
		MetaEntity me2 = new MetaEntity(key2);
		meta.addMetaEntity(me1);
		meta.addMetaEntity(me2);
		
		assertEquals(2, meta.getMetaEntityCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#updateMetaEntity(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testAddMetaEntity() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);
		
		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		me.setName("fred");
		me.setDescription("fredDescription");
		meta.addMetaEntity(me);

		verify(listener, times(1)).metaEntityAdded(any(MetaModelChangeEvent.class));

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#updateMetaEntity(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testUpdateMetaEntity() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);

		UUID key = new UUID();
		
		// Create and save
		MetaEntity me = new MetaEntity(key);
		me.setName("fred");
		me.setDescription("fredDescription");
		meta.addMetaEntity(me);

		// Now update the same one
		me.setName("Jim");
		me.setDescription("JimDescription");
		meta.updateMetaEntity(me);

		// Update should have fired
		verify(listener, times(1)).metaEntityChanged(any(MetaModelChangeEvent.class));
		
		// Make sure it was updated
		MetaEntity retrieved = meta.getMetaEntity(key);
		assertEquals("Jim",retrieved.getName());
		assertEquals("JimDescription",retrieved.getDescription());
		
		// And make sure we can update it again as this time it's
		// a read modify write cycle.
		retrieved.setName("albert");
		retrieved.setDescription("albertDescription");
		meta.updateMetaEntity(retrieved);
		
		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#deleteMetaEntity(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testDeleteMetaEntity() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);

		UUID key = new UUID();
		MetaEntity me = new MetaEntity(key);
		me.setName("fred");
		me.setDescription("fredDescription");
		meta.addMetaEntity(me);
		
		meta.deleteMetaEntity(key,me.getVersion().getVersion());
		
		verify(listener, times(1)).metaEntityDeleted(any(MetaModelChangeEvent.class));

		assertEquals(0, meta.getMetaEntityCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationship(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testGetMetaRelationship() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());

		start.setName("startRole");
		start.setDescription("startDescription");
		start.setMultiplicity(Multiplicity.fromString("zero, one or many"));
		start.setConnection(sme);
		
		finish.setName("finishRole");
		finish.setDescription("finishDescription");
		finish.setMultiplicity(Multiplicity.fromString("many"));
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		MetaRelationship mrRetrieved = meta.getMetaRelationship(key);
		assertEquals("Fred", mrRetrieved.getName());
		assertEquals("FredDescription", mrRetrieved.getDescription());
		
		assertEquals("startRole",mrRetrieved.start().getName());
		assertEquals("startDescription",mrRetrieved.start().getDescription());
		assertEquals("zero, one or many",mrRetrieved.start().getMultiplicity().toString());

		assertEquals("finishRole",mrRetrieved.finish().getName());
		assertEquals("finishDescription",mrRetrieved.finish().getDescription());
		assertEquals("many",mrRetrieved.finish().getMultiplicity().toString());

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#addMetaRelationship(alvahouse.eatool.repository.metamodel.MetaRelationship)}.
	 */
	@Test
	public void testAddMetaRelationship() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);

		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		verify(listener, times(1)).metaRelationshipAdded(any(MetaModelChangeEvent.class));
				
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#updateMetaRelationship(alvahouse.eatool.repository.metamodel.MetaRelationship)}.
	 */
	@Test
	public void testUpdateMetaRelationship() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);

		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);

		mr.setName("Jim");
		mr.setDescription("JimDescription");
		meta.updateMetaRelationship(mr);
		
		verify(listener, times(1)).metaRelationshipChanged(any(MetaModelChangeEvent.class));
		
		MetaRelationship mrRetrieved = meta.getMetaRelationship(key);
		assertEquals("Jim", mrRetrieved.getName());
		assertEquals("JimDescription", mrRetrieved.getDescription());
		
		mrRetrieved.setName("albert");
		mrRetrieved.setDescription("albertDesc");
		meta.updateMetaRelationship(mrRetrieved);
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#deleteMetaRelationship(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testDeleteMetaRelationship() throws Exception{

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);

		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		meta.deleteMetaRelationship(key,mr.getVersion().getVersion());
		
		verify(listener, times(1)).metaRelationshipDeleted(any(MetaModelChangeEvent.class));

		assertEquals(0,meta.getMetaRelationshipCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationships()}.
	 */
	@Test
	public void testGetMetaRelationships() throws Exception{
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		Collection<MetaRelationship> retrieved = meta.getMetaRelationships();
		assertEquals(1, retrieved.size());
		assertTrue(retrieved.contains(mr));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipsAsArray()}.
	 */
	@Test
	public void testGetMetaRelationshipsAsArray() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		MetaRelationship[] retrieved = meta.getMetaRelationshipsAsArray();
		assertEquals(1, retrieved.length);
		assertTrue(retrieved[0].equals(mr));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipCount()}.
	 */
	@Test
	public void testGetMetaRelationshipCount() throws Exception{
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		assertEquals(0,meta.getMetaRelationshipCount());
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		assertEquals(1,meta.getMetaRelationshipCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testGetMetaRelationshipsFor() throws Exception{
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		Set<MetaRelationship> retrieved = meta.getMetaRelationshipsFor(sme);
		assertEquals(retrieved.size(),1);
		assertTrue(retrieved.contains(mr));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getDeclaredMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testGetDeclaredMetaRelationshipsFor() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity derived = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(derived);
		finish.setConnection(fme);
		
		mr.setName("Derived");
		mr.setDescription("DerivedDescription");

		UUID keyBase = new UUID();
		MetaRelationship mrForBase = new MetaRelationship(keyBase);
		MetaRole startForBase = mrForBase.getMetaRole(new UUID());
		MetaRole finishForBase = mrForBase.getMetaRole(new UUID());
		MetaEntity base = new MetaEntity(new UUID());
		MetaEntity other = new MetaEntity(new UUID());
		startForBase.setConnection(base);
		finishForBase.setConnection(other);
		
		mrForBase.setName("Base");
		mrForBase.setDescription("BaseDescription");

		derived.setBase(base);
		
		meta.addMetaEntity(base);
		meta.addMetaEntity(other);
		meta.addMetaEntity(derived);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		meta.addMetaRelationship(mrForBase);
		
		// Derived meta entity should have 2 meta relationships, one through the
		// derived class, one through the base class
		Set<MetaRelationship> retrieved = meta.getMetaRelationshipsFor(derived);
		assertEquals(2,retrieved.size());
		assertTrue(retrieved.contains(mr));
		assertTrue(retrieved.contains(mrForBase));

		// Declared meta relationships should only return the one(s) directly attached to
		// the derived class.
		Set<MetaRelationship> derivedOnly = meta.getDeclaredMetaRelationshipsFor(derived);
		assertEquals(derivedOnly.size(),1);
		assertTrue(derivedOnly.contains(mr));
		assertFalse(derivedOnly.contains(mrForBase));

		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaRelationship)}.
	 */
	@Test
	public void testGetDeleteDependenciesDeleteDependenciesListMetaRelationship() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		DeleteDependenciesList dependencies = new DeleteDependenciesList();
		meta.getDeleteDependencies(dependencies, mr);
		
		assertTrue(dependencies.containsTarget(mr.getKey())); 
		assertFalse(dependencies.containsTarget(sme.getKey()));
		assertFalse(dependencies.containsTarget(fme.getKey())); 
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testGetDeleteDependenciesDeleteDependenciesListMetaEntity() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		DeleteDependenciesList dependencies = new DeleteDependenciesList();
		meta.getDeleteDependencies(dependencies, sme, repository);
		
		assertTrue(dependencies.containsTarget(sme.getKey())); // the item we're deleting
		assertFalse(dependencies.containsTarget(fme.getKey())); // independent now but no reason to delete. 
		assertTrue(dependencies.containsTarget(mr.getKey())); // can't have relationship to missing meta entity.
	}

	@Test
	public void testGetDeleteDependenciesDeleteDependenciesListMetaEntityInheritance() throws Exception {
		MetaEntity base = new MetaEntity(new UUID());
		MetaEntity derived = new MetaEntity(new UUID());

		derived.setBase(base);
		meta.addMetaEntity(base);
		meta.addMetaEntity(derived);
		
		DeleteDependenciesList dependencies = new DeleteDependenciesList();
		meta.getDeleteDependencies(dependencies, base, repository);
		
		assertTrue(dependencies.containsTarget(base.getKey())); // the item we're deleting
		assertTrue(dependencies.containsTarget(derived.getKey())); // and the one derived from it
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#deleteContents()}.
	 */
	@Test
	public void testDeleteContents() throws Exception {
		UUID key = new UUID();
		MetaRelationship mr = new MetaRelationship(key);
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		MetaEntity sme = new MetaEntity(new UUID());
		MetaEntity fme = new MetaEntity(new UUID());
		start.setConnection(sme);
		finish.setConnection(fme);
		
		mr.setName("Fred");
		mr.setDescription("FredDescription");
		
		meta.addMetaEntity(sme);
		meta.addMetaEntity(fme);
		meta.addMetaRelationship(mr);
		
		assertEquals(1, meta.getMetaRelationships().size());
		assertEquals(2, meta.getMetaEntities().size());
		
		meta.deleteContents();
		
		assertEquals(0, meta.getMetaRelationships().size());
		assertEquals(0, meta.getMetaEntities().size());

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#addChangeListener(alvahouse.eatool.repository.metamodel.MetaModelChangeListener)}.
	 */
	@Test
	public void testAddChangeListener() throws Exception{
		
		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);
		assertTrue(meta.isActive(listener));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#removeChangeListener(alvahouse.eatool.repository.metamodel.MetaModelChangeListener)}.
	 */
	@Test
	public void testRemoveChangeListener() {
		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);
		assertTrue(meta.isActive(listener));
		
		meta.removeChangeListener(listener);
		assertFalse(meta.isActive(listener));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#fireModelUpdated()}.
	 */
	@Test
	public void testFireModelUpdated() throws Exception {

		MetaModelChangeListener listener = mock(MetaModelChangeListener.class);
		meta.addChangeListener(listener);
		assertTrue(meta.isActive(listener));

		meta.fireModelUpdated();
		
		verify(listener, times(1)).modelUpdated(any(MetaModelChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getDerivedMetaEntities(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	public void testGetDerivedMetaEntities() throws Exception {
		MetaEntity base = new MetaEntity(new UUID());
		MetaEntity derived = new MetaEntity(new UUID());
		MetaEntity other = new MetaEntity(new UUID());
		derived.setBase(base);
		meta.addMetaEntity(base);
		meta.addMetaEntity(derived);
		meta.addMetaEntity(other);

		Collection<MetaEntity> retrieved = meta.getDerivedMetaEntities(base);
		assertTrue(retrieved.contains(derived));
		assertFalse(retrieved.contains(base));
		assertFalse(retrieved.contains(other));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#getKey()}.
	 * Test method for {@link alvahouse.eatool.repository.metamodel.MetaModel#setKey(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	public void testGetSetKey() throws Exception{
		UUID key = new UUID();
		meta.setKey(key);
		UUID retrieved = meta.getKey();
		assertEquals(key,retrieved);
	}

}
