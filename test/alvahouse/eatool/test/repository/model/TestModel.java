/**
 * 
 */
package alvahouse.eatool.test.repository.model;

import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.repository.persist.memory.MetaModelPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ModelPersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestModel {

	Repository repository;
	
	MetaPropertyTypes types;
	MetaModelPersistence metaPersistence;
	MetaModel meta;
	MetaEntity base;
	MetaEntity derived;
	MetaEntity other;
	MetaRelationship mr;
	MetaRelationship mr2;
	
	ModelPersistence persistence;
	Model model;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		initialiseMetaModel();
		initialiseModel();
	}

	/**
	 * 
	 */
	private void initialiseMetaModel() throws Exception {
		metaPersistence = new MetaModelPersistenceMemory();
		ExtensibleTypes extensibleTypes = new ExtensibleTypes(metaPersistence);
		MetaPropertyTypes types = new MetaPropertyTypes(extensibleTypes);
		meta = new MetaModel(metaPersistence, types);
		
		repository = mock(Repository.class);
		when(repository.getTypes()).thenReturn(types);
		when(repository.getMetaModel()).thenReturn(meta);
		
		base = new MetaEntity(new UUID());
		base.setName("base");
		base.setAbstract(true);
		base.addMetaProperty(createMetaProperty("base int", "int", base));
		base.addMetaProperty(createMetaProperty("base string", "string", base));
		meta.addMetaEntity(base);
		
		derived = new MetaEntity(new UUID());
		derived.setName("derived");
		derived.setBase(base);
		derived.addMetaProperty(createMetaProperty("derived int", "int", derived));
		derived.addMetaProperty(createMetaProperty("derived string", "string", derived));
		meta.addMetaEntity(derived);
		
		other = new MetaEntity(new UUID());
		other.setName("other");
		other.addMetaProperty(createMetaProperty("other int", "int", other));
		other.addMetaProperty(createMetaProperty("other string", "string", other));
		meta.addMetaEntity(other);
		
		mr = new MetaRelationship(new UUID());
		MetaRole start = mr.getMetaRole(new UUID());
		MetaRole finish = mr.getMetaRole(new UUID());
		start.setConnection(derived);
		finish.setConnection(other);
		meta.addMetaRelationship(mr);
		
		mr2 = new MetaRelationship(new UUID());
		MetaRole start2 = mr2.getMetaRole(new UUID());
		MetaRole finish2 = mr2.getMetaRole(new UUID());
		start2.setConnection(derived);
		finish2.setConnection(other);
		meta.addMetaRelationship(mr2);
	}

	/**
	 * @param string
	 * @param string2
	 * @param base2
	 * @return
	 */
	private MetaProperty createMetaProperty(String name, String type, MetaPropertyContainer container) {
		MetaProperty mp = new MetaProperty(new UUID());
		mp.setName(name);
		mp.setMetaPropertyType(MetaPropertyTypes.getBuiltInType(type));
		mp.setContainer(container);
		return mp;
	}

	/**
	 * 
	 */
	private void initialiseModel() throws Exception {
		persistence = new ModelPersistenceMemory();
		model = new Model(meta, persistence);
		when(repository.getModel()).thenReturn(model);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}





	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getMeta()}.
	 */
	@Test
	void testGetMeta() {
		MetaModel meta = model.getMeta();
		assertTrue(meta == this.meta);
	}

	/**
	* Given I create an entity when I add it then I can retrieve it by key.
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getEntity(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testGetEntity() throws Exception{
		Entity e = new Entity(other);
		
		MetaProperty mpInt = null;
		MetaProperty mpString = null;
		for(MetaProperty mp : other.getMetaProperties()) {
			if(mp.getName().equals("other int")) mpInt = mp;
			if(mp.getName().equals("other string")) mpString = mp;
		}
			
		e.getPropertyByMeta(mpInt.getKey()).setValue("42");
		e.getPropertyByMeta(mpString.getKey()).setValue("some random string");
		
		model.addEntity(e);
		
		Entity retrieved = model.getEntity(e.getKey());
		assertEquals(e.getPropertyCount(), retrieved.getPropertyCount());
		e.getProperties().forEach( p -> assertTrue(retrieved.getProperties().contains(p)));
		assertEquals(retrieved.getPropertyByMeta(mpInt.getKey()).getValue(), "42");
		assertEquals(retrieved.getPropertyByMeta(mpString.getKey()).getValue(), "some random string");
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#updateEntity(alvahouse.eatool.repository.model.Entity)}.
	 */
	@Test
	void testUpdateEntity() throws Exception{
		Entity e = new Entity(other);
		
		MetaProperty mpInt = null;
		MetaProperty mpString = null;
		for(MetaProperty mp : other.getMetaProperties()) {
			if(mp.getName().equals("other int")) mpInt = mp;
			if(mp.getName().equals("other string")) mpString = mp;
		}
			
		e.getPropertyByMeta(mpInt.getKey()).setValue("42");
		e.getPropertyByMeta(mpString.getKey()).setValue("some random string");
		
		model.addEntity(e);

		e.getPropertyByMeta(mpInt.getKey()).setValue("24");
		e.getPropertyByMeta(mpString.getKey()).setValue("another random string");

		model.updateEntity(e);
		
		Entity retrieved = model.getEntity(e.getKey());
		assertEquals(e.getPropertyCount(), retrieved.getPropertyCount());
		e.getProperties().forEach( p -> assertTrue(retrieved.getProperties().contains(p)));
		assertEquals(retrieved.getPropertyByMeta(mpInt.getKey()).getValue(), "24");
		assertEquals(retrieved.getPropertyByMeta(mpString.getKey()).getValue(), "another random string");
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getEntities()}.
	 */
	@Test
	void testGetEntities() throws Exception{
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		model.addEntity(o);
		model.addEntity(d);
		
		Collection<Entity> entities = model.getEntities();
		assertEquals(2, entities.size());
		assertTrue(entities.contains(o));
		assertTrue(entities.contains(d));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getEntityCount()}.
	 */
	@Test
	void testGetEntityCount() throws Exception {
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		model.addEntity(o);
		model.addEntity(d);
		
		assertEquals(2, model.getEntityCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getEntitiesOfType(alvahouse.eatool.repository.metamodel.MetaEntity)}.
	 */
	@Test
	void testGetEntitiesOfType()  throws Exception{
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		model.addEntity(o);
		model.addEntity(d);
		
		Collection<Entity> entities = model.getEntitiesOfType(other);
		assertEquals(1, entities.size());
		assertTrue(entities.contains(o));
		assertFalse(entities.contains(d));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getRelationship(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testGetRelationship() throws Exception{

		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);
		
		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);

		Relationship retrieved = model.getRelationship(r.getKey());
		assertEquals(retrieved.start().connectionKey(), o.getKey());
		assertEquals(retrieved.finish().connectionKey(), d.getKey());
		
		
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#updateRelationship(alvahouse.eatool.repository.model.Relationship)}.
	 */
	@Test
	void testUpdateRelationship() throws Exception {
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);
		
		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);

		Entity o2 = new Entity(other);
		model.addEntity(o2);
		finish.setConnection(o2);
		model.updateRelationship(r);
		
		Relationship retrieved = model.getRelationship(r.getKey());
		
		assertEquals(retrieved.start().connectionKey(), o.getKey());
		assertEquals(retrieved.finish().connectionKey(), o2.getKey());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#deleteRelationship(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testDeleteRelationship() throws Exception {
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);
		
		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);
		
		assertEquals(1, model.getRelationshipCount());
		
		model.deleteRelationship(r.getKey());
		
		assertEquals(0, model.getRelationshipCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getRelationships()}.
	 */
	@Test
	void testGetRelationships() throws Exception {

		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);
		
		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);
		
		Collection<Relationship> relationships = model.getRelationships();
		assertEquals(1, relationships.size());
		assertTrue(relationships.contains(r));
		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getRelationshipCount()}.
	 */
	@Test
	void testGetRelationshipCount() throws Exception {
		Entity o = new Entity(other);
		Entity d = new Entity(derived);
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		assertEquals(0, model.getRelationshipCount());

		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);
		
		assertEquals(1, model.getRelationshipCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getRelationshipsOfType(alvahouse.eatool.repository.metamodel.MetaRelationship)}.
	 */
	@Test
	void testGetRelationshipsOfType() throws Exception{
		Entity o = new Entity(other);
		Entity d = new Entity(derived);

		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		Collection<Relationship> ofType = model.getRelationshipsOfType(mr);
		
		assertEquals(1, ofType.size());
		assertTrue(ofType.contains(r));
		assertFalse(ofType.contains(r2));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getConnectedRelationships(alvahouse.eatool.repository.model.Entity)}.
	 */
	@Test
	void testGetConnectedRelationships() throws Exception{
		
		Entity o = new Entity(other);
		Entity o2 = new Entity(other);
		Entity d = new Entity(derived);

		// r from o to d
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		// r2 from o2 to d
		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o2);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(o2);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		// r is only connect to o
		Collection<Relationship> relationships = model.getConnectedRelationships(o);
		assertEquals(1, relationships.size());
		assertTrue(relationships.contains(r));
		assertFalse(relationships.contains(r2));

		// r2 is only connected to o2
		relationships = model.getConnectedRelationships(o2);
		assertEquals(1, relationships.size());
		assertFalse(relationships.contains(r));
		assertTrue(relationships.contains(r2));

		// d is connected to both r and r2
		relationships = model.getConnectedRelationships(d);
		assertEquals(2, relationships.size());
		assertTrue(relationships.contains(r));
		assertTrue(relationships.contains(r2));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getConnectedRelationshipsOf(alvahouse.eatool.repository.model.Entity, alvahouse.eatool.repository.metamodel.MetaRelationship)}.
	 */
	@Test
	void testGetConnectedRelationshipsOf() throws Exception{
		Entity o = new Entity(other);
		Entity o2 = new Entity(other);
		Entity d = new Entity(derived);

		// r from o to d
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		// r2 from o2 to d
		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o2);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(o2);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		// d is connected to o and o2 via r and r2 respectively but only get type mr.
		Collection<Relationship> relationships = model.getConnectedRelationshipsOf(d, mr);
		assertEquals(1, relationships.size());
		assertTrue(relationships.contains(r));
		assertFalse(relationships.contains(r2));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.model.Relationship)}.
	 */
	@Test
	void testGetDeleteDependenciesDeleteDependenciesListRelationship() throws Exception{
		
		Entity o = new Entity(other);
		Entity o2 = new Entity(other);
		Entity d = new Entity(derived);

		// r from o to d
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		// r2 from o2 to d
		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o2);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(o2);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		DeleteDependenciesList dependencies = new DeleteDependenciesList();
		model.getDeleteDependencies(dependencies, r);
		
		// The only dependency on deleting a relationship is itself.
		UUID targets[] = dependencies.getDependencyTargets();
		assertEquals(1, targets.length);
		assertEquals(targets[0], r.getKey());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.model.Entity)}.
	 */
	@Test
	void testGetDeleteDependenciesDeleteDependenciesListEntity() throws Exception {
		Entity o = new Entity(other);
		Entity o2 = new Entity(other);
		Entity d = new Entity(derived);

		// r from o to d
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		// r2 from o2 to d
		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o2);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(o2);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		// Take out d which also has 2 relationships
		DeleteDependenciesList dependencies = new DeleteDependenciesList();
		model.getDeleteDependencies(dependencies, d, repository);
		
		// The only dependency on deleting a relationship is itself.
		UUID targets[] = dependencies.getDependencyTargets();
		assertEquals(3, targets.length);
		assertThat(targets, hasItemInArray(d.getKey()));
		assertThat(targets, hasItemInArray(r.getKey()));
		assertThat(targets, hasItemInArray(r2.getKey()));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception{
		Entity o = new Entity(other);
		Entity o2 = new Entity(other);
		Entity d = new Entity(derived);

		// r from o to d
		Relationship r = new Relationship(mr);
		Role start = new Role(mr.start());
		Role finish = new Role(mr.finish());
		start.setConnection(o);
		finish.setConnection(d);
		r.setStart(start);
		r.setFinish(finish);

		// r2 from o2 to d
		Relationship r2 = new Relationship(mr2);
		Role start2 = new Role(mr2.start());
		Role finish2 = new Role(mr2.finish());
		start2.setConnection(o2);
		finish2.setConnection(d);
		r2.setStart(start2);
		r2.setFinish(finish2);

		model.addEntity(o);
		model.addEntity(o2);
		model.addEntity(d);
		model.addRelationship(r);
		model.addRelationship(r2);
		
		assertEquals(3, model.getEntityCount());
		assertEquals(2, model.getRelationshipCount());
		
		model.deleteContents();
		
		assertEquals(0, model.getEntityCount());
		assertEquals(0, model.getRelationshipCount());
		assertTrue(model.getEntities().isEmpty());
		assertTrue(model.getRelationships().isEmpty());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#addChangeListener(alvahouse.eatool.repository.model.ModelChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		ModelChangeListener listener = mock(ModelChangeListener.class);
		model.addChangeListener(listener);
		assertTrue(model.isActive(listener));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#removeChangeListener(alvahouse.eatool.repository.model.ModelChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		ModelChangeListener listener = mock(ModelChangeListener.class);
		model.addChangeListener(listener);
		assertTrue(model.isActive(listener));
		
		model.removeChangeListener(listener);
		assertFalse(model.isActive(listener));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#fireModelUpdated()}.
	 */
	@Test
	void testFireModelUpdated() throws Exception{
		ModelChangeListener listener = mock(ModelChangeListener.class);
		model.addChangeListener(listener);
		assertTrue(model.isActive(listener));

		model.fireModelUpdated();
		
		verify(listener, times(1)).modelUpdated(any(ModelChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.model.Model#getKey()}.
	 * Test method for {@link alvahouse.eatool.repository.model.Model#setKey(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testGetSetKey() {
		UUID key = new UUID();
		model.setKey(key);
		
		assertEquals(key, model.getKey());

	}



}
