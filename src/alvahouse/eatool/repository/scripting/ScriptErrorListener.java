/*
 * ScriptErrorListener
 * Project: EATool
 * Created on 12-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

/**
 * ScriptErrorListener is an iterface to allow the script manager to signal
 * script errors to the appropriate part of the UI, log or whatever.
 * 
 * @author rbp28668
 */
public interface ScriptErrorListener {
    
    /**
     * Signals an error in the script.
     * @param err
     */
    public void scriptError(String err);
    
}
