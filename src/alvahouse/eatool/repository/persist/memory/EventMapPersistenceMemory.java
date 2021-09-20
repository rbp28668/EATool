/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.repository.persist.EventMapPersistence;
import alvahouse.eatool.repository.scripting.EventMap;

/**
 * @author bruce_porteous
 *
 */
public class EventMapPersistenceMemory implements EventMapPersistence {

	private EventMapDto eventMap = null;
	
	/**
	 * 
	 */
	public EventMapPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#get()
	 */
	@Override
	public EventMapDto get() throws Exception {
		if(eventMap == null) {
			throw new NullPointerException("EventMap was never set");
		}
		return eventMap;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#set(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void set(EventMapDto eventMap) throws Exception {
		this.eventMap = eventMap; 
	}

}
