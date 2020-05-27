/*
 * PropertySet.java
 * Project: EATool
 * Created on 12-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.model.Property;

/**
 * PropertySet is a set of Property.
 * 
 * @author rbp28668
 */
@Scripted(description="")
public class PropertySet {

    private List<Property> properties;
    /**
     * 
     */
    PropertySet(Collection<Property> properties) {
        super();
        this.properties = new LinkedList<Property>(properties);
    }
    
    /**
     * Gets the number of Property in this set.
     * @return property count.
     */
    @Scripted(description="")
    public int getCount(){
        return properties.size();
    }
    
    /**
     * Determines whether this set is empty or not.
     * @return true if the set is empty, false if not.
     */
    @Scripted(description="")
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * Removes and returns the first entry in the set. Use with isEmpty() to
     * iterate through all the properties in a set.  If you don't want to 
     * destroy the set then use copy and work through the copy instead.
     * @return the first property.
     */
    @Scripted(description="")
    public PropertyProxy removeFirst(){
        return new PropertyProxy(properties.remove(0));
    }
    
    /**
     * Copies the property set.  Note that the new property set shares
     * the underlying properties of this property set.
     * @return a new PropertySet.
     */
    @Scripted(description="")
    public PropertySet copy(){
        return new PropertySet(properties);
    }
}
