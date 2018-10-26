/**
 * 
 */
package alvahouse.eatool.repository.base;

import alvahouse.eatool.util.UUID;

/**
 * Interface to be implemented by anything which is tracked by a UUID key.
 * @author bruce.porteous
 *
 */
public interface KeyedItem {

	 /** Gets the key that uniquely identifies this repository item
     * @return the item's key
     */
    public UUID getKey();
    
    /** sets the key for this repository item
     * @param uuid is the key for the item
     */
    public void setKey(UUID uuid);

}
