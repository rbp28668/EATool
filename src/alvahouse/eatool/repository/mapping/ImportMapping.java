/*
 * ImportMapping.java
 * Project: EATool
 * Created on 04-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.XMLWriter;

/**
 * ImportMapping provides a single import mapping translation.
 * 
 * @author rbp28668
 */
public class ImportMapping {
    private String name="";
    private String description="";
    private String parserName="XML";
    private String transformPath = null;
    private List<EntityTranslation> entityTranslations = new LinkedList<EntityTranslation>();
    private List<RelationshipTranslation> relationshipTranslations = new LinkedList<RelationshipTranslation>();

    public ImportMapping(){
    }


    /**
     * Gets the name of this import mapping.
     * @return the mappings's name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets this import mapping's name.
     * @param name is the name to set.
     */
    public void setName(String name){
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }
    /**
     * @return Returns the entityTranslations.
     */
    public List<EntityTranslation> getEntityTranslations() {
        return entityTranslations;
    }
    
    /**
     * @return Returns the relationshipTranslations.
     */
    public List<RelationshipTranslation> getRelationshipTranslations() {
        return relationshipTranslations;
    }
    /**
     * Gets the description of this import mapping.
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description of this import mapping.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the name of the parser to use when reading the input. Defaults
     * to XML but must match one of the ImportParsers entries in the config
     * file.
     * @return Returns the parserName.
     */
    public String getParserName() {
        return parserName;
    }
    
    /**
     * Sets the parser to use for this import. Must match one of the ImportParsers entries in the config
     * file.
     * @param parserName The parserName to set.
     */
    public void setParserName(String parserName) {
        this.parserName = parserName;
    }
    
    /**
     * @return Returns the transformPath.
     */
    public String getTransformPath() {
        return transformPath;
    }
    /**
     * @param transformPath The transformPath to set.
     */
    public void setTransformPath(String transformPath) {
        this.transformPath = transformPath;
    }
    
    /**
     * Adds a new EntityTranslation to the mapping.
     * @param translation is the translation to add.
     * @throws IllegalArgumentException if there is already an EntityTranslation
     * with the same type name.
     */
    public void add(EntityTranslation translation) {
        if(translation == null){
            throw new NullPointerException("Can't add null EntityTranslation to mappings");
        }
        
        // if type name appears twice then translation is ambiguous so fail.
        // duplicate meta is odd, but possible.
        for(EntityTranslation et : entityTranslations){
            if(et.getTypeName().equals(translation.getTypeName())){
                throw new IllegalArgumentException("Entity type name already exists in import mapping");
            }
        }
        
        entityTranslations.add(translation);
    }

    /**
     * Removes a given EntityTranslation from the mapping.
     * @param translation is the EntityTranslation to remove.
     */
    public void remove(EntityTranslation translation) {
        if(translation == null){
            throw new NullPointerException("Can't remove null EntityTranslation from mappings");
        }
        entityTranslations.remove(translation);
    }

    /**
     * Adds a new RelationshipTranslation to the mapping.
     * @param translation is the translation to add.
     * @throws IllegalArgumentException if there is already a RelationshipTranslation
     * with the same type name.
     */
    public void add(RelationshipTranslation translation) {
        if(translation == null){
            throw new NullPointerException("Can't add null RelationshipTranslation to mappings");
        }
        
        // if type name appears twice then translation is ambiguous so fail.
        // duplicate meta is odd, but possible.
        for(RelationshipTranslation rt : relationshipTranslations){
            if(rt.getTypeName().equals(translation.getTypeName())){
                throw new IllegalArgumentException("Relationship type name already exists in import mapping");
            }
        }
        
        relationshipTranslations.add(translation);
    }

    /**
     * Removes a given RelationshipTranslation from the mapping.
     * @param translation is the RelationshipTranslation to remove.
     */
    public void remove(RelationshipTranslation translation) {
        if(translation == null){
            throw new NullPointerException("Can't remove null RelationshipTranslation from mappings");
        }
        relationshipTranslations.remove(translation);
    }

    /**
     * Writes the ImportMapping out as XML.
     * @param writer is the XMLWriterDirect to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("ImportTranslation");
        writer.addAttribute("name",getName());
        
        writer.textEntity("Description",description);
        writer.textEntity("Parser", parserName);
        if(transformPath != null){
            writer.textEntity("Transform",transformPath);
        }
        writer.startEntity("EntityTranslationSet");
        for(EntityTranslation et : entityTranslations){
            et.writeXML(writer);
        }
        writer.stopEntity();
        
        writer.startEntity("RelationshipTranslationSet");
        for(RelationshipTranslation rt : relationshipTranslations){
            rt.writeXML(writer);
        }
        writer.stopEntity();
        
        writer.stopEntity();
        
    }


    
}
