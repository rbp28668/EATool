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

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.EntityDeleteProxy;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.RelationshipDeleteProxy;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ModelPersistenceMemory implements ModelPersistence {

	private UUID uuid;
	private Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
	private Map<UUID, Relationship> relationships = new HashMap<UUID, Relationship>();
	private Map<UUID, List<Entity>> entityCacheByType = new HashMap<UUID, List<Entity>>(); // good candidate for weak
																							// ref.
	private Map<UUID, List<Relationship>> relationshipCacheByType = new HashMap<UUID, List<Relationship>>(); // ditto

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
	public Entity getEntity(UUID uuid) {
		Entity e = entities.get(uuid);
		if(e == null) {
			throw new IllegalArgumentException("Key " + uuid + " does not reference an entity in the model");
		}
		return (Entity) e.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#addEntity(alvahouse.
	 * eatool.repository.model.Entity)
	 */
	@Override
	public void addEntity(Entity e) throws Exception {
		if (entities.containsKey(e.getKey()))
			throw new IllegalStateException("Entity " + e.getKey() + " already exists in model");
		e = (Entity) e.clone();
		entities.put(e.getKey(), e);
		entityCacheByType.remove(e.getMeta().getKey());
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
	public void updateEntity(Entity e) throws Exception {
		e = (Entity) e.clone();
		entities.put(e.getKey(), e);
		entityCacheByType.remove(e.getMeta().getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.ModelPersistence#deleteEntity(alvahouse.
	 * eatool.util.UUID)
	 */
	@Override
	public Entity deleteEntity(UUID uuid) throws Exception {
		Entity e = (Entity) entities.remove(uuid);
		if (e == null)
			throw new IllegalStateException("Entity " + uuid + " cannot be deleted - does not exist");

		// Invalidate cache for this type.
		entityCacheByType.remove(e.getMeta().getKey());
		return e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getEntities()
	 */
	@Override
	public Collection<Entity> getEntities() {
		ArrayList<Entity> copy = new ArrayList<>(entities.size());
		for(Entity e : entities.values()) {
			copy.add( (Entity) e.clone());
		}
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
	public List<Entity> getEntitiesOfType(MetaEntity meta) {
		UUID key = meta.getKey();
		List<Entity> cache = entityCacheByType.get(key);
		if (cache == null) { // not cached.
			cache = new LinkedList<Entity>();
			for (Entity e : getEntities()) {
				if (e.getMeta().equals(meta)) {
					cache.add(e);
				}
			}
			entityCacheByType.put(key, cache);
		}
		ArrayList<Entity> copy = new ArrayList<>(cache.size());
		for(Entity e : cache) {
			copy.add( (Entity)e.clone());
		}
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationship(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public Relationship getRelationship(UUID uuid) {
		Relationship r = relationships.get(uuid);
		if(r == null) {
			throw new IllegalArgumentException("Key " + uuid + " does not reference relationship in the model");
		}
		return (Relationship) r.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#addRelationship(
	 * alvahouse.eatool.repository.model.Relationship)
	 */
	@Override
	public void addRelationship(Relationship r) throws Exception {
		if (relationships.containsKey(r.getKey()))
			throw new IllegalStateException("Relationship " + r.getKey() + " already exists in model");

		r = (Relationship) r.clone();
		relationships.put(r.getKey(), r);
		relationshipCacheByType.remove(r.getMeta().getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#updateRelationship(
	 * alvahouse.eatool.repository.model.Relationship)
	 */
	@Override
	public void updateRelationship(Relationship r) throws Exception {
		r = (Relationship) r.clone();
		relationships.put(r.getKey(), r);
		relationshipCacheByType.remove(r.getMeta().getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#deleteRelationship(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public Relationship deleteRelationship(UUID uuid) throws Exception {
		Relationship r = (Relationship) relationships.remove(uuid);
		if (r == null)
			throw new IllegalStateException("Relationship " + uuid + " cannot be deleted - does not exist");

		// Invalidate cache for this type.
		relationshipCacheByType.remove(r.getMeta().getKey());
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.ModelPersistence#getRelationships()
	 */
	@Override
	public Collection<Relationship> getRelationships() {
		ArrayList<Relationship> copy = new ArrayList<>(relationships.size());
		for(Relationship r : relationships.values()) {
			copy.add( (Relationship) r.clone());
		}
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
	public List<Relationship> getRelationshipsOfType(MetaRelationship meta) {
		List<Relationship> cache = relationshipCacheByType.get(meta.getKey());
		if (cache == null) { // not cached.
			cache = new LinkedList<Relationship>();
			for (Relationship r : getRelationships()) {
				if (r.getMeta().equals(meta))
					cache.add(r);
			}
		}
		relationshipCacheByType.put(meta.getKey(), cache);
		
		ArrayList<Relationship> copy = new ArrayList<>(cache.size());
		for(Relationship r : cache) {
			copy.add( (Relationship)r.clone());
		}
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
    public Set<Relationship> getConnectedRelationships(Model model, Entity e) throws Exception{
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        Set<Relationship> rels = new HashSet<Relationship>();
        for(Relationship rel : model.getRelationships()) {
            if(rel.start().connectsTo().equals(e) ||
                rel.finish().connectsTo().equals(e)) {
                rels.add( (Relationship)rel.clone());
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
    public Set<Relationship> getConnectedRelationshipsOf(Model model, Entity e, MetaRelationship meta) throws Exception {
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        Set<Relationship> rels = new HashSet<Relationship>();
        for(Relationship rel :  model.getRelationships()) {
            if((rel.start().connectsTo().equals(e) ||
                rel.finish().connectsTo().equals(e)) &&
                rel.getMeta().equals(meta)) {
                rels.add( (Relationship)rel.clone());
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
	 * alvahouse.eatool.repository.model.Relationship)
	 */
	@Override
	public void getDeleteDependencies(Model model, DeleteDependenciesList dependencies, Relationship r) {
		dependencies.addDependency(new RelationshipDeleteProxy(model, r));
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
	public void getDeleteDependencies(Model model, DeleteDependenciesList dependencies, Entity e) throws Exception {
		dependencies.addDependency(new EntityDeleteProxy(model, e));

		// Mark any relationships that depend on this entity for deletion
		for (Relationship r : getRelationships()) {
			if (r.start().connectsTo().equals(e) || r.finish().connectsTo().equals(e)) {
				if (!dependencies.containsTarget(r))
					dependencies.addDependency(new RelationshipDeleteProxy(model, r));
			}
		}
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
