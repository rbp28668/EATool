/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;
import java.util.Set;

import alvahouse.eatool.repository.dto.DeleteDependenciesListDto;
import alvahouse.eatool.repository.dto.metamodel.MetaEntityDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
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
/**
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
    public MetaEntityDto getMetaEntity(UUID uuid) throws Exception;
    
    /** adds a new meta-entity to the collection
     * @param me is the meta-entity to add
     * @return the added meta-entity (to allow chaining)
     * @throws Exception 
     */
    public void addMetaEntity(MetaEntityDto me) throws Exception;
    
    /** Gets a Collection of all the MetaEntities in the meta-model.
     * @return Collection of MetaEntity.
     */
    public Collection<MetaEntityDto> getMetaEntities() throws Exception;
    /**
     * Gets the count of the number of MetaEntities in the meta model.
     * @return meta entity count.
     */
    public int getMetaEntityCount() throws Exception;

    /** updates a meta-entity in the collection
     * @param me is the meta-entity to update
     * @return the updated meta-entity (to allow chaining)
     * @throws Exception 
     */
    public void updateMetaEntity(MetaEntityDto me) throws Exception;

    
    /** deletes a meta-entity from the meta-model
     * @param uuid is the identifier of the meta-entity to delete
     * @throws Exception 
     */
    public MetaEntityDto deleteMetaEntity(UUID uuid) throws Exception;
    
    /**
     * Gets a MetaRelationship from the meta model, creating a new one if there
     * is not an existing MetaRelationship with the given UUID
     * @param uuid is the key for the existing (or new) MetaRelationship
     * @return the MetaRelationship keyed by uuid
     */
    public MetaRelationshipDto getMetaRelationship(UUID uuid) throws Exception;
    
    /** adds a new meta-relationship to the collection
     * @param mr is the meta-relationship to add
     * @return the added meta-relationship (to allow chaining)
     * @throws Exception 
     */
    public void addMetaRelationship(MetaRelationshipDto mr) throws Exception;
    
    /** updates a meta relationship in the collection
     * @param mr is the meta-relationship to update
     * @return the updated meta relationship (to allow chaining)
     * @throws Exception 
     */
    public void updateMetaRelationship(MetaRelationshipDto mr) throws Exception;

    /** deletes a meta-relationship from the meta-model
     * @param uuid is the key for the meta-relationship to delete
     * @throws Exception 
     */
    public MetaRelationshipDto deleteMetaRelationship(UUID uuid) throws Exception;
    
    /** Get the collection of MetaRelationships in the meta-model.
     * @return a Collection containing MetaRelationship.
     */
    public Collection<MetaRelationshipDto> getMetaRelationships() throws Exception;

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
    public Set<MetaRelationshipDto> getMetaRelationshipsFor(UUID metaEntityKey) throws Exception;

    /**
     * Gets a collection of MetaRelationships that are declared to connect to a given
     * MetaEntity. This ignores any inheritence in the Meta-entity. 
     * Useful for creating a new Relationship to provide a pick
     * list of possible relationship types.
     * @param me is the MetaEntity to connect to.
     * @return a Set of MetaRelationship. Maybe empty, never null.
     */
    public Set<MetaRelationshipDto> getDeclaredMetaRelationshipsFor(UUID metaEntityKey) throws Exception;

    // Nothing needed here as nothing else in the metamodel depends on a meta relationship
//    /** This gets gets the list of dependent objects that
//     * are dependent on the object who's key is given.
//     * This allows the user to be presented with a list of
//     * all the objects that have to be deleted if any given
//     * object is deleted and also collects the information
//     * needed to do the delete
//     * @param dependencies is the list of dependencies to add to.
//     * @param mr is the meta-relationship to find the dependencies of.
//     */
//   public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaRelationshipDto mr) throws Exception ;

   /** This gets gets the list of dependent objects that
    * are dependent on the object who's key is given.
    * This allows the user to be presented with a list of
    * all the objects that have to be deleted if any given
    * object is deleted and also collects the information
    * needed to do the delete
    * @param dependencies is the list of dependencies to add to.
    * @param target is the meta-entity to find the dependencies of.
    */
    public DeleteDependenciesListDto getDeleteDependencies( UUID metaEntityKey) throws Exception;
    
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
    public Collection<MetaEntityDto> getDerivedMetaEntities(UUID metaEntityKey) throws Exception ;
    
    /**
     * getDefinedTypes gets the list of extensible meta property types that are known
     * to the persistence layer. These are all types that can be extended in the meta
     * model e.g. a controlled list.
     * @return a collection of ExtensibleMetaPropertyType. May be empty, never null.
     * @throws Exception
     */
    public Collection<ExtensibleMetaPropertyType> getDefinedTypes() throws Exception;
    
	/**
	 * Adds a single ExtensibleMetaPropertyType to the repository. The meta property
	 * type must not already exist in the repository.
	 * @param mpt is the ExtensibleMetaPropertyType to add to the repository.
	 * @throws Exception
	 */
	public void addType(ExtensibleMetaPropertyType mpt) throws Exception;

	/**
	 * Updates a single ExtensibleMetaPropertyType to the repository. The meta property
	 * type must already exist in the repository.
	 * @param mpt is the ExtensibleMetaPropertyType to update in the repository.
	 * @throws Exception
	 */
	public void updateType(ExtensibleMetaPropertyType mpt) throws Exception;

    /**
     * Removes an ExtensibleMetaPropertyType from the repository identified by its key.
     * @param key is the key of the type to remove.
     */
    public void deleteType(UUID key) throws Exception;

    /**
     * Removes all ExtensibleMetaPropertyTypes from the repository.
     */
    public void deleteDefinedTypes() throws Exception;

    
	public UUID getKey() ;

	public void setKey(UUID uuid);
    
}
