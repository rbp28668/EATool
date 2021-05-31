/*
 * PropertyTranslationCollection.java
 *
 * Created on 26 February 2002, 03:24
 */

package alvahouse.eatool.repository.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Property;

/**
 * This is a collection of PropertyTranslation that, as whole, can map, or identify an entity.
 * @author  rbp28668
 */
public class PropertyTranslationCollection {

    private List<PropertyTranslation> props = new LinkedList<PropertyTranslation>(); // property input descriptions
    private Map<String,PropertyTranslation> lookup = new HashMap<String,PropertyTranslation>();     // and for fast lookup


    /** Creates new empty PropertyTranslationCollection */
    public PropertyTranslationCollection() {
    }


    /** adds a new property translation to the collection
     * @param pt is the property translation to add
     */
    public void addProperty(PropertyTranslation pt) {
        if(pt == null){
            throw new NullPointerException("Can't add a null PropertyTranslation");
        }
        props.add(pt);
        lookup.put(pt.getTypeName(), pt);
    }
    
    /**
     * Removes a PropertyTranslation from the list.
     * @param translation is the PropertyTranslation to remove.
     */
    public void remove(PropertyTranslation translation) {
        props.remove(translation);
        lookup.remove(translation.getTypeName());
     }
    
    /** Looks up a property translation by the typename for that property
     * where this typename is the name used to identify a property in
     * a data import.
     * @param typename is the name of the property to look up.
     * @return the corresponding property.
     */
    PropertyTranslation getPropertyTranslationByType(String typename) {
        return lookup.get(typename);
    }

    /** get a collection of all the property translations
     * @return a Collection.
     */
    public Collection<PropertyTranslation> getPropertyTranslations() {
        return props;
    }

    /** given an entity, this works out a natural key string based on which
     * property translations in the collection are marked as key properties
     * @param e is the entity to find the key of
     * @return a string key value for this entity
     */
    String getKeyOf(Entity e) {
        StringBuffer key = new StringBuffer();
        for(PropertyTranslation pt : getPropertyTranslations()){
            if(pt.isKeyValue()){
                Property p = e.getPropertyByMeta(pt.getMeta().getKey());
                String value = p.getValue().trim();
                if(value.length() == 0){
                    //throw new IllegalArgumentException("Key Property " + pt.getTypeName() + " missing when importing XML");
                    System.out.println("Warning Key Property " + pt.getTypeName() + " missing when importing XML");
                }
                if(key.length() > 0) { 
                    key.append('|');
                }
                key.append(value);
            }
        }
        //System.out.println("Key: " + key.toString());
        return key.toString();
    }

    /** gets the number of properties in the list
     * @return the size of the list
     */
    int getPropertyCount() {
        return props.size();
    }
    
    protected void cloneTo(PropertyTranslationCollection copy) {
        for(PropertyTranslation pt : props) {
        	copy.addProperty( (PropertyTranslation) pt.clone());
        }

    }

    
}
