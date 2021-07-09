/**
 * 
 */
package alvahouse.eatool.test.repository.mapping;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.mapping.EntityTranslation;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappingChangeListener;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.mapping.MappingChangeEvent;
import alvahouse.eatool.repository.mapping.PropertyTranslation;
import alvahouse.eatool.repository.mapping.RelationshipTranslation;
import alvahouse.eatool.repository.mapping.RoleTranslation;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.persist.ImportMappingPersistence;
import alvahouse.eatool.repository.persist.memory.ImportMappingPersistenceMemory;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestImportMappings {

	private ImportMappingPersistence persistence;
	private ImportMappings mappings;
	private ImportMappingChangeListener listener;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new ImportMappingPersistenceMemory();
		mappings = new ImportMappings(persistence);
		listener = mock(ImportMappingChangeListener.class);
		mappings.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	ImportMapping createMapping() {
		ImportMapping mapping = new ImportMapping(new UUID());
		mapping.setName("import mapping");
		mapping.setDescription("An import mapping");
		mapping.setParserName("org.thingy.Xerces");
		mapping.setTransformPath("c:\\temp\\bogus.xslt");
		
		EntityTranslation et = new EntityTranslation();
		et.setMeta(new MetaEntity(new UUID()));
		et.setType("entity");
		
		PropertyTranslation pt = new PropertyTranslation();
		pt.setMeta(new MetaProperty(new UUID()));
		pt.setTypeName("aproperty");
		et.addProperty(pt);
		
		mapping.add(et);
		
		MetaRelationship mr = new MetaRelationship(new UUID());
		MetaRole mrStart = mr.getMetaRole(new UUID());
		MetaRole mrFinish = mr.getMetaRole(new UUID());

		RelationshipTranslation rt = new RelationshipTranslation();
		rt.setMeta(mr);
		rt.setType("relationship");
		RoleTranslation start = new RoleTranslation(rt);
		start.setMeta(mrStart);
		start.setType("start");
		RoleTranslation finish = new RoleTranslation(rt);
		finish.setMeta(mrFinish);
		finish.setType("finish");
		rt.setStart(start);
		rt.setFinish(finish);
	
		mapping.add(rt);
		
		return mapping;
	}
	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#getImportMappings()}.
	 */
	@Test
	void testGetImportMappings() throws Exception {
		ImportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		Collection<ImportMapping> m = mappings.getImportMappings();
		assertEquals(1, m.size());
		objectsEqual(mapping, m.iterator().next());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception{
		ImportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		assertEquals(1, mappings.getImportMappings().size());
		
		mappings.deleteContents();
		
		assertEquals(0, mappings.getImportMappings().size());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#add(alvahouse.eatool.repository.mapping.ImportMapping)}.
	 */
	@Test
	void testAddAndLookup() throws Exception {
		ImportMapping mapping = createMapping();
		UUID key = mapping.getKey();
		
		mappings.add(mapping);
		
		ImportMapping retrieved = mappings.lookupMapping(key);
		assertThat( retrieved, samePropertyValuesAs(mapping, "version", "entityTranslations", "relationshipTranslations"));
		objectsEqual(retrieved, mapping);
		verify(listener, times(1)).MappingAdded(any(MappingChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#update(alvahouse.eatool.repository.mapping.ImportMapping)}.
	 */
	@Test
	void testUpdate() throws Exception {
		ImportMapping mapping = createMapping();
		UUID key = mapping.getKey();
		
		mappings.add(mapping);
		
		ImportMapping retrieved = mappings.lookupMapping(key);
		assertThat( retrieved, samePropertyValuesAs(mapping, "version", "entityTranslations", "relationshipTranslations"));
		objectsEqual(retrieved, mapping);

		mapping.setName("Updated");
		mapping.setDescription("Updated mapping");
		mappings.update(mapping);
		
		retrieved = mappings.lookupMapping(key);
		assertThat( retrieved, samePropertyValuesAs(mapping, "version", "entityTranslations", "relationshipTranslations"));
		objectsEqual(retrieved, mapping);
		verify(listener, times(1)).MappingEdited(any(MappingChangeEvent.class));

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#remove(alvahouse.eatool.repository.mapping.ImportMapping)}.
	 */
	@Test
	void testRemove() throws Exception {
		ImportMapping mapping = createMapping();
		
		mappings.add(mapping);
		
		assertEquals(1, mappings.getImportMappings().size());
		
		mappings.remove(mapping);
		
		assertEquals(0, mappings.getImportMappings().size());

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#addChangeListener(alvahouse.eatool.repository.mapping.ImportMappingChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		ImportMappingChangeListener l = mock(ImportMappingChangeListener.class);
		
		assertFalse(mappings.isActive(l));
		mappings.addChangeListener(l);
		assertTrue(mappings.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#removeChangeListener(alvahouse.eatool.repository.mapping.ImportMappingChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		ImportMappingChangeListener l = mock(ImportMappingChangeListener.class);
		
		assertFalse(mappings.isActive(l));
		mappings.addChangeListener(l);
		assertTrue(mappings.isActive(l));
		
		mappings.removeChangeListener(l);
		assertFalse(mappings.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#setParsers(alvahouse.eatool.util.SettingsManager)}.
	 */
	@Test
	void testSetParsers() throws Exception {
	
		SettingsManager settings = getParserSettings();
		mappings.setParsers(settings);
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private SettingsManager getParserSettings() throws UnsupportedEncodingException {
		String importData = 
				"<?xml version=\"1.0\"?>\n" +
				"<EAToolConfig>\n"+
				"	<ImportParsers>\n" + 
				"			<Parser name=\"XML\" class=\"org.apache.xerces.parsers.SAXParser\" suffix=\".xml\" filetype=\"XML Files\"/>\n" + 
				"			<Parser name=\"CSV\" class=\"alvahouse.eatool.repository.mapping.CSVXMLReader\" suffix=\".csv\" filetype=\"CSV Files\"/>\n" + 
				"			<Parser name=\"CSV Hierarchy\" class=\"alvahouse.eatool.repository.mapping.CSVHierarchyXMLReader\" suffix=\".csv\" filetype=\"CSV Files\"/>\n" + 
				"			<Parser name=\"CSV Transform\" class=\"alvahouse.eatool.util.CSVBasicXMLReader\" suffix=\".csv\" filetype=\"CSV Files\"/>\n" + 
				"	</ImportParsers>\n" +
				"</EAToolConfig>\n";
		SettingsManager settings = new SettingsManager();
		InputStream stream = new ByteArrayInputStream(importData.getBytes("UTF-8"));
		settings.load(stream);
		return settings;
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#getParserNames()}.
	 */
	@Test
	void testGetParserNames() throws Exception {
		SettingsManager settings = getParserSettings();
		mappings.setParsers(settings);
		
		String[] names = mappings.getParserNames();
		assertThat(names, hasItemInArray("XML"));
		assertThat(names, hasItemInArray("CSV"));
		assertThat(names, hasItemInArray("CSV Hierarchy"));
		assertThat(names, hasItemInArray("CSV Transform"));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#lookupParser(java.lang.String)}.
	 */
	@Test
	void testLookupParser() throws Exception {
		SettingsManager settings = getParserSettings();
		mappings.setParsers(settings);
		
		assertEquals("org.apache.xerces.parsers.SAXParser", mappings.lookupParser("XML"));
		assertEquals("alvahouse.eatool.repository.mapping.CSVXMLReader", mappings.lookupParser("CSV"));
		assertEquals("alvahouse.eatool.repository.mapping.CSVHierarchyXMLReader", mappings.lookupParser("CSV Hierarchy"));
		assertEquals("alvahouse.eatool.util.CSVBasicXMLReader", mappings.lookupParser("CSV Transform"));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#lookupFileExt(java.lang.String)}.
	 */
	@Test
	void testLookupFileExt() throws Exception {
		SettingsManager settings = getParserSettings();
		mappings.setParsers(settings);
		
		assertEquals(".xml", mappings.lookupFileExt("XML"));
		assertEquals(".csv", mappings.lookupFileExt("CSV"));
		assertEquals(".csv", mappings.lookupFileExt("CSV Hierarchy"));
		assertEquals(".csv", mappings.lookupFileExt("CSV Transform"));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#lookupFileDesc(java.lang.String)}.
	 */
	@Test
	void testLookupFileDesc() throws Exception {
		SettingsManager settings = getParserSettings();
		mappings.setParsers(settings);
		
		assertEquals("XML Files", mappings.lookupFileDesc("XML"));
		assertEquals("CSV Files", mappings.lookupFileDesc("CSV"));
		assertEquals("CSV Files", mappings.lookupFileDesc("CSV Hierarchy"));
		assertEquals("CSV Files", mappings.lookupFileDesc("CSV Transform"));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.mapping.ImportMappings#fireUpdated()}.
	 */
	@Test
	void testFireUpdated() {
		listener = mock(ImportMappingChangeListener.class);
		mappings.addChangeListener(listener);
		
		mappings.fireUpdated();
		
		verify(listener, times(1)).Updated(any(MappingChangeEvent.class));
	}

}
