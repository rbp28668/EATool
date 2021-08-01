/**
 * 
 */
package alvahouse.eatool.test.repository.graphical;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.gui.graphical.time.TimeDiagramType;
import alvahouse.eatool.gui.graphical.time.TimeDiagramTypeFamily;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.graphical.DiagramsChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramsChangeListener;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramTypeFamily;
import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.repository.persist.memory.DiagramPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.DiagramTypePersistenceMemory;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestDiagrams {
	private DiagramPersistence persistence;
	private DiagramTypePersistence typePersistence;
	private DiagramTypes allTypes;
	private Diagrams diagrams;
	private DiagramType diagramType;
	private DiagramTypeFamily family;
	private DiagramsChangeListener listener;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new DiagramPersistenceMemory();
		typePersistence = new DiagramTypePersistenceMemory();
		
		diagrams = new Diagrams(persistence);
		diagramType = new StandardDiagramType("test type", new UUID());
		allTypes = new DiagramTypes(typePersistence);
		family = new StandardDiagramTypeFamily();
		family.setParent(allTypes);
		diagramType.setFamily(family);
		family.add(diagramType);
		listener = mock(DiagramsChangeListener.class);
		diagrams.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#lookup(alvahouse.eatool.util.UUID)}.
	 * @throws Exception 
	 */
	@Test
	void testAddAndLookup() throws Exception {
		Diagram diagram = createDiagram();
		diagrams.add(diagram);
		
		Diagram retrieved = diagrams.lookup(diagram.getKey());
		
		assertThat( retrieved, samePropertyValuesAs(diagram, "version", "eventMap", "nodes", "symbols","connectors", "images","textBoxes"));
		objectsEqual(retrieved, diagram);
		verify(listener, times(1)).diagramAdded(any(DiagramsChangeEvent.class));
		
	}

	/**
	 * @return
	 */
	private Diagram createDiagram() {
		StandardDiagram diagram = new StandardDiagram(diagramType, new UUID());
		diagram.setName("Standard");
		diagram.setDescription("A standard diagram");
		diagram.setBackgroundColour(Color.red);
		diagram.setDynamic(false);
		return diagram;
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#update(alvahouse.eatool.repository.graphical.Diagram)}.
	 * @throws Exception 
	 */
	@Test
	void testUpdate() throws Exception {
		Diagram diagram = createDiagram();
		UUID key = diagram.getKey();
		
		diagrams.add(diagram);
		
		Diagram retrieved = diagrams.lookup(key);
		assertThat( retrieved, samePropertyValuesAs(diagram, "version", "eventMap", "nodes", "symbols","connectors", "images","textBoxes"));
		objectsEqual(retrieved, diagram);
		
		diagram.setName("updated diagram");
		diagram.setDescription("updated standard diagram");
		diagram.setBackgroundColour(Color.blue);
		
		diagrams.update(diagram);
		
		retrieved = diagrams.lookup(key);
		assertThat( retrieved, samePropertyValuesAs(diagram, "version", "eventMap", "nodes", "symbols","connectors", "images","textBoxes"));
		objectsEqual(retrieved, diagram);
		verify(listener, times(1)).diagramChanged(any(DiagramsChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#newDiagramOfType(alvahouse.eatool.repository.graphical.DiagramType)}.
	 * @throws Exception 
	 */
	@Test
	void testNewDiagramOfType() throws Exception {
		Diagram diagram = diagrams.newDiagramOfType(diagramType);
		assertThat( diagram, instanceOf(StandardDiagram.class));
		diagrams.add(diagram);
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#removeDiagram(alvahouse.eatool.repository.graphical.Diagram)}.
	 * @throws Exception 
	 */
	@Test
	void testRemoveDiagram() throws Exception {
		
		Diagram diagram = createDiagram();
		UUID key = diagram.getKey();
		
		diagrams.add(diagram);
		assertTrue(diagrams.contains(key));
		
		diagrams.removeDiagram(diagram);
		assertFalse(diagrams.contains(key));

		verify(listener, times(1)).diagramDeleted(any(DiagramsChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#getDiagramsOfType(alvahouse.eatool.repository.graphical.DiagramType)}.
	 * @throws Exception 
	 */
	@Test
	void testGetDiagramsOfType() throws Exception {
		
		// Magic up the time diagram type
		DiagramType timeDiagramType = new TimeDiagramType( new UUID());
		DiagramTypeFamily timeFamily = new TimeDiagramTypeFamily();
		timeFamily.setParent(allTypes);
		timeDiagramType.setFamily(timeFamily);
		timeFamily.add(timeDiagramType);

		Diagram standardDiagram = createDiagram();
		diagrams.add(standardDiagram);
		
		Collection<Diagram> standard = diagrams.getDiagramsOfType(diagramType);
		assertEquals(1, standard.size());
		
		Collection<Diagram> time = diagrams.getDiagramsOfType(timeDiagramType);
		assertEquals(0, time.size());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#addChangeListener(alvahouse.eatool.repository.graphical.DiagramsChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		DiagramsChangeListener l = mock(DiagramsChangeListener.class);
		
		assertFalse(diagrams.isActive(l));
		diagrams.addChangeListener(l);
		assertTrue(diagrams.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#removeChangeListener(alvahouse.eatool.repository.graphical.DiagramsChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		DiagramsChangeListener l = mock(DiagramsChangeListener.class);
		
		assertFalse(diagrams.isActive(l));
		diagrams.addChangeListener(l);
		assertTrue(diagrams.isActive(l));
		
		diagrams.removeChangeListener(l);
		assertFalse(diagrams.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.Diagrams#deleteContents()}.
	 * @throws Exception 
	 */
	@Test
	void testDeleteContents() throws Exception {
		Diagram diagram = createDiagram();
		UUID key = diagram.getKey();
		
		diagrams.add(diagram);
		assertTrue(diagrams.contains(key));
		
		diagrams.deleteContents();
		assertFalse(diagrams.contains(key));
	}

}
