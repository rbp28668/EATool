/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramTypePersistenceCouchDb implements DiagramTypePersistence {

	private final CouchDB couch;
	private final String database;

	/**
	 * @param couch
	 * @param database
	 */
	public DiagramTypePersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("diagramTypes")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramTypeDto' || doc.type_name ==='TimeDiagramTypeDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byFamily", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramTypeDto' || doc.type_name ==='TimeDiagramTypeDto') {\n" + 
				"    emit(doc.familyKey, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramTypeDto' || doc.type_name ==='TimeDiagramTypeDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getDiagramType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DiagramTypeDto getDiagramType(UUID typeID) throws Exception {
		return Helpers.get(couch, database, typeID, DiagramTypeDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#addDiagramType(alvahouse.eatool.repository.dto.graphical.DiagramTypeDto)
	 */
	@Override
	public String addDiagramType(DiagramTypeDto dt) throws Exception {
		return Helpers.add(couch, database, dt);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#updateDiagramType(alvahouse.eatool.repository.dto.graphical.DiagramTypeDto)
	 */
	@Override
	public String updateDiagramType(DiagramTypeDto dt) throws Exception {
		return Helpers.update(couch, database, dt);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#delete(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void delete(UUID key, String version) throws Exception {
		Helpers.delete(couch, version, key, version, DiagramTypeDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getTypes()
	 */
	@Override
	public Collection<DiagramTypeDto> getTypes() throws Exception {
		String json = couch.database().queryView(database,"diagramTypes", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, DiagramTypeDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		Helpers.deleteViewContents(couch, database, "diagramTypes", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getDiagramTypesOfFamily(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Collection<DiagramTypeDto> getDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception {
		String json = couch.database().queryView(database,"diagramTypes", "byFamily", 
				new CouchDB.Query()
				.add("key", diagramTypeFamilyKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		return Helpers.getViewCollectionFrom(json, DiagramTypeDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#deleteDiagramTypesOfFamily(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception {
		String json = couch.database().queryView(database,"diagramTypes", "byFamily", 
				new CouchDB.Query()
				.add("key", diagramTypeFamilyKey.asJsonId())
				.toString());
		Helpers.deleteRecordsByResult(couch, database, json);
	}

}
