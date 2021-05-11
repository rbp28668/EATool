/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.util.UUID;

/**
 * ModelPersistence provides persistence services to the model.  Interface as may be 
 * implemented by in memory, database, remote or other variations of persisting
 * and querying the model.
 * Aim is to persist the model at a reasonably granular size, i.e. 
 * entities and relationships rather than their individual properties and roles.
 * May also be used as a basis for adapters such as broadcast of changes, data
 * security or logging and audit.
 * @author bruce_porteous
 *
 */
public interface ModelPersistence {

	/**
	 * Clears down the model.
	 */
	public void dispose();

	/**
	 * Looks up an entity by UUID.
	 * 
	 * @param uuid is the key of the entity to get
	 * @return the corresponding entity, or null if not in the model
	 */
	public Entity getEntity(UUID uuid);

	/**
	 * adds a new entity to the collection.
	 * 
	 * @param e is the entity to add.
	 * @throws IllegalStateException if the entity already exists in the model.
	 */
	public void addEntity(Entity e) throws Exception;

	/**
	 * Updates and existing entity in the model.
	 * 
	 * @param e is the entity to update.
	 * @throws IllegalStateException if the entity already exists in the model.
	 */
	public void updateEntity(Entity e) throws Exception;

	/**
	 * Deletes an entity keyed by UUID.
	 * 
	 * @param uuid is the key of the entity to delete.
	 * @return the deleted Entity
	 * @throws IllegalStateException if deleting an entity not in the model.
	 */
	public Entity deleteEntity(UUID uuid) throws Exception;

	/**
	 * Gets an iterator that iterates though all the entites.
	 * 
	 * @return an iterator for the entities.
	 */
	public Collection<Entity> getEntities();

	/**
	 * DEBUG only
	 * 
	 * @return
	 */
	public int getEntityCount();

	/**
	 * Gets a list of entities corresponding to a given meta-entity.
	 * 
	 * @param meta is the meta-entity we want the entities for.
	 * @return a list of all the entities of the given type.
	 */
	public List<Entity> getEntitiesOfType(MetaEntity meta);

	/**
	 * Gets the relationship with the given key.
	 * 
	 * @param uuid is the key for the relationship.
	 * @return the relationship corresponding to uuid or null if not in the model.
	 */
	public Relationship getRelationship(UUID uuid);

	/**
	 * adds a new relationship to the collection
	 * 
	 * @param r is the relationship to add
	 * @throws IllegalStateException if the relationship is already in the model.
	 */
	public void addRelationship(Relationship r) throws Exception;

	/**
	 * updates an existing relationship.
	 * 
	 * @param r is the relationship to update
	 * @throws IllegalStateException if the relationship is already in the model.
	 */
	public void updateRelationship(Relationship r) throws Exception;

	/**
	 * Deletes the relationship for a given key.
	 * 
	 * @param uuid is the key for the relationship to delete
	 * @return the deleted relationship.
	 * @throws IllegalStateException if the key does not correspond to a
	 *                               relationship in the model.
	 */
	public Relationship deleteRelationship(UUID uuid) throws Exception;

	/**
	 * Gets an iterator that iterates through all the relationships in the model.
	 * 
	 * @return an iterator.
	 */
	public Collection<Relationship> getRelationships();

	/**
	 * Gets the number of relationships in the model.
	 * 
	 * @return the count of relationships.
	 */
	public int getRelationshipCount();

	/**
	 * Gets a list containing all the relationships in the model of a given type.
	 * 
	 * @param meta is the type of relationship required.
	 * @return a list of all the corresponding relationships.
	 */
	public List<Relationship> getRelationshipsOfType(MetaRelationship meta);

	/**
	 * This gets the set of relationships that are connected to this entity in the
	 * model that the entity belongs to. Note - this is in no-way optimised.
	 * 
	 * @param model is the model to use to query for the relationships.
	 * @param e     e is the entity we want the relationships for.
	 * @return the set of connected relationships.
	 */
	public Set<Relationship> getConnectedRelationships(Model model, Entity e) throws Exception;

	/**
	 * This gets the set of relationships that are connected to this entity in the
	 * model that the entity belongs to. Note - this is in no-way optimised.
	 * 
	 * @param model is the model to use to query for the relationships.
	 * @param e     e is the entity we want the relationships for.
	 * @param meta  is used to restrict the set of relationships to this particular
	 *              type.
	 * @return the set of connected relationships.
	 */
	public Set<Relationship> getConnectedRelationshipsOf(Model model, Entity e, MetaRelationship meta) throws Exception;

	/**
	 * This gets gets the list of dependent objects that are dependent on the object
	 * who's key is given. This allows the user to be presented with a list of all
	 * the objects that have to be deleted if any given object is deleted and also
	 * collects the information needed to do the delete
	 * 
	 * @param dependencies is the list of dependencies to add to.
	 * @param e            is the entity which will be deleted.
	 */
	public void getDeleteDependencies(Model model, DeleteDependenciesList dependencies, Relationship r) throws Exception;

	/**
	 * This gets gets the list of dependent objects that are dependent on the object
	 * who's key is given. This allows the user to be presented with a list of all
	 * the objects that have to be deleted if any given object is deleted and also
	 * collects the information needed to do the delete
	 * 
	 * @param dependencies is the list of dependencies to add to.
	 * @param e            is the entity which will be deleted.
	 */
	public void getDeleteDependencies(Model model, DeleteDependenciesList dependencies, Entity e) throws Exception;

	/** Deletes the entire contents of the model */
	public void deleteContents() throws Exception;

	// Unique key for the whole model.
	public UUID getKey();

	public void setKey(UUID uuid);

}
