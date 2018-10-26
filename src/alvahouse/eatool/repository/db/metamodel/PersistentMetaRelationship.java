/*
 * MetaRelationship.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;



public class PersistentMetaRelationship extends PersistentMetaPropertyContainer {

	private PersistentMetaRole start;
	private PersistentMetaRole finish;
	private PersistentMetaRelationshipRestriction restriction;
    
	/**
     * Gets the MetaRole at the start end of the MetaRelationship.
     * @return MetaRole at the start end.
     */
    public  PersistentMetaRole getStart(){
    	return start;
    }

    
    /**
	 * @param start the start to set
	 */
	public void setStart(PersistentMetaRole start) {
		this.start = start;
	}


	/**
     * Gets the MetaRole at the finish end of the MetaRelationship.
     * @return MetaRole at the finish end.
     */
    public PersistentMetaRole getFinish(){
    	return finish;
    }
    
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(PersistentMetaRole finish) {
		this.finish = finish;
	}


    /**
     * Get any additional restriction placed on this relationship.
     * @return MetaRelationshipRestriction. Should never be null.
     */
    public  PersistentMetaRelationshipRestriction getRestriction(){
    	return restriction;
    }

	/**
	 * @param restriction the restriction to set
	 */
	public void setRestriction(PersistentMetaRelationshipRestriction restriction) {
		this.restriction = restriction;
	}

    
}