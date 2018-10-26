package alvahouse.eatool.repository.db.metamodel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.UUID;
/**
 * Class that provides information on how an entity should be named (as there
 * are not necessarily any common properties).
 * @author  rbp28668
 */
public class PersistentMetaEntityDisplayHint {
    
    /** List of the UUIDs of the properties to display */
    private List<UUID> keys = new LinkedList<UUID>();
    
    /** Target meta-entity this applies to */
    private PersistentMetaEntity target;
    
    
    /** Creates new MetaEntityDisplayHint  */
    public PersistentMetaEntityDisplayHint(PersistentMetaEntity target) {
    	this.target = target;
    }

    
    /** adds a property key to the list of properties to use
     * to form the name of a corresponding entity.
     * @param key is the property key to add.
     */
    public void addPropertyKey(UUID key) {
        keys.add(key);
    }
    
    /**
	 * clearPropertyKeys clears the entire list of 
	 * property keys.
	 */
	public void clearPropertyKeys() {
    	keys.clear();
    }
	
	/**
	 * Gets the collection of keys in this hint.
	 * @return a collection of keys (may be empty, never null).
	 */
	public Collection<UUID> getKeys(){
	    return Collections.unmodifiableCollection(keys);
	}
	
	/**
	 * Method referencesProperty determines whether a display
	 * hint references a given meta-property.
	 * @param mp is the MetaProperty to check against.
	 * @return boolean if the given meta-property is referenced, false otherwise
	 */
    public boolean referencesProperty(PersistentMetaProperty mp) {
    	for (UUID element : keys) {
			if(mp.getKey().equals(element)) {
				return true;
			}
		}
		return false;
    }
    
 }

