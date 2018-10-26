/*
 * Relationship.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

/**
 * Relationship connects 2 Entity together in the model.
 * 
 * @author rbp28668
 */
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
    
    /**
     * Gets the name of this Relationship from the meta-model.
     * @return the MetaProperty name.
     */
    public String getName(){
        return relationship.getMeta().getName();
    }
    
    /**
     * Gets the description of this Relationship from the meta-model.
     * @return the description.
     */
    public String getDescription(){
        return relationship.getMeta().getDescription();
    }

    /**
     * Gets the start Role of the Relationship.  Note that the relationship
     * name and description should refer to the direction from start to finish.
     * @return the start Role.
     */
    public RoleProxy getStart(){
        return new RoleProxy(relationship.start());
    }

    /**
     * Gets the finish Role of the Relationship.  Note that the relationship
     * name and description should refer to the direction from start to finish.
     * @return the finish Role.
     */
    public RoleProxy getFinish(){
        return new RoleProxy(relationship.start());
    }
    
    /**
     * Gets the MetaRelationship that corresponds to this Relationship.
     * @return the corresponding MetaRelationship.
     */
    public MetaRelationshipProxy getMeta(){
        return new MetaRelationshipProxy(relationship.getMeta());
    }

    /**
     * Gets a new RelationshipSet containing just this Relationship.
     * @return a new RelationshipSet.
     */
    public RelationshipSet toSet(){
        RelationshipSet set = new RelationshipSet();
        set.add(relationship);
        return set;
    }
    
}
