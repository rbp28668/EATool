/*
 * Permission.java
 * Project: EATool
 * Created on 19-Nov-2005
 *
 */
package alvahouse.eatool.repository.security;

import java.security.acl.Permission;

/**
 * Permission encapsulates a security permission for an object.
 * 
 * @author rbp28668
 */
public class SecurityPermission implements Permission{

    public static final SecurityPermission READ = new SecurityPermission("read"); 
    public static final SecurityPermission UPDATE = new SecurityPermission("update"); 
    public static final SecurityPermission CREATE = new SecurityPermission("create"); 
    public static final SecurityPermission DELETE = new SecurityPermission("delete"); 

    private String type;
    
    private SecurityPermission(String type){
        this.type = type;
    }
    
    public String toString() {
        return type;
    }
    
    /* (non-Javadoc)
     * Note - compare by reference as people shouldn't be able to magic up permissions.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object another){
        return this == another;
    }
}
