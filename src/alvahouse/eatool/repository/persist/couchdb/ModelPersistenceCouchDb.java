/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import alvahouse.eatool.repository.dto.DeleteDependenciesListDto;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.model.EntityDto;
import alvahouse.eatool.repository.dto.model.RelationshipDto;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ModelPersistenceCouchDb implements ModelPersistence {

	private final CouchDB couch;
	private final String database;
	private UUID key;

	/**
	 * @param couch
	 * @param database
	 */
	public ModelPersistenceCouchDb(CouchDB couch, String database) throws Exception{
		this.couch = couch;
		this.database = database;

		String json = couch.database().getDocument(database, ModelDto.KEY);
		ModelDto dto = Serialise.unmarshalFromJson(json, ModelDto.class);
		this.key = dto.getKey();
	}

	
	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		UUID key = new UUID();
		ModelDto dto = new ModelDto();
		dto.setKey(key);
		String json = Serialise.marshalToJSON(dto);
		couch.database().putDocument(database, "model", json);
	
		DesignDocument.create("entities")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'EntityDto') {\n" + 
				"    emit(doc._id, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byMetaEntity", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'EntityDto') {\n" + 
				"    emit(doc.metaKey, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'EntityDto') {\n" + 
				"    emit(doc._id, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
		
		DesignDocument.create("relationships")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'RelationshipDto') {\n" + 
				"    emit(doc._id, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byMetaRelationship", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'RelationshipDto') {\n" + 
				"    emit(doc.metaKey, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byEntity", 
				"// Find relationships that connect to a given entity\n" + 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'RelationshipDto') {\n" + 
				"    emit(doc.start.connects, [doc._rev]);\n" + 
				"    emit(doc.finish.connects, [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byEntityAndMetaRelationship", 
				"// Find relationships of a given type that connect to a given entity\n" + 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'RelationshipDto') {\n" + 
				"    emit([doc.start.connects,doc.metaKey], [doc._rev]);\n" + 
				"    emit([doc.finish.connects,doc.metaKey], [doc._rev]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'RelationshipDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
		
	}


	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntity(alvahouse.eatool.util.UUID)
	 */
	@Override
	public EntityDto getEntity(UUID uuid) throws Exception{
		return Helpers.get(couch, database, uuid, EntityDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#addEntity(alvahouse.eatool.repository.dto.model.EntityDto)
	 */
	@Override
	public String addEntity(EntityDto e) throws Exception {
		return Helpers.add(couch, database, e);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#updateEntity(alvahouse.eatool.repository.dto.model.EntityDto)
	 */
	@Override
	public String updateEntity(EntityDto e) throws Exception {
		return Helpers.add(couch, database, e);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteEntity(alvahouse.eatool.util.UUID)
	 */
	@Override
	public EntityDto deleteEntity(UUID uuid, String version) throws Exception {
		return Helpers.delete(couch, database, uuid, version, EntityDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntities()
	 */
	@Override
	public Collection<EntityDto> getEntities() throws Exception{
		String json = couch.database().queryView(database,"entities", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, EntityDto.class);
	}



	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntityCount()
	 */
	@Override
	public int getEntityCount() throws Exception{
		String json = couch.database().queryView(database,"entities", "count");
		return Helpers.getCountFrom(json);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntitiesOfType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public List<EntityDto> getEntitiesOfType(UUID metaEntityKey) throws Exception{
		String json = couch.database().queryView(database,"entities", "byMetaEntity",
				new CouchDB.Query()
				.addKey(metaEntityKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		return Helpers.getViewCollectionFrom(json, EntityDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationship(alvahouse.eatool.util.UUID)
	 */
	@Override
	public RelationshipDto getRelationship(UUID uuid) throws Exception{
		return Helpers.get(couch, database, uuid, RelationshipDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#addRelationship(alvahouse.eatool.repository.dto.model.RelationshipDto)
	 */
	@Override
	public String addRelationship(RelationshipDto r) throws Exception {
		return Helpers.add(couch, database, r);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#updateRelationship(alvahouse.eatool.repository.dto.model.RelationshipDto)
	 */
	@Override
	public String updateRelationship(RelationshipDto r) throws Exception {
		return Helpers.update(couch, database, r);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteRelationship(alvahouse.eatool.util.UUID)
	 */
	@Override
	public RelationshipDto deleteRelationship(UUID uuid, String version) throws Exception {
		return Helpers.delete(couch, database, uuid, version, RelationshipDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationships()
	 */
	@Override
	public Collection<RelationshipDto> getRelationships() throws Exception {
		String json = couch.database().queryView(database,"relationships", "all", CouchDB.Query.INCLUDE_DOCS);
		return Helpers.getViewCollectionFrom(json, RelationshipDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationshipCount()
	 */
	@Override
	public int getRelationshipCount() throws Exception {
		String json = couch.database().queryView(database,"relationships", "count");
		return Helpers.getCountFrom(json);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationshipsOfType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public List<RelationshipDto> getRelationshipsOfType(UUID metaRelationshipKey) throws Exception {
		String json = couch.database().queryView(database,"relationships", "byMetaRelationship",
				new CouchDB.Query()
				.addKey(metaRelationshipKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		return Helpers.getViewCollectionFrom(json, RelationshipDto.class);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getConnectedRelationships(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Set<RelationshipDto> getConnectedRelationships(UUID entityKey) throws Exception {
		String json = couch.database().queryView(database,"relationships", "byEntity", 
				new CouchDB.Query()
				.addKey(entityKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		List<RelationshipDto> dtos = Helpers.getViewCollectionFrom(json, RelationshipDto.class);
		Set<RelationshipDto> unique = new HashSet<>(dtos.size());
		unique.addAll(dtos);
		return unique;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getConnectedRelationshipsOf(alvahouse.eatool.util.UUID, alvahouse.eatool.util.UUID)
	 */
	@Override
	public Set<RelationshipDto> getConnectedRelationshipsOf(UUID entityKey, UUID metaRelationshipKey) throws Exception {
		String json = couch.database().queryView(database,"relationships", "byEntityAndMetaRelationship", 
				new CouchDB.Query()
				.addKey(entityKey.asJsonId(),metaRelationshipKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		List<RelationshipDto> dtos = Helpers.getViewCollectionFrom(json, RelationshipDto.class);
		Set<RelationshipDto> unique = new HashSet<>(dtos.size());
		unique.addAll(dtos);
		return unique;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getDeleteDependencies(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DeleteDependenciesListDto getDeleteDependencies(UUID entityKey) throws Exception {
		DeleteDependenciesListDto dependencies = new DeleteDependenciesListDto();

		// Get this entity and mark for deletion
		EntityDto e = getEntity(entityKey);
		DeleteProxyDto proxy = new DeleteProxyDto();
		proxy.setItemType("entity");
		proxy.setItemKey(e.getKey());
		proxy.setVersion(e.getVersion().getVersion());
		dependencies.getDependencies().add(proxy);
		
		String json = couch.database().queryView(database,"relationships", "byEntity", 
				new CouchDB.Query()
				.add("key", entityKey.asJsonId())
				.toString());

		JsonNode root = Serialise.parseToJsonTree(json);
		
		JsonNode rows = root.get("rows");
		if(rows != null && rows.isArray()) {
			for(JsonNode row : rows) {
				String id = row.get("id").asText();
				String rev = row.get("value").get(0).asText();
				proxy = new DeleteProxyDto();
				proxy.setItemType("relationship");
				proxy.setItemKey(UUID.fromJsonId(id));
				proxy.setVersion(rev);
				dependencies.getDependencies().add(proxy);
			}
		}

		return dependencies;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		Helpers.deleteViewContents(couch, database, "entities", "all");
		Helpers.deleteViewContents(couch, database, "relationships", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getKey()
	 */
	@Override
	public UUID getKey() {
		return this.key;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#setKey(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) throws Exception{
		String json = couch.database().getDocument(database, ModelDto.KEY);
		ModelDto dto = Serialise.unmarshalFromJson(json, ModelDto.class);
		dto.setKey(uuid);
		json = Serialise.marshalToJSON(dto);
		couch.database().putDocument(database, ModelDto.KEY, json);
		this.key = uuid;
	}

	// Class to hold data specific to the meta model (rather than its contents) such as it's UUID
	// and possibly later security information.
	private static class ModelDto {
		private UUID key;
		private String rev;
		static final String KEY = "model";
		/**
		 * @return the key
		 */
		@JsonProperty(value = "_id") // _id is required for CouchDB
		public String getId() {
			return KEY;
		}

		/**
		 * @param key the key to set
		 */
		public void setId(String id) {
			assert(id.equals(KEY));
		}

		
		/**
		 * @return the key
		 */
		public UUID getKey() {
			return key;
		}

		/**
		 * @param key the key to set
		 */
		public void setKey(UUID key) {
			this.key = key;
		}

		/**
		 * @return the key
		 */
		@JsonProperty(value = "key") // _id is required for CouchDB
		public String getKeyJson() {
			return key.asJsonId();
		}

		/**
		 * @param key the key to set
		 */
		public void setKeyJson(String key) {
			this.key = UUID.fromJsonId(key);
		}

		/**
		 * revision information for CouchDB
		 * @return the rev
		 */
		@JsonProperty("_rev")
		public String getRev() {
			return rev;
		}

		/**
		 * @param rev the rev to set
		 */
		public void setRev(String rev) {
			this.rev = rev;
		}
	}


}
