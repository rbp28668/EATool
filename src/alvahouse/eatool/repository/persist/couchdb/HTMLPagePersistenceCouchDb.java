/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.html.HTMLPageDto;
import alvahouse.eatool.repository.persist.HTMLPagePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class HTMLPagePersistenceCouchDb implements HTMLPagePersistence {

	private final CouchDB couch;
	private final String database;

	/**
	 * @param couch
	 * @param database
	 */
	public HTMLPagePersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("htmlPages")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'HTMLPageDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'HTMLPageDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		Helpers.deleteViewContents(couch, database, "htmlPages", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public HTMLPageDto getHTMLPage(UUID key) throws Exception {
		return Helpers.get(couch, database, key, HTMLPageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#addHTMLPage(alvahouse.eatool.repository.dto.html.HTMLPageDto)
	 */
	@Override
	public String addHTMLPage(HTMLPageDto page) throws Exception {
		return Helpers.add(couch, database, page);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#updateHTMLPage(alvahouse.eatool.repository.dto.html.HTMLPageDto)
	 */
	@Override
	public String updateHTMLPage(HTMLPageDto page) throws Exception {
		return Helpers.update(couch, database, page);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#deleteHTMLPage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteHTMLPage(UUID key, String version) throws Exception {
		Helpers.delete(couch, version, key, version, HTMLPageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPages()
	 */
	@Override
	public Collection<HTMLPageDto> getHTMLPages() throws Exception {
		String json = couch.database().queryView(database,"htmlPages", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, HTMLPageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.HTMLPagePersistence#getHTMLPageCount()
	 */
	@Override
	public int getHTMLPageCount() throws Exception {
		String json = couch.database().queryView(database,"htmlPages", "count");
		return Helpers.getCountFrom(json);
	}

}
