/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import alvahouse.eatool.repository.persist.couchdb.CouchDbServer;

public class SelectCouchDbRolesPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox reader;
		private JCheckBox contributor;
		private JCheckBox designer;
		private JCheckBox admin;
		
		SelectCouchDbRolesPanel(List<String> selectedRoles){

	        GridBagLayout grid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        
	        setLayout(grid);
	       
	        c.anchor = GridBagConstraints.LINE_START;
	        c.weighty = 1.0;
	        c.insets = new Insets(5,10,5,10);

			reader = addCheck(grid, c, selectedRoles, CouchDbServer.Roles.READER);
			contributor = addCheck(grid, c, selectedRoles, CouchDbServer.Roles.CONTRIBUTOR);
			designer = addCheck(grid, c, selectedRoles, CouchDbServer.Roles.DESIGNER);
			admin = addCheck(grid, c, selectedRoles, CouchDbServer.Roles.DB_ADMIN);
		}

		/**
		 * @param roles
		 */
		public void updateRoles(List<String> roles) {
			roles.clear();
			if(reader.isSelected()) roles.add(CouchDbServer.Roles.READER.toString());
			if(contributor.isSelected()) roles.add(CouchDbServer.Roles.CONTRIBUTOR.toString());
			if(designer.isSelected()) roles.add(CouchDbServer.Roles.DESIGNER.toString());
			if(admin.isSelected()) roles.add(CouchDbServer.Roles.DB_ADMIN.toString());
		}

		/**
		 * @return
		 */
		public boolean validateInput() {
			return true;
		}

		/**
		 * @param grid
		 * @param c
		 * @param usernameField
		 */
		private JCheckBox addCheck(GridBagLayout grid, GridBagConstraints c, List<String> selectedRoles, CouchDbServer.Roles role) {
			JCheckBox check = new JCheckBox(role.toString(),
					selectedRoles.contains(role.toString()));

//			JLabel l = new JLabel(role.toString());
//            c.gridwidth = GridBagConstraints.RELATIVE;
//            grid.setConstraints(l,c);
//            add(l);
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            grid.setConstraints(check,c);
            add(check);
            return check;
		}

	}