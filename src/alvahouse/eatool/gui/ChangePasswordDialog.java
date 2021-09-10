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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author bruce_porteous
 *
 */
public class ChangePasswordDialog extends BasicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PasswordPanel panel;

	/**
	 * @param parent
	 * @param title
	 */
	public ChangePasswordDialog(Component parent, String password) {
		super(parent, "Enter Credentials");
        panel = new PasswordPanel( password);
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
	
    String getPassword() {
        return panel.getPassword();
    }


	private class PasswordPanel extends javax.swing.JPanel {

		private static final long serialVersionUID = 1L;
		
        private JTextField passwordField;
        private JTextField password2Field;

	    public PasswordPanel(String password) {
	    	
	        GridBagLayout grid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        
	        setLayout(grid);
	       
	        c.anchor = GridBagConstraints.LINE_START;
	        c.weighty = 1.0;
	        c.insets = new Insets(5,10,5,10);

	        
            passwordField = new JPasswordField(password);
            passwordField.setColumns(40);
            password2Field = new JPasswordField(password);
            password2Field.setColumns(40);
            addField(grid, c, "Password", passwordField);
            addField(grid, c, "Repeat Password", password2Field);
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
	        if(passwordField.getText().length() == 0) {
	            JOptionPane.showMessageDialog(this,
	                "Please enter a value for the password", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            passwordField.requestFocus();
	            return false;
	        }
	        if(password2Field.getText().length() == 0) {
	            JOptionPane.showMessageDialog(this,
	                "Please repeat the password", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            passwordField.requestFocus();
	            return false;
	        }
	        
	        if(!passwordField.getText().equals(password2Field.getText())) {
	            JOptionPane.showMessageDialog(this,
		                "Passwords do not match", 
		                "EATool", JOptionPane.INFORMATION_MESSAGE);
		            passwordField.requestFocus();
		            return false;
	        }
	        return true;
	    }
	    
	    String getPassword() {
	        return passwordField.getText();
	    }
	    
	    
	}

}
