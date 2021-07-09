/**
 * 
 */
package alvahouse.eatool.test.repository.mapping;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappingChangeListener;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.MappingChangeEvent;
import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.repository.persist.memory.ExportMappingPersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestExportMappings {

	private ExportMappingPersistence persistence;
	private ExportMappings mappings;
	private ExportMappingChangeListener listener;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new ExportMappingPersistenceMemory();
		mappings = new ExportMappings(persistence);
		listener = mock(ExportMappingChangeListener.class);
		mappings.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * @return
	 */
	private ExportMapping createMapping() {
		ExportMapping mapping = new ExportMapping();
		mapping.setName("export mapping");
		mapping.setDescription("An export mapping");
		mapping.setComponents(42);
		mapping.setTransformPath("c://temp//bogus.xslt");
		return mapping;
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception{
		ExportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		assertEquals(1, mappings.getExportMappings().size());
		
		mappings.deleteContents();
		
		assertEquals(0, mappings.getExportMappings().size());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#getExportMappings()}.
	 */
	@Test
	void testGetExportMappings() throws Exception {
		ExportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		Collection<ExportMapping> m = mappings.getExportMappings();
		assertEquals(1, m.size());
		objectsEqual(mapping, m.iterator().next());
	}

	
	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#add(alvahouse.eatool.repository.mapping.ExportMapping)}.
	 */
	@Test
	void testAddAndLookup() throws Exception {
		ExportMapping mapping = createMapping();
		UUID key = mapping.getKey();
		
		mappings.add(mapping);
		
		ExportMapping retrieved = mappings.lookupMapping(key);
		objectsEqual(retrieved, mapping);
		verify(listener, times(1)).MappingAdded(any(MappingChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#update(alvahouse.eatool.repository.mapping.ExportMapping)}.
	 */
	@Test
	void testUpdate() throws Exception {
		ExportMapping mapping = createMapping();
		UUID key = mapping.getKey();
		
		mappings.add(mapping);
		
		ExportMapping retrieved = mappings.lookupMapping(key);
		objectsEqual(retrieved, mapping);

		mapping.setName("Updated");
		mapping.setDescription("Updated mapping");
		mappings.update(mapping);
		
		retrieved = mappings.lookupMapping(key);
		objectsEqual(retrieved, mapping);
		verify(listener, times(1)).MappingEdited(any(MappingChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#remove(alvahouse.eatool.repository.mapping.ExportMapping)}.
	 */
	@Test
	void testRemove() throws Exception {
		ExportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		assertEquals(1, mappings.getExportMappings().size());
		
		mappings.remove(mapping);
		
		assertEquals(0, mappings.getExportMappings().size());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#addChangeListener(alvahouse.eatool.repository.mapping.ExportMappingChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		ExportMappingChangeListener l = mock(ExportMappingChangeListener.class);
		
		assertFalse(mappings.isActive(l));
		mappings.addChangeListener(l);
		assertTrue(mappings.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ExportMappings#removeChangeListener(alvahouse.eatool.repository.mapping.ExportMappingChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		ExportMappingChangeListener l = mock(ExportMappingChangeListener.class);
		
		assertFalse(mappings.isActive(l));
		mappings.addChangeListener(l);
		assertTrue(mappings.isActive(l));
		
		mappings.removeChangeListener(l);
		assertFalse(mappings.isActive(l));
	}

}
