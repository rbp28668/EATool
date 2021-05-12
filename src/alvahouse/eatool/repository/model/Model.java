/*
 * Model.java
 *
 * Created on 11 January 2002, 19:57
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;


/**
 * The model holds the data for the repository.  Entities and Properties in this model correspond
 * to those defined in the corresponding MetaModel.
 * @author  rbp28668
 */
public class Model extends MetaModelChangeAdapter implements KeyedItem{

	private ModelPersistence persistence;
	private UUID uuid;
    private MetaModel meta;
    private LinkedList<ModelChangeListener> listeners = new LinkedList<ModelChangeListener>();

    /** Creates new Model */
    public Model(MetaModel meta, ModelPersistence persistence) {
        this.meta = meta;
        this.persistence = persistence;
        
        uuid = new UUID();
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
        Entity e = persistence.getEntity(uuid);
        e.setModel(this);
        return e;
    }

    /** adds a new entity to the collection.
     * @param e is the entity to add.
     * @return the added entity (to allow chaining).
     * @throws IllegalStateException if the entity already exists in the model.
     */
    public Entity addEntity(Entity e) throws Exception {
        e.setModel(this);
		String user = System.getProperty("user.name");
		e.getVersion().createBy(user);
        persistence.addEntity(e);
        fireEntityAdded(e);
        return e;
    }
    
    /**
     * Internal for loading repository from file etc.
     * @param e
     * @throws Exception
     */
    public void _add(Entity e) throws Exception {
        e.setModel(this);
        persistence.addEntity(e);
    }

    /** Updates and existing entity in the model.
     * @param e is the entity to update.
     * @return the added entity (to allow chaining).
     * @throws IllegalStateException if the entity already exists in the model.
     */
    public Entity updateEntity(Entity e) throws Exception {
        e.setModel(this);
		String user = System.getProperty("user.name");
		e.getVersion().modifyBy(user);
        persistence.updateEntity(e);
        fireEntityChanged(e);
        return e;
    }

    /** Deletes an entity keyed by UUID.
     * @param uuid is the key of the entity to delete.
     * @throws IllegalStateException if deleting an entity not in the model.
     */
    public void deleteEntity(UUID uuid) throws Exception {
    	Entity e = persistence.deleteEntity(uuid);
        fireEntityDeleted(e);
    }

    /** Gets an iterator that iterates though all the entites.
     * @return an iterator for the entities.
     */
    public Collection<Entity> getEntities() {
        Collection<Entity> entities = persistence.getEntities();
        for(Entity e : entities) {
        	e.setModel(this);
        }
        return entities;
    }
    
    /**
     * DEBUG only
     * @return
     */
    public int getEntityCount(){
        return persistence.getEntityCount();
    }
    
    /** Gets a list of entities corresponding to a given meta-entity.
     * @param meta is the meta-entity we want the entities for.
     * @return a list of all the entities of the given type.
     */
    public List<Entity> getEntitiesOfType(MetaEntity meta) {
        List<Entity> entities = persistence.getEntitiesOfType(meta);
        for(Entity e : entities) {
        	e.setModel(this);
        }
        return entities;
   }
    
    /** Gets the relationship with the given key.
     * @param uuid is the key for the relationship.
     * @return the relationship corresponding to uuid or null if not in the model.
     */
    public Relationship getRelationship(UUID uuid) {
        Relationship r =  persistence.getRelationship(uuid);
        r.setModel(this);
        return r;
    }

     /** adds a new relationship to the collection
     * @param r is the relationship to add
     * @return the added relationship (to allow chaining)
      * @throws IllegalStateException if the relationship is already in the model.
     */
    public Relationship addRelationship(Relationship r) throws Exception {
        r.setModel(this);
		String user = System.getProperty("user.name");
		r.getVersion().createBy(user);
        persistence.addRelationship(r);
        fireRelationshipAdded(r);
        return r;
    }

    /** Internal to add a relationship
    * @param r is the relationship to add
    * @return the added relationship (to allow chaining)
     * @throws IllegalStateException if the relationship is already in the model.
    */
   public void _add(Relationship r) throws Exception {
       r.setModel(this);
       persistence.addRelationship(r);
   }

    /** updates an existing relationship.
     * @param r is the relationship to update
     * @return the added relationship (to allow chaining)
      * @throws IllegalStateException if the relationship is already in the model.
     */
    public Relationship updateRelationship(Relationship r) throws Exception {
        r.setModel(this);
		String user = System.getProperty("user.name");
		r.getVersion().modifyBy(user);
        persistence.updateRelationship(r);
        fireRelationshipAdded(r);
        return r;
    }

    /** Deletes the relationship for a given key.
     * @param uuid is the key for the relationship to delete
     * @throws IllegalStateException if the key does not correspond to a
     * relationship in the model.
     */
    public void deleteRelationship(UUID uuid) throws Exception {
        Relationship r = persistence.deleteRelationship(uuid);
        fireRelationshipDeleted(r);
    }
    
    /** Gets an iterator that iterates through all the relationships in the model.
     * @return an iterator.
     */
    public Collection<Relationship> getRelationships() {
    	
        Collection<Relationship> relationships =  persistence.getRelationships();
        for(Relationship r : relationships) {
        	r.setModel(this);
        }
        return relationships;
    }

    /**
     * Gets the number of relationships in the model.
     * @return the count of relationships.
     */
    public int getRelationshipCount() {
        return persistence.getRelationshipCount();
    }

    /** Gets a list containing all the relationships in the model of a given
     * type.
     * @param meta is the type of relationship required.
     * @return a list of all the corresponding relationships.
     */
    public List<Relationship> getRelationshipsOfType(MetaRelationship meta) {
        List<Relationship> relationships =  persistence.getRelationshipsOfType(meta);
        for(Relationship r : relationships) {
        	r.setModel(this);
        }
        return relationships;

    }
    
	/**
	 * This gets the set of relationships that are connected to the given entity in this model. 
	 * 
	 * @param model is the model to use to query for the relationships.
	 * @param e     e is the entity we want the relationships for.
	 * @return the set of connected relationships.
	 */
	public Set<Relationship> getConnectedRelationships(Entity e) throws Exception{
        Set<Relationship> relationships =  persistence.getConnectedRelationships(this, e);
        for(Relationship r : relationships) {
        	r.setModel(this);
        }
        return relationships;

	}

	/**
	 * This gets the set of relationships that are connected to the given entity in this model. 
	 * The only relationships returned will correspond to the supplied MetaRelationship.
	 * 
	 * @param model is the model to use to query for the relationships.
	 * @param e     e is the entity we want the relationships for.
	 * @param meta  is used to restrict the set of relationships to this particular
	 *              type.
	 * @return the set of connected relationships.
	 */
	public Set<Relationship> getConnectedRelationshipsOf(Entity e, MetaRelationship meta) throws Exception{
		Set<Relationship> relationships = persistence.getConnectedRelationshipsOf(this, e, meta);
        for(Relationship r : relationships) {
        	r.setModel(this);
        }
        return relationships;
	}

    
      /**
     * Writes the Model out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Model");
        out.addAttribute("uuid", uuid.toString());
        
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
    public void getDeleteDependencies(DeleteDependenciesList dependencies, Relationship r) throws Exception {
        persistence.getDeleteDependencies(this, dependencies, r);
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
    public void getDeleteDependencies(DeleteDependenciesList dependencies, Entity e) throws Exception {
        persistence.getDeleteDependencies(this, dependencies, e);
    }

    /** Deletes the entire contents of the model */
    public void deleteContents()  throws Exception {
        persistence.deleteContents();
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
    public void fireModelUpdated() throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(this);
        for(ModelChangeListener l : listeners){
            l.modelUpdated(evt);
        }
    }
    
    private void fireEntityAdded(Entity me) throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityAdded(evt);
        }
    }
    
    private void fireEntityChanged(Entity me) throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityChanged(evt);
        }
    }
    
    private void fireEntityDeleted(Entity me) throws Exception{ 
        ModelChangeEvent evt = new ModelChangeEvent(me);
        for(ModelChangeListener l : listeners){
            l.EntityDeleted(evt);
        }
    }
        
    private void fireRelationshipAdded(Relationship mr) throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipAdded(evt);
        }
    }
    
    private void fireRelationshipChanged(Relationship mr) throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipChanged(evt);
        }
    }
    
    private void fireRelationshipDeleted(Relationship mr) throws Exception{
        ModelChangeEvent evt = new ModelChangeEvent(mr);
        for(ModelChangeListener l : listeners){
            l.RelationshipDeleted(evt);
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#modelUpdated(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void modelUpdated(MetaModelChangeEvent e) throws Exception {
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
    public void metaEntityChanged(MetaModelChangeEvent e)  throws Exception{
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
    private void revalidateEntities(MetaEntity meta) throws Exception {
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
    private void revalidateSingleLevel(MetaEntity meta) throws Exception {
        for(Entity entity : getEntitiesOfType(meta)){
            if(entity.revalidate(entity.getMeta())){
                fireEntityChanged(entity);
            }
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaEntityDeleted(MetaModelChangeEvent e) throws Exception {
        MetaEntity meta = (MetaEntity)e.getSource();
         for(Entity entity : getEntitiesOfType(meta)){
            deleteEntity(entity.getKey());
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaRelationshipChanged(MetaModelChangeEvent e)  throws Exception {
        MetaRelationship meta = (MetaRelationship)e.getSource();
        revalidateRelationships(meta);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    public void metaRelationshipDeleted(MetaModelChangeEvent e) throws Exception {
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
    private void revalidateRelationships(MetaRelationship meta) throws Exception{
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

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.base.KeyedItem#getKey()
	 */
	@Override
	public UUID getKey() {
		return uuid;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.base.KeyedItem#setKey(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) {
		this.uuid = uuid;
	}
 
 }
