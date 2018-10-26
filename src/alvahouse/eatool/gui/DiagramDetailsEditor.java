/*
 * DiagramDetailsEditor.java
 * Project: EATool
 * Created on 10-Apr-2007
 *
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import alvahouse.eatool.repository.graphical.Diagram;

/**
 * DiagramDetailsEditor allows editing of diagram information. Currently
 * just name and description.
 * 
 * @author rbp28668
 */
public class DiagramDetailsEditor extends BasicDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Panel for common NamedRepositoryItem properties */
    private NamedRepositoryItemPanel nriPanel;
    
    /** Diagram being edited. */
    private Diagram diagram;


    /**
     * @param parent
     * @param title
     */
    public DiagramDetailsEditor(Component parent, Diagram diagram) {
        super(parent, "Edit Diagram Information");
        this.diagram = diagram;
        nriPanel = new NamedRepositoryItemPanel(diagram);
        getContentPane().add(nriPanel,BorderLayout.CENTER);
        
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        diagram.setName(nriPanel.getName());
        diagram.setDescription(nriPanel.getDescription());
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput();
    }

}
