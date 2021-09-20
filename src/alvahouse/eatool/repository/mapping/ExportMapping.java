/*
 * ExportMapping.java
 * Project: EATool
 * Created on 29-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.mapping.ExportMappingDto;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * ExportMapping describes how to export the repository, or part of the repository.
 * 
 * @author rbp28668
 */
public class ExportMapping  extends NamedRepositoryItem implements Versionable {

    private int components = 0;
    private String transformPath = null;
    private VersionImpl version = new VersionImpl();
    
    /**
     * Creates an empty export mapping.
     */
    public ExportMapping() {
        super(new UUID());
    }

    public ExportMapping(ExportMappingDto dto){
    	super(dto);
    	this.components = dto.getComponents();
    	this.transformPath = dto.getTransformPath();
    	version.fromDto(dto.getVersion());
    }

    public ExportMappingDto toDto() {
    	ExportMappingDto dto = new ExportMappingDto();
    	copyTo(dto);
    	return dto;
    }
    
    /**
     * Sets a component of the repository for export.
     * @param component is the value of the component (from Repository) to set.
     * @param value is the value to set it to.
     */
    public void setComponent(int component, boolean value) {
        if(value){
            components |= component;
        } else {
            components &= ~component;
        }
    }
    
    /**
     * Sees whether a component is marked for export.
     * @param value is the value of the component (from Repository).
     * @return true if that component should be exported.
     */
    public boolean hasComponent(int value) {
        return (components & value) != 0;
    }

    /**
     * Gets the transform path.  This should refer to an XSLT transform that
     * transforms the output XML. Null if no transform path is set.
     * @return Returns the transformPath or <code>null</code> if none set.
     */
    public String getTransformPath() {
        return transformPath;
    }
    /**
     * Sets the transform path.
     * @param transformPath The transformPath to set.
     */
    public void setTransformPath(String transformPath) {
        this.transformPath = transformPath;
    }

    /**
     * Writes the ExportTransform as XML.
     * @param writer is the XMLWriter to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("ExportTranslation");
        super.writeAttributesXML(writer);
        writer.addAttribute("components",components);
        
        if(transformPath != null){
            writer.textEntity("Transform",transformPath);
        }
        
        writer.stopEntity();
    }

    /**
     * Sets all the export components in one hit.
     * @param components The components to set.
     */
    public void setComponents(int components) {
        this.components = components;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return getName();
    }

    @Override
    public Object clone() {
    	ExportMapping copy = new ExportMapping();
    	cloneTo(copy);
    	return copy;
    }
    
    protected void cloneTo(ExportMapping copy) {
    	super.cloneTo(copy);
    	copy.components = components;
    	copy.transformPath = transformPath;
    	version.cloneTo(copy.version);
    }
    
    protected void copyTo(ExportMappingDto dto) {
    	super.copyTo(dto);
    	dto.setComponents(components);
    	dto.setTransformPath(transformPath);
    	dto.setVersion(version.toDto());
    }
    
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	@Override
	public Version getVersion() {
		return version;
	}
}
