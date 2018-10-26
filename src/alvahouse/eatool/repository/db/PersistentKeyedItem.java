/**
 * 
 */
package alvahouse.eatool.repository.db;

import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce.porteous
 *
 */
public class PersistentKeyedItem extends VersionImpl{

	private UUID uuid;
	 /** Gets the key that uniquely identifies this repository item
     * @return the item's key
     */
    public UUID getKey(){
    	return uuid;
    }
    
    /** sets the key for this repository item
     * @param uuid is the key for the item
     */
    public void setKey(UUID uuid){
    	assert(uuid != null);
    	this.uuid = uuid;
    }

}
