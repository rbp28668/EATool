/*
 * RepositoryPropertiesDialog.java
 * Project: EATool
 * Created on 12-May-2006
 *
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;

import alvahouse.eatool.repository.RepositoryProperties;

/**
 * RepositoryPropertiesDialog is a dialog class to edit the repository properties.
 * 
 * @author rbp28668
 */
public class RepositoryPropertiesDialog extends BasicDialog {

	private static final long serialVersionUID = 1L;
	private PropertiesEditPanel panel;
	private RepositoryProperties properties;
    /**
     * @param parent
     * @param title
     */
    public RepositoryPropertiesDialog(JDialog parent, String title, RepositoryProperties properties) {
        super(parent, title);
        init(properties);
    }

    /**
     * @param parent
     * @param title
     */
    public RepositoryPropertiesDialog(Component parent, String title,RepositoryProperties properties) {
        super(parent, title);
        init(properties);
    }

    private void init(RepositoryProperties properties){
    	this.properties = properties;
        setLayout(new BorderLayout());
        panel = new PropertiesEditPanel(properties.get());
        add(panel, BorderLayout.CENTER);
        add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        panel.onOK();
        properties.set(panel.getProperties());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return true;
    }

}
