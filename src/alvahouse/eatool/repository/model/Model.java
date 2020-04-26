/*
 * Model.java
 *
 * Created on 11 January 2002, 19:57
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;


/**
 * The model holds the data for the repository.  Entities and Properties in this model correspond
 * to those defined in the corresponding MetaModel.
 * @author  rbp28668
 */
public class Model extends MetaModelChangeAdapter{

    private MetaModel meta;
    private Map<UUID,Entity> entities = new HashMap<UUID,Entity>();
    private Map<UUID,Relationship> relationships = new HashMap<UUID,Relationship>();
    private LinkedList<ModelChangeListener> listeners = new LinkedList<ModelChangeListener>();
    private Map<UUID,List<Entity>> entityCacheByType = new HashMap<UUID,List<Entity>>(); // good candidate for weak ref.
    private Map<UUID,List<Relationship>> relationshipCacheByType = new HashMap<UUID,List<Relationship>>(); // ditto

    /** Creates new Model */
    public Model(MetaModel meta) {
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
    public Entity getEntity(UUID uuid) {
        Entity e = (Entity)entities.get(uuid);
        return e;
    }

    /** Deletes an entity keyed by UUID.
     * @param uuid is the key of the entity to delete.
     * @throws IllegalStateException if deleting an entity not in the model.
     */
    public void deleteEntity(UUID uuid) {
        Entity e = (Entity)entities.remove(uuid);
        if(e == null)
            throw new IllegalStateException("Entity " + uuid + " cannot be deleted - does not exist");

        List<Entity> cache = entityCacheByType.get(e.getMeta().getKey());
        if(cache != null) cache.remove(e);
        
        fireEntityDeleted(e);
    }

    /** adds a new entity to the collection.
     * @param e is the entity to add.
     * @return the added entity (to allow chaining).
     * @throws IllegalStateException if the entity already exists in the model.
     */
    public Entity addEntity(Entity e) {
        if(entities.containsKey(e.getKey()))
            throw new IllegalStateException("Entity " + e.getKey() + " already exists in model");
        
        e.setModel(this);
        entities.put(e.getKey(), e);
        
        List<Entity> cache = entityCacheByType.get(e.getMeta().getKey());
        if(cache != null) cache.add(e);

        fireEntityAdded(e);
        return e;
    }
    
    /** Gets an iterator that iterates though all the entites.
     * @return an iterator for the entities.
     */
    public Collection<Entity> getEntities() {
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
    public List<Entity> getEntitiesOfType(MetaEntity meta) {
        List<Entity> cache = entityCacheByType.get(meta.getKey());
        if(cache == null) {   // not cached.
            cache = new LinkedList<Entity>();
            for(Entity e : getEntities()){
                if(e.getMeta().equals(meta)) {
                	cache.add(e);
                }
            }
            entityCacheByType.put(meta.getKey(), cache);
        }
        return cache;
    }
    
    /** Gets the relationship with the given key.
     * @param uuid is the key for the relationship.
     * @return the relationship corresponding to uuid or null if not in the model.
     */
    public Relationship getRelationship(UUID uuid) {
        Relationship mr = (Relationship)relationships.get(uuid);
        return mr;
    }

     /** adds a new relationship to the collection
     * @param r is the relationship to add
     * @return the added relationship (to allow chaining)
      * @throws IllegalStateException if the relationship is already in the model.
     */
    public Relationship addRelationship(Relationship r) {
        
        if(relationships.containsKey(r.getKey()))
            throw new IllegalStateException("Relationship " + r.getKey() + " already exists in model");

        r.setModel(this);
        relationships.put(r.getKey(),r);

        List<Relationship> cache = relationshipCacheByType.get(r.getMeta().getKey());
        if(cache != null) cache.add(r);
        
        fireRelationshipAdded(r);
        return r;
    }

    /** Deletes the relationship for a given key.
     * @param uuid is the key for the relationship to delete
     * @throws IllegalStateException if the key does not correspond to a
     * relationship in the model.
     */
    public void deleteRelationship(UUID uuid) {
        Relationship r = (Relationship)relationships.remove(uuid);
        if(r == null)
            throw new IllegalStateException("Relationship " + uuid + " cannot be deleted - does not exist");
        
        List<Relationship> cache = relationshipCacheByType.get(r.getMeta().getKey());
        if(cache != null) cache.remove(r);
        
        fireRelationshipDeleted(r);
    }
    
    /** Gets an iterator that iterates through all the relationships in the model.
     * @return an iterator.
     */
    public Collection<Relationship> getRelationships() {
        return relationships.values();
    }

    /**
     * Gets the number of relationships in the model.
     * @return the count of relationships.
     */
    public int getRelationshipCount() {
        return relationships.size();
    }

    /** Gets a list containing all the relationships in the model of a given
     * type.
     * @param meta is the type of relationship required.
     * @return a list of all the corresponding relationships.
     */
    public List<Relationship> getRelationshipsOfType(MetaRelationship meta) {
        List<Relationship> cache = relationshipCacheByType.get(meta.getKey());
        if(cache == null) {   // not cached.
            cache = new LinkedList<Relationship>();
            for(Relationship r : getRelationships()) {
                if(r.getMeta() == meta) cache.add(r);
            }
        }
        relationshipCacheByType.put(meta.getKey(), cache);
        return cache;
    }
    
      /**
     * Writes the Model out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Model");
        for(Entity e : getEntities()){
            e.writeXML(out);
        }
        
        for(Relationship r : getRelationships()) {
            r.writeXML(out);
        }
        out.stopEntity();
    }

    /** This gets gets the list of dependent objects that
     * are dependent on the object who's key is given.
     * This allows the user to be presented with a list of
     * all the objects that have to be deleted if any given
     * object is deleted and also collects the information
     * needed to do the delete
     * @param dependencies is the list of dependencies to add to.
     * @param e is the entity which will be deleted.
     */    
    public void getDeleteDependencies(DeleteDependenciesList dependencies, Relationship r) {
        dependencies.addDependency(new RelationshipDeleteProxy(r));
    }
    
    /** This gets gets the list of dependent objects that
     * are dependent on the object who's key is given.
     * This allows the user to be presented with a list of
     * all the objects that have to be deleted if any given
     * object is deleted and also collects the information
     * needed to do the delete
     * @param dependencies is the list of dependencies to add to.
     * @param e is the entity which will be deleted.
     */    
    public void getDeleteDependencies(DeleteDependenciesList dependencies, Entity e) {
        dependencies.addDependency(new EntityDeleteProxy(e));

        // Mark any relationships that depend on this entity for deletion
        for(Relationship r : getRelationships()) {
            if(r.start().connectsTo().equals(e) || r.finish().connectsTo().equals(e)) {
                if(!dependencies.containsTarget(r))
                    dependencies.addDependency(new RelationshipDeleteProxy(r));
            }
        }
    }

    /** Deletes the entire contents of the model */
    public void deleteContents() {
        entities.clear();
        relationships.clear();
        entityCacheByType.clear();
        relationshipCacheByType.clear();
        fireModelUpdated();
    }

    /**
     * Adds a change listener that will be notified of any changes
     * to the model.
     * @param listener is the ModelChangeListener to add.
     */
    public void addChangeListener(ModelChangeListener listener) {
        listeners.addLast(listener);
    }

    /**
     * Removes a change listener from the list.
     * @param listener is the ModelChangeListener to remove.
     */
    public void removeChangeListener(ModelChangeListener listener) {
        listeners.remove(listener);
    }


    /** Signals a major change in the model to any registered
     * listeners
     */
    public void fireModelUpdated() {
        ModelChangeEvent evt = new ModelChangeEvent(this);
        for(ModelChangeListener l : listeners){
            l.modelUpdated(evt);
        }
    }
    
    public void fireEntityAdded(Entity me){
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityAdded(evt);
        }
    }
    
    public void fireEntityChanged(Entity me){
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityChanged(evt);
        }
    }
    
    public void fireEntityDeleted(Entity me){ 
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityDeleted(evt);
        }
    }
        
    public void fireRelationshipAdded(Relationship mr){
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipAdded(evt);
        }
    }
    
    public void fireRelationshipChanged(Relationship mr){
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipChanged(evt);
        }
    }
    
    public void fireRelationshipDeleted(Relationship mr){
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipDeleted(evt);
        }
    }


    /** Proxy class for recording dependent -entities
     */
    private class EntityDeleteProxy implements IDeleteDependenciesProxy {
        /** Creates a new proxy for deleting dependent entities
         * @param me is the dependent entity
         */        
        public EntityDeleteProxy(Entity e) {
            entity = e;
        }
        
        /** gets the name of the dependent  entity
         * @return name for the dependent entity
         */        
        public String toString() {
            return "Entity " + entity.toString();
        }

        /** deletes the dependent entity
         */
        public void delete() {
            deleteEntity(entity.getKey());
        }
        
        /** gets the dependent entity
         * @return the dependent entity
         */        
        public Object getTarget() {
            return entity;
        }
        
        private Entity entity;
    }
    
    /** Proxy class for recording dependent -relationship
     */
    private class RelationshipDeleteProxy implements IDeleteDependenciesProxy {
        /** Creates a new proxy for deleting a dependent relationship
         * @param m is the dependent relationship
         */        
        public RelationshipDeleteProxy(Relationship r) {
            relationship = r;
        }
        
        /** gets the name of the dependent  relationship
         * @return name for the dependent relationship
         */        
        public String toString() {
            return "Relationship " + relationship.toString();
        }

        /** deletes the dependent relationship
         */
        public void delete() {
            deleteRelationship(relationship.getKey());
        }
        
        /** gets the dependent -relationship
         * @return the dependent -relationship
         */
        public Object getTarget() {
            return relationship;
        }
        
        private Relationship relationship;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#modelUpdated(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void modelUpdated(MetaModelChangeEvent e) {
        // Should only occur in situations where major change 
        // is occuring and that means deletion of everything.
        MetaModel meta = (MetaModel)e.getSource();
        if(meta.getMetaEntityCount() == 0 && meta.getMetaRelationshipCount() == 0){
            deleteContents();
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaEntityChanged(MetaModelChangeEvent e) {
        MetaEntity meta = (MetaEntity)e.getSource();
        revalidateEntities(meta);
    }

    /**
     * If a meta-entity changes any corresponding entities are likely to be
     * invalid. For example have extra or too few properties or properties of the
     * wrong type.  This checks the entities of the given type and revalidates them
     * and any entities of derived types.
     * @param meta is the change MetaEntity.
     */
    private void revalidateEntities(MetaEntity meta) {
        revalidateSingleLevel(meta);
        
        // Any derived entities will need to be revalidated also as they may
        // have gained/lost properties.
        MetaModel metaModel = meta.getModel();
        Collection<MetaEntity> derived = metaModel.getDerivedMetaEntities(meta);
        for(MetaEntity derivedMeta : derived){
           revalidateSingleLevel(derivedMeta);
        }
    }

    /**
     * Similar to revalidateEntities except it doesn't check the inheritance
     * hierarchy.
     * @param meta is the affected meta-entity.
     */
    private void revalidateSingleLevel(MetaEntity meta) {
        for(Entity entity : getEntitiesOfType(meta)){
            if(entity.revalidate(entity.getMeta())){
                fireEntityChanged(entity);
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaEntityDeleted(MetaModelChangeEvent e) {
        MetaEntity meta = (MetaEntity)e.getSource();
         for(Entity entity : getEntitiesOfType(meta)){
            deleteEntity(entity.getKey());
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaRelationshipChanged(MetaModelChangeEvent e) {
        MetaRelationship meta = (MetaRelationship)e.getSource();
        revalidateRelationships(meta);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaRelationshipDeleted(MetaModelChangeEvent e) {
        MetaRelationship meta = (MetaRelationship)e.getSource();
        for(Relationship r : getRelationshipsOfType(meta)){
            deleteRelationship(r.getKey());
	    }
    }

    /**
     * Revalidates all the relationships of a given type. If they
     * don't connect to the correct types then delete them.
     * @param meta is the type of relationships to check.
     */
    private void revalidateRelationships(MetaRelationship meta){
        MetaEntity start = meta.start().connectsTo();
        MetaEntity finish = meta.finish().connectsTo();
        for(Relationship r : getRelationshipsOfType(meta)){
            if( !(r.start().connectsTo().getMeta().equals(start)
                    && r.finish().connectsTo().getMeta().equals(finish))){
                deleteRelationship(r.getKey());
            } else {
                boolean changed = false;
                changed |= r.revalidate(r.getMeta());
                changed |= r.start().revalidate(r.start().getMeta());
                changed |= r.finish().revalidate(r.finish().getMeta());
                if(changed){
                    fireRelationshipChanged(r);
                }
                
            }
	    }
    }
 
 }
