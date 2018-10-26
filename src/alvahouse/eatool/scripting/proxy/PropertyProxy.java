/*
 * Property.java
 * Project: EATool
 * Created on 12-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;


/**
 * Property holds a single value as part of an Entity.  As well as
 * allowing access to the property's value, this class also provides convenience methods which
 * provide meta-information pertaining to this property.
 * 
 * @author rbp28668
 */
public class PropertyProxy {

    private alvahouse.eatool.repository.model.Property property;
    /**
     * Creates a proxy wrapping the given Property.
     */
    PropertyProxy(alvahouse.eatool.repository.model.Property property) {
        super();
        this.property = property;
    }
    
    /**
     * Get the value of this property as a string.
     * @return the property's value.
     */
    public String getValue(){
        return property.getValue();
    }

    /**
     * Sets the value of this property.  Note that this will throw
     * an exception if the property value is invalid.
     * @param value is the value to set.
     */
    public void setValue(String value){
        property.setValue(value);
    }
    
    /**
     * Determines whether the given value is valid for this property.
     * @param value is the value to check.
     * @return true if valid, false otherwise.
     */
    public boolean isValid(String value){
        boolean valid = true;
        try {
            property.getMeta().getMetaPropertyType().validate(value);
        } catch (IllegalArgumentException e) {
            valid = false;
        }
        return valid;
    }
    
    /**
     * Gets the name of this property.
     * @return the property name.
     */
    public String getName(){
        return property.getMeta().getName();
    }
    
    /**
     * Gets the description of this property.
     * @return the property description.
     */
    public String getDescription(){
        return property.getMeta().getDescription();
    }
    
    /**
     * Gets the data type name for this property.
     * @return a String containing the type name.
     */
    public String getTypeName(){
        return property.getMeta().getMetaPropertyType().getName();
    }
    
    /**
     * Determines whether this property must have a value.
     * @return true if mandatory, false if not.
     */
    public boolean isMandatory(){
        return property.getMeta().isMandatory();
    }
    
    /**
     * Gets the default value for this property.
     * @return the default value.
     */
    public String getDefaultValue(){
        return property.getMeta().getDefaultValue();
    }

    /**
     * Gets the  MetaProperty that describes this Property.
     * @return the corresponding MetaProperty.
     */
    public MetaPropertyProxy getMeta(){
        return new MetaPropertyProxy(property.getMeta());
    }
}
