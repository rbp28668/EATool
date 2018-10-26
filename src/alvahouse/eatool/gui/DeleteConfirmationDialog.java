/*
 * DeleteConfirmationDialog.java
 *
 * Created on 08 February 2002, 14:45
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JScrollPane;

import alvahouse.eatool.repository.base.DeleteDependenciesList;
/**
 * Provides a dialog to confirm whether the user wants to delete an
 * object from the repository.  This also shows all the dependent objects
 * that will be deleted along with the selected object.
 * @author  rbp28668
 */
public final class DeleteConfirmationDialog extends BasicDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Creates new form DeleteConfirmationDialog */
    public DeleteConfirmationDialog(java.awt.Component parent, DeleteDependenciesList deps) {
        super(parent, "Confirm Deletion");

        dependencies = deps;
        
        JList<String> list = new JList<String>();
        list.setListData(deps.getDependencyNames());
        list.setEnabled(false); // no point selecting items.
        JScrollPane scroll = new JScrollPane(list);
        getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
		pack();
    }

	/**
	 * Validates the input for this dialog - nothing to validate here
	 * so just return true.
	 * @return true.
	 */
	protected boolean validateInput() {
		return true;
	}
	
	/**
	 * Called when the user presses OK - deletes all the dependencies.
	 */
	protected void onOK(){
		dependencies.deleteDependencies();
	}
	
  
  	/** List of dependencies to delete */
    private DeleteDependenciesList dependencies;
     
}
