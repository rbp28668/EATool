package alvahouse.eatool.repository.security;

import java.util.LinkedHashSet;
import java.util.Set;

/** 
 *  A specific user of the system.
 */
public class User extends Principal {

	
	/** Hash of the user's password */
	private String pwdHash;
	
	/** Which groups this user is a member of */
	private Set<SecurityGroup> memberOf = new LinkedHashSet<SecurityGroup>();

	/** Primary group this user is a member of- used for defaulting values */
	private SecurityGroup primaryGroup;
  
	public User(){
		super(false);
	}
	
	public User(String name){
		super(name,false);
	}
  
	/**
	 * Gets the password hash for this user.
	 * @return the pwdHash
	 */
	public String getPwdHash() {
		return pwdHash;
	}

	/**
	 * @param pwdHash the pwdHash to set
	 */
	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}

	/**
	 * @return the memberOf
	 */
	public Set<SecurityGroup> getMemberOf() {
		return memberOf;
	}

	/**
	 * Setter for Hibernate.
	 * @param memberOf the memberOf to set
	 */
	@SuppressWarnings("unused")
	private void setMemberOf(Set<SecurityGroup> memberOf) {
		this.memberOf = memberOf;
	}
  
	
	/**
	 * @return the primaryGroup
	 */
	public SecurityGroup getPrimaryGroup() {
		return primaryGroup;
	}

	/**
	 * @param primaryGroup the primaryGroup to set
	 */
	public void setPrimaryGroup(SecurityGroup primaryGroup) {
		this.primaryGroup = primaryGroup;
	}

	public void addToGroup(SecurityGroup group){
		if(group == null){
			throw new NullPointerException("Can't add user to null security group");
		}
		memberOf.add(group);
	}
	
	public void removeFromGroup(SecurityGroup group){
		if(group == null){
			throw new NullPointerException("Can't remove user from null security group");
		}

		memberOf.remove(group);
	}
  
}