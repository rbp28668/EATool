/*
 * PropertyContainerFactory.java
 * Project: EATool
 * Created on 07-Jul-2006
 *
 */
package alvahouse.eatool.repository.model;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.UUID;

/**
 * PropertyContainerFactory is a base class for factory classes of
 * objects that inherit from PropertyContainer.
 * 
 * @author rbp28668
 */
public class PropertyContainerFactory extends FactoryBase{

    /** The Property being read in at the current time */
    private Property currentProperty = null;
    
    /**
     * 
     */
    public PropertyContainerFactory() {
        super();
    }

    /**
     * Starts a property on a "Property" tag.
     * @param container is the PropertyContainer for the property.
     * @param uri
     * @param local
     * @param attrs
     * @return true if the XML tag was "Property".
     */
    protected boolean startProperty(PropertyContainer container,String uri, String local, Attributes attrs){
        boolean isProperty = false;
        if (local.equals("Property")) {
	        if(container == null) 
	            throw new InputException("Property outside Entity while loading XML");
	            
	        if(currentProperty != null)
	            throw new InputException("Nested Property loading XML into repository");
	
	        UUID uuid = getUUID(attrs);
	
	        String instance = attrs.getValue("instanceof");
	        if(instance == null)
	            throw new IllegalArgumentException("Missing instanceof attribute of Property while loading XML");
	        UUID uuidMeta = new UUID(instance);
	        //MetaProperty mp = currentEntity.getMeta().getMetaProperty(new UUID(instance));
	        
	        currentProperty = container.getPropertyByMeta(uuidMeta);
	        if(currentProperty == null)
	            throw new IllegalArgumentException("Class of property does not match current class while loading XML");
	        
	        currentProperty.setKey(uuid);
	        isProperty = true;
	    }
        return isProperty;
    }
    
    /**
     * Terminates a property on a Property closing tag.
     * @param container is the PropertyContainer for the property.
     * @param uri
     * @param local
     * @return true if the closing tag was "Property".
     */
    protected boolean endProperty(PropertyContainer container,String uri, String local){
        boolean isProperty = false;
        if (local.equals("Property")) {
            currentProperty = null;
	        isProperty = true;
	    }
        return isProperty;
    }
    
    /**
     * Optionally sets the property value.
     * @param str is the value to set.
     */
    public void setPropertyValue(String str) {
        if(currentProperty != null) {
            currentProperty.setValue(str);
        }
    }

}
