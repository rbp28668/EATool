package alvahouse.eatool.repository.metamodel;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;
/**
 * Class that provides information on how an entity should be named (as there
 * are not necessarily any common properties).
 * @author  rbp28668
 */
public class MetaEntityDisplayHint {
    
    /** List of the UUIDs of the properties to display */
    private List<UUID> keys = new LinkedList<UUID>();
    
    /** Target meta-entity this applies to */
    private MetaEntity target;
    
    
    /** Creates new MetaEntityDisplayHint  */
    public MetaEntityDisplayHint(MetaEntity target) {
    	this.target = target;
    }

    /** Creates a copy of the MetaEntityDisplayHint
     * @return a new MetaEntityDisplayHint 
     */
    public Object clone() {
        MetaEntityDisplayHint copy = new MetaEntityDisplayHint(this.target);
        cloneTo(copy);
        return copy;
    }
    
    /**
     * In conjunction with clone, allow the copy to be bound to
     * a new MetaEntity.
     * @param target is the new meta entity to bind to.
     */
    void reBindTo(MetaEntity target) {
    	this.target = target;
    }
//    
//    /** Updates this meta entity from a copy.  Used for editing - the 
//     * copy should be edited (use clone to get the copy) and only if the
//     * edit is successful should the original be updated by calling this
//     * method on the original MetaEntityDisplayHint.  This ensures that any listeners
//     * are updated properly.
//     * @param copy is the meta-entity to copy from
//     */
//    public void updateFromCopy(MetaEntityDisplayHint copy) {
//        copy.cloneTo(this);
//     }
    
    
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
    public boolean referencesProperty(MetaProperty mp) {
    	for (UUID element : keys) {
			if(mp.getKey().equals(element)) {
				return true;
			}
		}
		return false;
    }
    
   /**
     * Writes the DisplayHint out as XML
     * @param out is the XMLWriterDirect to write the XML to
     * @throws IOException
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("DisplayHint");
        out.addAttribute("hintFor",target.getKey().toString());
        for(UUID key : keys){
			out.startEntity("NameKey");
        	out.addAttribute("key",key.toString());
            out.stopEntity();
        }
     	out.stopEntity();
  
    }
    
	/**
	 * Method cloneTo implements a copy from one 
	 * MetaEntityDisplayHint to another
	 * @param copy is the MetaEntityDisplayHint to be copied
	 * to.
	 */
    private void cloneTo(MetaEntityDisplayHint copy) {
    	copy.target = target;
    	copy.keys = new LinkedList<UUID>();
    	copy.keys.addAll(keys);
    }
}

