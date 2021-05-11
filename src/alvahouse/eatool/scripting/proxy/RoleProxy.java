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
@Scripted(description="Role is one end of a Relationship and links to the related Entity.")
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
    @Scripted(description="Gets the name of this Role from the meta model.")
    public String getName(){
        return role.getMeta().getName();
    }
    
    /**
     * Gets the description of this Role from the meta-model.
     * @return the description.
     */
    @Scripted(description="Gets the description of this role from the meta model.")
    public String getDescription(){
        return role.getMeta().getDescription();
    }
    
    /**
     * Gets the Entity this Role connects to.
     * @return the connected Entity.
     */
    @Scripted(description="Gets the entity this role connects to.")
    public EntityProxy connectsTo() throws Exception{
        return new EntityProxy(role.connectsTo());
    }

    /**
     * Gets the MetaRole that describes this Role.
     * @return the corresponding MetaRole.
     */
    @Scripted(description="Gets the meta-role for this role.")
    public MetaRoleProxy getMeta(){
        return new MetaRoleProxy(role.getMeta());
    }
}
