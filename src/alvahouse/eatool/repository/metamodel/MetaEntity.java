/*
 * MetaDefinition.java
 *
 * Created on 10 January 2002, 20:43
 */

package alvahouse.eatool.repository.metamodel;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

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
public class MetaEntity extends MetaPropertyContainer implements  Versionable{

    private MetaModel model = null;     // model this belongs to.
    private MetaEntityProxy base = new MetaEntityProxy();   // allows inheritance in meta-model
    private boolean m_isAbstract = false; // set true for an abstract meta entity
    private MetaEntityDisplayHint displayHint = null; // how entities of this types should display
    private VersionImpl version = new VersionImpl();
    
    /** 
     * Creates new MetaDefinition with a given UUID
     * @param uuid is the key for this MetaEntity
     */
    public MetaEntity(UUID uuid) {
        super(uuid);
    }

    /** Creates a copy of the meta-entity
     * @return a new meta-entity (with the same key)
     */
    public Object clone() {
        MetaEntity copy = new MetaEntity(getKey());
        cloneTo(copy);
        return copy;
    }
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
    
    /**
     * Sets the base MetaEntity that this MetaEntity inherits from. This allows the
     * meta model to provide inheritance of entities.
     * @param base is the MetaEntities superclass.
     */
    public void setBase(MetaEntity me) {
        base.set(me);
    }
 
    /**
     * Sets the key for the base MetaEntity that this MetaEntity inherits from.  This
     * allows lazy loading of the base from the meta-model and persistence layer.
     * @param key
     */
    public void setBaseKey(UUID key) {
    	base.setKey(key);
    }
    
    /**
     * Determines whether this meta entity inherits from another one.
     * @return true if this inherits i.e. there is a base meta-entity.
     */
    public boolean hasBase() {
    	return !base.isNull();
    }
    
    /** 
     * Gets the base MetaEntity this MetaEntity inherits from (if any).
     * @return the base meta entity or null if this does not inherit.
     */
    public MetaEntity getBase() throws Exception {
        return base.get(model);
    }

    /** 
     * Gets the key for the base MetaEntity this MetaEntity inherits from (if any).
     * @return the base key if set.
     * @throws IllegalStateException if base is marked as null i.e. this does not inherit.
     */
    public UUID getBaseKey() throws Exception {
    	if(base.isNull()) {
    		throw new IllegalStateException("Unable to get base key where there is no base MetaEntity set");
    	}
        return base.getKey();
    }

    /**
     * Sets a MetaEntity as being abstract.  If abstract it cannot be instantiated
     * in a model but can be used as a base entity for other MetaEntities to
     * inherit from.
     * @param isAbstract sets this MetaEntity abstract if true, concrete if false.
     */
    public void setAbstract(boolean isAbstract) {
        m_isAbstract = isAbstract;
    }
    
    /**
     * Determines whether this MetaEntity is abstract
     */
    public boolean isAbstract() {
        return m_isAbstract;
    }
    
    /** sets a display hint for this meta-entity. 
     * This describes how to render a corresponding Entity as a String.
     * @param hint is the display hint to set.
     */
    public void setDisplayHint(MetaEntityDisplayHint hint) {
        displayHint = hint;
    }
    
    /** get any display hint from this meta-entity (or it's base)
     * @returns the display hint, or null if none found.
     */
    public MetaEntityDisplayHint getDisplayHint() throws Exception{
        if(displayHint == null) {
            if(base.isNull())
            	return null;
            else
                return base.get(model).getDisplayHint();
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
        if(!base.isNull()) out.addAttribute("extends",base.getKey().toString());
        version.writeXML(out);
        super.writeXML(out);
        out.stopEntity();
        
        if(displayHint != null){
        	displayHint.writeXML(out);
        }
    }


    /** copies this meta-entity to a copy
     * @param copy is the meta entity to copy to
     */
    protected void cloneTo(MetaEntity copy) {
        super.cloneTo(copy);
        copy.base = (MetaEntityProxy)base.clone();
        copy.m_isAbstract = m_isAbstract;
        if(displayHint != null) {
	        copy.displayHint = (MetaEntityDisplayHint)displayHint.clone();
	        copy.displayHint.reBindTo(copy);
        } else  {
        	copy.displayHint = null;
        }
        version.cloneTo(copy.version);
        copy.setModel(null); // disconnect it
    }

    /** Sets the parent meta-model
     * @param m is the meta model to set
     */
    public void setModel(MetaModel m) {
        model = m;
    }
    
    /** Gets the parent meta-model
     * @return the parent meta-model
     */
    public MetaModel getModel() {
        return model;
    }
    
    /** This adds a new MetaProperty to the MetaEntity. If an existing meta
     * property with the same UUID already exists then it is replaced.
     * @param mp is the meta-property to be added.
     */
    public MetaProperty addMetaProperty(MetaProperty mp)  throws Exception {
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
    public MetaProperty getMetaProperty(UUID uuid) throws Exception{
        MetaProperty mp = super.getMetaProperty(uuid);
        if((mp == null) && (!base.isNull()))
            mp = getBase().getMetaProperty(uuid);
        return mp;
    }

    /** This deletes a named MetaProperty from the MetaEntity.
     * @param name is the name of the property to delete
     * @throws Exception 
     * @returns the delete MetaProperty or null if no match for the name
     */
    public MetaProperty deleteMetaProperty(UUID uuid) throws Exception {
        MetaProperty mp = super.deleteMetaProperty(uuid);
        if(model != null && mp != null)
            model.fireMetaEntityChanged(this);
        return mp;
    }
    
    /** gets a collection of all the meta-properties 
     * for this meta-entity including any defined in the base meta-entity.
     * @return a collection of meta-properties.
     */
    public Collection<MetaProperty> getMetaProperties() throws Exception{
        LinkedList<MetaProperty> props = new LinkedList<MetaProperty>(getDeclaredMetaProperties());
        if(!base.isNull()){
            props.addAll(getBase().getMetaProperties());
        }
        return props;
    }

    /**
     * Compare allows comparison of meta-entities for sorting.
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
