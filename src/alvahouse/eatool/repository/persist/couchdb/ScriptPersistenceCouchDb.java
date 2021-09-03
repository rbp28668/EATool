/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.model.EntityDto;
import alvahouse.eatool.repository.dto.scripting.ScriptDto;
import alvahouse.eatool.repository.persist.ScriptPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ScriptPersistenceCouchDb implements ScriptPersistence {

	private final CouchDB couch;
	private final String database;

	
	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
	
		DesignDocument.create("scripts")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ScriptDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ScriptDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
	}
	
	/**
	 * @param couch2
	 * @param database
	 */
	public ScriptPersistenceCouchDb(CouchDB couch, String database) {
		this.couch = couch;
		this.database = database;
	}

	/**
	 * Gets Script DTOs from the result of querying a view.
	 * @param json
	 * @return
	 * @throws IOException
	 * @throws JacksonException
	 * @throws JsonProcessingException
	 */
	private List<ScriptDto> getScriptsFromView(String json)
			throws IOException, JacksonException, JsonProcessingException {
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		List<ScriptDto> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			ScriptDto dto = Serialise.unmarshalFromJson(doc, ScriptDto.class);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		Helpers.deleteViewContents(couch, database, "scripts", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ScriptDto getScript(UUID key) throws Exception {
		String json = couch.database().getDocument(database, key.asJsonId());
		ScriptDto dto = Serialise.unmarshalFromJson(json, ScriptDto.class);
		dto.getVersion().update(dto.getRev());
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#addScript(alvahouse.eatool.repository.dto.scripting.ScriptDto)
	 */
	@Override
	public String addScript(ScriptDto s) throws Exception {
		String document = Serialise.marshalToJSON(s);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, s.getKeyJson(), document);
		String revision = response.rev;
		return revision;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#updateScript(alvahouse.eatool.repository.dto.scripting.ScriptDto)
	 */
	@Override
	public String updateScript(ScriptDto s) throws Exception {
		s.setRev(s.getVersion().getVersion());
		String document = Serialise.marshalToJSON(s);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, s.getKeyJson(), document);
		return response.rev;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#deleteScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteScript(UUID key, String version) throws Exception {
		couch.database().deleteDocument(database, key.asJsonId(), version);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScripts()
	 */
	@Override
	public Collection<ScriptDto> getScripts() throws Exception {
		String json = couch.database().queryView(database,"scripts", "all", CouchDB.Query.INCLUDE_DOCS);
		return getScriptsFromView(json);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScriptCount()
	 */
	@Override
	public int getScriptCount() throws Exception {
		String json = couch.database().queryView(database,"scripts", "count");
		return Helpers.getCountFrom(json);
	}

}
