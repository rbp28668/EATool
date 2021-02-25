/*
 * MetaModel.java
 *
 * Created on 11 January 2002, 19:47
 */

package alvahouse.eatool.repository.metamodel.impl;

import java.io.IOException;
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
import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Manages the meta-model for the repository.
 * @author  rbp28668
 */
public class MetaModelImpl implements MetaModel {

	private UUID uuid; // Uniquely identify this meta model
    
    /** Map of MetaEntity keyed by UUID for fast lookup */
    private Map<UUID,MetaEntity> metaEntities = new HashMap<UUID,MetaEntity>();
    
    /** Map of MetaRelationship keyed by UUID for fast lookup*/
    private Map<UUID,MetaRelationship> metaRelationships = new HashMap<UUID,MetaRelationship>();
    
    /** Set of MetaEntity kept in sorted order */
    private Set<MetaEntity> sortedEntities = new TreeSet<MetaEntity>();
    
    /** Set of MetaRelationship kept in sorted order */
    private Set<MetaRelationship> sortedRelationships = new TreeSet<MetaRelationship>();
    
    /** Change listeners */
    private LinkedList<MetaModelChangeListener> listeners = new LinkedList<MetaModelChangeListener>();
    
    /** Creates new MetaModel */
    public MetaModelImpl() {
    	uuid = new UUID();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntity(alvahouse.eatool.util.UUID)
     */
    public MetaEntity getMetaEntity(UUID uuid) {
        MetaEntity me = metaEntities.get(uuid);
        return me;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#addMetaEntity(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public MetaEntity addMetaEntity(MetaEntity me) throws Exception {
        if(metaEntities.containsKey(me.getKey()))
            throw new IllegalStateException("Meta Entity already exists in meta-model");
        
        me.setModel(this);
        metaEntities.put(me.getKey(), me);
        sortedEntities.add(me);
        fireMetaEntityAdded(me);
        return me;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntities()
     */    
    public Collection<MetaEntity> getMetaEntities() {
        //return metaEntities.values().iterator();
        return Collections.unmodifiableCollection(sortedEntities);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaEntityCount()
     */
    public int getMetaEntityCount() {
        return sortedEntities.size();
    }
    
//    /** gets the Meta-entities as an array
//     * @return an array of MetaEntities
//     */    
//    public MetaEntity[] getMetaEntitiesAsArray() {
//        //return (MetaEntity [])metaEntities.values().toArray();
//        return (MetaEntity [])sortedEntities.toArray(new MetaEntity[sortedEntities.size()]);
//    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#deleteMetaEntity(alvahouse.eatool.util.UUID)
     */    
    public void deleteMetaEntity(UUID uuid) throws Exception {
        MetaEntity me = metaEntities.remove(uuid);
        sortedEntities.remove(me);
        fireMetaEntityDeleted(me);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationship(alvahouse.eatool.util.UUID)
     */
    public MetaRelationship getMetaRelationship(UUID uuid) {
        MetaRelationship mr = metaRelationships.get(uuid);
        return mr;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#addMetaRelationship(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public MetaRelationship addMetaRelationship(MetaRelationship mr) throws Exception {
        mr.setModel(this);
        metaRelationships.put(mr.getKey(),mr);
        sortedRelationships.add(mr);
        fireMetaRelationshipAdded(mr);
        return mr;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#deleteMetaRelationship(alvahouse.eatool.util.UUID)
     */    
    public void deleteMetaRelationship(UUID uuid) throws Exception {
        MetaRelationship mr  = (MetaRelationship)metaRelationships.remove(uuid);
        sortedRelationships.remove(mr);
        fireMetaRelationshipDeleted(mr);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationships()
     */    
    public Collection<MetaRelationship> getMetaRelationships() {
        return Collections.unmodifiableCollection(sortedRelationships);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipsAsArray()
     */    
    public MetaRelationship[] getMetaRelationshipsAsArray() {
        return (MetaRelationship[])sortedRelationships.toArray(
        	new MetaRelationship[sortedRelationships.size()]);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipCount()
     */
    public int getMetaRelationshipCount() {
        return sortedRelationships.size();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public Set<MetaRelationship> getMetaRelationshipsFor(MetaEntity me){
        
        Set<MetaRelationship> valid = getDeclaredMetaRelationshipsFor(me);
        me = me.getBase();
        while(me != null){
            valid.addAll(getDeclaredMetaRelationshipsFor(me));
            me = me.getBase();
        }
        return valid;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getDeclaredMetaRelationshipsFor(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public Set<MetaRelationship> getDeclaredMetaRelationshipsFor(MetaEntity me){
        Set<MetaRelationship> valid = new HashSet<MetaRelationship>();
        for(MetaRelationship mr : sortedRelationships){
            if(mr.start().connectsTo().equals(me) || mr.finish().connectsTo().equals(me)){
                valid.add(mr);
            }
        }
        return valid;
    }
    
     /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaRelationship)
     */    
    public void getDeleteDependencies(DeleteDependenciesList dependencies, MetaRelationship mr) {
        dependencies.addDependency(new MetaRelationshipDeleteProxy(mr));
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getDeleteDependencies(alvahouse.eatool.repository.base.DeleteDependenciesList, alvahouse.eatool.repository.metamodel.MetaEntity)
     */    
    public void getDeleteDependencies(DeleteDependenciesList dependencies, MetaEntity target) {

        dependencies.addDependency(new MetaEntityDeleteProxy(target));
        
        // Look for any meta-entities that are derived from the one being deleted.
        for(MetaEntity derived : sortedEntities) {
            if(derived.getBase() != null) {
                if(derived.getBase().equals(target)) {
                    getDeleteDependencies(dependencies, derived);
                }
            }
        }

        // Mark any relationships that depend on this meta entity for deletion
        for(MetaRelationship mr : sortedRelationships) {
            if(mr.start().connectsTo().equals(target) ||
                mr.finish().connectsTo().equals(target)) {
                dependencies.addDependency(new MetaRelationshipDeleteProxy(mr));
            }
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("MetaModel");
        out.addAttribute("uuid", uuid.toString());

        Set<UUID> written = new HashSet<UUID>();
        for(MetaEntity me : sortedEntities) {
            writeMetaEntity(me,out,written);
        }
        
        for(MetaRelationship mr : sortedRelationships) {
            mr.writeXML(out);
        }
        out.stopEntity();
    }
    
    /** writes a meta entity to the XML output whilst ensuring that any
     * base meta-entities that haven't already been written, are written
     * out
     * @param me is the meta-entity to write
     * @param out is the output stream to write to
     * @written is a set to record which meta-entities have been written
     */
    private void writeMetaEntity(MetaEntity me, XMLWriter out,  Set<UUID> written) throws IOException{
        
        if(written.contains(me.getKey()))
            return;
        
        if(me.getBase() != null)
            writeMetaEntity(me.getBase(), out, written);
        me.writeXML(out);
        written.add(me.getKey());
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#deleteContents()
     */
    public void deleteContents() throws Exception {
        metaEntities.clear();
        metaRelationships.clear();
        sortedEntities.clear();
        sortedRelationships.clear();
        fireModelUpdated();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#addChangeListener(alvahouse.eatool.repository.metamodel.MetaModelChangeListener)
     */
    public void addChangeListener(MetaModelChangeListener listener) {
        listeners.addLast(listener);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#removeChangeListener(alvahouse.eatool.repository.metamodel.MetaModelChangeListener)
     */
    public void removeChangeListener(MetaModelChangeListener listener) {
        listeners.remove(listener);
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireModelUpdated()
     */
    public void fireModelUpdated()  throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(this);
        for(MetaModelChangeListener l : listeners){
            l.modelUpdated(evt);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaEntityAdded(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public void fireMetaEntityAdded(MetaEntity me) throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
        for(MetaModelChangeListener l : listeners){
            l.metaEntityAdded(evt);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaEntityChanged(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public void fireMetaEntityChanged(MetaEntity me) throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
        for(MetaModelChangeListener l : listeners){
            l.metaEntityChanged(evt);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaEntityDeleted(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public void fireMetaEntityDeleted(MetaEntity me) throws Exception{ 
        MetaModelChangeEvent evt = new MetaModelChangeEvent(me);
        for(MetaModelChangeListener l : listeners){
            l.metaEntityDeleted(evt);
        }
    }

        
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaRelationshipAdded(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public void fireMetaRelationshipAdded(MetaRelationship mr) throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
        for(MetaModelChangeListener l : listeners){
            l.metaRelationshipAdded(evt);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public void fireMetaRelationshipChanged(MetaRelationship mr) throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
        for(MetaModelChangeListener l : listeners){
            l.metaRelationshipChanged(evt);
        }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#fireMetaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public void fireMetaRelationshipDeleted(MetaRelationship mr) throws Exception{
        MetaModelChangeEvent evt = new MetaModelChangeEvent(mr);
        for(MetaModelChangeListener l : listeners){
            l.metaRelationshipDeleted(evt);
        }
    }

 
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModel#getDerivedMetaEntities(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public Collection<MetaEntity> getDerivedMetaEntities(MetaEntity meta) {
        List<MetaEntity> derived = new LinkedList<MetaEntity>(); 
        for(MetaEntity me : sortedEntities) {
            
            // Trundle up the inheritance hierarchy looking for "meta"
            boolean isDerived = false;
            MetaEntity base = me.getBase();
            while(base != null){
                if(base.equals(meta)){
                    isDerived = true;
                    break;
                }
                base = base.getBase();
            }
            
            if(isDerived){
                derived.add(me);
            }
        }
        
        return derived;
    }
    
    /** Proxy class for recording dependent meta-entities
     */
    private class MetaEntityDeleteProxy implements IDeleteDependenciesProxy {
        /** Creates a new proxy for deleting dependent meta-entities
         * @param me is the dependent meta-entity
         */        
        public MetaEntityDeleteProxy(MetaEntity me) {
            entity = me;
        }
        
        /** gets the name of the dependent meta entity
         * @return name for the dependent meta-entity
         */        
        public String toString() {
            return "Meta-Entity " + entity.toString();
        }

        /** deletes the dependent meta-entity
         */
        public void delete() throws Exception {
            deleteMetaEntity(entity.getKey());
        }
        
        /** gets the dependent meta-entity
         * @return the dependent meta-entity
         */        
        public Object getTarget() {
            return entity;
        }
        
        private MetaEntity entity;
    }
    
    /** Proxy class for recording dependent meta-relationship
     */
    private class MetaRelationshipDeleteProxy implements IDeleteDependenciesProxy {
        /** Creates a new proxy for deleting a dependent meta-relationshi
         * @param m is the dependent meta-relationship
         */        
        public MetaRelationshipDeleteProxy(MetaRelationship mr) {
            relationship = mr;
        }
        
        /** gets the name of the dependent meta relationship
         * @return name for the dependent meta-relationshp
         */        
        public String toString() {
            return "Meta-Relationship " + relationship.toString();
        }

        /** deletes the dependent meta-relationship
         */
        public void delete() throws Exception {
            deleteMetaRelationship(relationship.getKey());
        }
        
        /** gets the dependent meta-relationship
         * @return the dependent meta-relationship
         */
        public Object getTarget() {
            return relationship;
        }
        
        private MetaRelationship relationship;
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
		assert(uuid != null);
		this.uuid = uuid;
	}
    
}
