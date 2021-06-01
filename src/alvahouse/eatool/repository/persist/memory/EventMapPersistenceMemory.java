/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.persist.EventMapPersistence;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;

/**
 * @author bruce_porteous
 *
 */
public class EventMapPersistenceMemory implements EventMapPersistence {

	private EventMap eventMap = null;
	
	/**
	 * 
	 */
	public EventMapPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#get()
	 */
	@Override
	public EventMap get() throws Exception {
		if(eventMap == null) {
			throw new NullPointerException("EventMap was never set");
		}
		return (EventMap)eventMap.clone();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#set(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void set(EventMap eventMap) throws Exception {
		this.eventMap = (EventMap)eventMap.clone(); // disconnect from scripts
	}

}
