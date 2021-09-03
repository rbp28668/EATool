/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import alvahouse.eatool.repository.dto.RepositoryItemDto;
import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionedDto;
import alvahouse.eatool.repository.dto.model.EntityDto;
import alvahouse.eatool.util.UUID;

/**
 * Helper functions for couch persistence.
 * 
 * @author bruce_porteous
 *
 */
class Helpers {

	/**
	 * Don't instantiate - all static methods.
	 */
	private Helpers() {
	}

	static <T extends VersionedDto> T get(CouchDB couch, String database, UUID key, Class<T> dtoClass)
			throws Exception {
		String json = couch.database().getDocument(database, key.asJsonId());
		T dto = Serialise.unmarshalFromJson(json, dtoClass);
		dto.getVersion().update(dto.getRev());
		return dto;
	}

	static <T extends RepositoryItemDto> String add(CouchDB couch, String database, T dto) throws Exception {
		String document = Serialise.marshalToJSON(dto);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, dto.getKeyJson(), document);
		String revision = response.rev;
		return revision;
	}

	static <T extends RepositoryItemDto & VersionedDto> String update(CouchDB couch, String database, T dto) throws Exception {
		dto.setRev(dto.getVersion().getVersion());
		String document = Serialise.marshalToJSON(dto);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, dto.getKeyJson(), document);
		return response.rev;
	}

	static <T> T delete(CouchDB couch, String database, UUID uuid, String version,Class<T> dtoClass) throws Exception {
		String json = couch.database().getDocument(database, uuid.asJsonId());
		T dto = Serialise.unmarshalFromJson(json, dtoClass);
		couch.database().deleteDocument(database, uuid.asJsonId(), version);
		return dto;
	}

	/**
	 * Gets the integer count value from a view that uses the _count reduce
	 * function.
	 * 
	 * @param json
	 * @return
	 * @throws JacksonException
	 * @throws IOException
	 */
	static int getCountFrom(String json) throws JacksonException, IOException {
		JsonNode root = Serialise.parseToJsonTree(json);
		JsonNode rows = root.get("rows");
		if (rows.size() == 0) {
			return 0;
		}
		int value = rows.get(0).get("value").asInt();
		return value;

	}

	/**
	 * Deletes all the documents returned by the given view. Note that the view must
	 * return an array where doc._rev is the first element.
	 * 
	 * @param designDoc
	 * @param viewName
	 * @throws Exception
	 */
	static void deleteViewContents(CouchDB couch, String database, String designDoc, String viewName) throws Exception {
		String json = couch.database().queryView(database, designDoc, viewName);
		deleteRecordsByResult(couch, database, json);
	}

	/**
	 * Deletes all the documents returned by the given view. Note that the view must
	 * return an array where doc._rev is the first element.
	 * 
	 * @param designDoc
	 * @param viewName
	 * @throws Exception
	 */
	static void deleteRecordsByResult(CouchDB couch, String database, String json) throws Exception {
		JsonNode root = Serialise.parseToJsonTree(json);

		int rowCount = root.get("total_rows").asInt();
		if (rowCount == 0) {
			return;
		}

		ObjectNode out = Serialise.createObjectNode();
		ArrayNode toDelete = out.putArray("docs");

		JsonNode rows = root.get("rows");
		assert (rows.isArray());
		for (JsonNode row : rows) {
			String id = row.get("id").asText();
			String rev = row.get("value").get(0).asText();

			ObjectNode record = toDelete.addObject();
			record.put("_id", id);
			record.put("_rev", rev);
			record.put("_deleted", true);
		}

		String deleteDoc = Serialise.writeJsonTree(out);
		couch.database().bulkUpdate(database, deleteDoc);
	}

	static <T extends VersionedDto> List<T> getViewCollectionFrom(String json, Class<T> dtoClass)
			throws JacksonException, IOException {
		JsonNode root = Serialise.parseToJsonTree(json);

		int rowCount = root.get("total_rows").asInt();
		List<T> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert (rows.isArray());
		for (JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			T dto = Serialise.unmarshalFromJson(doc, dtoClass);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;

	}
}
