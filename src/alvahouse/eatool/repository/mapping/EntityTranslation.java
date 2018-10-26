/*
 * EntityTranslation.java
 * Project: EATool
 * Created on 04-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.util.XMLWriter;


/**
 * EntityTranslation does the mapping needed to map an entitity (and its
 * properties) in the import XML format into the existing model.
 * <pre>
 * <Entity type="BusinessProcess">
 *      <Property type="Name" value="discover drugs"/>
 *      <Property type="CellRef" value="F7"/>
 * </Entity>
 *
 * <EntityTranslation type="Business Process" uuid="9e6b51-cd45-11d5-936a-00047660c89a">
 *      <PropertyTranslation type="Name" uuid="000000-0000-0000-0000-00000000" key="true"/>
 *      <PropertyTranslation type="SourceSystemID" uuid="000000-0000-0000-0000-00000000" key="true"/>
 * </EntityTranslation>
 * </pre>
 */
public class EntityTranslation extends PropertyTranslationCollection{

    private String type="";                // what the "type" attribute of the input record is
    private MetaEntity meta = null;        // maps to this meta entity 
    

    /**
     * Create an empty translation.
     */
    public EntityTranslation() {
    }

    /**
     * Gets the type name used to identify the entity in the input XML.
     * @return the input type name.
     */
    public String getTypeName() {
        return type;
    }
    
    /**
     * Gets the MetaEntity that an imported entity with the given
     * type name will be mapped to.
     * @return the target MetaEntity.
     */
    public MetaEntity getMeta() {
        return meta;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Entity " + type + " to " + meta.getName();
    }

    /**
     * @param type
     */
    public void setType(String type) {
        if(type == null || type.length() == 0){
            throw new IllegalArgumentException("Must supply a valid type name");
        }
        this.type = type;
    }

    /**
     * @param meta
     */
    public void setMeta(MetaEntity meta) {
        if(meta == null){
            throw new NullPointerException("Can't tie a mapping to a null MetaEntity");
        }
        this.meta = meta;
    }

    /**
     * Writes the EntityTranslation (and its children) to XML.
     * @param writer is the XMLWriterDirect to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("EntityTranslation");
        writer.addAttribute("type",getTypeName());
        writer.addAttribute("uuid",getMeta().getKey().toString());

        for(PropertyTranslation pt : getPropertyTranslations()){
            pt.writeXML(writer);
        }
        
        writer.stopEntity();
    }

 
}