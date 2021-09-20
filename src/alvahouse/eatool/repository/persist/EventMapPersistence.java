/**
 * 
 */
package alvahouse.eatool.repository.persist;

import alvahouse.eatool.repository.dto.scripting.EventMapDto;

/**
 * @author bruce_porteous
 *
 */
public interface EventMapPersistence {

	/**
	 * Gets the event map
	 * @return the event map.
	 */
	public EventMapDto get() throws Exception;
	
	/**
	 * Sets the event map.
	 * @param eventMap
	 */
	public void set(EventMapDto eventMap) throws Exception;

}
