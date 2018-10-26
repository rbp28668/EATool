/*
 * MetaProperty.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.metamodel.MetaProperty;

/**
 * MetaProperty is part of the meta-model and describes a Property.
 * 
 * @author rbp28668
 */
public class MetaPropertyProxy {

    private MetaProperty metaProperty;
    /**
     * 
     */
    MetaPropertyProxy(MetaProperty metaProperty) {
        super();
        this.metaProperty = metaProperty;
    }

    /**
     * Gets the name of this MetaProperty.
     * @return the MetaProperty name.
     */
    public String getName(){
        return metaProperty.getName();
    }
    
    /**
     * Gets the description of this MetaProperty.
     * @return the MetaProperty description.
     */
    public String getDescription(){
        return metaProperty.getDescription();
    }
    
    /**
     * Gets the data type name for this MetaProperty.
     * @return a String containing the type name.
     */
    public String getTypeName(){
        return metaProperty.getMetaPropertyType().getName();
    }
    
    /**
     * Determines whether this properties corresponding to this 
     * MetaProperty must have a value.
     * @return true if mandatory, false if not.
     */
    public boolean isMandatory(){
        return metaProperty.isMandatory();
    }
    
    /**
     * Gets the default value for properties corresponding to this MetaProperty.
     * @return the default value.
     */
    public String getDefaultValue(){
        return metaProperty.getDefaultValue();
    }
    
}
