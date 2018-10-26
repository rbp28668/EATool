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
     * Get the version key for the versionable object.  The version key should
     * be updated whenever the object is modified.  A modified object can then
     * be detected by version != originalVersion.
     * @return the version
     */
    public UUID getVersion();
    
    /**
     * @param version the version to set
     */
    public void setVersion(UUID version);

    /**
     * Get an original version as stored in a persistence mechanism.  Used for
     * optimistic locking - if a modified object is written back to persistent 
     * store but the original version does not match the version in the 
     * persistent store then the persistent store has been modified.
     * @return the original version as stored in a persistence mechanism.
     */
    public UUID getOriginalVersion();
    
    /**
     * Sets the original version.
     * @param version the version to set
     */
    public void setOriginalVersion(UUID version);
    
    
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
}
