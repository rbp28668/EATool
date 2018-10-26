/*
 * EventErrorHandler.java
 * Project: EATool
 * Created on 26-Mar-2006
 *
 */
package alvahouse.eatool.gui.graphical;

import java.awt.Component;

import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.repository.scripting.ScriptErrorListener;


/**
 * EventErrorHandler is an error handler for general display of scripting errors.
 * 
 * @author rbp28668
 */
public class EventErrorHandler implements ScriptErrorListener{

    private final Component parent;

    /**
     * @param handler
     */
    public EventErrorHandler(Component parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.scripting.ScriptErrorListener#scriptError(java.lang.String)
     */
    public void scriptError(String err) {
        Dialogs.warning(parent,err);
    }
    
}