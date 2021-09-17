/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.persist.couchdb.CouchDbServer;

/**
 * @author bruce_porteous
 *
 */
public class EditCouchUserDialog extends BasicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserDetailsPanel panel;
	private SelectCouchDbRolesPanel rolesPanel;

	/**
	 * To create a new user.
	 * @param parent
	 */
	public EditCouchUserDialog(Component parent) {
		this(parent, null);
	}
	
	/**
	 * To create a new user or edit an existing user.
	 * @param parent
	 * @param user is the couchdb user to edit.
	 */
	public EditCouchUserDialog(Component parent, CouchDbServer.User user) {
		super(parent, (user == null) ? "Create CouchDB User" : "Edit User Information for " + user.getName());
		
        panel = new UserDetailsPanel(user);
    	panel.setBorder(new TitledBorder( "User Details"));
    	
    	List<String> roles = (user == null) ? new LinkedList<String>() : user.getRoles();
        rolesPanel = new SelectCouchDbRolesPanel(roles);
    	rolesPanel.setBorder(new TitledBorder( "Roles"));

        JPanel contents = new JPanel();
    	contents.setLayout(new BoxLayout(contents, BoxLayout.PAGE_AXIS));	
    	contents.add(panel);
    	contents.add(rolesPanel);
    	
        getContentPane().add(contents,BorderLayout.CENTER);
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
	
	void updateUser(CouchDbServer.User user) {
		user.setName(panel.getUsername());
		user.setPassword(panel.getPassword());
		user.setFullName(panel.getFullName());
		user.setContactEmail(panel.getEmail());
		user.setContactPhone(panel.getPhone());
		
		rolesPanel.updateRoles(user.getRoles());
	}
	


	private class UserDetailsPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
        private JTextField usernameField;
        private JTextField passwordField;
        private JTextField passwordField2;
        private JTextField fullNameField;
        private JTextField emailField;
        private JTextField phoneField;
        
       
	    public UserDetailsPanel(CouchDbServer.User user) {
	        GridBagLayout grid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        
	        setLayout(grid);
	       
	        c.anchor = GridBagConstraints.LINE_START;
	        c.weighty = 1.0;
	        c.insets = new Insets(5,10,5,10);

	        String username = "";
	        String password = "";
	        String fullName = "";
	        String email = "";
	        String phone = "";
	        
	        if(user != null) {
	        	username = user.getName();
	        	password = user.getPassword();
	        	fullName = user.getFullName();
	        	email = user.getContactEmail();
	        	phone = user.getContactPhone();
	        }
	        
            usernameField = new JTextField(username);
            usernameField.setColumns(40);
            usernameField.setEditable(user == null);
            
            passwordField = new JPasswordField(password);
            passwordField.setColumns(40);
            passwordField2 = new JPasswordField(password);
            passwordField2.setColumns(40);
            fullNameField = new JTextField(fullName);
            fullNameField.setColumns(40);
            emailField = new JTextField(email);
            emailField.setColumns(40);
            phoneField = new JTextField(phone);
            phoneField.setColumns(40);
            
            addField(grid, c, "Username", usernameField);
            addField(grid, c, "Password", passwordField);
            addField(grid, c, "Retype Password", passwordField2);
            addField(grid, c, "Full Name", fullNameField);
            addField(grid, c, "Contact email", emailField);
            addField(grid, c, "Contact phone", phoneField);
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
	    
	    String getFullName() {
	    	return fullNameField.getText();
	    }
	    
	    String getEmail() {
	    	return emailField.getText();
	    }
	    
	    String getPhone() {
	    	return phoneField.getText();
	    }
	    
	}

}
