/**
 * 
 */
package alvahouse.eatool.test.repository.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.hamcrest.core.IsNull.notNullValue;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.memory.MetaModelPersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestEntity {

	Repository repository;
	
	MetaPropertyTypes types;
	MetaModelPersistence metaPersistence;
	MetaModel meta;
	MetaEntity base;
	MetaEntity derived;
	MetaEntity other;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		initialiseMetaModel();
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
		
		derived = new MetaEntity(new UUID());
		derived.setName("derived");
		derived.setBase(base);
		derived.addMetaProperty(createMetaProperty("derived int", "int", derived));
		derived.addMetaProperty(createMetaProperty("derived string", "string", derived));
		
		other = new MetaEntity(new UUID());
		other.setName("other");
		other.addMetaProperty(createMetaProperty("other int", "int", other));
		other.addMetaProperty(createMetaProperty("other string", "string", other));
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
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	
	// Given a meta entity, when I create an entity from it, I expect the entity to have properties with
	// number, types and keys that match the meta entity
	@Test
	void testCreateFromMetaEntity() throws Exception {
		Entity e = new Entity(other);
		
		assertEquals(e.getProperties().size(), other.getMetaProperties().size());
		other.getMetaProperties().forEach( mp -> {
			assertThat(e.getPropertyByMeta(mp.getKey()), notNullValue());
		});
	}

}
