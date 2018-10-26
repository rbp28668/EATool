/*
 * RoleTranslation.java
 * Project: EATool
 * Created on 04-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.util.XMLWriter;


/**
 * RoleTranslation is used to map roles for importing XML. Note that as well
 * as mapping directly to the MetaRole, a RoleTranslation also defines a set of
 * PropertyTranslations.  These serve to uniquely identify the entity that this
 * role should connect to.
 * 
 * @author rbp28668
 */
public class RoleTranslation extends PropertyTranslationCollection{

    private String type="";          // what the "type" attribute of the input record is
    private MetaRole meta = null;    // maps to this meta role 
    

    /**
     * Creates a null role tranlsation.
     */
    public RoleTranslation() {
     }

    /**
     * Gets the type name used for this role translation. The type name in this 
     * context is the name used to identify a given role in the input XML.
     * @return the type name.
     */
    public String getTypeName() {
        return type;
    }
    
    /**
     * Gets the MetaRole that the type name is bound to.
     * @return the MetaRole that is the target for any Roles that
     * match the given type name.
     */
    public MetaRole getMeta() {
        return meta;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Role " + type + " to " + meta.getName();
    }

    /**
     * Sets the MetaRole that this type name is bound to.
     * @param role is the MetaRole that is the target for any correctly named
     * items in the input stream.
     */
    public void setMeta(MetaRole role) {
        if(role == null){
            throw new NullPointerException("Can't set null MetaRole in RoleTranslation");
        }
        meta = role;
    }

    /**
     * Sets the type name that identifies an item of the corresponding MetaRole
     * in the input data.
     * @param type is the type name to set.
     */
    public void setType(String type) {
        if(type == null || type.length() == 0){
            throw new IllegalArgumentException("Must supply a valid type name");
        }
        this.type = type;
    }

    /**
     * Writes the RoleTranslation to XML.
     * @param writer is the XMLWriterDirect to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("RoleTranslation");
        writer.addAttribute("type",getTypeName());
        writer.addAttribute("uuid",getMeta().getKey().toString());
        
        for(PropertyTranslation pt : getPropertyTranslations()){
            pt.writeXML(writer);
        }
        writer.stopEntity();
    }

}