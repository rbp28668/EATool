/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import alvahouse.eatool.repository.dao.DeleteDependenciesListDao;
import alvahouse.eatool.repository.dao.DeleteProxyDao;
import alvahouse.eatool.repository.dao.metamodel.MetaEntityDao;
import alvahouse.eatool.repository.dao.metamodel.MetaRelationshipDao;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class MetaModelPersistenceMemory implements MetaModelPersistence {

	private UUID uuid; // Uniquely identify this meta model

	/** Map of MetaEntityDao keyed by UUID for fast lookup */
	private Map<UUID, MetaEntityDao> metaEntities = new HashMap<UUID, MetaEntityDao>();

	/** Map of MetaRelationshipDao keyed by UUID for fast lookup */
	private Map<UUID, MetaRelationshipDao> metaRelationships = new HashMap<UUID, MetaRelationshipDao>();

	/** Set of MetaEntityDao kept in sorted order */
	private Set<MetaEntityDao> sortedEntities = new TreeSet<MetaEntityDao>();

	/** Set of MetaRelationshipDao kept in sorted order */
	private Set<MetaRelationshipDao> sortedRelationships = new TreeSet<MetaRelationshipDao>();

	/** Map of MetaPropertyType keyed by their key for the user defined (i.e. not predefined/built-in) types. */
	private Map<UUID, ExtensibleMetaPropertyType> userDefinedTypes = new HashMap<UUID, ExtensibleMetaPropertyType>();
	/**
	 * 
	 */
	public MetaModelPersistenceMemory() {
		uuid = new UUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntityDao(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntityDao getMetaEntity(UUID uuid) throws Exception{
		MetaEntityDao me = metaEntities.get(uuid);
		if (me == null) {
			throw new IllegalArgumentException("No meta entity with key " + uuid + " in the meta-model");
		}
		return me;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaEntityDao(
	 * alvahouse.eatool.repository.metamodel.MetaEntityDao)
	 */
	@Override
	public void addMetaEntity(MetaEntityDao me) throws Exception {
		if (metaEntities.containsKey(me.getKey()))
			throw new IllegalStateException("Meta Entity already exists in meta-model");

		metaEntities.put(me.getKey(), me);
		sortedEntities.add(me);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntities()
	 */
	@Override
	public Collection<MetaEntityDao> getMetaEntities() {
		Set<MetaEntityDao> result = new TreeSet<MetaEntityDao>();
		result.addAll(sortedEntities);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaEntityDaoCount()
	 */
	@Override
	public int getMetaEntityCount() {
		return sortedEntities.size();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaEntityDao(alvahouse.eatool.repository.metamodel.MetaEntityDao)
	 */
	@Override
	public void updateMetaEntity(MetaEntityDao me) throws Exception {
		UUID key = me.getKey();
		MetaEntityDao existing = metaEntities.get(key);
		if(existing == null) {
			throw new IllegalArgumentException("Trying to update a meta entity that is not in the repository");
		}
		sortedEntities.remove(existing);
		metaEntities.put(key,me);
		sortedEntities.add(me);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#deleteMetaEntityDao(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaEntityDao deleteMetaEntity(UUID uuid) throws Exception {
		MetaEntityDao me = metaEntities.remove(uuid);
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
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationshipDao(
	 * alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationshipDao getMetaRelationship(UUID uuid) throws Exception {
		MetaRelationshipDao mr = metaRelationships.get(uuid);
		if (mr == null) {
			throw new IllegalArgumentException("Meta relationship with key " + uuid + " not found in repository");
		}
		return mr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#addMetaRelationshipDao(
	 * alvahouse.eatool.repository.metamodel.MetaRelationshipDao)
	 */
	@Override
	public void addMetaRelationship(MetaRelationshipDao mr) throws Exception {

		// This meta relationship should reference meta entities already in the store.
		UUID startKey = mr.getStart().getConnects();
		UUID finishKey = mr.getFinish().getConnects();

		if ( !(metaEntities.containsKey(startKey) && metaEntities.containsKey(finishKey)) ) {
			throw new IllegalStateException("Saving meta relationship that references meta-entities not in the model");
		}

		// And save
		metaRelationships.put(mr.getKey(), mr);
		sortedRelationships.add(mr);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateMetaRelationshipDao(alvahouse.eatool.repository.metamodel.MetaRelationshipDao)
	 */
	@Override
	public void updateMetaRelationship(MetaRelationshipDao mr) throws Exception {
		// This meta relationship should reference meta entities already in the store.
		UUID startKey = mr.getStart().getConnects();
		UUID finishKey = mr.getFinish().getConnects();

		if ( !(metaEntities.containsKey(startKey) && metaEntities.containsKey(finishKey)) ) {
			throw new IllegalStateException("Updating meta relationship that references meta-entities not in the model");
		}

		MetaRelationshipDao original = metaRelationships.remove(mr.getKey());
		sortedRelationships.remove(original);
		
		// And save
		metaRelationships.put(mr.getKey(), mr);
		sortedRelationships.add(mr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * deleteMetaRelationshipDao(alvahouse.eatool.util.UUID)
	 */
	@Override
	public MetaRelationshipDao deleteMetaRelationship(UUID uuid) throws Exception {
		MetaRelationshipDao mr = (MetaRelationshipDao) metaRelationships.remove(uuid);
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
	 * alvahouse.eatool.repository.persist.MetaModelPersistence#getMetaRelationshipDaos
	 * ()
	 */
	@Override
	public Collection<MetaRelationshipDao> getMetaRelationships() {
		Set<MetaRelationshipDao> result = new TreeSet<MetaRelationshipDao>();
		result.addAll(sortedRelationships);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getMetaRelationshipDaoCount()
	 */
	@Override
	public int getMetaRelationshipCount() {
		return sortedRelationships.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getMetaRelationshipDaosFor(alvahouse.eatool.repository.metamodel.MetaEntityDao)
	 */
	@Override
	public Set<MetaRelationshipDao> getMetaRelationshipsFor(UUID metaEntityKey) throws Exception {
		Set<MetaRelationshipDao> valid = new HashSet<MetaRelationshipDao>();
		
		MetaEntityDao me = metaEntities.get(metaEntityKey);
		while(me != null) {
			valid.addAll(getDeclaredMetaRelationshipsFor(me.getKey()));
			UUID baseKey = me.getBase();
			me = (baseKey != null) ? metaEntities.get(baseKey) : null;
		}
		return valid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDeclaredMetaRelationshipDaosFor(alvahouse.eatool.repository.metamodel.
	 * MetaEntityDao)
	 */
	@Override
	public Set<MetaRelationshipDao> getDeclaredMetaRelationshipsFor(UUID metaEntityKey) throws Exception{
		Set<MetaRelationshipDao> valid = new HashSet<MetaRelationshipDao>();
		for (MetaRelationshipDao mr : sortedRelationships) {
			if (mr.getStart().getConnects().equals(metaEntityKey) || mr.getFinish().getConnects().equals(metaEntityKey)) {
				valid.add(mr);
			}
		}
		return valid;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
//	 * getDeleteDependencies(alvahouse.eatool.repository.base.
//	 * DeleteDependenciesList,
//	 * alvahouse.eatool.repository.metamodel.MetaRelationshipDao)
//	 */
//	@Override
//	public void getDeleteDependencies(MetaModel metaModel, DeleteDependenciesList dependencies, MetaRelationshipDao mr) {
//		dependencies.addDependency(new MetaRelationshipDeleteProxy(metaModel, mr));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#
	 * getDeleteDependencies(alvahouse.eatool.repository.base.
	 * DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaEntityDao)
	 */
	@Override
	public DeleteDependenciesListDao getDeleteDependencies( UUID metaEntityKey) throws Exception {
		DeleteDependenciesListDao dependencies = new DeleteDependenciesListDao();
		getDeleteDependencies(dependencies, metaEntityKey);
		return dependencies;
	}

	private  void getDeleteDependencies( DeleteDependenciesListDao dependencies, UUID metaEntityKey) throws Exception {
		
		DeleteProxyDao proxy = new DeleteProxyDao();
		proxy.setItemType("metaEntity");
		proxy.setItemKey(metaEntityKey);
		dependencies.getProperties().add(proxy);

	
		// Look for any meta-entities that are derived from the one being deleted.
		for (MetaEntityDao derived : sortedEntities) {
			if (derived.getBase() != null) {
				if (derived.getBase().equals(metaEntityKey)) {
					getDeleteDependencies(dependencies, derived.getKey());
				}
			}
		}

		// Mark any relationships that depend on this meta entity for deletion
		for (MetaRelationshipDao mr : sortedRelationships) {
			if (mr.getStart().getConnects().equals(metaEntityKey) || mr.getFinish().getConnects().equals(metaEntityKey)) {
				proxy = new DeleteProxyDao();
				proxy.setItemType("metaRelationship");
				proxy.setItemKey(mr.getKey());
				dependencies.getProperties().add(proxy);
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
	 * getDerivedMetaEntities(alvahouse.eatool.repository.metamodel.MetaEntityDao)
	 */
	@Override
	public Collection<MetaEntityDao> getDerivedMetaEntities(UUID metaEntityKey) throws Exception {
		List<MetaEntityDao> derived = new LinkedList<MetaEntityDao>();
		
		for (MetaEntityDao me : sortedEntities) {

			// Trundle up the inheritance hierarchy looking for "meta"
			boolean isDerived = false;
			
			MetaEntityDao current = me;
			while(current.getBase() != null) {
				UUID baseKey = me.getBase();
				if (baseKey.equals(metaEntityKey)) {
					isDerived = true;
					break;
				}
				current = metaEntities.get(baseKey);
			}

			if (isDerived) {
				derived.add(me);
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

	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#getDefinedTypes()
	 */
	@Override
    public Collection<ExtensibleMetaPropertyType> getDefinedTypes() throws Exception{
		Collection<ExtensibleMetaPropertyType> copies = new LinkedList<ExtensibleMetaPropertyType>();
		for(ExtensibleMetaPropertyType mpt : userDefinedTypes.values()) {
			copies.add( (ExtensibleMetaPropertyType)mpt.clone());
		}
		return copies;
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#addType(alvahouse.eatool.repository.metamodel.types.MetaPropertyType)
	 */
	@Override
	public void addType(ExtensibleMetaPropertyType mpt)  throws Exception{
		if(userDefinedTypes.containsKey(mpt.getKey())) {
			throw new IllegalStateException("MetaPropertyType " + mpt.getName() + " with key " + mpt.getKey() + " already exists");
		}
		ExtensibleMetaPropertyType copy = (ExtensibleMetaPropertyType) mpt.clone();
		userDefinedTypes.put(copy.getKey(), copy);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#updateType(alvahouse.eatool.repository.metamodel.types.MetaPropertyType)
	 */
	@Override
	public void updateType(ExtensibleMetaPropertyType mpt)  throws Exception{
		if(!userDefinedTypes.containsKey(mpt.getKey())) {
			throw new IllegalStateException("MetaPropertyType " + mpt.getName() + " with key " + mpt.getKey() + " does not exist in repository");
		}
		ExtensibleMetaPropertyType copy = (ExtensibleMetaPropertyType) mpt.clone();
		userDefinedTypes.put(copy.getKey(), copy);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#removeType(alvahouse.eatool.repository.metamodel.types.MetaPropertyType)
	 */
	@Override
	public void deleteType(UUID uuid) throws Exception{
		if(!userDefinedTypes.containsKey(uuid)) {
			throw new IllegalStateException("Can't delete MetaPropertyType with key " + uuid + " - not in repository");
		}
		userDefinedTypes.remove(uuid);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.MetaModelPersistence#deleteDefinedTypes()
	 */
	@Override
	public void deleteDefinedTypes() {
		userDefinedTypes.clear();
	}



}
