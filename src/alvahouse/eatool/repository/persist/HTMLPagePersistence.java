/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface HTMLPagePersistence {

	/**
	 * Clears down the model.
	 */
	public void dispose() throws Exception;

	/**
	 * Looks up a html page by UUID.
	 * 
	 * @param key is the key of the html page to get
	 * @return the corresponding html page, or null if not in the model
	 */
	public HTMLPage getHTMLPage(UUID key) throws Exception;

	/**
	 * adds a new html page to the collection.
	 * 
	 * @param s is the html page to add.
	 * @throws IllegalStateException if the html page already exists in the model.
	 */
	public void addHTMLPage(HTMLPage page) throws Exception;

	/**
	 * Updates and existing html page in the model.
	 * 
	 * @param s is the html page to update.
	 * @throws IllegalStateException if the html page already exists in the model.
	 */
	public void updateHTMLPage(HTMLPage page) throws Exception;

	/**
	 * Deletes an html page keyed by UUID.
	 * 
	 * @param key is the key of the html page to delete.
	 * @throws IllegalStateException if deleting a html page not in the model.
	 */
	public void deleteHTMLPage(UUID key) throws Exception;

	/**
	 * Gets an iterator that iterates though all the entites.
	 * 
	 * @return an iterator for the entities.
	 */
	public Collection<HTMLPage> getHTMLPages() throws Exception;

	/**
	 * DEBUG only
	 * 
	 * @return
	 */
	public int getHTMLPageCount() throws Exception;

}
