/**
 * 
 */
package alvahouse.eatool.test.repository.graphical;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.gui.graphical.time.TimeDiagramType;
import alvahouse.eatool.gui.graphical.time.TimeDiagramTypeFamily;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.DiagramsChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramsChangeListener;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramTypeFamily;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.repository.persist.memory.DiagramTypePersistenceMemory;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestDiagramTypes {

	private Repository repository;
	private DiagramTypePersistence typePersistence;
	private DiagramTypes diagramTypes;
	private DiagramsChangeListener listener;
	private DiagramTypeFamily standardFamily;
	private DiagramTypeFamily timeFamily;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		repository = mock(Repository.class);
		typePersistence = new DiagramTypePersistenceMemory();
		diagramTypes = new DiagramTypes(repository,typePersistence);

		standardFamily = new StandardDiagramTypeFamily();
		standardFamily.setParent(diagramTypes);
		diagramTypes.addDiagramFamily(standardFamily);

		timeFamily = new TimeDiagramTypeFamily();
		timeFamily.setParent(diagramTypes);
		diagramTypes.addDiagramFamily(timeFamily);

		listener = mock(DiagramsChangeListener.class);
		diagramTypes.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}


	


	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#add(alvahouse.eatool.repository.graphical.DiagramType)}.
	 */
	@Test
	void testAdd() throws Exception {
		
		DiagramType standardType = new StandardDiagramType(repository,"test type", new UUID());
		standardType.setFamily(standardFamily);

		diagramTypes.add(standardType);

		Collection<DiagramType> retrieved = diagramTypes.getDiagramTypes();
		assertEquals(1,retrieved.size());
		
		DiagramType t = retrieved.iterator().next();
		objectsEqual(t, standardType);
		
		verify(listener, times(1)).diagramTypeAdded(any(DiagramsChangeEvent.class));

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#update(alvahouse.eatool.repository.graphical.DiagramType)}.
	 */
	@Test
	void testUpdate() throws Exception {
		
		DiagramType standardType = new StandardDiagramType(repository, "test type", new UUID());
		standardType.setFamily(standardFamily);
		diagramTypes.add(standardType);

		Collection<DiagramType> retrieved = diagramTypes.getDiagramTypes();
		assertEquals(1,retrieved.size());
		
		standardType.setName("modified");
		diagramTypes.update(standardType);

		retrieved = diagramTypes.getDiagramTypes();
		DiagramType t = retrieved.iterator().next();
		assertEquals(1,retrieved.size());
		objectsEqual(t, standardType);
		
		verify(listener, times(1)).diagramTypeChanged(any(DiagramsChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#delete(alvahouse.eatool.repository.graphical.DiagramType)}.
	 */
	@Test
	void testDelete() throws Exception {
		DiagramType standardType = new StandardDiagramType(repository,"test type", new UUID());
		standardType.setFamily(standardFamily);
		diagramTypes.add(standardType);

		Collection<DiagramType> retrieved = diagramTypes.getDiagramTypes();
		assertEquals(1,retrieved.size());
		
		diagramTypes.delete(standardType);

		retrieved = diagramTypes.getDiagramTypes();
		assertTrue(retrieved.isEmpty());
		
		verify(listener, times(1)).diagramTypeDeleted(any(DiagramsChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#get(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testGet() throws Exception {
		DiagramType standardType = new StandardDiagramType(repository,"test type", new UUID());
		standardType.setFamily(standardFamily);

		diagramTypes.add(standardType);

		DiagramType t = diagramTypes.get(standardType.getKey());
		objectsEqual(t, standardType);
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#getDiagramTypes()}.
	 */
	@Test
	void testGetDiagramTypes() throws Exception {
		
		Collection<DiagramType> retrieved; 
		
		retrieved = diagramTypes.getDiagramTypes();
		assertEquals(0,retrieved.size());

		DiagramType standardType = new StandardDiagramType(repository, "test type", new UUID());
		standardType.setFamily(standardFamily);
		diagramTypes.add(standardType);

		retrieved = diagramTypes.getDiagramTypes();
		assertEquals(1,retrieved.size());

		standardType = new StandardDiagramType(repository, "test type 2", new UUID());
		standardType.setFamily(standardFamily);
		diagramTypes.add(standardType);

		
		retrieved = diagramTypes.getDiagramTypes();
		assertEquals(2,retrieved.size());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#addChangeListener(alvahouse.eatool.repository.graphical.DiagramsChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		DiagramsChangeListener l = mock(DiagramsChangeListener.class);
		
		assertFalse(diagramTypes.isActive(l));
		diagramTypes.addChangeListener(l);
		assertTrue(diagramTypes.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#removeChangeListener(alvahouse.eatool.repository.graphical.DiagramsChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		DiagramsChangeListener l = mock(DiagramsChangeListener.class);
		
		assertFalse(diagramTypes.isActive(l));
		diagramTypes.addChangeListener(l);
		assertTrue(diagramTypes.isActive(l));
		
		diagramTypes.removeChangeListener(l);
		assertFalse(diagramTypes.isActive(l));
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception {
		
		Collection<DiagramType> retrieved; 
		
		DiagramType standardType = new StandardDiagramType(repository,"test type", new UUID());
		standardType.setFamily(standardFamily);
		diagramTypes.add(standardType);

		retrieved = diagramTypes.getDiagramTypes();
		assertEquals(1,retrieved.size());

		diagramTypes.deleteContents();

		retrieved = diagramTypes.getDiagramTypes();
		assertEquals(0,retrieved.size());
	}



	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#getDiagramTypesOfFamily(alvahouse.eatool.repository.graphical.DiagramTypeFamily)}.
	 */
	@Test
	void testGetDiagramTypesOfFamily() throws Exception {
		

		DiagramTypeFamily standardFamily = diagramTypes.lookupFamily(StandardDiagramTypeFamily.FAMILY_KEY);
		DiagramType standardType = new StandardDiagramType(repository, "test type", new UUID());
		standardFamily.add(standardType);

		standardType = new StandardDiagramType(repository, "test type 2", new UUID());
		standardFamily.add(standardType);

		DiagramTypeFamily timeFamily = diagramTypes.lookupFamily(TimeDiagramTypeFamily.FAMILY_KEY);
		DiagramType timeType = new TimeDiagramType();
		timeFamily.add(timeType);

		// Should have 3 diagram types
		assertEquals(3, diagramTypes.getDiagramTypes().size());

		// 2 of standard
		Collection<DiagramType> standard = diagramTypes.getDiagramTypesOfFamily(standardFamily);
		assertEquals(2, standard.size());

		// 1 of time
		Collection<DiagramType> time = diagramTypes.getDiagramTypesOfFamily(timeFamily);
		assertEquals(1, time.size());
		
		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#deleteDiagramTypesOfFamily(alvahouse.eatool.repository.graphical.DiagramTypeFamily)}.
	 */
	@Test
	void testDeleteDiagramTypesOfFamily()  throws Exception {

		DiagramTypeFamily standardFamily = diagramTypes.lookupFamily(StandardDiagramTypeFamily.FAMILY_KEY);
		DiagramType standardType = new StandardDiagramType(repository, "test type", new UUID());
		standardFamily.add(standardType);

		standardType = new StandardDiagramType(repository, "test type 2", new UUID());
		standardFamily.add(standardType);

		DiagramTypeFamily timeFamily = diagramTypes.lookupFamily(TimeDiagramTypeFamily.FAMILY_KEY);
		DiagramType timeType = new TimeDiagramType();
		timeFamily.add(timeType);
		
		diagramTypes.deleteDiagramTypesOfFamily(standardFamily);
		
		// Delete standard types, then just 1 left of time type
		Collection<DiagramType> standard = diagramTypes.getDiagramTypesOfFamily(standardFamily);
		assertEquals(0, standard.size());

		Collection<DiagramType> time = diagramTypes.getDiagramTypesOfFamily(timeFamily);
		assertEquals(1, time.size());
		
		assertEquals(1, diagramTypes.getDiagramTypes().size());

		
	}

}
