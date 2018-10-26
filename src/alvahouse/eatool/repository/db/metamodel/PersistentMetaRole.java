/*
 * MetaRole.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;


public class PersistentMetaRole extends PersistentMetaPropertyContainer{

	private PersistentMultiplicity multiplicity;
	
	private PersistentMetaEntity connection;
	
	private PersistentMetaRelationship metaRelationship;
	
	
    /**
     * Gets the allowed multiplicity for this meta-role.
     * @return the multiplicity.
     */
    public  PersistentMultiplicity getMultiplicity(){
    	return multiplicity;
    }

    /**
     * Sets the allowed multiplicity for this meta-role.
     * @param m is the multiplicity to set.
     */
    public  void setMultiplicity(PersistentMultiplicity m){
    	assert(m != null);
    	this.multiplicity = m;
    }

    /**
     * Gets the MetaEntity this MetaRole connects to.
     * @return the associated MetaEntity.
     */
    public PersistentMetaEntity getConnection(){
    	return connection;
    }

    /**
     * Connects this MetaRole to a MetaEntity.  
     * @param connection is the MetaEntity to connect to.
     */
    public  void setConnection(PersistentMetaEntity connection){
    	assert(connection != null);
    	this.connection = connection;
    }


     /**
     * Gets the MetaRelationship this MetaRole belongs to. 
     * @return the parent MetaRelationship.
     */
    public  PersistentMetaRelationship getMetaRelationship(){
    	return metaRelationship;
    	
    }
    
    /**
     * Sets the parent MetaRelationhip.
     * @param mr is the parent to set.
     */
    public  void setMetaRelationship(PersistentMetaRelationship mr){
    	assert(mr != null);
    	this.metaRelationship = mr;
    }


}