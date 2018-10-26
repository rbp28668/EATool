/*
 * MetaProperty.java
 *
 * Created on 10 January 2002, 21:31
 */

package alvahouse.eatool.repository.metamodel.impl;


import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * MetaProperty describes a property of an Entity.
 * @author  rbp28668
 */
public class MetaPropertyImpl  extends NamedRepositoryItem implements  MetaProperty {

    private MetaPropertyContainer container = null;
    private MetaPropertyType m_type;
    private String m_default = "";
    private boolean m_mandatory = false;
    private boolean readOnly = false;
    private boolean summary = false;
    
    /** Creates new MetaProperty with default type of string.
     *  @param uuid is the UUID that identifies this MetaProperty.
    */
    public MetaPropertyImpl(UUID uuid) {
        super(uuid);
        m_type = MetaPropertyTypes.getBuiltInType("string"); // use string as default type.
    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#clone()
//     */
//    public Object clone() {
//        MetaProperty copy = new MetaProperty(getKey());
//        cloneTo(copy);
//        return copy;
//    }
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
        
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#getMetaPropertyType()
     */
    public MetaPropertyType getMetaPropertyType() {
        return m_type;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#setMetaPropertyType(alvahouse.eatool.repository.metamodel.types.MetaPropertyType)
     */
    public void setMetaPropertyType(MetaPropertyType type) {
        m_type = type;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#isMandatory()
     */
    public boolean isMandatory() {
        return m_mandatory;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#setMandatory(boolean)
     */
    public void setMandatory( boolean isMandatory ) {
        m_mandatory = isMandatory;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    
    /**
	 * @return the summary
	 */
	public boolean isSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#getDefaultValue()
     */
    public String getDefaultValue() {
        return m_default;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#setDefaultValue(java.lang.String)
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

//    /** copies the values of one instance to a copy
//     * @param copy is the meta-property to copy the values to
//     */
//    protected void cloneTo(MetaProperty copy) {
//        super.cloneTo(copy);
//        copy.m_type = m_type;
//        copy.m_mandatory = m_mandatory;
//        copy.readOnly = readOnly;
//        copy.m_default = new String(m_default);
//        copy.container = null;
//    }

    /** sets the parent meta entity for this property
     * package scope so that the meta-model can maintain its integrety
     * @param me is the parent meta-entity for this meta-property
     */
    public void setContainer(MetaPropertyContainer container) {
        this.container = container;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaProperty#getContainer()
     */
    public MetaPropertyContainer getContainer() {
        return container;
    }
    
     
}
