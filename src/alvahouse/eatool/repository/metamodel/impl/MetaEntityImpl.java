/*
 * MetaDefinition.java
 *
 * Created on 10 January 2002, 20:43
 */

package alvahouse.eatool.repository.metamodel.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;
/**
 * MetaEntity is an entity in the meta-model. MetaEntities have MetaProperties and
 * are related by MetaRelationships.
 * @author  rbp28668
 */
public class MetaEntityImpl extends MetaPropertyContainerImpl implements  Versionable, MetaEntity{

    private MetaModel model = null;     // model this belongs to.
    private MetaEntity m_base = null;   // allows inheritance in meta-model
    private boolean m_isAbstract = false; // set true for an abstract meta entity
    private MetaEntityDisplayHint displayHint = null; // how entities of this types should display
    private VersionImpl version = new VersionImpl();
    
    /** 
     * Creates new MetaDefinition with a given UUID
     * @param uuid is the key for this MetaEntity
     */
    public MetaEntityImpl(UUID uuid) {
        super(uuid);
    }

//    /** Creates a copy of the meta-entity
//     * @return a new meta-entity (with the same key)
//     */
//    public Object clone() {
//        MetaEntity copy = new MetaEntity(getKey());
//        cloneTo(copy);
//        return copy;
//    }
//    
//    /** Updates this meta entity from a copy.  Used for editing - the 
//     * copy should be edited (use clone to get the copy) and only if the
//     * edit is successful should the original be updated by calling this
//     * method on the original meta-entity.  This ensures that any listeners
//     * are updated properly.
//     * @param copy is the meta-entity to copy from
//     */
//    public void updateFromCopy(MetaEntity copy) {
//        MetaModel parent = getModel();
//        copy.cloneTo(this);
//        setModel(parent);
//        
//        if(model != null)
//            model.fireMetaEntityChanged(this);
//    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#setBase(alvahouse.eatool.repository.metamodel.MetaEntityImpl)
     */
    public void setBase(MetaEntity base) {
        m_base = base;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#getBase()
     */
    public MetaEntity getBase() {
        return m_base;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#setAbstract(boolean)
     */
    public void setAbstract(boolean isAbstract) {
        m_isAbstract = isAbstract;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#isAbstract()
     */
    public boolean isAbstract() {
        return m_isAbstract;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#setDisplayHint(alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint)
     */
    public void setDisplayHint(MetaEntityDisplayHint hint) {
        displayHint = hint;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#getDisplayHint()
     */
    public MetaEntityDisplayHint getDisplayHint() {
        if(displayHint == null) {
            if(m_base != null) 
                return m_base.getDisplayHint();
            else
                return null;
        }
        return displayHint;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("MetaEntity");
        super.writeAttributesXML(out);
        if(isAbstract()) out.addAttribute("abstract","true");
        if(getBase() != null) out.addAttribute("extends",getBase().getKey().toString());
        version.writeXML(out);
        super.writeXML(out);
        out.stopEntity();
        
        if(displayHint != null){
        	displayHint.writeXML(out);
        }
    }


//    /** copies this meta-entity to a copy
//     * @param copy is the meta entity to copy to
//     */
//    protected void cloneTo(MetaEntity copy) {
//        super.cloneTo(copy);
//        copy.m_base = m_base;
//        copy.m_isAbstract = m_isAbstract;
//        version.cloneTo(copy.version);
//        copy.setModel(null); // disconnect it
//    }

    /** Sets the parent meta-model
     * @param m is the meta model to set
     */
    public void setModel(MetaModel m) {
        model = m;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaEntity#getModel()
     */
    public MetaModel getModel() {
        return model;
    }
    
    /** This adds a new MetaProperty to the MetaEntity. If an existing meta
     * property with the same UUID already exists then it is replaced.
     * @param mp is the meta-property to be added.
     */
    public MetaProperty addMetaProperty(MetaProperty mp)
    {
        super.addMetaProperty(mp);
        if(model != null)
            model.fireMetaEntityChanged(this);
        return mp;
    }

    /** Gets a child meta-property given its key (UUID)
     * Note that this will search up the inheritance hierarchy
     * so may return meta-properties from any base meta-entities.
     * @param uuid is the key for the meta-property
     * @return the meta-property corresponding to the key
     */
    public MetaProperty getMetaProperty(UUID uuid) {
        MetaProperty mp = super.getMetaProperty(uuid);
        if((mp == null) && (getBase() != null))
            mp = getBase().getMetaProperty(uuid);
        return mp;
    }

    /** This deletes a named MetaProperty from the MetaEntity.
     * @param name is the name of the property to delete
     * @returns the delete MetaProperty or null if no match for the name
     */
    public MetaProperty deleteMetaProperty(UUID uuid) {
        MetaProperty mp = super.deleteMetaProperty(uuid);
        if(model != null && mp != null)
            model.fireMetaEntityChanged(this);
        return mp;
    }
    
    /** gets a collection of all the meta-properties 
     * for this meta-entity including any defined in the base meta-entity.
     * @return a collection of meta-properties.
     */
    public Collection<MetaProperty> getMetaProperties() {
        LinkedList<MetaProperty> props = new LinkedList<MetaProperty>(getDeclaredMetaProperties());
        if(m_base != null){
            props.addAll(m_base.getMetaProperties());
        }
        return props;
    }

    /**
     * Compare allows comparison of meta-entities for sorting.
     * 
     * @author rbp28668
     */
    public static class Compare implements Comparator<MetaEntity> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(MetaEntity arg0, MetaEntity arg1) {
             return arg0.getName().compareTo(arg1.getName());
        }
         
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Version#getVersion()
     */
    @Override
    public Version getVersion() {
        return version;
    }
    
}
