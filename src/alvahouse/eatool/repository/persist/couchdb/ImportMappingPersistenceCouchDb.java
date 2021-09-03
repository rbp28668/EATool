/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.images.ImageDto;
import alvahouse.eatool.repository.dto.mapping.ImportMappingDto;
import alvahouse.eatool.repository.persist.ImportMappingPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ImportMappingPersistenceCouchDb implements ImportMappingPersistence {


	private final CouchDB couch;
	private final String database;
	
	/**
	 * @param couch
	 * @param database
	 */
	public ImportMappingPersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("importMappings")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ImportMappingDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ImportMappingDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#getMappings()
	 */
	@Override
	public Collection<ImportMappingDto> getMappings() throws Exception {
		String json = couch.database().queryView(database,"importMappings", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, ImportMappingDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#lookupMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ImportMappingDto lookupMapping(UUID key) throws Exception {
		return Helpers.get(couch, database, key, ImportMappingDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#addMapping(alvahouse.eatool.repository.dto.mapping.ImportMappingDto)
	 */
	@Override
	public String addMapping(ImportMappingDto mapping) throws Exception {
		return Helpers.add(couch, database, mapping);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#updateMapping(alvahouse.eatool.repository.dto.mapping.ImportMappingDto)
	 */
	@Override
	public String updateMapping(ImportMappingDto mapping) throws Exception {
		return Helpers.update(couch, database, mapping);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#deleteMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteMapping(UUID key, String version) throws Exception {
		Helpers.delete(couch, database, key, version, ImportMappingDto.class);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#deleteAll()
	 */
	@Override
	public void deleteAll() throws Exception {
		Helpers.deleteViewContents(couch, database, "importMappings", "all");
	}

}
