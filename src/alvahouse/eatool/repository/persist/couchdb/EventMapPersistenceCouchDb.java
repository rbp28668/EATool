/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.repository.persist.EventMapPersistence;

/**
 * @author bruce_porteous
 *
 */
public class EventMapPersistenceCouchDb implements EventMapPersistence {

	private final CouchDB couch;
	private final String database;
	private final String key;
	/**
	 * @param couch
	 * @param database
	 */
	public EventMapPersistenceCouchDb(CouchDB couch, String database, String key) {
		this.couch = couch;
		this.database = database;
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#get()
	 */
	@Override
	public EventMapDto get() throws Exception {
		String json = couch.database().getDocument(database, key);
		EventMapDto dto = Serialise.unmarshalFromJson(json, EventMapDto.class);
		dto.getVersion().update(dto.getRev());
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.EventMapPersistence#set(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void set(EventMapDto eventMap) throws Exception {
		eventMap.setRev(eventMap.getVersion().getVersion());
		String json = Serialise.marshalToJSON(eventMap);
		couch.database().putDocument(database, key, json);
	}

}
