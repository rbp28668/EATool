/*
 * Scripts.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.persist.ScriptPersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Scripts is a collection of Script.
 * 
 * @author rbp28668
 */
public class Scripts {

	private ScriptPersistence persistence;
	
    /** List of Script */
//    private List<Script> scripts = new LinkedList<Script>();
//    private Set<UUID> keys = new HashSet<>();
    
    /** List of ScriptsChangeListener for change notification */
    private List<ScriptsChangeListener> listeners = new LinkedList<ScriptsChangeListener>();
    
    /**
     * Creates an empty scripts object.
     * @param scriptPersistence 
     */
    public Scripts(ScriptPersistence scriptPersistence) {
        super();
        this.persistence = scriptPersistence;
    }

    /**
     * Adds a Script to the list.
     * @param script is the script to be added.
     */
    public void add(Script script) throws Exception {
 		String user = System.getProperty("user.name");
		script.getVersion().createBy(user);
		persistence.addScript(script);
        fireScriptAdded(script);
    }

    /**
     * Internal version of add that doesn't change the version information.
     * @param script is the script to be added.
     */
    public void _add(Script script) throws Exception {
		persistence.addScript(script);
    }

    /**
     * Updates a script in the repository.
     * @param script is the script to be added.
     */
    public void update(Script script) throws Exception {
 		String user = System.getProperty("user.name");
		script.getVersion().modifyBy(user);
		persistence.updateScript(script);
        fireScriptChanged(script);
    }

    /**
     * Deletes the given script from the list.
     * @param script is the script to delete.
     */
    public void delete(Script script)  throws Exception {
    	UUID key = script.getKey();
    	persistence.deleteScript(key);
        fireScriptDeleted(script);
    }

    /**
     * Gets an unmodifiable collection of all the scripts.
     * @return an unmodifiable collection of Script.
     */
    public Collection<Script> getScripts() throws Exception{
        return persistence.getScripts();
    }
    
    /**
     * Writes the Script out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Scripts");
        
        try {
	        for(Script script: getScripts()) {
	            script.writeXML(out);
	        }
        } catch (Exception e) {
        	throw new IOException("Unable to write scripts to XML", e);
        }
        
        out.stopEntity();
    }

    /**
     * Clears the list of Scripts.
     */
    public void deleteContents()  throws Exception {
        persistence.dispose();
        fireUpdated();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
    	try {
    		return "Scripts (" + persistence.getScriptCount() + ")";
    	} catch (Exception e) {
    		return "Scripts";
    	}
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
    public void fireUpdated() throws Exception {
        ScriptChangeEvent event = new ScriptChangeEvent(this);
        for(ScriptsChangeListener listener : listeners) {
            listener.updated(event);
        }
    }

    /**
     * Signals to any attached listeners that a script has been added.
     * @param script is the script that has been added.
     */
    private void fireScriptAdded(Script script) throws Exception {
        ScriptChangeEvent event = new ScriptChangeEvent(script);
        for(ScriptsChangeListener listener : listeners) {
            listener.scriptAdded(event);
        }
    }

    /**
     * Signals to any attached listeners that a script has been modified.
     * @param script is the script that has been modified.
     */
    private void fireScriptChanged(Script script) throws Exception {
        ScriptChangeEvent event = new ScriptChangeEvent(script);
        for(ScriptsChangeListener listener : listeners) {
            listener.scriptChanged(event);
        }
    }
    
    /**
     * Signals to any attached listeners that a script has been deleted.
     * @param script is the script that has been deleted.
     */
    private void fireScriptDeleted(Script script) throws Exception {
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
    public Script lookupScript(UUID uuid) throws Exception {
        Script theScript = persistence.getScript(uuid);
        return theScript;
    }

    /**
     * Gets the number of scripts.  May be more efficient than calling
     * <code>getScripts().getSize()</code>
     * @return the number of scripts.
     */
    public int getScriptCount() throws Exception{
        return persistence.getScriptCount();
    }

    
}
