/*
 * HTMLPagePropertiesEditor.java
 * Project: EATool
 * Created on 10-Apr-2007
 *
 */
package alvahouse.eatool.gui.html;

import java.awt.BorderLayout;
import java.awt.Component;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.repository.html.HTMLPage;

/**
 * HTMLPagePropertiesEditor allows editing of HTMLProxy Page property information. Currently
 * just name and description.
 * 
 * @author rbp28668
 */
public class HTMLPagePropertiesEditor extends BasicDialog {

 	private static final long serialVersionUID = 1L;

	/** Panel for common NamedRepositoryItem properties */
    private NamedRepositoryItemPanel nriPanel;
    
    /** Page being edited. */
    private HTMLPage page;


    /**
     * @param parent
     * @param title
     */
    public HTMLPagePropertiesEditor(Component parent, HTMLPage page) {
        super(parent, "Edit Page Information");
        this.page = page;
        nriPanel = new NamedRepositoryItemPanel(page);
        getContentPane().add(nriPanel,BorderLayout.CENTER);
        
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        page.setName(nriPanel.getName());
        page.setDescription(nriPanel.getDescription());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput();
    }

}
