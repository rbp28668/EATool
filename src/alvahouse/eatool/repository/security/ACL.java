package alvahouse.eatool.repository.security;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.util.UUID;

/**
 * Describes which users and groups have access to which controlled items.
 */
public class ACL extends RepositoryItem {

	/** */
	private List<ACLEntry> entries = new LinkedList<ACLEntry>();

	public ACL(){
		this(new UUID());
	}
	
	public ACL(UUID uuid){
		super(uuid);
	}
	
	/**
	 * @return the entries
	 */
	private List<ACLEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	@SuppressWarnings("unused")
	private void setEntries(List<ACLEntry> entries) {
		this.entries = entries;
	}

	public Collection<ACLEntry> getACLEntries(){
		return Collections.unmodifiableCollection(entries);
	}
	
	/**
	 * @param user
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(User user, Permission permission) {

		boolean allow = false; // Default is to deny.
		// For starters - does this ACL directly reference the user?
		boolean found = false;
		for (ACLEntry entry : entries) {
			if (entry.getPrincipal().equals(user)
					&& entry.hasPermission(permission)) {
				allow = entry.isAllow();
				found = true;
				break;
			}
		}

		// If not found - then see what groups the user is in
		if (!found) {
			for (SecurityGroup group : user.getMemberOf()) {
				if (isDenied(group, permission)) {
					allow = false;
					break;
				}
				if (isAllowed(group, permission)) {
					allow = true;
					break;
				}
			}
		}
		return allow;
	}

	/**
	 * @param group
	 * @param permission
	 * @return
	 */
	private boolean isAllowed(SecurityGroup group, Permission permission) {
		boolean allow = false; // Default is to deny.

		if (group == null) {
			return allow;
		}

		// For starters - does this ACL directly reference the group?
		boolean found = false;
		for (ACLEntry entry : entries) {
			
			if (entry.getPrincipal().equals(group)
					&& entry.hasPermission(permission)) {
				
				if(entry.isAllow()){
					allow = true;
				}
				
				found = true;
				break;
			}
		}

		// If nothing doing - try parent group
		if (!found) {
			allow = isAllowed(group.getParent(), permission);
		}
		return allow;
	}

	/**
	 * @param group
	 * @param permission
	 * @return
	 */
	private boolean isDenied(SecurityGroup group, Permission permission) {

		boolean deny = false;

		if (group == null) {
			return false; // not explicitly denied - may be allowed
							// explicitly.
		}

		// For starters - does this ACL directly reference the group?
		boolean found = false;
		for (ACLEntry entry : entries) {

			if (entry.getPrincipal().equals(group)
					&& entry.hasPermission(permission)) {
				if(!entry.isAllow()){
					deny = true;
				}
				found = true;
				break;
			}
		}

		// If nothing doing - try parent group
		if (!found) {
			deny = isDenied(group.getParent(), permission);
		}

		return deny;
	}

	/**
	 * @param entry
	 */
	public void addEntry(ACLEntry entry) {
		if (entry == null) {
			throw new NullPointerException("Can't add null entry to ACL");
		}
		entries.add(entry);

	}

	/**
	 * Force the loading of children.
	 */
	public void load() {
		getEntries().size();
		
	}

	/**
	 * 
	 */
	public void clearEntries() {
		entries.clear();
	}
}