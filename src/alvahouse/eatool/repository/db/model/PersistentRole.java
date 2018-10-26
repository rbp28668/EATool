/*
 * Role.java
 *
 * Created on 11 January 2002, 01:39
 */

package alvahouse.eatool.repository.db.model;

import alvahouse.eatool.repository.db.metamodel.PersistentMetaRole;
import alvahouse.eatool.util.UUID;


/**
 * Role models the end of a relationship in that a relationship always has
 * 2 roles.
 * @author  rbp28668
 */
public class PersistentRole extends PersistentPropertyContainer implements Cloneable{

    private PersistentMetaRole meta;              // that this role is an instance of
    private PersistentRelationship relationship;  // that this role belongs to.
    private PersistentEntity connection;          // that this role connects to.
    
    /** Creates new RelationshipEnd */
    public PersistentRole(UUID uuid, PersistentMetaRole mr) {
        super(uuid);
        meta = mr;
    }

    /** Creates new RelationshipEnd */
    public PersistentRole(PersistentMetaRole mr) {
        super(new UUID());
        meta = mr;
    }

    /** sets the entity this role connects to
     * @param e is the entity to connect to
     */
    public void setConnection(PersistentEntity e) {
        if(e == null) {
            throw new NullPointerException("Connecting Role to null Entity");
        }
        connection = e;
    }
    
    /**
     * Disconnects the entity from this role.
     */
    public void disconnect(){
        connection = null;
    }
    
    /** gets the entity this role connects to
     * @return the connected entity
     */
    public PersistentEntity getConnection() {
        return connection;
    }
    
    /** sets the meta Role that this Role is an instance of
     * @param me is the associated meta Role
     */
     public void setMeta(PersistentMetaRole mr) {
        meta = mr;
    }
    
    /** gets the meta Role that this Role is an instance of
     * @return the associated meta Role
     */
    public PersistentMetaRole getMeta() {
        return meta;
    }


    /** sets the parent relationship
     * @param r is the parent relationship
     */
    public void setRelationship(PersistentRelationship r) {
        relationship = r;
    }
    
    /** gets the parent relationship
     * @return the parent relationship
     */
    public PersistentRelationship getRelationship() {
        return relationship;
    }
    
}
