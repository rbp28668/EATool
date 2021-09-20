/*
 * Versionable.java
 * Project: EATool
 * Created on 25 Nov 2007
 *
 */
package alvahouse.eatool.repository.version;

import java.util.Date;

import alvahouse.eatool.util.UUID;

/**
 * Versionable interface that defines an object as containing version information.
 * As well as the usual create/modify dates and users, it also contains a UUID
 * that identifies a given version.  This can be used for optimistic locking.
 * 
 * @author rbp28668
 */
public interface Version {
    /**
     * @return the createDate
     */
    public Date getCreateDate();
    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate);
    /**
     * @return the createUser
     */
    public String getCreateUser();
    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser);
    /**
     * @return the modifyDate
     */
    public Date getModifyDate();
    /**
     * @param modifyDate the modifyDate to set
     */
    public void setModifyDate(Date modifyDate);
    /**
     * @return the modifyUser
     */
    public String getModifyUser();
    /**
     * @param modifyUser the modifyUser to set
     */
    public void setModifyUser(String modifyUser);
    
    /**
     * Get the version key for the versionable object. 
     * The version key should be supplied by the persistence mechanism.  Used for
     * optimistic locking - if a modified object is written back to persistent 
     * store but the supplied version does not match the version in the 
     * persistent store then the persistent store has been modified (and will
     * result in  stale data exception).
     * @return the version
     */
    public String getVersion();
    
    /**
     * @param version the version to set
     */
    public void setVersion(String version);

    
    /**
     * Gets the previous version that was in play before the current one.  
     * Intended to help diagnose conflicts.
     */
    public String getOriginalVersion();
    
    /**
     * Sets the original version.
     * @param version the version to set
     */
    public void setOriginalVersion(String version);
    
    
    /**
     * Convenience method to set all the fields at creation time.
     * @param user is the user creating the object.
     */
    public void createBy(String user);

    /**
     * Convenience method to set appropriate fields at modification time.
     * @param user is the user modifying the object.
     */
    public void modifyBy(String user);
    
	/**
	 * Call when persistence layer provides a version string from a create or update operation.
	 * If version is null then the version is not updated. This is to support in memory 
	 * repository where you don't want to update the version every time you load from disk.
	 * @param version
	 */
	public void update(String version);
}
