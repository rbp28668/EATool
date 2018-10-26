/*
 * PropertyContainer.java
 * Project: EATool
 * Created on 05-Jul-2006
 *
 */
package alvahouse.eatool.repository.db.model;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.util.UUID;

/**
 * PropertyContainer provides a generic base class for any class with
 * a set of properties. 
 * 
 * @author rbp28668
 */
public abstract class PersistentPropertyContainer extends RepositoryItem{

    private LinkedList<PersistentProperty> propertyList = new LinkedList<PersistentProperty>(); // to keep appropriate order

    /**
     * 
     */
    public PersistentPropertyContainer(UUID uuid) {
        super(uuid);
    }
    

    /** gets an iterator that can be used to retrieve all the -properties
     * for this -entity
     * @return an iterator for -properties
     */
    public List<PersistentProperty> getProperties() {
        return propertyList;
    }
    
     
    /**
	 * @return the propertyList
	 */
	public List<PersistentProperty> getPropertyList() {
		return propertyList;
	}


	/**
	 * @param propertyList the propertyList to set
	 */
	public void setPropertyList(LinkedList<PersistentProperty> propertyList) {
		this.propertyList = propertyList;
	}


	/**
     * Adds a property - should only be called in response
     * to a change in the meta-model.
     * @param p is the property to add.
     */
    protected void addProperty(PersistentProperty p){
        if(p == null){
            throw new NullPointerException("Can't add null property");
        }
        propertyList.add(p);
    }
    
    /**
     * Removes a property.  Should only be called
     * in response to a change in the meta-model.
     * @param p is the property to remove.
     */
    protected void removeProperty(PersistentProperty p){
        propertyList.remove(p);
    }
    
    
 
}
