/*
 * Entity.java
 * Project: EATool
 * Created on 12-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;


import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.UUID;

/**
 * Entity describes a "thing" in the model and contain the model's core data as
 * a set of Property.  Entities can be related by Relationships and are described
 * by MetaEntities.
 * 
 * @author rbp28668
 */

@Scripted(name = "Entity", description =
"Entity describes a \"thing\" in the model and contain the model's core data as " +
" a set of Property.  Entities can be related by Relationships and are described" +
" by MetaEntities.")

public class EntityProxy {

    private final Entity entity;
    
    /**
     * Creates a new proxy wrapping the given entity.
     */
    EntityProxy(alvahouse.eatool.repository.model.Entity entity) {
        super();
        this.entity = entity;
    }

    
    /**
     * Gets the key as a string.
     * @return
     */
    public String getKey() {
    	return entity.getKey().toString();
    }
    
    
    /**
     * Determines if this object has the given key.
     * @param key
     * @return
     */
    public boolean is(String key) {
    	return entity.getKey().equals(new UUID(key));
    }
    
    /**
     * Gets the name of the entity (as defined by the type's display hint).
     * @return the entity name.
     */
    public String getName(){
        return entity.toString();
    }
    
    /**
     * Gets the type name for this entity.
     * @return the type name as defined in the metamodel.
     */
    public String getTypeName(){
        return entity.getMeta().getName();
    }
    
    /**
     * Gets a given Property using the identifier for its type (meta-property).
     * @param key is a string representation of a UUID that identifies which property is wanted.
     * @return the Property corresponding to the MetaProperty defined by key.
     */
    public PropertyProxy getProperty(String key){
        Property p = entity.getPropertyByMeta(new UUID(key));
        return new PropertyProxy(p);
    }

    /**
     * Gets all the properties for this entity.
     * @return a new PropertySet with all this entity's properties.
     */
    public PropertySet getProperties(){
        return new PropertySet(entity.getProperties());
    }

    /**
     * Converts this entity into an EntitySet containing just this Entity.
     * @return a new EntitySet.
     */
    public EntitySet toSet(){
        EntitySet set = new EntitySet();
        set.add(entity);
        return set;
    }
    
    /**
     * Gets the MetaEntity that describe this Entity.
     * @return the corresponding MetaEntity.
     */
    public MetaEntityProxy getMeta(){
        return new MetaEntityProxy(entity.getMeta());
    }
}
