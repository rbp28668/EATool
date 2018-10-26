package alvahouse.eatool.repository.security;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.util.UUID;

/** 
 *  Allows the user do do something - e.g. "create"
 */
public class SecurityRole extends RepositoryItem implements Securable {

	/** ACL which controls access to this role */
	private ACL acl;
	
	/** Name of this security role */
	private String name;
	
	public SecurityRole(){
		super(new UUID());
	}
	
	/**
	 * @return the acl
	 */
	public ACL getAcl() {
		return acl;
	}

	/**
	 * @param acl the acl to set
	 */
	public void setAcl(ACL acl) {
		this.acl = acl;
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

	public boolean isUserInRole(User user) {
		return hasPermission(user, Permission.ALL);
	}

	/* (non-Javadoc)
	 * @see com.northgate.orion.model.security.Securable#hasPermission(com.northgate.orion.model.security.User, com.northgate.orion.model.security.Permission)
	 */
	public boolean hasPermission(User user, Permission permission) {
		return acl.hasPermission(user, permission);
	}
	
	
}