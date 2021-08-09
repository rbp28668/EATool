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
class TestDiagramFamilies {

	private Repository repository;
	private DiagramTypePersistence typePersistence;
	private DiagramTypes diagramTypes;
	private DiagramsChangeListener listener;
//	private DiagramTypeFamily family;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		repository = mock(Repository.class);
		typePersistence = new DiagramTypePersistenceMemory();
		diagramTypes = new DiagramTypes(repository,typePersistence);

//		family = new StandardDiagramTypeFamily();
//		family.setParent(diagramTypes);
//		diagramTypes.addDiagramFamily(family);
//
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
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private SettingsManager getDiagramFamilyConfig() throws UnsupportedEncodingException {
		String importData = 
				"<?xml version=\"1.0\"?>\n" +
				"<EAToolConfig>\n"+
						"	<DiagramFamilies>\n" + 
						"		<DiagramFamily name=\"Standard\" class=\"alvahouse.eatool.repository.graphical.standard.StandardDiagramTypeFamily\" />\n" + 
						"		<DiagramFamily name=\"Time\"     class=\"alvahouse.eatool.gui.graphical.time.TimeDiagramTypeFamily\" />\n" + 
						"	</DiagramFamilies>\n" + 
				"</EAToolConfig>\n";
		SettingsManager settings = new SettingsManager();
		InputStream stream = new ByteArrayInputStream(importData.getBytes("UTF-8"));
		settings.load(stream);
		return settings;
	}

	
	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#setFamilies(alvahouse.eatool.util.SettingsManager)}.
	 */
	@Test
	void testSetFamilies() throws Exception {
		
		SettingsManager config =  getDiagramFamilyConfig();
		diagramTypes.setFamilies(config);
		
		Collection<DiagramTypeFamily> families = diagramTypes.getDiagramTypeFamilies();
		assertEquals(2, families.size());
		
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#getDiagramTypeFamilies()}.
	 */
	@Test
	void testGetDiagramTypeFamilies() throws Exception {

		SettingsManager config =  getDiagramFamilyConfig();
		diagramTypes.setFamilies(config);
		
		Collection<DiagramTypeFamily> families = diagramTypes.getDiagramTypeFamilies();
		assertEquals(2, families.size());

		String[] names = {"Standard", "Time"};
		
		for(DiagramTypeFamily family: families) {
			assertTrue(Arrays.asList(names).contains(family.getName()));
		}
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.graphical.DiagramTypes#lookupFamily(alvahouse.eatool.util.UUID)}.
	 */
	@Test
	void testLookupFamily() throws Exception{
		
		SettingsManager config =  getDiagramFamilyConfig();
		diagramTypes.setFamilies(config);
		
		DiagramTypeFamily family = diagramTypes.lookupFamily(StandardDiagramTypeFamily.FAMILY_KEY);
		assertNotNull(family);

		family = diagramTypes.lookupFamily(TimeDiagramTypeFamily.FAMILY_KEY);
		assertNotNull(family);
	}


}
