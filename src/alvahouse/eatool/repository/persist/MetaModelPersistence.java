/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;
import java.util.Set;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;

/**
 * MetaModelPersistence provides persistence services to the meta-model.  Interface as may be 
 * implemented by in memory, database, remote or other variations of persisting
 * and querying the meta-model.
 * Aim is to persist the meta-model at a reasonably granular size, i.e. 
 * meta-entities and meta-relationships rather than their individual properties and roles.
 * May also be used as a basis for adapters such as broadcast of changes, data
 * security or logging and audit.
 * @author bruce_porteous
 *
 */
public interface MetaModelPersistence {

	
    /**
     * Gets a MetaEntity from the meta model.  Note, where inheritance is used in the meta model
     * this should also return any base meta entities.
     * @param uuid is the key for the existing (or new) MetaEntity
     * @return the MetaEntity keyed by uuid
     */
    public MetaEntity getMetaEntity(UUID uuid) throws Exception;
    
    /** adds a new meta-entity to the collection
     * @param me is the meta-entity to add
     * @return the added meta-entity (to allow chaining)
     * @throws Exception 
     */
    public MetaEntity addMetaEntity(MetaEntity me) throws Exception;
    
    /** Gets a Collection of all the MetaEntities in the meta-model.
     * @return Collection of MetaEntity.
     */
    public Collection<MetaEntity> getMetaEntities() throws Exception;
    /**
     * Gets the count of the number of MetaEntities in the meta model.
     * @return meta entity count.
     */
    public int getMetaEntityCount() throws Exception;
    
    
    /** deletes a meta-entity from the meta-model
     * @param uuid is the identifier of the meta-entity to delete
     * @throws Exception 
     */
    public MetaEntity deleteMetaEntity(UUID uuid) throws Exception;
    
    /**
     * Gets a MetaRelationship from the meta model, creating a new one if there
     * is not an existing MetaRelationship with the given UUID
     * @param uuid is the key for the existing (or new) MetaRelationship
     * @return the MetaRelationship keyed by uuid
     */
    public MetaRelationship getMetaRelationship(UUID uuid) throws Exception;
    
    /** adds a new meta-relationship to the collection
     * @param mr is the meta-relationship to add
     * @return the added meta-relationship (to allow chaining)
     * @throws Exception 
     */
    public MetaRelationship addMetaRelationship(MetaRelationship mr) throws Exception;
    
    /** deletes a meta-relationship from the meta-model
     * @param uuid is the key for the meta-relationship to delete
     * @throws Exception 
     */
    public MetaRelationship deleteMetaRelationship(UUID uuid) throws Exception;
    
    /** Get the collection of MetaRelationships in the meta-model.
     * @return a Collection containing MetaRelationship.
     */
    public Collection<MetaRelationship> getMetaRelationships() throws Exception;

    /**
     * Gets the count of MetaRelationships.
     * @return the number of MetaRelationships.
     */
    public int getMetaRelationshipCount() throws Exception;
    /**
     * Gets a collection of MetaRelationships that connect to a given
     * MetaEntity.  Useful for creating a new Relationship to provide a pick
     * list of possible relationship types.
     * @param me is the MetaEntity to connect to.
     * @return a Set of MetaRelationship. Maybe empty, never null.
     */
    public Set<MetaRelationship> getMetaRelationshipsFor(MetaEntity me) throws Exception;

    /**
     * Gets a collection of MetaRelationships that are declared to connect to a given
     * MetaEntity. This ignores any inheritence in the Meta-entity. 
     * Useful for creating a new Relationship to provide a pick
     * list of possible relationship types.
     * @param me is the MetaEntity to connect to.
     * @return a Set of MetaRelationship. Maybe empty, never null.
     */
    public Set<MetaRelationship> getDeclaredMetaRelationshipsFor(MetaEntity me) throws Exception;
    
    /** This gets gets the list of dependent objects that
     * are dependent on the object who's key is given.
     * This allows the user to be presented with a list of
     * all the objects that have to be deleted if any given
     * object is deleted and also collects the information
     * needed to do the delete
     * @param dependencies is the list of dependencies to add to.
     * @param mr is the meta-relationship to find the dependencies of.
     */
   public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaRelationship mr) throws Exception ;

   /** This gets gets the list of dependent objects that
    * are dependent on the object who's key is given.
    * This allows the user to be presented with a list of
    * all the objects that have to be deleted if any given
    * object is deleted and also collects the information
    * needed to do the delete
    * @param dependencies is the list of dependencies to add to.
    * @param target is the meta-entity to find the dependencies of.
    */
    public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaEntity target) throws Exception;
    
    /**
     * Deletes the contents of the meta model.
     * @throws Exception 
     */
    public void deleteContents() throws Exception ;
 
    /**
     * getDerivedMetaEntites gets a list of MetaEntity that are derived
     * from the given MetaEntity.  Needed for tracking changes to the
     * meta-model as if any meta entity is changed then all its derived
     * meta-entities inherently change too.
     * @param meta is the MetaEntity to get derived MetaEntities for.
     * @return Collection of MetaEntity.  Collection may be empty, never null.
     */
    public Collection<MetaEntity> getDerivedMetaEntities(MetaEntity meta) throws Exception ;
    
	public UUID getKey() ;

	public void setKey(UUID uuid);
    
}
