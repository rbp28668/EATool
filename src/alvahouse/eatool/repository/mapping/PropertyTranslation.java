/*
 * PropertyTranslation.java
 *
 * Created on 26 February 2002, 03:30
 */

package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.repository.dto.mapping.PropertyTranslationDto;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * PropertyTranslation manages the mapping of a property in the input
 * file to a given property in an entity.  Only used within this package
 * for model import.
 * @author  rbp28668

 */
public class PropertyTranslation {

    private String type = "";           // typename for input property
    private UUID metaPropertyKey = null;   // corresponding meta property
    private boolean isKey = false;  	// true if forms part of identify

    /**
     * Creates an empty translation.
     */
    public PropertyTranslation(){
    }
    
    public PropertyTranslation(PropertyTranslationDto dto)  {
    	this.type = dto.getType();
    	this.metaPropertyKey = dto.getMetaPropertyKey();
    	this.isKey = dto.isKey();
    }
    
    /**
     * Gets the type name for this property translation.  The type name
     * is the name in the input XML used to identify this type of property.
     * @return String with the type name.
     */
    public String getTypeName() {
        return type;
    }

    /**
     * Sets the type name for this property translation.  The type name
     * is the name in the input XML used to identify this type of property.
     * @param typeName is the type name.
     */
    public void setTypeName(String typeName) {
        if(typeName == null || typeName.length() == 0){
            throw new IllegalArgumentException("Can't set null or empty type name on a PropertyTranslation");
        }
        type = typeName;
    }
    
    /**
     * Gets the MetaProperty this maps to.
     * @return the target MetaProperty.
     */
    public UUID getMetaPropertyKey() {
        return metaPropertyKey;
    }

    /**
     * Sets the MetaProperty this maps to.
     * @param meta is the MetaProperty to map to.
     */
    public void setMeta(MetaProperty meta) {
        if(meta == null) {
            throw new NullPointerException("Can't set null MetaProperty for PropertyTranslation");
        }
        this.metaPropertyKey = meta.getKey();
    }
    
   /**
     * Gets whether this property translation is a key value.  A key value is
     * where this property is one of set of properties which forms a key to 
     * uniquely identify the parent entity in the model.
     * @return true if this property is, or forms part of, the key for its parent entity.
     */
    public boolean isKeyValue() {
        return isKey;
    }

    /**
     * Sets whether this property translation is a key value.  A key value is
     * where this property is one of set of properties which forms a key to 
     * uniquely identify the parent entity in the model.
     * @param b is true if this property is a key value, else false.
     */
    public void setKeyValue(boolean b) {
        isKey = b;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Property " + type + " to " + metaPropertyKey + ((isKey) ? " [key]" : " ");
    }

    /**
     * Writes the property translation to XML.
     * @param writer is the XMLWriterDirect to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("PropertyTranslation");
        writer.addAttribute("key",isKeyValue());
        writer.addAttribute("type",getTypeName());
        writer.addAttribute("uuid",metaPropertyKey.toString());
        writer.stopEntity();
    }

    @Override
    public Object clone() {
    	PropertyTranslation copy = new PropertyTranslation();
        copy.type = type;
        copy.metaPropertyKey = metaPropertyKey;
        copy.isKey = isKey;
        return copy;
    }

	/**
	 * @return
	 */
	public PropertyTranslationDto toDto() {
		PropertyTranslationDto dto = new PropertyTranslationDto();
		dto.setType(type);
		dto.setMetaPropertyKey(metaPropertyKey);
		dto.setKey(isKey);
		return dto;
	}
 
}

