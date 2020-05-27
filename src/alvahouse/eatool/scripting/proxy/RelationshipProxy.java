/*
 * Relationship.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.util.UUID;

/**
 * Relationship connects 2 Entity together in the model.
 * 
 * @author rbp28668
 */
@Scripted(description="Relationship connects 2 Entities together in the model")
public class RelationshipProxy {

    private alvahouse.eatool.repository.model.Relationship relationship;
    
    /**
     * 
     */
    RelationshipProxy(
            alvahouse.eatool.repository.model.Relationship relationship) {
        super();
        this.relationship = relationship;
    }
    
    Relationship get() {
    	return relationship;
    }
    
    /**
     * Gets the name of this Relationship from the meta-model.
     * @return the MetaProperty name.
     */
    @Scripted(description="Gets the name of this Relationship from the meta-model.")
    public String getName(){
        return relationship.getMeta().getName();
    }
    
    /**
     * Gets the description of this Relationship from the meta-model.
     * @return the description.
     */
    @Scripted(description="Gets the description of this Relationship from the meta-model.")
    public String getDescription(){
        return relationship.getMeta().getDescription();
    }

    /**
     * Gets the start Role of the Relationship.  Note that the relationship
     * name and description should refer to the direction from start to finish.
     * @return the start Role.
     */
    @Scripted(description="Gets the start Role of the Relationship.  Note that the relationship" + 
    		" name and description should refer to the direction from start to finish.")
    public RoleProxy getStart(){
        return new RoleProxy(relationship.start());
    }

    /**
     * Gets the finish Role of the Relationship.  Note that the relationship
     * name and description should refer to the direction from start to finish.
     * @return the finish Role.
     */
    @Scripted(description="Gets the finish Role of the Relationship.  Note that the relationship" + 
    		" name and description should refer to the direction from start to finish.")
    public RoleProxy getFinish(){
        return new RoleProxy(relationship.start());
    }
    
    /**
     * Gets the MetaRelationship that corresponds to this Relationship.
     * @return the corresponding MetaRelationship.
     */
    @Scripted(description="Gets the MetaRelationship that corresponds to this Relationship.")
    public MetaRelationshipProxy getMeta(){
        return new MetaRelationshipProxy(relationship.getMeta());
    }

    /**
     * Gets a new RelationshipSet containing just this Relationship.
     * @return a new RelationshipSet.
     */
    @Scripted(description="Gets a new RelationshipSet containing just this Relationship.")
    public RelationshipSet toSet(){
        RelationshipSet set = new RelationshipSet();
        set.add(relationship);
        return set;
    }
    
    /**
     * Gets a given Property using the identifier for its type (meta-property).
     * @param key is a string representation of a UUID that identifies which property is wanted.
     * @return the Property corresponding to the MetaProperty defined by key.
     */
    @Scripted(description="Gets a given Property using the key for its meta-property.")
    public PropertyProxy getProperty(String key){
        Property p = relationship.getPropertyByMeta(new UUID(key));
        return new PropertyProxy(p);
    }

    /**
     * Gets all the properties for this entity.
     * @return a new PropertySet with all this entity's properties.
     */
    @Scripted(description="Gets all the properties for this relationship.")
    public PropertySet getProperties(){
        return new PropertySet(relationship.getProperties());
    }

}
