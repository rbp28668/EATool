/*
 * MetaEntity.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.util.UUID;

/**
 * MetaEntity is part of the meta-model and describes one class of
 * entities in the model.
 * 
 * @author rbp28668
 */
@Scripted(description="Meta entity describes one class of entities in the model.")    
public class MetaEntityProxy {

     private MetaEntity metaEntity;
    
    /**
     * 
     */
    MetaEntityProxy(MetaEntity metaEntity) {
        super();
        this.metaEntity = metaEntity;
    }

    MetaEntity get() {
    	return metaEntity;
    }
    
    /**
     * Converts this MetaEntity into a MetaEntitySet containing just this
     * MetaEntity.
     * @return a new MetaEntitySet containing this MetaEntity.
     */
    @Scripted(description="Converts this MetaEntity into a MetaEntitySet containing just this MetaEntity.")    
    public MetaEntitySet toSet(){
        MetaEntitySet set = new MetaEntitySet();
        set.add(metaEntity);
        return set;
    }
    
    /**
     * Gets the name of this MetaEntity.
     * @return String containing the name.
     */
    @Scripted(description="Gets the name of this MetaEntity.")    
    public String getName(){
        return metaEntity.getName();
    }
    
    /**
     * Gets the description of this MetaEntity.
     * @return String containing the description.
     */
    @Scripted(description="Gets the description of this MetaEntity.")    
    public String getDescription(){
        return metaEntity.getDescription();
    }
    
    /**
     * Gets any base MetaEntity of this MetaEntity.  If this MetaEntity
     * is derived from (i.e. has a base of) another, then this MetaEntity
     * will inherit all the properties of the base MetaEntity.
     * @return any base MetaEntity or null if this is not derived.
     */
    @Scripted(description="Gets any base MetaEntity of this MetaEntity. "
    		+ "If this MetaEntity is derived from (i.e. has a base of) another,"
    		+ " then this MetaEntity will inherit all the properties of the base MetaEntity."
    		+ " This returns any base MetaEntity or null if this is not derived.")    
    public MetaEntityProxy getBase() throws Exception{
    	MetaEntity base = metaEntity.getBase();
        return (base != null) ? new MetaEntityProxy(base) : null;
    }
    
    /**
     * Gets the set of MetaProperties corresponding to this MetaEntity.
     * @return the MetaPropertySet.
     */
    @Scripted(description="Gets the set of MetaProperties corresponding to this MetaEntity.")    
    public MetaPropertySet getMetaProperties() throws Exception{
        return new MetaPropertySet(metaEntity.getMetaProperties());
    }
    
    /**
     * Gets an individual meta property corresponding to the given key.
     * @return the MetaPropertySet.
     */
    @Scripted(description="Gets an individual meta property corresponding to the given key.")    
    public MetaPropertyProxy getMetaProperty(String key) throws Exception{
        MetaProperty p = metaEntity.getMetaProperty(new UUID(key));
        return new MetaPropertyProxy(p);
    }

}
