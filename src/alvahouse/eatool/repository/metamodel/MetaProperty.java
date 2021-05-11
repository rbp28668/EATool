/*
 * MetaProperty.java
 *
 * Created on 10 January 2002, 21:31
 */

package alvahouse.eatool.repository.metamodel;


import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * MetaProperty describes a property of an Entity.
 * @author  rbp28668
 */
public class MetaProperty  extends NamedRepositoryItem  {

    private MetaPropertyContainer container = null;
    private MetaPropertyType m_type;
    private String m_default = "";
    private boolean m_mandatory = false;
    private boolean readOnly = false;
    private boolean summary = false;
    
    /** Creates new MetaProperty with default type of string.
     *  @param uuid is the UUID that identifies this MetaProperty.
    */
    public MetaProperty(UUID uuid) {
        super(uuid);
        m_type = MetaPropertyTypes.getBuiltInType("string"); // use string as default type.
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        MetaProperty copy = new MetaProperty(getKey());
        cloneTo(copy);
        return copy;
    }
//
//    /**
//     * Updates this MetaProperty from a copy.  For example a copy
//     * may be made for editing and the original is then updated on
//     * OK.
//     * @param copy is the copy to copy from.
//     */
//    public void updateFromCopy(MetaProperty copy) {
//        // copy back maintaining orignal parent
//        MetaPropertyContainer parent = container;
//        copy.cloneTo(this);
//        container = parent;
//    }
        
    /**
     * Gets the data type of this MetaProperty. 
     * @return the MetaPropertyType giving the data type.
     */
    public MetaPropertyType getMetaPropertyType() {
        return m_type;
    }
    
    /**
     * Sets the data type of this MetaProperty.
     * @param type is the MetaPropertyType to set.
     */
    public void setMetaPropertyType(MetaPropertyType type) {
        m_type = type;
    }

    /**
     * Get whether Properties of this type are mandatory.
     * @return true if mandatory.
     */
    public boolean isMandatory() {
        return m_mandatory;
    }
    
    /**
     * Sets whether Properties of this type are mandatory.
     * @param isMandatory is true if mandatory.
     */
    public void setMandatory( boolean isMandatory ) {
        m_mandatory = isMandatory;
    }
    
    /**
     * Gets whether properties of this type should be read-only.
     * @return Returns the read-only status.
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /**
     * Sets whether properties of this type should be read-only.
     * @param readOnly sets the read-only status.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    
    /**
     * Gets whether properties of this type should be included in a summary of the entity.
     * @return Returns the summary status.
     */
	public boolean isSummary() {
		return summary;
	}

    /**
     * Sets whether properties of this type should be included in a summary of the entity.
     * @param summary sets the summary status.
     */
	public void setSummary(boolean summary) {
		this.summary = summary;
	}

    /**
     * Get the default value for any new Properties of this type.
     * @return the default value.
     */
    public String getDefaultValue() {
        return m_default;
    }
    
    /**
     * Sets the default value for any new Properties of this type.
     * @param def is the default value.
     */
    public void setDefaultValue(String def) {
        m_default = def;
    }
    
     /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("MetaProperty");
        super.writeAttributesXML(out);
        out.addAttribute("type",m_type.getKey().toString());

        if(getDefaultValue().length() != 0)    
            out.addAttribute("default",getDefaultValue());
        if(isMandatory())  
            out.addAttribute("mandatory","true");
        if(isReadOnly()){
            out.addAttribute("readonly","true");
        }
        if(isSummary()){
        	out.addAttribute("summary","true");
        }
        
        out.stopEntity();
    }

    /** copies the values of one instance to a copy
     * @param copy is the meta-property to copy the values to
     */
    protected void cloneTo(MetaProperty copy) {
        super.cloneTo(copy);
        copy.m_type = m_type;
        copy.m_mandatory = m_mandatory;
        copy.readOnly = readOnly;
        copy.m_default = new String(m_default);
        copy.container = null;
    }

    /** sets the parent meta entity / meta relationship for this property
     * @param me is the parent meta-entity for this meta-property
     */
    public void setContainer(MetaPropertyContainer container) {
        this.container = container;
    }
    
    /** gets the parent container (meta entity or meta relationship) for this property
     * @return the parent meta property container
     */
    public MetaPropertyContainer getContainer() {
        return container;
    }
    
     
}
