/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alvahouse.eatool.repository.dto.DeleteDependenciesListDto;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.repository.dto.model.EntityDto;
import alvahouse.eatool.repository.dto.model.RelationshipDto;
import alvahouse.eatool.repository.model.RelationshipDeleteProxy;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ModelPersistenceMemory implements ModelPersistence {

	private UUID uuid;
	private Map<UUID, EntityDto> entities = new HashMap<>();
	private Map<UUID, RelationshipDto> relationships = new HashMap<>();
	private Map<UUID, List<EntityDto>> entityCacheByType = new HashMap<>(); // good candidate for weak ref.
	private Map<UUID, List<RelationshipDto>> relationshipCacheByType = new HashMap<>(); // ditto

	/**
	 * 
	 */
	public ModelPersistenceMemory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#dispose()
	 */
	@Override
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#getEntity(alvahouse.
	 * eatool.util.UUID)
	 */
	@Override
	public EntityDto getEntity(UUID uuid) {
		EntityDto e = entities.get(uuid);
		if(e == null) {
			throw new IllegalArgumentException("Key " + uuid + " does not reference an entity in the model");
		}
		return e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#addEntity(alvahouse.
	 * eatool.repository.model.Entity)
	 */
	@Override
	public void addEntity(EntityDto e) throws Exception {
		if (entities.containsKey(e.getKey()))
			throw new IllegalStateException("Entity " + e.getKey() + " already exists in model");
		entities.put(e.getKey(), e);
		entityCacheByType.remove(e.getMetaEntityKey());
		//System.out.println("Added Entity " + e.getKey() + " to model");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#updateEntity(alvahouse.
	 * eatool.repository.model.Entity)
	 */
	@Override
	public void updateEntity(EntityDto e) throws Exception {
		entities.put(e.getKey(), e);
		entityCacheByType.remove(e.getMetaEntityKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#deleteEntity(alvahouse.
	 * eatool.util.UUID)
	 */
	@Override
	public EntityDto deleteEntity(UUID uuid) throws Exception {
		EntityDto e = entities.remove(uuid);
		if (e == null)
			throw new IllegalStateException("Entity " + uuid + " cannot be deleted - does not exist");

		// Invalidate cache for this type.
		entityCacheByType.remove(e.getMetaEntityKey());
		return e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntities()
	 */
	@Override
	public Collection<EntityDto> getEntities() {
		ArrayList<EntityDto> copy = new ArrayList<>(entities.size());
		copy.addAll(entities.values());
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntityCount()
	 */
	@Override
	public int getEntityCount() {
		return entities.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntitiesOfType(
	 * alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public List<EntityDto> getEntitiesOfType(UUID metaEntityKey) {
		List<EntityDto> cache = entityCacheByType.get(metaEntityKey);
		if (cache == null) { // not cached.
			cache = new LinkedList<EntityDto>();
			for (EntityDto e : getEntities()) {
				if (e.getMetaEntityKey().equals(metaEntityKey)) {
					cache.add(e);
				}
			}
			entityCacheByType.put(metaEntityKey, cache);
		}
		ArrayList<EntityDto> copy = new ArrayList<>(cache.size());
		copy.addAll(cache);
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationship(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public RelationshipDto getRelationship(UUID uuid) {
		RelationshipDto r = relationships.get(uuid);
		if(r == null) {
			throw new IllegalArgumentException("Key " + uuid + " does not reference relationship in the model");
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#addRelationship(
	 * alvahouse.eatool.repository.model.Relationship)
	 */
	@Override
	public void addRelationship(RelationshipDto r) throws Exception {
		if (relationships.containsKey(r.getKey()))
			throw new IllegalStateException("Relationship " + r.getKey() + " already exists in model");

		relationships.put(r.getKey(), r);
		relationshipCacheByType.remove(r.getMetaRelationshipKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#updateRelationship(
	 * alvahouse.eatool.repository.model.Relationship)
	 */
	@Override
	public void updateRelationship(RelationshipDto r) throws Exception {
		relationships.put(r.getKey(), r);
		relationshipCacheByType.remove(r.getMetaRelationshipKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteRelationship(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public RelationshipDto deleteRelationship(UUID uuid) throws Exception {
		RelationshipDto r = relationships.remove(uuid);
		if (r == null)
			throw new IllegalStateException("Relationship " + uuid + " cannot be deleted - does not exist");

		// Invalidate cache for this type.
		relationshipCacheByType.remove(r.getMetaRelationshipKey());
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationships()
	 */
	@Override
	public Collection<RelationshipDto> getRelationships() {
		ArrayList<RelationshipDto> copy = new ArrayList<>(relationships.size());
		copy.addAll(relationships.values());
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#getRelationshipCount()
	 */
	@Override
	public int getRelationshipCount() {
		return relationships.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#getRelationshipsOfType(
	 * alvahouse.eatool.repository.metamodel.MetaRelationship)
	 */
	@Override
	public List<RelationshipDto> getRelationshipsOfType(UUID metaRelationshipKey) {
		List<RelationshipDto> cache = relationshipCacheByType.get(metaRelationshipKey);
		if (cache == null) { // not cached.
			cache = new LinkedList<RelationshipDto>();
			for (RelationshipDto r : getRelationships()) {
				if (r.getMetaRelationshipKey().equals(metaRelationshipKey))
					cache.add(r);
			}
		}
		relationshipCacheByType.put(metaRelationshipKey, cache);
		
		ArrayList<RelationshipDto> copy = new ArrayList<>(cache.size());
		copy.addAll(cache);
		return copy;
	}

	
	/**
	 * This gets the set of relationships that are connected to this entity in the
	 * model that the entity belongs to. Note - this is in no-way optimised.
	 * 
	 * @param model is the model to use to query for the relationships.
	 * @param e     e is the entity we want the relationships for.
	 * @return the set of connected relationships.
	 */
	@Override
    public Set<RelationshipDto> getConnectedRelationships(UUID entityKey) throws Exception{
        Set<RelationshipDto> rels = new HashSet<>();
        for(RelationshipDto rel : getRelationships()) {
            if(rel.getStart().getConnects().equals(entityKey) ||
                rel.getFinish().getConnects().equals(entityKey)) {
                rels.add(rel);
            }
        }
        return rels;
    }

    /** This gets the set of relationships that are connected to this entity in the model
     * that the entity belongs to.  Note - this is in no-way optimised.
     * @param model is the model to use to query for the relationships.
     * @param e e is the entity we want the relationships for.
     * @param meta is used to restrict the set of relationships to this particular type.
     * @return the set of connected relationships.
     */
    @Override
    public Set<RelationshipDto> getConnectedRelationshipsOf( UUID entityKey, UUID metaRelationshipKey) throws Exception {
        Set<RelationshipDto> rels = new HashSet<>();
        for(RelationshipDto rel :  getRelationships()) {
            if((rel.getStart().getConnects().equals(entityKey) ||
                rel.getFinish().getConnects().equals(entityKey)) &&
                rel.getMetaRelationshipKey().equals(metaRelationshipKey)) {
                rels.add( rel);
            }
        }
        return rels;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#getDeleteDependencies(
	 * alvahouse.eatool.repository.base.DeleteDependenciesList,
	 * alvahouse.eatool.repository.model.Entity)
	 */
	@Override
	public DeleteDependenciesListDto getDeleteDependencies( UUID entityKey) throws Exception {
		DeleteDependenciesListDto dependencies = new DeleteDependenciesListDto();
		DeleteProxyDto proxy = new DeleteProxyDto();
		proxy.setItemType("entity");
		proxy.setItemKey(entityKey);
		proxy.setName("entity " + entityKey.toString()); // TODO - need to get name for entity rather than just the UUID
		dependencies.getDependencies().add(proxy);
		

		// Mark any relationships that depend on this entity for deletion
		Set<UUID> found = new HashSet<>(); // track what we've added to avoid duplicates
		for (RelationshipDto r : getRelationships()) {
			if (r.getStart().getConnects().equals(entityKey) || r.getFinish().getConnects().equals(entityKey)) {
				UUID relationshipKey = r.getKey();
				if (!found.contains(relationshipKey)) {
					proxy = new DeleteProxyDto();
					proxy.setItemType("relationship");
					proxy.setItemKey(relationshipKey);
					proxy.setName("relationship " + relationshipKey.toString()); // TODO - need to get name for entity rather than just the UUID
					dependencies.getDependencies().add(proxy);
					found.add(relationshipKey);
				}
			}
		}
		return dependencies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		entities.clear();
		relationships.clear();
		entityCacheByType.clear();
		relationshipCacheByType.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getKey()
	 */
	@Override
	public UUID getKey() {
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#setKey(alvahouse.eatool.
	 * util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) {
		this.uuid = uuid;

	}

}
