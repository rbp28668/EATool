/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author bruce_porteous
 *
 */
public class EditCouchAdminDialog extends BasicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserDetailsPanel panel;

	/**
	 * To create a new user or edit an existing user.
	 * @param parent
	 * @param user is the couchdb user to edit.
	 */
	public EditCouchAdminDialog(Component parent) {
		super(parent, "Create or update CouchDB Administrator");
		
        panel = new UserDetailsPanel();

        getContentPane().add(panel,BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#onOK()
	 */
	@Override
	protected void onOK() throws Exception {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	@Override
	protected boolean validateInput() throws Exception {
		return panel.validateInput();
	}
	
	
    String getUsername() {
        return panel.getUsername();
    }

    String getPassword() {
        return panel.getPassword();
    }


	private class UserDetailsPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
        private JTextField usernameField;
        private JTextField passwordField;
        private JTextField passwordField2;
        
       
	    public UserDetailsPanel() {
	        GridBagLayout grid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        
	        setLayout(grid);
	       
	        c.anchor = GridBagConstraints.LINE_START;
	        c.weighty = 1.0;
	        c.insets = new Insets(5,10,5,10);

	        String username = "";
	        String password = "";
	        
            usernameField = new JTextField(username);
            usernameField.setColumns(40);
            
            passwordField = new JPasswordField(password);
            passwordField.setColumns(40);
            passwordField2 = new JPasswordField(password);
            passwordField2.setColumns(40);
            
            addField(grid, c, "Username", usernameField);
            addField(grid, c, "Password", passwordField);
            addField(grid, c, "Retype Password", passwordField2);
	    }

		/**
		 * @param grid
		 * @param c
		 * @param usernameField
		 */
		private void addField(GridBagLayout grid, GridBagConstraints c, String label, JTextField field) {
			JLabel l = new JLabel(label);
            c.gridwidth = GridBagConstraints.RELATIVE;
            grid.setConstraints(l,c);
            add(l);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            grid.setConstraints(field,c);
            add(field);
		}

	    /** validates values in the panel
	     * @return true if input is valid, false if invalid
	     **/
	    public boolean validateInput() {
	        if(usernameField.getText().isEmpty()) {
	            JOptionPane.showMessageDialog(this,
	                "Please enter a value for the name", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            usernameField.requestFocus();
	            return false;
	        }

	        if(passwordField.getText().isEmpty()) {
	            JOptionPane.showMessageDialog(this,
	                "Please enter a value for the password", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            passwordField.requestFocus();
	            return false;
	        }
	        
	        if(passwordField2.getText().isEmpty()) {
	            JOptionPane.showMessageDialog(this,
	                "Please repeat the password", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            passwordField2.requestFocus();
	            return false;
	        }
	        
	        if(!passwordField.getText().equals(passwordField2.getText())) {
	            JOptionPane.showMessageDialog(this,
		                "Passwords do not match", 
		                "EATool", JOptionPane.INFORMATION_MESSAGE);
		            passwordField.requestFocus();
		            return false;
	        }

	        return true;
	    }
	    
	    String getUsername() {
	        return usernameField.getText();
	    }

	    String getPassword() {
	        return passwordField.getText();
	    }
	    
	    
	}

}
