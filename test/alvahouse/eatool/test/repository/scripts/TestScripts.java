/**
 * 
 */
package alvahouse.eatool.test.repository.scripts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.persist.ScriptPersistence;
import alvahouse.eatool.repository.persist.memory.ScriptPersistenceMemory;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptChangeEvent;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.repository.scripting.ScriptsChangeListener;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestScripts {
	
	private ScriptPersistence persistence;
	private Scripts scripts;
	private ScriptsChangeListener listener;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new ScriptPersistenceMemory();
		scripts = new Scripts(persistence);
		listener = mock(ScriptsChangeListener.class);
		scripts.addChangeListener(listener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	private Script createScript(String name, String desc, String lang, String text) {
		UUID key = new UUID();
		Script script = new Script(key);
		script.setName(name);
		script.setDescription(desc);
		script.setLanguage(lang);
		script.setScript(text);
		return script;
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#add(alvahouse.eatool.repository.scripting.Script)}.
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#_add(alvahouse.eatool.repository.scripting.Script)}.
	 */
	@Test
	void testAddAndGet() throws Exception {
		
		Script script = createScript("Test Script","A test script","jython","__init__(param1)");
		UUID key = script.getKey();
		
		scripts.add(script);
		
		Script retrieved = scripts.lookupScript(key);
		assertThat( retrieved, samePropertyValuesAs(script, "version"));

		verify(listener, times(1)).scriptAdded(any(ScriptChangeEvent.class));

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#update(alvahouse.eatool.repository.scripting.Script)}.
	 */
	@Test
	void testUpdate() throws Exception{
		Script script = createScript("Test Script","A test script","jython","__init__(param1)");
		UUID key = script.getKey();
		
		scripts.add(script);

		Script retrieved = scripts.lookupScript(key);
		assertThat( retrieved, samePropertyValuesAs(script, "version"));

		script.setName("Modified script");
		script.setDescription("A modified script");
		script.setScript("exit(0);");
		
		scripts.update(script);
		
		retrieved = scripts.lookupScript(key);
		assertThat( retrieved, samePropertyValuesAs(script, "version"));
		verify(listener, times(1)).scriptChanged(any(ScriptChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#delete(alvahouse.eatool.repository.scripting.Script)}.
	 */
	@Test
	void testDelete() throws Exception{
		Script script = createScript("Test Script","A test script","jython","__init__(param1)");
		
		scripts.add(script);
		assertEquals(1, scripts.getScriptCount());
		
		scripts.delete(script);
		verify(listener, times(1)).scriptDeleted(any(ScriptChangeEvent.class));
		assertEquals(0, scripts.getScriptCount());


	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#getScripts()}.
	 */
	@Test
	void testGetScripts() throws Exception {
		Script script1 = createScript("Test Script","A test script","jython","__init__(param1)");
		Script script2 = createScript("Test Script2","A test script","jython","__init__(param2)");
		Script script3 = createScript("Test Script3","A test script","jython","__init__(param3)");

		scripts.add(script1);
		scripts.add(script2);
		scripts.add(script3);
		
		Collection<Script> retrieved = scripts.getScripts();
		assertEquals(3,retrieved.size());
		assertTrue(retrieved.contains(script1));
		assertTrue(retrieved.contains(script2));
		assertTrue(retrieved.contains(script3));
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception {
		Script script1 = createScript("Test Script","A test script","jython","__init__(param1)");
		Script script2 = createScript("Test Script2","A test script","jython","__init__(param2)");
		Script script3 = createScript("Test Script3","A test script","jython","__init__(param3)");

		scripts.add(script1);
		scripts.add(script2);
		scripts.add(script3);
		assertEquals(3, scripts.getScriptCount());

		scripts.deleteContents();
		
		assertEquals(0, scripts.getScriptCount());

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#addChangeListener(alvahouse.eatool.repository.scripting.ScriptsChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		ScriptsChangeListener l = mock(ScriptsChangeListener.class);
		
		assertFalse(scripts.isActive(l));
		scripts.addChangeListener(l);
		assertTrue(scripts.isActive(l));
		
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#removeChangeListener(alvahouse.eatool.repository.scripting.ScriptsChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		ScriptsChangeListener l = mock(ScriptsChangeListener.class);
		
		assertFalse(scripts.isActive(l));
		scripts.addChangeListener(l);
		assertTrue(scripts.isActive(l));
		scripts.removeChangeListener(l);
		assertFalse(scripts.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#fireUpdated()}.
	 */
	@Test
	void testFireUpdated() throws Exception{
		scripts.fireUpdated();
		verify(listener, times(1)).updated(any(ScriptChangeEvent.class));

	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#getScriptCount()}.
	 */
	@Test
	void testGetScriptCount() throws Exception {
		Script script1 = createScript("Test Script","A test script","jython","__init__(param1)");
		Script script2 = createScript("Test Script2","A test script","jython","__init__(param2)");
		Script script3 = createScript("Test Script3","A test script","jython","__init__(param3)");

		assertEquals(0, scripts.getScriptCount());
		scripts.add(script1);
		assertEquals(1, scripts.getScriptCount());
		scripts.add(script2);
		assertEquals(2, scripts.getScriptCount());
		scripts.add(script3);
		assertEquals(3, scripts.getScriptCount());
	}


}
