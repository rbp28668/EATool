/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.mapping.ExportMappingDto;
import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ExportMappingPersistenceCouchDb implements ExportMappingPersistence {

	private final CouchDB couch;
	private final String database;

	/**
	 * @param couch
	 * @param database
	 */
	public ExportMappingPersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("exportMappings")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ExportMappingDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ExportMappingDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#getMappings()
	 */
	@Override
	public Collection<ExportMappingDto> getMappings() throws Exception {
		String json = couch.database().queryView(database,"exportMappings", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, ExportMappingDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#addMapping(alvahouse.eatool.repository.dto.mapping.ExportMappingDto)
	 */
	@Override
	public String addMapping(ExportMappingDto mapping) throws Exception {
		return Helpers.add(couch, database, mapping);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#updateMapping(alvahouse.eatool.repository.dto.mapping.ExportMappingDto)
	 */
	@Override
	public String updateMapping(ExportMappingDto mapping) throws Exception {
		return Helpers.update(couch, database, mapping);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#deleteMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteMapping(UUID key, String version) throws Exception {
		Helpers.delete(couch, database, key, version, ExportMappingDto.class);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#lookupMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ExportMappingDto lookupMapping(UUID key) throws Exception {
		return Helpers.get(couch, database, key, ExportMappingDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		Helpers.deleteViewContents(couch, database, "exportMappings", "all");
	}

}
