/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.dto.html.HTMLPageDto;
import alvahouse.eatool.repository.persist.HTMLPagePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class HTMLPagePersistenceMemory implements HTMLPagePersistence {

    private Map<UUID,HTMLPageDto> pages = new HashMap<>();

	/**
	 * 
	 */
	public HTMLPagePersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		pages.clear();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public HTMLPageDto getHTMLPage(UUID key) throws Exception {
		HTMLPageDto page = pages.get(key);
		if(page == null) {
			throw new IllegalArgumentException("No page with key " + key + " in repository");
		}
		return page;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#addHTMLPage(alvahouse.eatool.repository.html.HTMLPage)
	 */
	@Override
	public void addHTMLPage(HTMLPageDto page) throws Exception {
		UUID key = page.getKey();
		if(pages.containsKey(key)) {
			throw new IllegalArgumentException("HTML Page with key " + key + " already exists in repository");
		}
		pages.put(key, page);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#updateHTMLPage(alvahouse.eatool.repository.html.HTMLPage)
	 */
	@Override
	public void updateHTMLPage(HTMLPageDto page) throws Exception {
		UUID key = page.getKey();
		if(!pages.containsKey(key)) {
			throw new IllegalArgumentException("HTML Page with key " + key + " does not exist in repository");
		}
		pages.put(key,page);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#deleteHTMLPage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteHTMLPage(UUID key) throws Exception {
		if(!pages.containsKey(key)) {
			throw new IllegalArgumentException("HTML Page with key " + key + " does not exist in repository");
		}
		pages.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPages()
	 */
	@Override
	public Collection<HTMLPageDto> getHTMLPages() throws Exception {
		List<HTMLPageDto> copy = new ArrayList<>(pages.size());
		copy.addAll(pages.values());
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPageCount()
	 */
	@Override
	public int getHTMLPageCount() throws Exception {
		return pages.size();
	}

}
