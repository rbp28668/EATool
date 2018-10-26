/*
 * ExportMappings.java
 * Project: EATool
 * Created on 29-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.XMLWriter;

/**
 * ExportMappings provides a collection of the export mapping.
 * 
 * @author rbp28668
 */
public class ExportMappings {

    private List<ExportMapping> mappings = new LinkedList<ExportMapping>();
    private List<ExportMappingChangeListener> listeners = new LinkedList<ExportMappingChangeListener>(); // of ExportMappingChangeListener
    
    /**
     * Creates an empty collection of export mappings 
     */
    public ExportMappings() {
        super();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return "Export Mappings";
    }
    
    /**
     * Writes the collection to the XMLWriter
     * @param writer is the writer to write to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("Export");
        
        for(ExportMapping mapping : mappings) {
            mapping.writeXML(writer);
        }
        
        writer.stopEntity();
    }
    
    /**
     * Gets the collection of ExportMapping.
     * @return Collection of ExportMapping, maybe empty, never null.
     */
    public Collection<ExportMapping> getExportMappings() {
        return mappings;
    }
    
    /**
     * Add an ExportMapping to the collection.
     * @param mapping is the ExportMapping to add.
     */
    public void add(ExportMapping mapping){
        if(mapping == null) {
            throw new NullPointerException("Can't add null export mapping");
        }
        mappings.add(mapping);
        fireMappingAdded(mapping);
    }
    
    /**
     * Removes an ExportMapping from the collection.
     * @param mapping is the ExportMapping to remove.
     */
    public void remove(ExportMapping mapping){
        mappings.remove(mapping);
        fireMappingDeleted(mapping);
    }
    
    /**
     * Adds an ExportMappingChangeListener to be notified when the export
     * mappings change.
     * @param listener is the ExportMappingChangeListener to add.
     */
    public void addChangeListener(ExportMappingChangeListener listener) {
        if(listener == null) {
            throw new NullPointerException("Can't add null export mapping change listener");
        }
        listeners.add(listener);
        
    }

    /**
     * Removes an ExportMappingChangeListener from the listeners list.
     * @param listener is the ExportMappingChangeListener to remove.
     */
    public void removeChangeListener(ExportMappingChangeListener listener){
        listeners.remove(listener);
    }

    /**
     * Signal to all the listeners that a mapping has been added.
     * @param mapping is the mapping that has been added.
     */
    private void fireMappingAdded(ExportMapping mapping) {
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ExportMappingChangeListener listener : listeners){
            listener.MappingAdded(e);
        }
    }
    
    /**
     * Signal to all the listeners that an ExportMapping has been edited.
     * @param mapping is the edited ExportMapping.
     */
    public void fireEdited(ExportMapping mapping) {
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ExportMappingChangeListener listener : listeners){
            listener.MappingEdited(e);
        }
    }
    
    /**
     * Signals to all the listeners that an ExportMapping has been deleted.
     * @param mapping is the ExportMapping that has been deleted.
     */
    private void fireMappingDeleted(ExportMapping mapping) {
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ExportMappingChangeListener listener : listeners){
            listener.MappingDeleted(e);
        }
    }
    
}
