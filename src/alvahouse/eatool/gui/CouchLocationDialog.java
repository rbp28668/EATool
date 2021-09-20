/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Enter data to locate a couchDB instance e.g. host & port.
 * @author bruce_porteous
 *
 */
public class CouchLocationDialog extends BasicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocationPanel panel;

	/**
	 * @param parent
	 * @param title
	 */
	public CouchLocationDialog(Component parent, String host, String port) {
		super(parent, "Enter CouchDB Location");
        panel = new LocationPanel(host, port);
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
	
    String getHost() {
        return panel.getHost();
    }

    String getPort() {
        return panel.getPort();
    }


	private class LocationPanel extends javax.swing.JPanel {

		private static final long serialVersionUID = 1L;
		
        private JTextField hostField;
        private JFormattedTextField portField;

	    public LocationPanel(String host, String port) {
	        GridBagLayout grid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        
	        setLayout(grid);
	       
	        c.anchor = GridBagConstraints.LINE_START;
	        c.weighty = 1.0;
	        c.insets = new Insets(5,10,5,10);

	        
            hostField = new JTextField(host);
            hostField.setColumns(40);
            
            NumberFormat fmt = NumberFormat.getIntegerInstance();
            fmt.setGroupingUsed(false); // don't want commas in port
            portField = new JFormattedTextField(fmt);
            portField.setColumns(5); // max 65535 so 5 digits
            portField.setValue(Long.parseLong(port));
            addField(grid, c, "Host", hostField);
            addField(grid, c, "Port", portField);
	    }

		/**
		 * @param grid
		 * @param c
		 * @param hostField
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
	        if(getHost().length() == 0) {
	            JOptionPane.showMessageDialog(this,
	                "Please enter a value for the host", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            hostField.requestFocus();
	            return false;
	        }

	        if(getPort().length() == 0) {
	            JOptionPane.showMessageDialog(this,
	                "Please enter a value for the port", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
	            portField.requestFocus();
	            return false;
	        }
	        
	        int portValue = Integer.parseInt(getPort());
	        if(portValue <= 0 || portValue > 65535) {
	            JOptionPane.showMessageDialog(this,
	                "Port must be between 0 and 63335", 
	                "EATool", JOptionPane.INFORMATION_MESSAGE);
		            portField.requestFocus();
		            return false;
	        }
	        return true;
	    }
	    
	    String getHost() {
	        return hostField.getText();
	    }

	    String getPort() {
	        return portField.getText();
	    }
	    
	    
	}

}
