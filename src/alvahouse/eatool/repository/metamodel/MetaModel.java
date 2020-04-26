/*
 * MetaModel.java
 * Project: EATool
 * Created on 23 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

public interface MetaModel {

    /**
     * Gets a MetaEntity from the meta model.
     * @param uuid is the key for the existing (or new) MetaEntity
     * @return the MetaEntity keyed by uuid
     */
    public abstract MetaEntity getMetaEntity(UUID uuid);

    /** adds a new meta-entity to the collection
     * @param me is the meta-entity to add
     * @return the added meta-entity (to allow chaining)
     */
    public abstract MetaEntity addMetaEntity(MetaEntity me);

    /** Gets a Collection of all the MetaEntities in the meta-model.
     * @return Collection of MetaEntity.
     */
    public abstract Collection<MetaEntity> getMetaEntities();

    /**
     * Gets the count of the number of MetaEntities in the meta model.
     * @return meta entity count.
     */
    public abstract int getMetaEntityCount();

    /** deletes a meta-entity from the meta-model
     * @param uuid is the identifier of the meta-entity to delete
     */
    public abstract void deleteMetaEntity(UUID uuid);

    /**
     * Gets a MetaRelationship from the meta model, creating a new one if there
     * is not an existing MetaRelationship with the given UUID
     * @param uuid is the key for the existing (or new) MetaRelationship
     * @return the MetaRelationship keyed by uuid
     */
    public abstract MetaRelationship getMetaRelationship(UUID uuid);

    /** adds a new meta-relationship to the collection
     * @param mr is the meta-relationship to add
     * @return the added meta-relationship (to allow chaining)
     */
    public abstract MetaRelationship addMetaRelationship(MetaRelationship mr);

    /** deletes a meta-relationship from the meta-model
     * @param uuid is the key for the meta-relationship to delete
     */
    public abstract void deleteMetaRelationship(UUID uuid);

    /** Get the collection of MetaRelationships in the meta-model.
     * @return a Collection containing MetaRelationship.
     */
    public abstract Collection<MetaRelationship> getMetaRelationships();

    /** gets the meta relationships as an array
     * @return an array containing the meta-relationships
     */
    public abstract MetaRelationship[] getMetaRelationshipsAsArray();

    /**
     * Gets the count of MetaRelationships.
     * @return the number of MetaRelationships.
     */
    public abstract int getMetaRelationshipCount();

    /**
     * Gets a collection of MetaRelationships that connect to a given
     * MetaEntity.  Useful for creating a new Relationship to provide a pick
     * list of possible relationship types.
     * @param me is the MetaEntity to connect to.
     * @return a Set of MetaRelationship. Maybe empty, never null.
     */
    public abstract Set<MetaRelationship> getMetaRelationshipsFor(MetaEntity me);

    /**
     * Gets a collection of MetaRelationships that are declared to connect to a given
     * MetaEntity. This ignores any inheritence in the Meta-entity. 
     * Useful for creating a new Relationship to provide a pick
     * list of possible relationship types.
     * @param me is the MetaEntity to connect to.
     * @return a Set of MetaRelationship. Maybe empty, never null.
     */
    public abstract Set<MetaRelationship> getDeclaredMetaRelationshipsFor(
            MetaEntity me);

    /** This gets gets the list of dependent objects that
     * are dependent on the object who's key is given.
     * This allows the user to be presented with a list of
     * all the objects that have to be deleted if any given
     * object is deleted and also collects the information
     * needed to do the delete
     * @param dependencies is the list of dependencies to add to.
     * @param mr is the meta-relationship to find the dependencies of.
     */
    public abstract void getDeleteDependencies(
            DeleteDependenciesList dependencies, MetaRelationship mr);

    /** This gets gets the list of dependent objects that
     * are dependent on the object who's key is given.
     * This allows the user to be presented with a list of
     * all the objects that have to be deleted if any given
     * object is deleted and also collects the information
     * needed to do the delete
     * @param dependencies is the list of dependencies to add to.
     * @param target is the meta-entity to find the dependencies of.
     */
    public abstract void getDeleteDependencies(
            DeleteDependenciesList dependencies, MetaEntity target);

    /**
     * Writes the MetaModel out as XML
     * @param out is the XMLWriterDirect to write the XML to
     * @throws IOException in the event of an io error
     */
    public abstract void writeXML(XMLWriter out) throws IOException;

    /**
     * Deletes the contents of the meta model.
     */
    public abstract void deleteContents();

    /**
     * Adds a MetaModelChangeListener to the list of listeners that
     * are notified when the meta model changes.
     * @param listener is the MetaModelChangeListener to add.
     */
    public abstract void addChangeListener(MetaModelChangeListener listener);

    /**
     * Removes a MetaModelChangeListener from the list of listeners.
     * @param listener is the MetaModelChangeListener to remove.
     */
    public abstract void removeChangeListener(MetaModelChangeListener listener);

    /** Signals a major change in the model to any registered
     * listeners
     */
    public abstract void fireModelUpdated();

    /**
     * Signals that a MetaEntity has been added to the MetaModel.
     * @param me is the new MetaEntity.
     */
    public abstract void fireMetaEntityAdded(MetaEntity me);

    /**
     * Signals to any listener that a MetaEntity has been changed.
     * @param me is the changed MetaEntity.
     */
    public abstract void fireMetaEntityChanged(MetaEntity me);

    /**
     * Signals to any listener that a MetaEntity has been deleted.
     * @param me is the deleted MetaEntity.
     */
    public abstract void fireMetaEntityDeleted(MetaEntity me);

    /**
     * Signals to any listener that a MetaRelationship has been added.
     * @param mr is the added MetaRelationship.
     */
    public abstract void fireMetaRelationshipAdded(MetaRelationship mr);

    /**
     * Signals to any listener that a MetaRelationship has been changed.
     * @param mr is the changed MetaRelationship.
     */
    public abstract void fireMetaRelationshipChanged(MetaRelationship mr);

    /**
     * Signals to any listener that a MetaRelationship has been deleted.
     * @param mr is the deleted MetaRelationship.
     */
    public abstract void fireMetaRelationshipDeleted(MetaRelationship mr);

    /**
     * getDerivedMetaEntites gets a list of MetaEntity that are derived
     * from the given MetaEntity.  Needed for tracking changes to the
     * meta-model as if any meta entity is changed then all its derived
     * meta-entities inherently change too.
     * @param meta is the MetaEntity to get derived MetaEntities for.
     * @return Collection of MetaEntity.  Collection may be empty, never null.
     */
    public abstract Collection<MetaEntity> getDerivedMetaEntities(
            MetaEntity meta);

}