/*
 * RepositoryItem.java
 *
 * Created on 19 January 2002, 16:23
 */

package alvahouse.eatool.repository.base;

import alvahouse.eatool.util.UUID;

/**
 * RepositoryItem.  Base class for all items in the repository.
 * @author  rbp28668
 */
public class RepositoryItem implements KeyedItem{

    /** Creates new RepositoryItem 
     * @param key is the key that uniquely identifies this repository item
     */
    public RepositoryItem(UUID key) {
        m_uuid = key;
    }
    
    /** Gets the key that uniquely identifies this repository item
     * @return the item's key
     */
    public UUID getKey() {
        return m_uuid;
    }
    
    /** sets the key for this repository item
     * @param uuid is the key for the item
     */
    public void setKey(UUID uuid) {
        m_uuid = uuid;
    }
    
    /** base implementation of cloneTo.
     * @param copy is the copy to be cloned to
     */
    protected void cloneTo(RepositoryItem copy) {
        copy.m_uuid = (UUID)m_uuid.clone();
    }
    
    /** Tests for equality between repository items 
     * @param other is the other repository item being tested for equality to this one
     * @return true if the items are the same item (keys identical) false otherwise
     */
    public boolean equals(Object other) {
        if(other instanceof RepositoryItem)
            return m_uuid.equals(((RepositoryItem)other).m_uuid);
        else
            return false;
    }
    
    /** Gets the hash-code for the repository item.
     * @return the item's hash code
     */
    public int hashCode() {
        return m_uuid.hashCode();
    }
    
    /** the internal key for the item */
    private UUID m_uuid;

}
