/*
 * Relationship.java
 *
 * Created on 11 January 2002, 01:36
 */

package alvahouse.eatool.repository.db.model;
import alvahouse.eatool.repository.db.metamodel.PersistentMetaRelationship;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;

/**
 * Relationship models a relationship between to Entities.  The Entities
 * are linked by means of Roles.  The Relationship is directional hence the 2
 * Roles are labelled start and finish.
 * @author  rbp28668
 */
public class PersistentRelationship extends PersistentPropertyContainer implements  Versionable{

    private PersistentMetaRelationship meta; 
    private PersistentModel model;       // that this belongs to
    private PersistentRole start;
    private PersistentRole finish;
    private VersionImpl version = new VersionImpl();

	/**
	 * Method Relationship creates a new relationship corresponding to
	 * an existing relationship where the UUID is known ( such as from
	 * persistent store).  This assumes that the roles will be added
	 * later by the persistence mechanism).
	 * @param uuid is the key to the existing relationship.
	 * @param mr is the corresponding meta-relationship.
	 */
    public PersistentRelationship(UUID uuid, PersistentMetaRelationship mr) {
        super(uuid);
        meta = mr;
    }
    
	/**
	 * Method Relationship creates a new, disconnected relationship.
	 * @param mr is the corresponding meta-relationship.
	 */
    public PersistentRelationship(PersistentMetaRelationship mr) {
    	super(new UUID());
    	meta = mr;
    	start = new PersistentRole(new UUID(), meta.getStart());
    	finish = new PersistentRole(new UUID(), meta.getFinish());
    	
    }

    /** sets the meta Relationship that this Relationship is an instance of
     * @param me is the associated meta Relationship
     */
    public void setMeta(PersistentMetaRelationship mr) {
        meta = mr;
    }
    
    /** gets the meta Relationship that this Relationship is an instance of
     * @return the associated meta Relationship
     */
    public PersistentMetaRelationship getMeta() {
        return meta;
    }
    
    
     
    public PersistentRole getStart() {
        return start;
    }
    
    public PersistentRole getFinish() {
        return finish;
    }
    
    /**
     * Sets the Role at the start of the relationship.
     * @param r is the Role to set.
     * @throws NullPointerException if r is null.
     * @throws IllegalArgumentException if r is of the wrong type as 
     * determined by the metamodel.
     */
    public void setStart(PersistentRole r){
        if(r == null) {
            throw new NullPointerException("Can't set a null start role on a relationship");
        }

        start = r;
    }
    
    /**
     * Sets the Role at the finish of the relationship.
     * @param r is the Role to set.
     * @throws NullPointerException if r is null.
     * @throws IllegalArgumentException if r is of the wrong type as 
     * determined by the metamodel.
     */
   public void setFinish(PersistentRole r){
        if(r == null) {
            throw new NullPointerException("Can't set a null finish role on a relationship");
        }

        finish = r;
        
    }
     /** Sets the parent model
     * @param m is the model to set
     */
    void setModel(PersistentModel m) {
        model = m;
    }
    
    /** Gets the parent model that this relationship belongs to.
     * @return the parent Model.
     */
    public PersistentModel getModel() {
        return model;
    }

 
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getVersion()
     */
    public Version getVersion() {
        return version;
    }

}
