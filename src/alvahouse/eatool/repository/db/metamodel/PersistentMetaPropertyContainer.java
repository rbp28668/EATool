/*
 * MetaPropertyContainer.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.db.PersistentNamedItem;

public class PersistentMetaPropertyContainer extends PersistentNamedItem{

	private PersistentMetaEntityDisplayHint displayHint;
	
	private List<PersistentMetaProperty> metaProperties = new LinkedList<PersistentMetaProperty>();
  
	
	
    /**
	 * @return the displayHint
	 */
	public PersistentMetaEntityDisplayHint getDisplayHint() {
		return displayHint;
	}

	/**
	 * @param displayHint the displayHint to set
	 */
	public void setDisplayHint(PersistentMetaEntityDisplayHint displayHint) {
		this.displayHint = displayHint;
	}

	public List<PersistentMetaProperty> getMetaProperties(){
    	return metaProperties;
    }
    
    /**
     * Sets the list of declared MetaProperties.
     * @param metaProperties
     */
    public  void setMetaProperties(List<PersistentMetaProperty> metaProperties){
    	assert(metaProperties != null);
    	this.metaProperties = metaProperties;
    }


 
}