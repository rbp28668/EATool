/*
 * ImportMappings.java
 * Project: EATool
 * Created on 04-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.persist.ImportMappingPersistence;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.XMLWriter;

/**
 * ImportMappings provides a top-level class for setting up and
 * maintaining import mappings.  It also provides the event mechanism for
 * propagating changes to the ImportMappings to any subscribing listeners.
 * 
 * @author rbp28668
 */
public class ImportMappings {
    
	private ImportMappingPersistence persistence;
    private List<ImportMappingChangeListener> listeners = new LinkedList< ImportMappingChangeListener>(); // of ImportMappingChangeListener
    private Map<String,ParserDescription> parsers = null;
    
    /**
     * Creates an empty list of import mappings.
     */
    public ImportMappings(ImportMappingPersistence persistence){
    	this.persistence = persistence;
    }
    /**
     * Gets the list of ImportMappings currently defined.
     * @return a list of ImportMapping, never null, maybe empty.
     */
    public Collection<ImportMapping> getImportMappings() throws Exception {
        return persistence.getMappings();
    }
    
    public String toString(){
        return "Import Mappings";
    }

    /**
     * Writes the import mappings to the given XMLWriterDirect.
     * @param writer is the writer to out to.
     * @throws IOException
     */
    public void writeXML(XMLWriter writer) throws IOException {
        writer.startEntity("Import");
        
        try {
	        for(ImportMapping mapping : persistence.getMappings()) {
	            mapping.writeXML(writer);
	        }
        } catch (Exception e) {
        	throw new IOException("Unable to write import mappings", e);
        }
        
        writer.stopEntity();
    }

    /**
     * Deletes all the import mappings. 
     */
    public void deleteContents() throws Exception {
       persistence.deleteAll();
       fireUpdated();
    }

    /**
     * Adds a mapping to the import mappings.
     * @param mapping is the ImportMapping to add.
     */
    public void add(ImportMapping mapping) throws Exception{
        if(mapping == null){
            throw new NullPointerException("Can't add null mapping to ImportMappings");
        }
		String user = System.getProperty("user.name");
		mapping.getVersion().createBy(user);
        persistence.addMapping(mapping);
        fireMappingAdded(mapping);
    }

    /**
     * Adds a mapping to the import mappings.
     * @param mapping is the ImportMapping to add.
     */
    public void _add(ImportMapping mapping) throws Exception {
        if(mapping == null){
            throw new NullPointerException("Can't add null mapping to ImportMappings");
        }
        persistence.addMapping(mapping);
    }

    /**
     * Updates a mapping to the import mappings.
     * @param mapping is the ImportMapping to add.
     */
    public void update(ImportMapping mapping) throws Exception {
        if(mapping == null){
            throw new NullPointerException("Can't add null mapping to ImportMappings");
        }
		String user = System.getProperty("user.name");
		mapping.getVersion().modifyBy(user);
        persistence.updateMapping(mapping);
        fireMappingChanged(mapping);
    }

    /**
     * Removes a given ImportMapping from the list.
     * @param mapping is the ImportMapping to remove.
     */
    public void remove(ImportMapping mapping) throws Exception {
        persistence.deleteMapping(mapping.getKey());
        fireMappingDeleted(mapping);
    }

    /**
     * Adds a listener that will be notified when the import mappings change.
     * @param listener is the listener to add.
     */
    public void addChangeListener(ImportMappingChangeListener listener){
        if(listener == null) {
            throw new NullPointerException("Can't add null import mapping change listener");
        }
        listeners.add(listener);
    }
    
    /**
     * Removes a given listener.
     * @param listener is the listener to remove.
     */
    public void removeChangeListener(ImportMappingChangeListener listener){
        listeners.remove(listener);
    }

    /**
     * Loads up the parsers. Must be called before getParserNames or lookupParser.
     * @param settings
     */
    public void setParsers(SettingsManager settings){
        if(parsers == null){
            loadParsers(settings);
        }
    }
    
    /**
     * Gets the possible parsers for an import mapping.
     * @return an array of parser names.
     */
    public String[] getParserNames(){
        if(parsers == null){
            throw new IllegalStateException("Parsers not initialised");
        }
        return parsers.keySet().toArray(new String[parsers.size()]);
    }

    /**
     * Given the name of a parser for an import mapping, returns the
     * class name of the parser.
     * @param name is the parser name.
     * @return the parser's class name.
     */
    public String lookupParser(String name){
        return lookupParserDescription(name).getParserClass();
    }
    
    /**
     * Gets the file extension corresponding to a parser name.
     * @param name
     * @return
     */
    public String lookupFileExt(String name){
        return lookupParserDescription(name).getFileExt();
    }
    
    /**
     * Gets a file description corresponding to a parser name.
     * @param name
     * @return
     */
    public String lookupFileDesc(String name){
        return lookupParserDescription(name).getFileDesc();
    }
    
    /**
     * Gets the parser description by name.
     * @param name
     * @return
     */
    private ParserDescription lookupParserDescription(String name){
        if(parsers == null){
            throw new IllegalStateException("Parsers not initialised");
        }
        
        ParserDescription pd = parsers.get(name);
        if(pd == null){
            throw new IllegalArgumentException(name + " is not a known import parser name");
        }
        return pd;
    }
    
    /**
     * Loads up the parsers (on first use). 
     */
    private void loadParsers(SettingsManager settings) {
        parsers = new LinkedHashMap<String,ParserDescription>();
        SettingsManager.Element root = settings.getElement("/ImportParsers");
        for(SettingsManager.Element parser : root.getChildren()){
            String name = parser.attributeRequired("name");
            String parserClass = parser.attributeRequired("class");
            String fileExt = parser.attributeRequired("suffix");
            String fileDesc = parser.attributeRequired("filetype");
            
            ParserDescription desc = new ParserDescription(name,parserClass,fileExt,fileDesc);
            parsers.put(name,desc);
        }
    }
    /**
     * Signals to any change listeners that a major structural change has
     * taken place to the import mappings. 
     */
    public void fireUpdated(){
        MappingChangeEvent e = new MappingChangeEvent(this);
        for(ImportMappingChangeListener listener : listeners){
            listener.Updated(e);
        }
    }

    /**
     * Signals to any change listeners that an import mapping has been added. 
     * @param mapping is the ImportMapping that has been added.
     */
    private void fireMappingAdded(ImportMapping mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.MappingAdded(e);
        }
    }

    /**
     * Signals to any change listeners that an import mapping has been edited. 
     * @param mapping is the ImportMapping that has been added.
     */
    private void fireMappingChanged(ImportMapping mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.MappingEdited(e);
        }
    }

    /**
     * Signals to any change listeners that an import mapping has been deleted.
     * @param mapping is the ImportMapping that has been deleted.
     */
    private void fireMappingDeleted(ImportMapping mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.MappingDeleted(e);
        }
    }
    
    /**
     * Signals to any change listeners that an EntityMapping has been added
     * to a given ImportMapping.
     * @param mapping is the ImportMapping that has just had the EntityMapping
     * added.
     */
    public void fireEntityMappingAdded(ImportMapping mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.EntityMappingAdded(e);
        }
    }

    /**
     * Signals to any change listeners that an EntityTranslation has just been
     * deleted from its parent.
     * @param deleted is the deleted EntityTranslation. 
     */
    public void fireEntityMappingDeleted(EntityTranslation deleted){
        MappingChangeEvent e = new MappingChangeEvent(deleted);
        for(ImportMappingChangeListener listener : listeners){
            listener.EntityMappingDeleted(e);
        }
    }

    /**
     * Signals to any change listeners that an EntityTranslation has been edited
     * @param edited is the changed EntityTranslation.
     */
    public void fireEntityMappingEdited(EntityTranslation edited){
        MappingChangeEvent e = new MappingChangeEvent(edited);
        for(ImportMappingChangeListener listener : listeners){
            listener.EntityMappingEdited(e);
        }
    }

    
    /**
     * Signals to any change listeners that a PropertyTranslation has just been
     * added to an EntityTranslation.
     * @param mapping is the parent EntityTranslation.
     */
    public void firePropertyMappingAdded(PropertyTranslationCollection mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.PropertyMappingAdded(e);
        }
    }

    /**
    * Signals to any change listeners that a PropertyTranslation has just been
    * edited.
    * @param deleted is the edited PropertyTranslation.
    * @param translation
    */
    public void firePropertyMappingEdited(PropertyTranslation mapping) {
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.PropertyMappingEdited(e);
        }
    }
    
    /**
     * Signals to any change listeners that a PropertyTranslation has just been
     * deleted from its parent.
     * @param deleted is the deleted PropertyTranslation.
     */
    public void firePropertyMappingDeleted(PropertyTranslation deleted){
        MappingChangeEvent e = new MappingChangeEvent(deleted);
        for(ImportMappingChangeListener listener : listeners){
            listener.PropertyMappingDeleted(e);
        }
        
    }
    
    /**
     * Signals to any change listeners that a RelationshipTranslation has just been
     * added to an ImportMapping.
     * @param mapping is the ImportMapping that has just been added to.
     */
    public void fireRelationshipMappingAdded(ImportMapping mapping){
        MappingChangeEvent e = new MappingChangeEvent(mapping);
        for(ImportMappingChangeListener listener : listeners){
            listener.RelationshipMappingAdded(e);
        }
        
    }

    /**
     * Signals to any change listeners that a RelationshipTranslation has just been
     * deleted from its parent ImportMapping.
     * @param deleted is the deleted RelationshipTranslation.
     */
    public void fireRelationshipMappingDeleted(RelationshipTranslation deleted){
        MappingChangeEvent e = new MappingChangeEvent(deleted);
        for(ImportMappingChangeListener listener : listeners){
            listener.RelationshipMappingDeleted(e);
        }
        
    }

    /**
     * Signals to any change listeners that a RelationshipTranslation has just
     * been changed.
     * @param edited is the edited RelationshipTranslation.
     */
    public void fireRelationshipMappingEdited(RelationshipTranslation edited){
        MappingChangeEvent e = new MappingChangeEvent(edited);
        for(ImportMappingChangeListener listener : listeners){
            listener.RelationshipMappingEdited(e);
        }
        
    }

    /**
     * Signals to any change listeners that a RoleTranslation has just
     * been changed.
     * @param edited is the edited RoleTranslation
     */
    public void fireRoleMappingChanged(RoleTranslation edited) {
        MappingChangeEvent e = new MappingChangeEvent(edited);
        for(ImportMappingChangeListener listener : listeners){
            listener.RoleMappingEdited(e);
        }
    }

    /**
     * ParserDescription describes a parser to use for an import.
     * 
     * @author rbp28668
     */
    private class ParserDescription{
        private String name;
        private String parserClass;
        private String fileExt;
        private String fileDesc;
        
        /**
         * @param name
         * @param parserClass
         * @param fileExt
         * @param fileDesc
         */
        public ParserDescription(String name, String parserClass, String fileExt, String fileDesc) {
            this.name = name;
            this.parserClass = parserClass;
            this.fileExt = fileExt;
            this.fileDesc = fileDesc;
        }

        /**
         * @return Returns the fileDesc.
         */
        public String getFileDesc() {
            return fileDesc;
        }
        /**
         * @return Returns the fileExt.
         */
        public String getFileExt() {
            return fileExt;
        }
        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        /**
         * @return Returns the parserClass.
         */
        public String getParserClass() {
            return parserClass;
        }
    }
    
}
