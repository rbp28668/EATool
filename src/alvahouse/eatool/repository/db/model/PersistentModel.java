/*
 * Model.java
 *
 * Created on 11 January 2002, 19:57
 */

package alvahouse.eatool.repository.db.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.util.UUID;


/**
 * The model holds the data for the repository.  Entities and Properties in this model correspond
 * to those defined in the corresponding MetaModel.
 * @author  rbp28668
 */
public class PersistentModel extends MetaModelChangeAdapter{

    private MetaModel meta;
    private Map<UUID,PersistentEntity> entities = new HashMap<UUID,PersistentEntity>();
    private Map<UUID,PersistentRelationship> relationships = new HashMap<UUID,PersistentRelationship>();
 
    /** Creates new Model */
    public PersistentModel(MetaModel meta) {
        this.meta = meta;
        meta.addChangeListener(this);
    }
    
    /**
     * Clears down the model.
     */
    public void dispose(){
        meta.removeChangeListener(this);
        meta = null;
    }

    /**
     * Gets the metamodel underlying this model.
     * @return the meta-model.
     */
    public MetaModel getMeta(){
        return meta;
    }
    
    /** Looks up an entity by UUID.
     * @param uuid is the key of the entity to get
     * @return the corresponding entity, or null if not in the model
     */
    public PersistentEntity getEntity(UUID uuid) {
        PersistentEntity e = (PersistentEntity)entities.get(uuid);
        return e;
    }

    /** Deletes an entity keyed by UUID.
     * @param uuid is the key of the entity to delete.
     * @throws IllegalStateException if deleting an entity not in the model.
     */
    public void deleteEntity(UUID uuid) {
       entities.remove(uuid);
        
    }

    /** adds a new entity to the collection.
     * @param e is the entity to add.
     * @return the added entity (to allow chaining).
     * @throws IllegalStateException if the entity already exists in the model.
     */
    public PersistentEntity addEntity(PersistentEntity e) {
        if(entities.containsKey(e.getKey()))
            throw new IllegalStateException("Entity " + e.getKey() + " already exists in model");
        
        e.setModel(this);
        entities.put(e.getKey(), e);
        
        return e;
    }
    
    /** Gets an iterator that iterates though all the entites.
     * @return an iterator for the entities.
     */
    public Collection<PersistentEntity> getEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }
    
    /**
     * DEBUG only
     * @return
     */
    public int getEntityCount(){
        return entities.size();
    }
    
    /** Gets a list of entities corresponding to a given meta-entity.
     * @param meta is the meta-entity we want the entities for.
     * @return a list of all the entities of the given type.
     */
    public List<PersistentEntity> getEntitiesOfType(MetaEntity meta) {
        List<PersistentEntity> cache = new LinkedList<PersistentEntity>();
            for(PersistentEntity e : getEntities()){
                if(e.getMeta().equals(meta)) {
                	cache.add(e);
                }
            }
        return cache;
    }
    
    /** Gets the relationship with the given key.
     * @param uuid is the key for the relationship.
     * @return the relationship corresponding to uuid or null if not in the model.
     */
    public PersistentRelationship getRelationship(UUID uuid) {
        PersistentRelationship mr = (PersistentRelationship)relationships.get(uuid);
        return mr;
    }

     /** adds a new relationship to the collection
     * @param r is the relationship to add
     * @return the added relationship (to allow chaining)
      * @throws IllegalStateException if the relationship is already in the model.
     */
    public PersistentRelationship addRelationship(PersistentRelationship r) {
        
        if(relationships.containsKey(r.getKey()))
            throw new IllegalStateException("Relationship " + r.getKey() + " already exists in model");

        r.setModel(this);
        relationships.put(r.getKey(),r);

        return r;
    }

    /** Deletes the relationship for a given key.
     * @param uuid is the key for the relationship to delete
     * @throws IllegalStateException if the key does not correspond to a
     * relationship in the model.
     */
    public void deleteRelationship(UUID uuid) {
        PersistentRelationship r = (PersistentRelationship)relationships.remove(uuid);
        if(r == null)
            throw new IllegalStateException("Relationship " + uuid + " cannot be deleted - does not exist");
        
    }
    
    /** Gets an iterator that iterates through all the relationships in the model.
     * @return an iterator.
     */
    public Collection<PersistentRelationship> getRelationships() {
        return relationships.values();
    }

    /**
     * Gets the number of relationships in the model.
     * @return the count of relationships.
     */
    public int getRelationshipCount() {
        return relationships.size();
    }

//    /** Gets a list containing all the relationships in the model of a given
//     * type.
//     * @param meta is the type of relationship required.
//     * @return a list of all the corresponding relationships.
//     */
//    public List<PersistentRelationship> getRelationshipsOfType(MetaRelationship meta) {
//        List<PersistentRelationship> cache= new LinkedList<PersistentRelationship>();
//            for(PersistentRelationship r : getRelationships()) {
//                if(r.getMeta() == meta) cache.add(r);
//            }
//        return cache;
//    }
    


    /** Deletes the entire contents of the model */
    public void deleteContents() {
        entities.clear();
        relationships.clear();
    }

 
 }
