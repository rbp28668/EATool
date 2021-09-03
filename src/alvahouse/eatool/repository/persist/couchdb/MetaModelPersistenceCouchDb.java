/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import alvahouse.eatool.repository.dto.DeleteDependenciesListDto;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto;
import alvahouse.eatool.repository.dto.metamodel.MetaEntityDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class MetaModelPersistenceCouchDb implements MetaModelPersistence {

	private final CouchDB couch;
	private final String database;
	private UUID key;

	
	/**
	 * 
	 */
	public MetaModelPersistenceCouchDb(	CouchDB couch, String database) throws Exception {
		this.couch = couch;
		this.database = database;
		
		String json = couch.database().getDocument(database, MetaModelDto.KEY);
		MetaModelDto dto = Serialise.unmarshalFromJson(json, MetaModelDto.class);
		this.key = dto.getKey();
	}

	static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		UUID key = new UUID();
		MetaModelDto dto = new MetaModelDto();
		dto.setKey(key);
		String json = Serialise.marshalToJSON(dto);
		couch.database().putDocument(database, "metaModel", json);
	
		DesignDocument.create("meta_entities")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaEntityDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byBase", 
				"// Identify all meta entities derived from a given base meta entity.\n" + 
				"// Single level only, need to process results recursively.\n" + 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaEntityDto') {\n" + 
				"    if(doc.base) {\n" + 
				"      emit(doc.base, [doc._rev, doc.name, doc.description]);\n" + 
				"    }\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaEntityDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
		
		DesignDocument.create("meta_relationships")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaRelationshipDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("byMetaEntity", 
				"// Find meta relationships that connect to a given menta entity\n" + 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaRelationshipDto') {\n" + 
				"    emit(doc.start.connects, [doc._rev, doc.name, doc.description]);\n" + 
				"    emit(doc.finish.connects, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.view("count", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'MetaRelationshipDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				,"_count"
				)
		.save(couch, database);
		
		DesignDocument.create("meta_property_types")
		.view("all", 
				"function (doc) {\n" + 
				"  if(doc.type_name === 'ControlledListTypeDto' || doc.type_name === 'RegexpCheckedTypeDto' || doc.type_name === 'TimeSeriesTypeDto') {\n" + 
				"    emit(doc._id, [doc._rev, doc.name, doc.description]);\n" + 
				"  }\n" + 
				"}"
				)
		.save(couch, database);
	}
	
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntity(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntityDto getMetaEntity(UUID uuid) throws Exception {
		String json = couch.database().getDocument(database, uuid.asJsonId());
		MetaEntityDto dto = Serialise.unmarshalFromJson(json, MetaEntityDto.class);
		dto.getVersion().update(dto.getRev());
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaEntity(alvahouse.eatool.repository.dto.metamodel.MetaEntityDto)
	 */
	@Override
	public String addMetaEntity(MetaEntityDto me) throws Exception {
		String document = Serialise.marshalToJSON(me);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, me.getKeyJson(), document);
		String revision = response.rev;
		return revision;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntities()
	 */
	@Override
	public Collection<MetaEntityDto> getMetaEntities() throws Exception {
		//_design/meta_entities/_view/all?include_docs=true
		
		String json = couch.database().queryView(database,"meta_entities", "all", CouchDB.Query.INCLUDE_DOCS);
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		List<MetaEntityDto> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			MetaEntityDto dto = Serialise.unmarshalFromJson(doc, MetaEntityDto.class);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntityCount()
	 */
	@Override
	public int getMetaEntityCount() throws Exception {
		//_design/meta_entities/_view/count
		String json = couch.database().queryView(database,"meta_entities", "count");
		return Helpers.getCountFrom(json);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaEntity(alvahouse.eatool.repository.dto.metamodel.MetaEntityDto)
	 */
	@Override
	public String updateMetaEntity(MetaEntityDto me) throws Exception {
		me.setRev(me.getVersion().getVersion());
		String document = Serialise.marshalToJSON(me);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, me.getKeyJson(), document);
		return response.rev;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteMetaEntity(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntityDto deleteMetaEntity(UUID uuid, String version) throws Exception {
		String json = couch.database().getDocument(database, uuid.asJsonId());
		MetaEntityDto dto = Serialise.unmarshalFromJson(json, MetaEntityDto.class);
		couch.database().deleteDocument(database, uuid.asJsonId(), version);
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationship(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationshipDto getMetaRelationship(UUID uuid) throws Exception {
		String json = couch.database().getDocument(database, uuid.asJsonId());
		MetaRelationshipDto dto = Serialise.unmarshalFromJson(json, MetaRelationshipDto.class);
		dto.getVersion().update(dto.getRev());
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaRelationship(alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto)
	 */
	@Override
	public String addMetaRelationship(MetaRelationshipDto mr) throws Exception {
		String document = Serialise.marshalToJSON(mr);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, mr.getKeyJson(), document);
		String revision = response.rev;
		return revision;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaRelationship(alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto)
	 */
	@Override
	public String updateMetaRelationship(MetaRelationshipDto mr) throws Exception {
		mr.setRev(mr.getVersion().getVersion());
		String document = Serialise.marshalToJSON(mr);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, mr.getKeyJson(), document);
		return response.rev;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteMetaRelationship(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationshipDto deleteMetaRelationship(UUID uuid, String version) throws Exception {
		String json = couch.database().getDocument(database, uuid.asJsonId());
		MetaRelationshipDto dto = Serialise.unmarshalFromJson(json, MetaRelationshipDto.class);
		couch.database().deleteDocument(database, uuid.asJsonId(), version);
		return dto;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationships()
	 */
	@Override
	public Collection<MetaRelationshipDto> getMetaRelationships() throws Exception {
		//_design/meta_entities/_view/all?include_docs=true
		
		String json = couch.database().queryView(database,"meta_relationships", "all", CouchDB.Query.INCLUDE_DOCS);
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		List<MetaRelationshipDto> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			MetaRelationshipDto dto = Serialise.unmarshalFromJson(doc, MetaRelationshipDto.class);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationshipCount()
	 */
	@Override
	public int getMetaRelationshipCount() throws Exception {
		//_design/meta_entities/_view/count
		String json = couch.database().queryView(database,"meta_relationships", "count");
		return Helpers.getCountFrom(json);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationshipsFor(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Set<MetaRelationshipDto> getMetaRelationshipsFor(UUID metaEntityKey) throws Exception {
		
		Set<MetaRelationshipDto> dtos = new HashSet<>();
		
		while(metaEntityKey != null) {
			dtos.addAll(getDeclaredMetaRelationshipsFor(metaEntityKey));
			MetaEntityDto me = getMetaEntity(metaEntityKey);
			metaEntityKey = me.getBase();
		}
		
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getDeclaredMetaRelationshipsFor(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Set<MetaRelationshipDto> getDeclaredMetaRelationshipsFor(UUID metaEntityKey) throws Exception {
		String json = couch.database().queryView(database,"meta_relationships", "byMetaEntity", 
				new CouchDB.Query()
				.add("key", metaEntityKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		Set<MetaRelationshipDto> dtos = new HashSet<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			MetaRelationshipDto dto = Serialise.unmarshalFromJson(doc, MetaRelationshipDto.class);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getDeleteDependencies(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DeleteDependenciesListDto getDeleteDependencies(UUID metaEntityKey) throws Exception {
		
		DeleteDependenciesListDto dependencies = new DeleteDependenciesListDto();

		// Get this ME and mark for deletion
		MetaEntityDto me = getMetaEntity(metaEntityKey);
		DeleteProxyDto proxy = new DeleteProxyDto();
		proxy.setItemType("metaEntity");
		proxy.setItemKey(me.getKey());
		proxy.setVersion(me.getVersion().getVersion());
		dependencies.getDependencies().add(proxy);

		// A bit of book-keeping to check which meta-entities need to be checked to see if they're 
		// the base for a derived meta entity.  In which case those need to be deleted too and also
		// checked to see if they're base of a further layer of derived meta entities and so on.
		Set<UUID> toCheck = new HashSet<>();
		Set<UUID> checked = new HashSet<>();  // single inheritence so should never check twice anyway but...
		
		toCheck.add(metaEntityKey);

		while(!toCheck.isEmpty()) {
			UUID key = toCheck.iterator().next();
			toCheck.remove(key);

			if(checked.contains(key)) {
				continue;
			}
			
			// Look for any meta-entities that are derived from the one being deleted.
			// By derived - they have a base meta entity that's being deleted.
			String json = couch.database().queryView(database,"meta_entities", "byBase", 
					new CouchDB.Query()
					.add("key", key.asJsonId())
					.add(CouchDB.Query.INCLUDE_DOCS)
					.toString());
			JsonNode root = Serialise.parseToJsonTree(json);
			JsonNode rows = root.get("rows");
			assert(rows.isArray());
			for(JsonNode row : rows) {
				JsonNode doc = row.get("doc");
				me = Serialise.unmarshalFromJson(doc, MetaEntityDto.class);

				proxy = new DeleteProxyDto();
				proxy.setItemType("metaEntity");
				proxy.setItemKey(me.getKey());
				proxy.setVersion(me.getVersion().getVersion());
				dependencies.getDependencies().add(proxy);
				
				toCheck.add(me.getKey()); // in case these are base of further derived.
			}

			checked.add(key); // this key now processed.
			// TODO make sure meta-relationships that reference this meta entity are added.
			
		}
		
		return dependencies;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		Helpers.deleteViewContents(couch, database, "meta_entities", "all");
		Helpers.deleteViewContents(couch, database, "meta_relationships", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getDerivedMetaEntities(alvahouse.eatool.util.UUID)
	 */
	@Override
	public Collection<MetaEntityDto> getDerivedMetaEntities(UUID metaEntityKey) throws Exception {
		// Any meta entities with a base reference that corresponds to this key?
		String json = couch.database().queryView(database,"meta_entities", "byBase", 
				new CouchDB.Query()
				.add("key", metaEntityKey.asJsonId())
				.add(CouchDB.Query.INCLUDE_DOCS)
				.toString());
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		List<MetaEntityDto> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			MetaEntityDto me = Serialise.unmarshalFromJson(doc, MetaEntityDto.class);
			me.getVersion().setVersion(me.getRev());
			dtos.add(me);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getDefinedTypes()
	 */
	@Override
	public Collection<ExtensibleMetaPropertyTypeDto> getDefinedTypes() throws Exception {
		String json = couch.database().queryView(database,"meta_property_types", "all", CouchDB.Query.INCLUDE_DOCS);
		JsonNode root = Serialise.parseToJsonTree(json);
		
		int rowCount = root.get("total_rows").asInt();
		List<ExtensibleMetaPropertyTypeDto> dtos = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert(rows.isArray());
		for(JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			ExtensibleMetaPropertyTypeDto dto = Serialise.unmarshalFromJson(doc, ExtensibleMetaPropertyTypeDto.class);
			dto.getVersion().update(dto.getRev());
			dtos.add(dto);
		}
		return dtos;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addType(alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto)
	 */
	@Override
	public String addType(ExtensibleMetaPropertyTypeDto mpt) throws Exception {
		String document = Serialise.marshalToJSON(mpt);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, mpt.getKeyJson(), document);
		String revision = response.rev;
		return revision;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateType(alvahouse.eatool.repository.dto.metamodel.ExtensibleMetaPropertyTypeDto)
	 */
	@Override
	public String updateType(ExtensibleMetaPropertyTypeDto mpt) throws Exception {
		mpt.setRev(mpt.getVersion().getVersion());
		String document = Serialise.marshalToJSON(mpt);
		CouchDB.PutDocumentResponse response = couch.database().putDocument(database, mpt.getKeyJson(), document);
		return response.rev;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteType(UUID key, String version) throws Exception {
		couch.database().deleteDocument(database, key.asJsonId(), version);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteDefinedTypes()
	 */
	@Override
	public void deleteDefinedTypes() throws Exception {
		Helpers.deleteViewContents(couch, database, "meta_property_types", "all");
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getKey()
	 */
	@Override
	public UUID getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#setKey(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) throws Exception {
		String json = couch.database().getDocument(database, MetaModelDto.KEY);
		MetaModelDto dto = Serialise.unmarshalFromJson(json, MetaModelDto.class);
		dto.setKey(uuid);
		json = Serialise.marshalToJSON(dto);
		couch.database().putDocument(database, MetaModelDto.KEY, json);
		this.key = uuid;
	}

	// Class to hold data specific to the meta model (rather than its contents) such as it's UUID
	// and possibly later security information.
	private static class MetaModelDto {
		private UUID key;
		private String rev;
		static final String KEY = "metaModel";
		
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
