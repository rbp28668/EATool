/*
 * MetaRelationshipRestriction.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;


public class PersistentMetaRelationshipRestriction {

	private String name;
	
    /**
     * Each restriction is named for reporting purposes.
     * @return the name of the restriction.
     */
    public String getName(){
    	return name;
    }

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

 
}