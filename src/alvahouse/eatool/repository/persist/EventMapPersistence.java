/**
 * 
 */
package alvahouse.eatool.repository.persist;

import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;

/**
 * @author bruce_porteous
 *
 */
public interface EventMapPersistence {

	/**
	 * Gets the event map
	 * @return the event map.
	 */
	public EventMap get(Scripts scripts) throws Exception;
	
	/**
	 * Sets the event map.
	 * @param eventMap
	 */
	public void set(EventMap eventMap) throws Exception;

}
