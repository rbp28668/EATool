/*
 * Role.java
 *
 * Created on 11 January 2002, 01:39
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;

import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;


/**
 * Role models the end of a relationship in that a relationship always has
 * 2 roles.
 * @author  rbp28668
 */
public class Role extends PropertyContainer implements Cloneable{

    private MetaRole meta;              // that this role is an instance of
    private Relationship relationship;  // that this role belongs to.
    private EntityProxy connection = new EntityProxy();   // that this role connects to.
    
    /** Creates new RelationshipEnd */
    public Role(UUID uuid, MetaRole mr) throws Exception {
        super(uuid);
        addDefaultProperties(mr);
        meta = mr;
    }

    /** Creates new RelationshipEnd */
    public Role(MetaRole mr) throws Exception{
        super(new UUID());
        addDefaultProperties(mr);
        meta = mr;
    }

    /**
     * Private constructor for clone.
     * @param uuid
     */
    private Role(UUID uuid) {
    	super(uuid);
    }
    
    public Object clone() {
        Role copy = new Role(getKey());
        
        cloneTo(copy);
        return copy;
    }
    
//    public void updateFromCopy(Role copy) {
//        // copy back maintaining same parent.
//        Relationship parent = relationship;
//        copy.cloneTo(this);
//        relationship = parent;
//    }

    /** sets the entity this role connects to
     * @param e is the entity to connect to
     */
    public void setConnection(Entity e) {
        if(e == null) {
            throw new NullPointerException("Connecting Role to null Entity");
        }
        connection.set(e);
    }
    
    /**
     * Sets the key for the connected entity so that it can be l
     * @param key
     */
    public void setConnectionKey(UUID key) {
    	connection.setKey(key);
    }
    
    /**
     * Disconnects the entity from this role.
     */
    public void disconnect(){
        connection.set(null);
    }
    
    /** gets the entity this role connects to
     * @return the connected entity
     */
    public Entity connectsTo() throws Exception {
        return connection.get(relationship.getModel());
    }
    
    /**
     * Gets the key corresponding to the connected Entity without
     * needing to fetch the entity itself.
     * @return
     */
    public UUID connectionKey() {
    	return connection.getKey();
    }
    
//    /** sets the meta Role that this Role is an instance of
//     * @param me is the associated meta Role
//     */
//     public void setMeta(MetaRole mr) {
//        meta = mr;
//    }
    
    /** gets the meta Role that this Role is an instance of
     * @return the associated meta Role
     */
    public MetaRole getMeta() {
        return meta;
    }

    /**
     * Writes the Role out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Role");
        out.addAttribute("uuid",getKey().toString());
        out.addAttribute("instanceof",meta.getKey().toString());
        out.addAttribute("connects",connection.getKey().toString());
        super.writeXML(out);
        out.stopEntity();
    }

    /** sets the parent relationship
     * @param r is the parent relationship
     */
    public void setRelationship(Relationship r) {
        relationship = r;
    }
    
    /** gets the parent relationship
     * @return the parent relationship
     */
    public Relationship getRelationship() {
        return relationship;
    }
    
    protected void cloneTo(Role copy) {
        super.cloneTo(copy);
        copy.connection = connection;
        copy.meta = meta;               // must be same type
        copy.relationship = null;   // possible different parent.
    }
    
    public String toString(){
        return meta.getName();
    }
}
