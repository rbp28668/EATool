/*
 * ScriptAttributesDialog.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import org.apache.bsf.BSFException;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptManager;

/**
 * ScriptAttributesDialog
 * 
 * @author rbp28668
 */
public class ScriptAttributesDialog extends BasicDialog {

    private NamedRepositoryItemPanel nriPanel;
    private JComboBox language;
    private Script script;
    
    
    /**
     * @param parent
     * @param title
     */
    public ScriptAttributesDialog(JDialog parent, String title, Script script) {
        super(parent, title);
        init(script);
    }

    /**
     * @param parent
     * @param title
     */
    public ScriptAttributesDialog(Component parent, String title, Script script) {
        super(parent, title);
        init(script);
    }

    private void init(Script script){
        
        this.script = script;
        try {
        
	        nriPanel = new NamedRepositoryItemPanel(script);
	        getContentPane().add(nriPanel, BorderLayout.NORTH);
	        
	        ScriptManager mgr;
	        mgr = ScriptManager.getInstance();
	        language = new JComboBox(mgr.getAvailableLanguages());
	        getContentPane().add(language);

        } catch (BSFException e) {
            new ExceptionDisplay((Frame)null,e);
        }
		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
		pack();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        script.setName(nriPanel.getName());
        script.setDescription(nriPanel.getDescription());
        script.setLanguage((String)language.getSelectedItem());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput() && language.getSelectedItem() != null;
    }

}
