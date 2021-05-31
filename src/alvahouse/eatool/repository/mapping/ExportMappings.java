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

import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.util.XMLWriter;

/**
 * ExportMappings provides a collection of the export mapping.
 * 
 * @author rbp28668
 */
public class ExportMappings {

	private ExportMappingPersistence persistence;
    private List<ExportMappingChangeListener> listeners = new LinkedList<ExportMappingChangeListener>(); // of ExportMappingChangeListener
    
    /**
     * Creates an empty collection of export mappings 
     */
    public ExportMappings(ExportMappingPersistence persistence) {
        super();
    	this.persistence = persistence;
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
        
        try {
	        for(ExportMapping mapping : persistence.getMappings()) {
	            mapping.writeXML(writer);
	        }
        } catch(Exception e) {
        	throw new IOException("Unable to write export mappings", e);
        }
	    
        writer.stopEntity();
    }
    
    /**
     * Gets the collection of ExportMapping.
     * @return Collection of ExportMapping, maybe empty, never null.
     */
    public Collection<ExportMapping> getExportMappings() throws Exception{
        return persistence.getMappings();
    }
    
    /**
     * Add an ExportMapping to the collection.
     * @param mapping is the ExportMapping to add.
     */
    public void add(ExportMapping mapping) throws Exception{
        if(mapping == null) {
            throw new NullPointerException("Can't add null export mapping");
        }
		String user = System.getProperty("user.name");
		mapping.getVersion().createBy(user);
        persistence.addMapping(mapping);
        fireMappingAdded(mapping);
    }

    /**
     * Internal add for loading up XML - doesn't update version or fire events.
     * @param mapping is the ExportMapping to add.
     */
    public void _add(ExportMapping mapping) throws Exception{
        if(mapping == null) {
            throw new NullPointerException("Can't add null export mapping");
        }
        persistence.addMapping(mapping);
    }

    /**
     * Updates an ExportMapping in the collection.
     * @param mapping is the ExportMapping to add.
     */
    public void update(ExportMapping mapping) throws Exception{
        if(mapping == null) {
            throw new NullPointerException("Can't add null export mapping");
        }
		String user = System.getProperty("user.name");
		mapping.getVersion().modifyBy(user);
        persistence.updateMapping(mapping);
        fireMappingUpdated(mapping);
    }

    /**
     * Removes an ExportMapping from the collection.
     * @param mapping is the ExportMapping to remove.
     */
    public void remove(ExportMapping mapping) throws Exception{
        persistence.deleteMapping(mapping.getKey());
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
    private void fireMappingUpdated(ExportMapping mapping) {
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
