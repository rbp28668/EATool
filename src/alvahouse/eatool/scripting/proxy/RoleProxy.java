/*
 * Role.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

/**
 * Role is one end of a Relationship and links to the related Entity.
 * A Relationship has 2 roles - one for the start end and one for the
 * finish end.  
 * 
 * @author rbp28668
 */
public class RoleProxy {

    private alvahouse.eatool.repository.model.Role role;
    /**
     * 
     */
    RoleProxy(
            alvahouse.eatool.repository.model.Role role) {
        super();
        this.role = role;
    }
    
    /**
     * Gets the name of this Role from the meta-model.
     * @return the name.
     */
    public String getName(){
        return role.getMeta().getName();
    }
    
    /**
     * Gets the description of this Role from the meta-model.
     * @return the description.
     */
    public String getDescription(){
        return role.getMeta().getDescription();
    }
    
    /**
     * Gets the Entity this Role connects to.
     * @return the connected Entity.
     */
    public EntityProxy connectsTo(){
        return new EntityProxy(role.connectsTo());
    }

    /**
     * Gets the MetaRole that describes this Role.
     * @return the corresponding MetaRole.
     */
    public MetaRoleProxy getMeta(){
        return new MetaRoleProxy(role.getMeta());
    }
}
