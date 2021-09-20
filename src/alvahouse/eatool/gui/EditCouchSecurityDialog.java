/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

import alvahouse.eatool.repository.persist.couchdb.CouchDbServer;
import alvahouse.eatool.repository.persist.couchdb.CouchDbServer.Security;
import alvahouse.eatool.repository.persist.couchdb.CouchDbServer.Security.Users;

/**
 * @author bruce_porteous
 *
 */
public class EditCouchSecurityDialog extends BasicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SecurityPanel panel;

	
	/**
	 * To edit an existing user.
	 * @param parent
	 * @param title
	 */
	public EditCouchSecurityDialog(Component parent, String dbName, List<CouchDbServer.User> allowedUsers, CouchDbServer.Security security) {
		super(parent, "Edit Database Security Information for " + dbName);
        panel = new SecurityPanel(allowedUsers, security);
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
	
	void updateSecurity(CouchDbServer.Security security) {
		panel.updateUser(security);
	}
	



	/**
	 * Panel to edit the entire security object for a database.
	 * @author bruce_porteous
	 *
	 */
	private class SecurityPanel extends javax.swing.JPanel {

		private static final long serialVersionUID = 1L;
		private UsersRolesPanel admins;
		private UsersRolesPanel members;
		
	    public SecurityPanel(List<CouchDbServer.User> allowedUsers, CouchDbServer.Security security) {
	    	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));	
	    	admins = new UsersRolesPanel("Administrators", allowedUsers, security.getAdmins());
	    	members = new UsersRolesPanel("Members", allowedUsers, security.getMembers());
	    	add(admins);
	    	add(members);
	    }


	    /**
		 * @param security
		 */
		public void updateUser(Security security) {
			admins.updateUsers(security.getAdmins());
			members.updateUsers(security.getMembers());
		}


		/** validates values in the panel
	     * @return true if input is valid, false if invalid
	     **/
	    public boolean validateInput() {
	        return admins.validateInput() && members.validateInput();
	    }
	}

	/**
	 * Panel to edit the users/roles for a particular class of users e.g. admins or members.
	 * @author bruce_porteous
	 *
	 */
	private class UsersRolesPanel extends javax.swing.JPanel {
		
		private static final long serialVersionUID = 1L;
		SelectCouchDbRolesPanel rolesPanel;
		SelectItemsPanel<CouchDbServer.User> usersPanel;
		
		UsersRolesPanel(String title, List<CouchDbServer.User> allowedUsers, CouchDbServer.Security.Users users) {
	    	setBorder(new TitledBorder( title));
	    	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));	
	    	usersPanel = new SelectItemsPanel<>(allowedUsers,selectUsers(allowedUsers,users.getNames()));
	    	rolesPanel = new SelectCouchDbRolesPanel(users.getRoles());

	    	usersPanel.setBorder(new TitledBorder( "Users"));
	    	rolesPanel.setBorder(new TitledBorder( "Roles"));

	    	add(usersPanel);
	    	add(rolesPanel);
		}

		/** validates values in the panel
	     * @return true if input is valid, false if invalid
	     **/
	    boolean validateInput() {
	        if(!rolesPanel.validateInput()) {
	        	return false;
	        }
	        return true;
	    }
	    
	    /**
	     * Updates the given Users object with the selected roles and names.
		 * @param users
		 */
		public void updateUsers(Users users) {
			rolesPanel.updateRoles(users.getRoles());
			
			Collection<CouchDbServer.User> selectedUsers = usersPanel.getSelectedItems();
			users.getNames().clear();
			selectedUsers.forEach(user -> users.getNames().add(user.getName()));
		}


		/**
	     * Converts a list of usernames to a list of users.
	     * @param allowedUsers has all the possible users.
	     * @param names identifies the users to select.
	     * @return
	     */
	    private List<CouchDbServer.User> selectUsers(List<CouchDbServer.User> allowedUsers, List<String> names){
	    	
	    	// Create lookup map by username
	    	Map<String, CouchDbServer.User> lookup = new HashMap<>(allowedUsers.size());
	    	allowedUsers.forEach( user -> lookup.put(user.getName(), user)); 

	    	// Select all the users given by names.
	    	List<CouchDbServer.User> selectedUsers = new LinkedList<>();
	    	names.forEach(name -> selectedUsers.add(lookup.get(name)));
	    	return selectedUsers;
	    }
	}
	

}
