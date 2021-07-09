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

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.mapping.EntityTranslationDto;
import alvahouse.eatool.repository.dto.mapping.ImportMappingDto;
import alvahouse.eatool.repository.dto.mapping.RelationshipTranslationDto;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * ImportMapping provides a single import mapping translation.
 * 
 * @author rbp28668
 */
public class ImportMapping extends NamedRepositoryItem implements Versionable{
    private String parserName="XML";
    private String transformPath = null;
    private List<EntityTranslation> entityTranslations = new LinkedList<EntityTranslation>();
    private List<RelationshipTranslation> relationshipTranslations = new LinkedList<RelationshipTranslation>();
    private VersionImpl version = new VersionImpl();
    
    public ImportMapping(){
    	super(new UUID());
    }

    public ImportMapping(UUID key){
    	super(key);
    }

    public ImportMapping(ImportMappingDto dto) {
    	super(dto);
    	this.parserName = dto.getParserName();
    	this.transformPath = dto.getTransformPath();
    	for(EntityTranslationDto etdto : dto.getEntityTranslations()) {
    		EntityTranslation et = new EntityTranslation(etdto);
    		entityTranslations.add(et);
    	}
    	for(RelationshipTranslationDto rtdto : dto.getRelationshipTranslations()) {
    		RelationshipTranslation rt = new RelationshipTranslation(rtdto);
    		relationshipTranslations.add(rt);
    	}
    	this.version.fromDto(dto.getVersion());
    }
    
	/**
	 * @return
	 */
	public ImportMappingDto toDto() {
		ImportMappingDto dto = new ImportMappingDto();
		copyTo(dto);
		return dto;
	}

    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
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
        super.writeAttributesXML(writer);
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


	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	@Override
	public Version getVersion() {
		return version;
	}

	public Object clone() {
		ImportMapping copy = new ImportMapping(getKey());
		cloneTo(copy);
		return copy;
	}
	
	protected void cloneTo(ImportMapping copy) {
		super.cloneTo(copy);
		copy.parserName = parserName;
		copy.transformPath = transformPath;
		copy.entityTranslations = new LinkedList<EntityTranslation>();
		for(EntityTranslation et : entityTranslations) {
			copy.entityTranslations.add( (EntityTranslation) et.clone());
		}
    	copy.relationshipTranslations = new LinkedList<RelationshipTranslation>();
    	for(RelationshipTranslation rt : relationshipTranslations) {
    		copy.relationshipTranslations.add( (RelationshipTranslation) rt.clone());
    	}
    	version.cloneTo(copy.version);
	}

	protected void copyTo(ImportMappingDto dto) {
		super.copyTo(dto);
		dto.setParserName(parserName);
		dto.setTransformPath(transformPath);
		for(EntityTranslation et : entityTranslations) {
			dto.getEntityTranslations().add(et.toDto());
		}
    	for(RelationshipTranslation rt : relationshipTranslations) {
    		dto.getRelationshipTranslations().add(rt.toDto());
    	}
    	dto.setVersion(version.toDto());

		
	}

    
}
