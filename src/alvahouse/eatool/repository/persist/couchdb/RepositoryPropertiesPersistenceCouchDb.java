/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import alvahouse.eatool.repository.dto.RepositoryPropertiesDto;
import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence;

/**
 * @author bruce_porteous
 *
 */
public class RepositoryPropertiesPersistenceCouchDb implements RepositoryPropertiesPersistence {

	private final CouchDB couch;
	private final String database;
	private final static String KEY = "repositoryProperties";
	/**
	 * @param couch
	 * @param database
	 */
	public RepositoryPropertiesPersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence#get()
	 */
	@Override
	public RepositoryPropertiesDto get() throws Exception {
		String json = couch.database().getDocument(database, KEY);
		RepositoryPropertiesDto dto = Serialise.unmarshalFromJson(json, RepositoryPropertiesDto.class);
		dto.getVersion().update(dto.getRev());
		return dto;

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence#set(alvahouse.eatool.repository.RepositoryProperties)
	 */
	@Override
	public void set(RepositoryPropertiesDto repositoryProperties) throws Exception {
		repositoryProperties.setRev(repositoryProperties.getVersion().getVersion());
		String json = Serialise.marshalToJSON(repositoryProperties);
		couch.database().putDocument(database, KEY, json);
	}

}
