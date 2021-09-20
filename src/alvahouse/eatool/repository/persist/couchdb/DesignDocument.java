/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import com.fasterxml.jackson.databind.node.ObjectNode;

import alvahouse.eatool.repository.dto.Serialise;

class DesignDocument {
	
	ObjectNode root;
	ObjectNode views;
	
	private DesignDocument(String name){
		root = Serialise.createObjectNode();
		root.put("_id", "_design/" + name);
		root.put("language", "javascript");
		views = root.putObject("views");
	}
	
	static DesignDocument create(String name) {
		return new DesignDocument(name);
	}

	DesignDocument view(String name, String map) {
		return view(name, map, null);
	}

	DesignDocument view(String name, String map, String reduce) {
		ObjectNode view = views.putObject(name);
		view.put("map", map);
		if(reduce != null) {
			view.put("reduce", reduce);
		}
		return this;
	}
	
	DesignDocument updateValidation(String function) {
		root.put("validate_doc_update", function);
		return this;
	}
	
	void save(CouchDB couch, String database) throws Exception {
		String name = root.get("_id").asText();
		String designDoc = Serialise.writeJsonTree(root);
		couch.database().putDocument(database, name, designDoc);
		return;
	}
}