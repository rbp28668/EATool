/*
 * MetaEntity.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.metamodel.MetaEntity;

/**
 * MetaEntity is part of the meta-model and describes one class of
 * entities in the model.
 * 
 * @author rbp28668
 */
public class MetaEntityProxy {

     private MetaEntity metaEntity;
    
    /**
     * 
     */
    MetaEntityProxy(MetaEntity metaEntity) {
        super();
        this.metaEntity = metaEntity;
    }

    /**
     * Converts this MetaEntity into a MetaEntitySet containing just this
     * MetaEntity.
     * @return a new MetaEntitySet containing this MetaEntity.
     */
    public MetaEntitySet toSet(){
        MetaEntitySet set = new MetaEntitySet();
        set.add(metaEntity);
        return set;
    }
    
    /**
     * Gets the name of this MetaEntity.
     * @return String containing the name.
     */
    public String getName(){
        return metaEntity.getName();
    }
    
    /**
     * Gets the description of this MetaEntity.
     * @return String containing the description.
     */
    public String getDescription(){
        return metaEntity.getDescription();
    }
    
    /**
     * Gets any base MetaEntity of this MetaEntity.  If this MetaEntity
     * is derived from (i.e. has a base of) another, then this MetaEntity
     * will inherit all the properties of the base MetaEntity.
     * @return any base MetaEntity or null if this is not derived.
     */
    public MetaEntityProxy getBase(){
        return new MetaEntityProxy( metaEntity.getBase());
    }
    
    /**
     * Gets the set of MetaProperties corresponding to this MetaEntity.
     * @return the MetaPropertySet.
     */
    public MetaPropertySet getMetaProperties(){
        return new MetaPropertySet(metaEntity.getMetaProperties());
    }
}
