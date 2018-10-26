/*
 * MetaEntity.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;


public class PersistentMetaEntity extends PersistentMetaPropertyContainer{

	private PersistentMetaEntity base;
	private boolean isAbstract;
	
    /** 
     * Gets the base MetaEntity this MetaEntity inherits from 
     */
    public PersistentMetaEntity getBase() {
    	return base;
    }
    
    /**
     * Sets the base MetaEntity that this MetaEntity inherits from. This allows the
     * meta model to provide inheritance of entities.
     * @param base is the MetaEntities superclass.
     */
    public  void setBase(PersistentMetaEntity base){
    	this.base = base;
    }

    /**
     * Determines whether this MetaEntity is abstract
     */
    public boolean isAbstract(){
    	return isAbstract;
    }

    /**
     * Sets a MetaEntity as being abstract.  If abstract it cannot be instantiated
     * in a model but can be used as a base entity for other MetaEntities to
     * inherit from.
     * @param isAbstract sets this MetaEntity abstract if true, concrete if false.
     */
    public  void setAbstract(boolean isAbstract){
    	this.isAbstract = isAbstract;
    }

}