package alvahouse.eatool.repository.security;



/** 
 *  Interface for an object with controlled access
 */
public interface Securable {
    
	public ACL getAcl();
	
	public void setAcl(ACL acl);
	
	public boolean hasPermission(User user,  Permission permission);
}