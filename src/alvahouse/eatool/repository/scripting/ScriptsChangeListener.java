/*
 * ScriptsChangeListener.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

/**
 * ScriptsChangeListener
 * 
 * @author rbp28668
 */
public interface ScriptsChangeListener {

    public void updated(ScriptChangeEvent e) throws Exception;
    
    public void scriptAdded(ScriptChangeEvent e) throws Exception;
    
    public void scriptChanged(ScriptChangeEvent e) throws Exception;
    
    public void scriptDeleted(ScriptChangeEvent e) throws Exception;
    
}
