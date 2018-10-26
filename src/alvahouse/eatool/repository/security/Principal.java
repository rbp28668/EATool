package alvahouse.eatool.repository.security;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.util.UUID;



/** 
 *  An entity that can have permissions
 */
public class Principal extends RepositoryItem {

    /**
   * 
   * @element-type ACLEntry
   */

	private String name;
	private boolean isGroup = false;
	
	public Principal(){
		this("",false);
	}
	
	public Principal(boolean isGroup){
		this("",isGroup);
	}
	
	public Principal(String name, boolean isGroup){
		super(new UUID());
		this.name = name;
		this.isGroup = isGroup;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Is this a group?  
	 * @return whether this principal is a group.
	 */
	public boolean isGroup() {
		return isGroup;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getName();
	}

}