/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.graphical.DiagramDto;
import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramPersistenceCouchDb implements DiagramPersistence {

	private final CouchDB couch;
	private final String database;

	/**
	 * @param couch
	 * @param database
	 */
	public DiagramPersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("diagrams")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramDto' || doc.type_name ==='TimeDiagramDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byType", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramDto' || doc.type_name ==='TimeDiagramDto') {\n" + 
				"    emit(doc.typeKey, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'StandardDiagramDto' || doc.type_name ==='TimeDiagramDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagramsByType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Collection<DiagramDto> getDiagramsByType(UUID typeKey) throws Exception {
		String json = couch.database().queryView(database,"diagrams", "byType", 
				new CouchDB.Query()
				.add("key", typeKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		return Helpers.getViewCollectionFrom(json, DiagramDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagrams()
	 */
	@Override
	public Collection<DiagramDto> getDiagrams() throws Exception {
		String json = couch.database().queryView(database,"diagrams", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, DiagramDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#contains(alvahouse.eatool.util.UUID)
	 */
	@Override
	public boolean contains(UUID uuid) throws Exception {
		return couch.database().documentExists(database, uuid.asJsonId());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#lookup(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DiagramDto lookup(UUID key) throws Exception {
		return Helpers.get(couch, database, key, DiagramDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#addDiagram(alvahouse.eatool.repository.dto.graphical.DiagramDto)
	 */
	@Override
	public String addDiagram(DiagramDto diagram) throws Exception {
		return Helpers.add(couch, database, diagram);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#updateDiagram(alvahouse.eatool.repository.dto.graphical.DiagramDto)
	 */
	@Override
	public String updateDiagram(DiagramDto diagram) throws Exception {
		return Helpers.update(couch, database, diagram);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#deleteDiagram(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteDiagram(UUID key, String version) throws Exception {
		Helpers.delete(couch, database, key, version, DiagramDto.class);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		Helpers.deleteViewContents(couch, database, "diagrams", "all");

	}

}
