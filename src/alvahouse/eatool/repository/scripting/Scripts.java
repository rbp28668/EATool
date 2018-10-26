/*
 * Scripts.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Scripts is a collection of Script.
 * 
 * @author rbp28668
 */
public class Scripts {

    /** List of Script */
    private List<Script> scripts = new LinkedList<Script>();
    
    /** List of ScriptsChangeListener for change notification */
    private List<ScriptsChangeListener> listeners = new LinkedList<ScriptsChangeListener>();
    
    /**
     * Creates an empty scripts object.
     */
    public Scripts() {
        super();
    }

    /**
     * Adds a Script to the list.
     * @param script is the script to be added.
     */
    public void add(Script script){
        scripts.add(script);
        fireScriptAdded(script);
    }
    
    /**
     * Gets an unmodifiable collection of all the scripts.
     * @return an unmodifiable collection of Script.
     */
    public Collection<Script> getScripts(){
        return Collections.unmodifiableCollection(scripts);
    }
    
    /**
     * Writes the Script out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Scripts");
        
        for(Script script: scripts) {
            script.writeXML(out);
        }
        
        out.stopEntity();
    }

    /**
     * Clears the list of Scripts.
     */
    public void deleteContents() {
        scripts.clear();
        fireUpdated();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return "Scripts (" + scripts.size() + ")";
    }

    /**
     * Deletes the given script from the list.
     * @param script is the script to delete.
     */
    public void delete(Script script) {
        scripts.remove(script);
        fireScriptDeleted(script);
    }

    /**
     * Adds a ScriptsChangeListener that will then be informed
     * of any changes to the scripts.
     * @param listener is the listener to add.
     */
    public void addChangeListener(ScriptsChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the list of listeners.
     * @param listener is the listener to remove.
     */
    public void removeChangeListener(ScriptsChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Signal to any attached listeners that the list of scripts
     * has fundamentally changed. 
     */
    public void fireUpdated(){
        ScriptChangeEvent event = new ScriptChangeEvent(this);
        for(ScriptsChangeListener listener : listeners) {
            listener.updated(event);
        }
    }

    /**
     * Signals to any attached listeners that a script has been added.
     * @param script is the script that has been added.
     */
    public void fireScriptAdded(Script script){
        ScriptChangeEvent event = new ScriptChangeEvent(script);
        for(ScriptsChangeListener listener : listeners) {
            listener.scriptAdded(event);
        }
    }

    /**
     * Signals to any attached listeners that a script has been modified.
     * @param script is the script that has been modified.
     */
    public void fireScriptChanged(Script script){
        ScriptChangeEvent event = new ScriptChangeEvent(script);
        for(ScriptsChangeListener listener : listeners) {
            listener.scriptChanged(event);
        }
    }
    
    /**
     * Signals to any attached listeners that a script has been deleted.
     * @param script is the script that has been deleted.
     */
    public void fireScriptDeleted(Script script){
        ScriptChangeEvent event = new ScriptChangeEvent(script);
        for(ScriptsChangeListener listener : listeners) {
            listener.scriptDeleted(event);
        }
    }

    /**
     * Does a lookup of a script with a given key.
     * @param uuid is the key of the script to find.
     * @return the Script or null if not found.
     */
    public Script lookupScript(UUID uuid) {
        Script theScript = null;
        for(Script script : scripts) {
            if(script.getKey().equals(uuid)){
                theScript = script;
                break;
            }
        }
        return theScript;
    }

    /**
     * Gets the number of scripts.  May be more efficient than calling
     * <code>getScripts().getSize()</code>
     * @return the number of scripts.
     */
    public int getScriptCount() {
        return scripts.size();
    }

    
}
