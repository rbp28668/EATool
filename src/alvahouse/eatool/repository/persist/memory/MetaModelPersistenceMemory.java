/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDeleteProxy;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipDeleteProxy;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class MetaModelPersistenceMemory implements MetaModelPersistence {

	private UUID uuid; // Uniquely identify this meta model

	/** Map of MetaEntity keyed by UUID for fast lookup */
	private Map<UUID, MetaEntity> metaEntities = new HashMap<UUID, MetaEntity>();

	/** Map of MetaRelationship keyed by UUID for fast lookup */
	private Map<UUID, MetaRelationship> metaRelationships = new HashMap<UUID, MetaRelationship>();

	/** Set of MetaEntity kept in sorted order */
	private Set<MetaEntity> sortedEntities = new TreeSet<MetaEntity>();

	/** Set of MetaRelationship kept in sorted order */
	private Set<MetaRelationship> sortedRelationships = new TreeSet<MetaRelationship>();

	/**
	 * 
	 */
	public MetaModelPersistenceMemory() {
		uuid = new UUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntity(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntity getMetaEntity(UUID uuid) throws Exception{
		MetaEntity me = metaEntities.get(uuid);
		if (me == null) {
			throw new IllegalArgumentException("No meta entity with key " + uuid + " in the meta-model");
		}
		
		// Return a copy of the MetaEntity complete with any base meta entities.
		MetaEntity copy = (MetaEntity)me.clone();
		MetaEntity base = me.getBase();
		while( base != null) {
			MetaEntity baseCopy = (MetaEntity)base.clone();
			copy.setBase(baseCopy);
			me = base;
			base = me.getBase();
		}
		return me;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaEntity(
	 * alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public MetaEntity addMetaEntity(MetaEntity me) throws Exception {
		if (metaEntities.containsKey(me.getKey()))
			throw new IllegalStateException("Meta Entity already exists in meta-model");

		me = (MetaEntity) me.clone();

		metaEntities.put(me.getKey(), me);
		sortedEntities.add(me);
		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntities()
	 */
	@Override
	public Collection<MetaEntity> getMetaEntities() {
		Set<MetaEntity> result = new TreeSet<MetaEntity>();
		for (MetaEntity me : sortedEntities) {
			result.add((MetaEntity) me.clone());
		}
		return Collections.unmodifiableCollection(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntityCount()
	 */
	@Override
	public int getMetaEntityCount() {
		return sortedEntities.size();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaEntity(alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public MetaEntity updateMetaEntity(MetaEntity me) throws Exception {
		UUID key = me.getKey();
		MetaEntity existing = metaEntities.get(key);
		if(existing == null) {
			throw new IllegalArgumentException("Trying to update a meta entity that is not in the repository");
		}
		sortedEntities.remove(existing);
		MetaEntity clone = (MetaEntity)me.clone();
		metaEntities.put(key,clone);
		sortedEntities.add(clone);

		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#deleteMetaEntity(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntity deleteMetaEntity(UUID uuid) throws Exception {
		MetaEntity me = metaEntities.remove(uuid);
		if (me == null) {
			throw new IllegalArgumentException(
					"Can't delete meta entity with key " + uuid + " not found in repository");
		}
		sortedEntities.remove(me);
		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationship(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationship getMetaRelationship(UUID uuid) throws Exception {
		MetaRelationship mr = metaRelationships.get(uuid);
		if (mr == null) {
			throw new IllegalArgumentException("Meta relationship with key " + uuid + " not found in repository");
		}

		mr = (MetaRelationship) mr.clone();
		return mr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaRelationship(
	 * alvahouse.eatool.repository.metamodel.MetaRelationship)
	 */
	@Override
	public MetaRelationship addMetaRelationship(MetaRelationship mr) throws Exception {

		// This meta relationship should reference meta entities already in the store.
		UUID startKey = mr.start().connectionKey();
		UUID finishKey = mr.finish().connectionKey();

		if ( !(metaEntities.containsKey(startKey) && metaEntities.containsKey(finishKey)) ) {
			throw new IllegalStateException("Saving meta relationship that references meta-entities not in the model");
		}

		// Clone - will break references to meta-entities but keys will remain
		mr = (MetaRelationship) mr.clone();

		// And save
		metaRelationships.put(mr.getKey(), mr);
		sortedRelationships.add(mr);
		return mr;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaRelationship(alvahouse.eatool.repository.metamodel.MetaRelationship)
	 */
	@Override
	public MetaRelationship updateMetaRelationship(MetaRelationship mr) throws Exception {
		// This meta relationship should reference meta entities already in the store.
		UUID startKey = mr.start().connectionKey();
		UUID finishKey = mr.finish().connectionKey();

		if ( !(metaEntities.containsKey(startKey) && metaEntities.containsKey(finishKey)) ) {
			throw new IllegalStateException("Updating meta relationship that references meta-entities not in the model");
		}

		MetaRelationship original = metaRelationships.remove(mr.getKey());
		sortedRelationships.remove(original);
		
		// Clone - will break references to meta-entities but keys will remain
		mr = (MetaRelationship) mr.clone();

		// And save
		metaRelationships.put(mr.getKey(), mr);
		sortedRelationships.add(mr);
		return mr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * deleteMetaRelationship(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationship deleteMetaRelationship(UUID uuid) throws Exception {
		MetaRelationship mr = (MetaRelationship) metaRelationships.remove(uuid);
		if (mr == null) {
			throw new IllegalArgumentException("Trying to delete a meta relationship not in the repository");
		}
		sortedRelationships.remove(mr);
		return mr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationships
	 * ()
	 */
	@Override
	public Collection<MetaRelationship> getMetaRelationships() {
		Set<MetaRelationship> result = new TreeSet<MetaRelationship>();
		for (MetaRelationship mr : sortedRelationships) {
			result.add((MetaRelationship) mr.clone());
		}
		return Collections.unmodifiableCollection(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getMetaRelationshipCount()
	 */
	@Override
	public int getMetaRelationshipCount() {
		return sortedRelationships.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public Set<MetaRelationship> getMetaRelationshipsFor(MetaEntity me) throws Exception {
		Set<MetaRelationship> valid = getDeclaredMetaRelationshipsFor(me);
		
		while(me.hasBase()) {
			me = me.getBase();
			valid.addAll(getDeclaredMetaRelationshipsFor(me));
		}
		return valid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDeclaredMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.
	 * MetaEntity)
	 */
	@Override
	public Set<MetaRelationship> getDeclaredMetaRelationshipsFor(MetaEntity me) throws Exception{
		Set<MetaRelationship> valid = new HashSet<MetaRelationship>();
		for (MetaRelationship mr : sortedRelationships) {
			if (mr.start().connectsTo().equals(me) || mr.finish().connectsTo().equals(me)) {
				valid.add((MetaRelationship) mr.clone());
			}
		}
		return valid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDeleteDependencies(alvahouse.eatool.repository.base.
	 * DeleteDependenciesList,
	 * alvahouse.eatool.repository.metamodel.MetaRelationship)
	 */
	@Override
	public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaRelationship mr) {
		dependencies.addDependency(new MetaRelationshipDeleteProxy(metaModel, mr));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDeleteDependencies(alvahouse.eatool.repository.base.
	 * DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaEntity target) throws Exception {
		dependencies.addDependency(new MetaEntityDeleteProxy(metaModel, target));

		// Look for any meta-entities that are derived from the one being deleted.
		for (MetaEntity derived : sortedEntities) {
			if (derived.hasBase()) {
				if (derived.getBase().equals(target)) {
					getDeleteDependencies(metaModel, dependencies, derived);
				}
			}
		}

		// Mark any relationships that depend on this meta entity for deletion
		for (MetaRelationship mr : sortedRelationships) {
			if (mr.start().connectsTo().equals(target) || mr.finish().connectsTo().equals(target)) {
				dependencies.addDependency(new MetaRelationshipDeleteProxy(metaModel, mr));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		metaEntities.clear();
		metaRelationships.clear();
		sortedEntities.clear();
		sortedRelationships.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDerivedMetaEntities(alvahouse.eatool.repository.metamodel.MetaEntity)
	 */
	@Override
	public Collection<MetaEntity> getDerivedMetaEntities(MetaEntity meta) throws Exception {
		List<MetaEntity> derived = new LinkedList<MetaEntity>();
		for (MetaEntity me : sortedEntities) {

			// Trundle up the inheritance hierarchy looking for "meta"
			boolean isDerived = false;
			
			MetaEntity base = me;
			while(base.hasBase()) {
				base = me.getBase();
				if (base.equals(meta)) {
					isDerived = true;
					break;
				}
			}

			if (isDerived) {
				derived.add((MetaEntity) me.clone());
			}
		}

		return derived;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getKey()
	 */
	@Override
	public UUID getKey() {
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#setKey(alvahouse.
	 * eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) {
		this.uuid = uuid;
	}



}
