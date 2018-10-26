/*
 * ExportMapping.java
 * Project: EATool
 * Created on 29-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import alvahouse.eatool.util.XMLWriter;

/**
 * ExportMapping describes how to export the repository, or part of the repository.
 * 
 * @author rbp28668
 */
public class ExportMapping {

    private String name = "";
    private String description = "";
    private int components = 0;
    private String transformPath = null;
    
    /**
     * Creates an empty export mapping.
     */
    public ExportMapping() {
        super();
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
     * Gets the description of the export mapping.
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description of the export mapping.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the name of the export mapping - used to identify the mapping
     * to the user.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the export mapping.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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
        writer.addAttribute("name",getName());
        writer.addAttribute("components",components);
        
        writer.textEntity("Description",description);
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
}
