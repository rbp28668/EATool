/*
 * MetaModel.java
 *
 * Created on 11 January 2002, 19:47
 */

package alvahouse.eatool.repository.metamodel;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Manages the meta-model for the repository.
 * 
 * @author rbp28668
 */
public class MetaModel implements KeyedItem {

	private MetaModelPersistence persistence;

	/** Change listeners */
	private LinkedList<MetaModelChangeListener> listeners = new LinkedList<MetaModelChangeListener>();

	/** Creates new MetaModel */
	public MetaModel(MetaModelPersistence persistence) {
		this.persistence = persistence;
	}

	/**
	 * Gets a MetaEntity from the meta model.
	 * 
	 * @param uuid is the key for the existing (or new) MetaEntity
	 * @return the MetaEntity keyed by uuid
	 */
	public MetaEntity getMetaEntity(UUID uuid) throws Exception {
		MetaEntity me =  persistence.getMetaEntity(uuid);
		me.setModel(this);
		return me;
	}

	/**
	 * adds a new meta-entity to the collection
	 * 
	 * @param me is the meta-entity to add
	 * @return the added meta-entity (to allow chaining)
	 * @throws Exception
	 */
	public MetaEntity addMetaEntity(MetaEntity me) throws Exception {
		me.setModel(this);
		String user = System.getProperty("user.name");
		me.getVersion().createBy(user);
		persistence.addMetaEntity(me);
		fireMetaEntityAdded(me);
		return me;
	}

	/**
	 * Internal to add a new meta-entity to the collection
	 * 
	 * @param me is the meta-entity to add
	 * @return the added meta-entity (to allow chaining)
	 * @throws Exception
	 */
	public void _add(MetaEntity me) throws Exception {
		me.setModel(this);
		persistence.addMetaEntity(me);
	}

	/**
	 * Gets a Collection of all the MetaEntities in the meta-model.
	 * 
	 * @return Collection of MetaEntity.
	 */
	public Collection<MetaEntity> getMetaEntities()  throws Exception{
		Collection<MetaEntity> mes = persistence.getMetaEntities();
		for(MetaEntity me : mes) {
			me.setModel(this);
		}
		return mes;
	}

	/**
	 * Gets the count of the number of MetaEntities in the meta model.
	 * 
	 * @return meta entity count.
	 */
	public int getMetaEntityCount()  throws Exception{
		return persistence.getMetaEntityCount();
	}

//    /** gets the Meta-entities as an array
//     * @return an array of MetaEntities
//     */    
//    public MetaEntity[] getMetaEntitiesAsArray() {
//        //return (MetaEntity [])metaEntities.values().toArray();
//        return (MetaEntity [])sortedEntities.toArray(new MetaEntity[sortedEntities.size()]);
//    }

	public MetaEntity updateMetaEntity(MetaEntity me) throws Exception{
		String user = System.getProperty("user.name");
		me.getVersion().modifyBy(user);
		persistence.updateMetaEntity(me);
		fireMetaEntityChanged(me);
		return me;
	}
	
	/**
	 * deletes a meta-entity from the meta-model
	 * 
	 * @param uuid is the identifier of the meta-entity to delete
	 * @throws Exception
	 */
	public void deleteMetaEntity(UUID uuid) throws Exception {
		MetaEntity me = persistence.deleteMetaEntity(uuid);
		fireMetaEntityDeleted(me);
	}

	/**
	 * Gets a MetaRelationship from the meta model, creating a new one if there is
	 * not an existing MetaRelationship with the given UUID
	 * 
	 * @param uuid is the key for the existing (or new) MetaRelationship
	 * @return the MetaRelationship keyed by uuid
	 */
	public MetaRelationship getMetaRelationship(UUID uuid)  throws Exception{
		MetaRelationship mr = persistence.getMetaRelationship(uuid);
		mr.setModel(this);
		return mr;
	}

	/**
	 * adds a new meta-relationship to the collection
	 * 
	 * @param mr is the meta-relationship to add
	 * @return the added meta-relationship (to allow chaining)
	 * @throws Exception
	 */
	public MetaRelationship addMetaRelationship(MetaRelationship mr) throws Exception {
		mr.setModel(this);
		String user = System.getProperty("user.name");
		mr.getVersion().createBy(user);
		persistence.addMetaRelationship(mr);
		fireMetaRelationshipAdded(mr);
		return mr;
	}

	/**
	 * Internal to adds a new meta-relationship to the collection
	 * 
	 * @param mr is the meta-relationship to add
	 * @return the added meta-relationship (to allow chaining)
	 * @throws Exception
	 */
	public void _add(MetaRelationship mr) throws Exception {
		mr.setModel(this);
		persistence.addMetaRelationship(mr);
	}

	/**
	 * Updates a meta relationship in the meta model.
	 * @param mr is the meta relationship to update.
	 * @return the meta relationship (for chaining)
	 * @throws Exception
	 */
	public MetaRelationship updateMetaRelationship(MetaRelationship mr) throws Exception {
		String user = System.getProperty("user.name");
		mr.getVersion().modifyBy(user);
		persistence.updateMetaRelationship(mr);
		fireMetaRelationshipChanged(mr);
		return mr;
	}
	
	/**
	 * deletes a meta-relationship from the meta-model
	 * 
	 * @param uuid is the key for the meta-relationship to delete
	 * @throws Exception
	 */
	public void deleteMetaRelationship(UUID uuid) throws Exception {
		MetaRelationship mr = persistence.deleteMetaRelationship(uuid);
		fireMetaRelationshipDeleted(mr);
	}

	/**
	 * Get the collection of MetaRelationships in the meta-model.
	 * 
	 * @return a Collection containing MetaRelationship.
	 */
	public Collection<MetaRelationship> getMetaRelationships()  throws Exception{
		Collection<MetaRelationship> mrs = persistence.getMetaRelationships();
		for(MetaRelationship mr : mrs) {
			mr.setModel(this);
		}
		return mrs;
	}

	/**
	 * gets the meta relationships as an array
	 * 
	 * @return an array containing the meta-relationships
	 */
	public MetaRelationship[] getMetaRelationshipsAsArray()  throws Exception{
		Collection<MetaRelationship> relationships = persistence.getMetaRelationships();
		for(MetaRelationship mr : relationships) {
			mr.setModel(this);
		}
		return (MetaRelationship[]) relationships.toArray(new MetaRelationship[relationships.size()]);
	}

	/**
	 * Gets the count of MetaRelationships.
	 * 
	 * @return the number of MetaRelationships.
	 */
	public int getMetaRelationshipCount()  throws Exception{
		return persistence.getMetaRelationshipCount();
	}

	/**
	 * Gets a collection of MetaRelationships that connect to a given MetaEntity.
	 * Useful for creating a new Relationship to provide a pick list of possible
	 * relationship types.
	 * 
	 * @param me is the MetaEntity to connect to.
	 * @return a Set of MetaRelationship. Maybe empty, never null.
	 */
	public Set<MetaRelationship> getMetaRelationshipsFor(MetaEntity me)  throws Exception{
		Set<MetaRelationship> relationships = persistence.getMetaRelationshipsFor(me);
		for(MetaRelationship mr : relationships) {
			mr.setModel(this);
		}
		return relationships;
	}

	/**
	 * Gets a collection of MetaRelationships that are declared to connect to a
	 * given MetaEntity. This ignores any inheritence in the Meta-entity. Useful for
	 * creating a new Relationship to provide a pick list of possible relationship
	 * types.
	 * 
	 * @param me is the MetaEntity to connect to.
	 * @return a Set of MetaRelationship. Maybe empty, never null.
	 */
	public Set<MetaRelationship> getDeclaredMetaRelationshipsFor(MetaEntity me)  throws Exception{
		Set<MetaRelationship> relationships = persistence.getDeclaredMetaRelationshipsFor(me);
		for(MetaRelationship mr : relationships) {
			mr.setModel(this);
		}
		return relationships;
	}

	/**
	 * This gets gets the list of dependent objects that are dependent on the object
	 * who's key is given. This allows the user to be presented with a list of all
	 * the objects that have to be deleted if any given object is deleted and also
	 * collects the information needed to do the delete
	 * 
	 * @param dependencies is the list of dependencies to add to.
	 * @param mr           is the meta-relationship to find the dependencies of.
	 */
	public void getDeleteDependencies(DeleteDependenciesList dependencies, MetaRelationship mr)  throws Exception{
		persistence.getDeleteDependencies(this, dependencies, mr);
	}

	/**
	 * This gets gets the list of dependent objects that are dependent on the object
	 * who's key is given. This allows the user to be presented with a list of all
	 * the objects that have to be deleted if any given object is deleted and also
	 * collects the information needed to do the delete
	 * 
	 * @param dependencies is the list of dependencies to add to.
	 * @param target       is the meta-entity to find the dependencies of.
	 */
	public void getDeleteDependencies(DeleteDependenciesList dependencies, MetaEntity target)  throws Exception{
		persistence.getDeleteDependencies(this, dependencies, target);
	}

	/**
	 * Writes the MetaModel out as XML
	 * 
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws Exception {
		out.startEntity("MetaModel");
		out.addAttribute("uuid", persistence.getKey().toString());

		Set<UUID> written = new HashSet<UUID>();
		for (MetaEntity me : persistence.getMetaEntities()) {
			writeMetaEntity(me, out, written);
		}

		for (MetaRelationship mr : persistence.getMetaRelationships()) {
			mr.writeXML(out);
		}
		out.stopEntity();
	}

	/**
	 * writes a meta entity to the XML output whilst ensuring that any base
	 * meta-entities that haven't already been written, are written out
	 * 
	 * @param me  is the meta-entity to write
	 * @param out is the output stream to write to
	 * @written is a set to record which meta-entities have been written
	 */
	private void writeMetaEntity(MetaEntity me, XMLWriter out, Set<UUID> written) throws IOException {

		if (written.contains(me.getKey()))
			return;

		try {
		if (me.hasBase())
			writeMetaEntity(me.getBase(), out, written);
		} catch (Exception e) {
			throw new IOException("Unable to get base meta entity to write as XML.", e);
		}
		me.writeXML(out);
		written.add(me.getKey());
	}

	/**
	 * Deletes the contents of the meta model.
	 * 
	 * @throws Exception
	 */
	public void deleteContents() throws Exception {
		persistence.deleteContents();
		fireModelUpdated();
	}

	/**
	 * Adds a MetaModelChangeListener to the list of listeners that are notified
	 * when the meta model changes.
	 * 
	 * @param listener is the MetaModelChangeListener to add.
	 */
	public void addChangeListener(MetaModelChangeListener listener) {
		listeners.addLast(listener);
	}

	/**
	 * Removes a MetaModelChangeListener from the list of listeners.
	 * 
	 * @param listener is the MetaModelChangeListener to remove.
	 */
	public void removeChangeListener(MetaModelChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Determines whether a given MetaModelChangeListener is included
	 * 
	 * @param listener is the MetaModelChangeListener to check.
	 */
	public boolean isActive(MetaModelChangeListener listener) {
		return listeners.contains(listener);
	}

	/**
	 * Signals a major change in the model to any registered listeners
	 * 
	 * @throws Exception
	 */
	public void fireModelUpdated() throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(this);
		for (MetaModelChangeListener l : listeners) {
			l.modelUpdated(evt);
		}
	}

	/**
	 * Signals that a MetaEntity has been added to the MetaModel.
	 * 
	 * @param me is the new MetaEntity.
	 */
	private void fireMetaEntityAdded(MetaEntity me) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
		for (MetaModelChangeListener l : listeners) {
			l.metaEntityAdded(evt);
		}
	}

	/**
	 * Signals to any listener that a MetaEntity has been changed.
	 * 
	 * @param me is the changed MetaEntity.
	 */
	private void fireMetaEntityChanged(MetaEntity me) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
		for (MetaModelChangeListener l : listeners) {
			l.metaEntityChanged(evt);
		}
	}

	/**
	 * Signals to any listener that a MetaEntity has been deleted.
	 * 
	 * @param me is the deleted MetaEntity.
	 */
	private void fireMetaEntityDeleted(MetaEntity me) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
		for (MetaModelChangeListener l : listeners) {
			l.metaEntityDeleted(evt);
		}
	}

	/**
	 * Signals to any listener that a MetaRelationship has been added.
	 * 
	 * @param mr is the added MetaRelationship.
	 */
	private void fireMetaRelationshipAdded(MetaRelationship mr) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
		for (MetaModelChangeListener l : listeners) {
			l.metaRelationshipAdded(evt);
		}
	}

	/**
	 * Signals to any listener that a MetaRelationship has been changed.
	 * 
	 * @param mr is the changed MetaRelationship.
	 */
	private void fireMetaRelationshipChanged(MetaRelationship mr) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
		for (MetaModelChangeListener l : listeners) {
			l.metaRelationshipChanged(evt);
		}
	}

	/**
	 * Signals to any listener that a MetaRelationship has been deleted.
	 * 
	 * @param mr is the deleted MetaRelationship.
	 */
	private void fireMetaRelationshipDeleted(MetaRelationship mr) throws Exception {
		MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
		for (MetaModelChangeListener l : listeners) {
			l.metaRelationshipDeleted(evt);
		}
	}

	/**
	 * getDerivedMetaEntites gets a list of MetaEntity that are derived from the
	 * given MetaEntity. Needed for tracking changes to the meta-model as if any
	 * meta entity is changed then all its derived meta-entities inherently change
	 * too.
	 * 
	 * @param meta is the MetaEntity to get derived MetaEntities for.
	 * @return Collection of MetaEntity. Collection may be empty, never null.
	 */
	public Collection<MetaEntity> getDerivedMetaEntities(MetaEntity meta)  throws Exception{
		return persistence.getDerivedMetaEntities(meta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.base.KeyedItem#getKey()
	 */
	@Override
	public UUID getKey() {
		return persistence.getKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.base.KeyedItem#setKey(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) {
		assert (uuid != null);
		persistence.setKey(uuid);
	}

}
