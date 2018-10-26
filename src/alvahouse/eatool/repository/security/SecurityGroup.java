/*
 * Group.java
 * Project: EATool
 * Created on 19-Nov-2005
 *
 */
package alvahouse.eatool.repository.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Group is a group of users.
 * 
 * @author rbp28668
 */
public class SecurityGroup  implements Group{
	private SecurityGroup parent;
    private List<Principal> principals = new LinkedList<Principal>();
    private String name;
    
    public SecurityGroup(String name){
        this.name = name;
    }

    
    
    /**
	 * @return the parent
	 */
	public SecurityGroup getParent() {
		return parent;
	}



	/**
	 * @param parent the parent to set
	 */
	public void setParent(SecurityGroup parent) {
		this.parent = parent;
	}



	@Override
    public boolean addMember(Principal user) {
        if(principals.contains(user)){
            return false;
        }
        principals.add(user);
        return true;
    }

    @Override
    public boolean isMember(Principal member) {
        return principals.contains(member);
    }

    @Override
    public Enumeration<? extends Principal> members() {
        ItEnum<? extends Principal> ie;
        ie = new ItEnum<Principal>(principals.iterator());
        return ie;
     }

    @Override
    public boolean removeMember(Principal user) {
        if(principals.contains(user)){
            return false;
        }
        principals.remove(user);
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
