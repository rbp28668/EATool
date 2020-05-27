/*
 * ScriptAction.java
 * Project: EATool
 * Created on 19-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptManager;

/**
 * ScriptAction is a Swing Action that runs a given script when fired.
 * Used when creating a menu to fire the script when the menu item is selected.
 * 
 * @author rbp28668
 */
public class ScriptAction extends AbstractAction{

    private static final long serialVersionUID = 1L;
    private Script script;
    private alvahouse.eatool.Application app;

    /**
     * Creates a new action bound to the given script.
     * @param script is the script to run when the action fires.
     */
    public ScriptAction(Script script, alvahouse.eatool.Application app) {
        super();
        this.script = script;
        this.app = app;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        try {
            ScriptManager.getInstance().runScript(script);
        } catch (Exception e) {
            new ExceptionDisplay(app.getCommandFrame(),e);
        }
    }

}
