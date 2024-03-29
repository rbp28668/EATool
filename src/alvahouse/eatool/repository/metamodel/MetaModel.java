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
import java.util.List;
import java.util.Set;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.dto.DeleteDependenciesListDto;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.repository.dto.metamodel.MetaEntityDto;
import alvahouse.eatool.repository.dto.metamodel.MetaRelationshipDto;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Manages the meta-model for the repository.
 * 
 * @author rbp28668
 */
public class MetaModel implements KeyedItem {

	private MetaPropertyTypes types;
	private MetaModelPersistence persistence;

	/** Change listeners */
	private LinkedList<MetaModelChangeListener> listeners = new LinkedList<MetaModelChangeListener>();

	/** Creates new MetaModel */
	public MetaModel(MetaModelPersistence persistence, MetaPropertyTypes types) {
		this.persistence = persistence;
		this.types = types;
	}

	/**
	 * Gets a MetaEntity from the meta model.
	 * 
	 * @param uuid is the key for the existing (or new) MetaEntity
	 * @return the MetaEntity keyed by uuid
	 */
	public MetaEntity getMetaEntity(UUID uuid) throws Exception {
		MetaEntityDto dao =  persistence.getMetaEntity(uuid);
		MetaEntity me = new MetaEntity(dao,types);
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
		
		MetaEntityDto dao = me.toDao();
		String version = persistence.addMetaEntity(dao);
		me.getVersion().update(version);
		
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
		MetaEntityDto dao = me.toDao();
		String version = persistence.addMetaEntity(dao);
		me.getVersion().update(version);
	}

	/**
	 * Gets a Collection of all the MetaEntities in the meta-model.
	 * 
	 * @return Collection of MetaEntity.
	 */
	public Collection<MetaEntity> getMetaEntities()  throws Exception{
		Collection<MetaEntityDto> daos = persistence.getMetaEntities();
		Collection<MetaEntity> mes = new LinkedList<>();
		for(MetaEntityDto dao : daos) {
			MetaEntity me = new MetaEntity(dao, types);
			me.setModel(this);
			mes.add(me);
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
		
		MetaEntityDto dao = me.toDao();
		String version = persistence.updateMetaEntity(dao);
		me.getVersion().update(version);

		fireMetaEntityChanged(me);
		return me;
	}

	/**
	 * Basic update of meta entity for serialisation.
	 * @param me
	 */
	public void _update(MetaEntity me) throws Exception{
		if(me.getModel() != this) {
			throw new IllegalArgumentException("Cannot move meta-entities across models");
		}
		MetaEntityDto dao = me.toDao();
		String version = persistence.updateMetaEntity(dao);
		me.getVersion().update(version);
	}

	/**
	 * deletes a meta-entity from the meta-model
	 * 
	 * @param uuid is the identifier of the meta-entity to delete
	 * @param version is the specific version to delete - can only delete if the
	 * repository still contains the referenced version otherwise it's been updated.
	 * @throws Exception
	 */
	public void deleteMetaEntity(UUID uuid, String version) throws Exception {
		MetaEntityDto dao = persistence.deleteMetaEntity(uuid, version);
		MetaEntity me = new MetaEntity(dao, types);
		fireMetaEntityDeleted(me);
	}

	/**
	 * Gets a MetaRelationship from the meta model.
	 * 
	 * @param uuid is the key for the existing (or new) MetaRelationship
	 * @return the MetaRelationship keyed by uuid
	 */
	public MetaRelationship getMetaRelationship(UUID uuid)  throws Exception{
		MetaRelationshipDto dao = persistence.getMetaRelationship(uuid);
		MetaRelationship mr = new MetaRelationship(dao, types);
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
		
		MetaRelationshipDto dao = mr.toDao();
		String version = persistence.addMetaRelationship(dao);
		mr.getVersion().update(version);

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
		MetaRelationshipDto dao = mr.toDao();
		String version = persistence.addMetaRelationship(dao);
		mr.getVersion().update(version);
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

		MetaRelationshipDto dao = mr.toDao();
		String version = persistence.updateMetaRelationship(dao);
		mr.getVersion().update(version);
		
		fireMetaRelationshipChanged(mr);
		return mr;
	}
	

	/**
	 * deletes a meta-relationship from the meta-model
	 * 
	 * @param uuid is the key for the meta-relationship to delete
	 * @param version is the specific version to delete - can only delete if the
	 * repository still contains the referenced version otherwise it's been updated.
	 * @throws Exception
	 */
	public void deleteMetaRelationship(UUID uuid, String version) throws Exception {
		MetaRelationshipDto dao = persistence.deleteMetaRelationship(uuid, version);
		MetaRelationship mr = new MetaRelationship(dao, types);
		fireMetaRelationshipDeleted(mr);
	}

	/**
	 * Get the collection of MetaRelationships in the meta-model.
	 * 
	 * @return a Collection containing MetaRelationship.
	 */
	public Collection<MetaRelationship> getMetaRelationships()  throws Exception{
		Collection<MetaRelationshipDto> daos = persistence.getMetaRelationships();
		List<MetaRelationship> mrs = new LinkedList<>();
		for(MetaRelationshipDto dao : daos) {
			MetaRelationship mr = new MetaRelationship(dao, types);
			mr.setModel(this);
			mrs.add(mr);
		}
		return mrs;
	}

	/**
	 * gets the meta relationships as an array
	 * 
	 * @return an array containing the meta-relationships
	 */
	public MetaRelationship[] getMetaRelationshipsAsArray()  throws Exception{
		Collection<MetaRelationship> relationships = getMetaRelationships();
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
		Set<MetaRelationshipDto> daos = persistence.getMetaRelationshipsFor(me.getKey());
		Set<MetaRelationship> relationships = new HashSet<>();
		for(MetaRelationshipDto dao : daos) {
			MetaRelationship mr = new MetaRelationship(dao, types);
			mr.setModel(this);
			relationships.add(mr);
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
		Set<MetaRelationshipDto> daos = persistence.getDeclaredMetaRelationshipsFor(me.getKey());
		Set<MetaRelationship> relationships = new HashSet<>();
		for(MetaRelationshipDto dao : daos) {
			MetaRelationship mr = new MetaRelationship(dao, types);
			mr.setModel(this);
			relationships.add(mr);
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
		MetaRelationshipDeleteProxy proxy = new MetaRelationshipDeleteProxy(this, mr.getKey(), mr.getName(), mr.getVersion().getVersion());
		dependencies.addDependency(proxy);
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
	public void getDeleteDependencies(DeleteDependenciesList dependencies, MetaEntity target, Repository repository)  throws Exception{
		DeleteDependenciesListDto dao = persistence.getDeleteDependencies(target.getKey());
		for(DeleteProxyDto proxyDao : dao.getDependencies()) {
			dependencies.addDependency(proxyDao, repository);
		}
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
		for (MetaEntity me : getMetaEntities()) {
			writeMetaEntity(me, out, written);
		}

		for (MetaRelationship mr : getMetaRelationships()) {
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
		
		
		Collection<MetaEntityDto> daos = persistence.getDerivedMetaEntities(meta.getKey());
		List<MetaEntity> derived = new LinkedList<>();
		for(MetaEntityDto dao : daos) {
			MetaEntity me = new MetaEntity(dao, types);
			me.setModel(this);
			derived.add(me);
		}
		return derived;
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
	public void setKey(UUID uuid) throws Exception {
		assert (uuid != null);
		persistence.setKey(uuid);
	}


}
