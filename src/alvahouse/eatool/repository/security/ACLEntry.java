package alvahouse.eatool.repository.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.util.UUID;

/** 
 *  Defines the permissions given to a specific Principal.  
 *  Note that a user may inherit permissions from parent groups.
 */
public class ACLEntry extends RepositoryItem{

	/** Whether to allow or deny access to the resource */
	private boolean allow = true;

	/** Who is being allowed access */
    private Principal principal;
    
    /** Permissions for this principal */
    public Set<Permission>  permissions = new HashSet<Permission>();

    public ACLEntry(){
    	this(new UUID());
    }
    
    public ACLEntry(UUID uuid){
    	super(uuid);
    }
	/**
	 * @return the allow
	 */
	public boolean isAllow() {
		return allow;
	}

	/**
	 * @return the principal
	 */
	public Principal getPrincipal() {
		return principal;
	}

	/**
	 * @return the permissions
	 */
	public Set<Permission> getPermissions() {
		return Collections.unmodifiableSet(permissions);
	}

	/**
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(Permission permission) {
		return permissions.contains(permission);
	}
    
    /**
     * Gets the set of permissions as a text string for persistence.
     * @return
     */
    public String getPermissionText(){
    	StringBuffer text = new StringBuffer();
    	for(Permission permission : permissions){
    		if(text.length() > 0){
    			text.append(';');
    		}
    		text.append(permission.toString());
    	}
    	return text.toString();
    }
    
    /**
     * Sets the permissions from a text string (produced by getPermissionText()).
     * @param text
     */
    public void setPermissionText(String text){
    	permissions.clear();
    	StringTokenizer toks = new StringTokenizer(text,";");
    	while(toks.hasMoreTokens()){
    		String tok = toks.nextToken();
    		Permission p = Permission.lookup(tok);
    		if(p == null){
    			throw new IllegalArgumentException("Invalid permission " + tok);
    		}
    		permissions.add(p);
    	}
    }

	/**
	 * @param allow the allow to set
	 */
	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @param permission is the Permission to add.
	 */
	public void addPermission(Permission permission) {
		if(permissions == null){
			throw new NullPointerException("Cannot add null Permission");
		}
		permissions.add(permission);
	}
}