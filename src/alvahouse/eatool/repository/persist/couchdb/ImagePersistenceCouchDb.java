/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;

import alvahouse.eatool.repository.dto.images.ImageDto;
import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ImagePersistenceCouchDb implements ImagePersistence {

	private final CouchDB couch;
	private final String database;

	/**
	 * @param couch
	 * @param database
	 */
	public ImagePersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		DesignDocument.create("images")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ImageDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ImageDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		Helpers.deleteViewContents(couch, database, "images", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ImageDto getImage(UUID key) throws Exception {
		return Helpers.get(couch, database, key, ImageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#addImage(alvahouse.eatool.repository.dto.images.ImageDto)
	 */
	@Override
	public String addImage(ImageDto image) throws Exception {
		return Helpers.add(couch, database, image);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#updateImage(alvahouse.eatool.repository.dto.images.ImageDto)
	 */
	@Override
	public String updateImage(ImageDto image) throws Exception {
		return Helpers.update(couch, database, image);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#deleteImage(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteImage(UUID key, String version) throws Exception {
		Helpers.delete(couch, version, key, version, ImageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImages()
	 */
	@Override
	public Collection<ImageDto> getImages() throws Exception {
		String json = couch.database().queryView(database,"images", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, ImageDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImagePersistence#getImageCount()
	 */
	@Override
	public int getImageCount() throws Exception {
		String json = couch.database().queryView(database,"images", "count");
		return Helpers.getCountFrom(json);
	}

}
