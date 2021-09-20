/**
 * 
 */
package alvahouse.eatool.test.repository.html;

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

import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.html.PageChangeEvent;
import alvahouse.eatool.repository.html.PagesChangeListener;
import alvahouse.eatool.repository.persist.HTMLPagePersistence;
import alvahouse.eatool.repository.persist.memory.HTMLPagePersistenceMemory;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestHtmlPages {

	private HTMLPagePersistence persistence;
	private HTMLPages pages;
	private PagesChangeListener listener;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new HTMLPagePersistenceMemory();
		pages = new HTMLPages(persistence);
		listener = mock(PagesChangeListener.class);
		pages.addChangeListener(listener);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	private HTMLPage createPage(String name, String desc) {
		UUID key = new UUID();
		HTMLPage page = new HTMLPage(key);
		page.setName(name);
		page.setDescription(desc);
		page.setDynamic(true);
		page.setHtml("<html><head></head><body><h1>Hello World</h1></body></html>");
		Script script = new Script(new UUID());
		script.setName("OnDisplayScript");
		script.setDescription("Script to create page content");
		script.setLanguage("javascript");
		script.setScript("alert('Hello World');");
		page.getEventMap().setHandler("OnDisplay", script);
		return page;
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#add(alvahouse.eatool.repository.html.HTMLPage)}.
	 */
	@Test
	void testAddandLookup() throws Exception {
		HTMLPage page = createPage("test","test page");
		UUID key = page.getKey();
		
		pages.add(page);
		
		HTMLPage retrieved = pages.lookup(key);
		assertThat( retrieved, samePropertyValuesAs(page, "version", "eventMap"));
		objectsEqual(retrieved, page);
		verify(listener, times(1)).pageAdded(any(PageChangeEvent.class));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#update(alvahouse.eatool.repository.html.HTMLPage)}.
	 */
	@Test
	void testUpdate() throws Exception {
		HTMLPage page = createPage("test","test page");
		UUID key = page.getKey();
		
		pages.add(page);
		
		HTMLPage retrieved = pages.lookup(key);
		assertThat( retrieved, samePropertyValuesAs(page, "version", "eventMap"));
		objectsEqual(retrieved, page);
		
		page.setName("updated test");
		page.setDescription("updated test page");
		page.setHtml("<h1>Test</h1>");
		
		pages.update(page);
		
		retrieved = pages.lookup(key);
		assertThat( retrieved, samePropertyValuesAs(page, "version", "eventMap"));
		objectsEqual(retrieved, page);
		
		verify(listener, times(1)).pageEdited(any(PageChangeEvent.class));
	}


	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#getPages()}.
	 */
	@Test
	void testGetPages() throws Exception {
		HTMLPage page1 = createPage("page1", "first page");
		HTMLPage page2 = createPage("page2", "second page");
		HTMLPage page3 = createPage("page3", "third page");
		
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);
		
		Collection<HTMLPage> retrieved = pages.getPages();
		assertEquals(3,retrieved.size());
		assertTrue(retrieved.contains(page1));
		assertTrue(retrieved.contains(page2));
		assertTrue(retrieved.contains(page3));

	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#delete(alvahouse.eatool.repository.html.HTMLPage)}.
	 */
	@Test
	void testDelete() throws Exception{
		HTMLPage page = createPage("page", "test page");
		
		pages.add(page);
		assertEquals(1, pages.getPageCount());
		
		pages.delete(page);
		
		verify(listener, times(1)).pageRemoved(any(PageChangeEvent.class));
		assertEquals(0, pages.getPageCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.scripting.Scripts#getScriptCount()}.
	 */
	@Test
	void testGetScriptCount() throws Exception {
		HTMLPage page1 = createPage("page1", "first page");
		HTMLPage page2 = createPage("page2", "second page");
		HTMLPage page3 = createPage("page3", "third page");
		
		assertEquals(0, pages.getPageCount());
		pages.add(page1);
		assertEquals(1, pages.getPageCount());
		pages.add(page2);
		assertEquals(2, pages.getPageCount());
		pages.add(page3);
		assertEquals(3, pages.getPageCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#deleteContents()}.
	 */
	@Test
	void testDeleteContents() throws Exception{
		HTMLPage page1 = createPage("page1", "first page");
		HTMLPage page2 = createPage("page2", "second page");
		HTMLPage page3 = createPage("page3", "third page");
		
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);
		
		assertEquals(3, pages.getPageCount());

		pages.deleteContents();
		
		assertEquals(0, pages.getPageCount());
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#addChangeListener(alvahouse.eatool.repository.html.PagesChangeListener)}.
	 */
	@Test
	void testAddChangeListener() {
		PagesChangeListener l = mock(PagesChangeListener.class);
		
		assertFalse(pages.isActive(l));
		pages.addChangeListener(l);
		assertTrue(pages.isActive(l));
	}

	/**
	 * Test method for {@link alvahouse.eatool.repository.html.HTMLPages#removeChangeListener(alvahouse.eatool.repository.html.PagesChangeListener)}.
	 */
	@Test
	void testRemoveChangeListener() {
		PagesChangeListener l = mock(PagesChangeListener.class);
		
		assertFalse(pages.isActive(l));
		pages.addChangeListener(l);
		assertTrue(pages.isActive(l));
		
		pages.removeChangeListener(l);
		assertFalse(pages.isActive(l));
	}

	
}
