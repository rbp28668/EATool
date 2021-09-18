/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.persist.couchdb.CouchDB.Query;

/**
 * Class for basic couch manipulation such as checking the server exists, creating databases etc.
 * Provides a high level abstraction for these whilst keeping the internal CouchDB implementation
 * private to this package.
 * @author bruce_porteous
 *
 */
public class CouchDbServer {

	private final CouchDB couch = new CouchDB();
	/**
	 * 
	 */
	public CouchDbServer() {
	}

	public void connect(String host, int port) throws Exception {
		couch.setLocation(host, port);
	}
	
	public Info getInstanceInfo() throws Exception {
		String json = couch.info();
		JsonNode root = Serialise.parseToJsonTree(json);
		String version = root.get("version").asText();
		String sha = root.get("git_sha").asText();
		String uuid = root.get("uuid").asText();
		String vendor = root.get("vendor").get("name").asText();
		return new Info(version, sha, uuid, vendor);
	}
	
	public String[] listDatabases() throws Exception {
		String json = couch.databases();
		JsonNode root = Serialise.parseToJsonTree(json);
		List<String> dbs = new LinkedList<String>();
		if(root.isArray()) {
			int size = root.size();
			for(int i=0; i<size; ++i ) {
				String db = root.get(i).textValue();
				if(!db.startsWith("_")) { // ignore system databases such as _user
					dbs.add(db);
				}
			}
		}
		return dbs.toArray(new String[dbs.size()]);
	}
	
	public int testDatabase(String name) throws Exception {
		return couch.database().databaseStatus(name);
	}
	
	public void createDatabase(String name) throws Exception{
		couch.database().create(name);
	}
	
	public void setCredentials(String user, String password) throws Exception{
		couch.setCredentials(user, password);
	}
	
	public void createAdminUser(String user, String password) throws Exception{
		//> curl -X PUT $HOST/_node/_local/_config/admins/anna -d '"secret"'
		// Note rawPutDocument as doesn't return a json response.
		couch.database().rawPutDocument("_node", "_local/_config/admins/" + user, "\"" + password + "\"");
	}
	
	public void createUser(CouchDbServer.User user) throws Exception{
		String document = Serialise.marshalToJSON(user);
		String key = "org.couchdb.user:" + user.getName();
		couch.database().putDocument("_users", key, document);
	}

	public void updateUser(CouchDbServer.User user) throws Exception {
		createUser(user);
	}

	public void changePassword(String name, String password) throws Exception{
		String key = "org.couchdb.user:" + name;
		String json = couch.database().getDocument("_users", key);
		
		JsonNode root = Serialise.parseToJsonTree(json);
		ObjectNode rootNode = (ObjectNode)root;
		rootNode.put("password",password);
		
		// Remove the previous hashing fields (optional but reduces size on wire).
		rootNode.remove("derived_key");
		rootNode.remove("iterations");
		rootNode.remove("password_scheme");
		rootNode.remove("salt");
		
		String document = Serialise.writeJsonTree(rootNode);
		couch.database().putDocument("_users", key, document);
	}
	
	public CouchDbServer.User getUser(String username) throws Exception{
		String key = "org.couchdb.user:" + username;
		String json = couch.database().getDocument("_users", key);
		CouchDbServer.User user = Serialise.unmarshalFromJson(json, CouchDbServer.User.class);
		return user;
	}

	public List<User> getAllUsers() throws Exception {
		String json = couch.database().getAllDocs("_users", new Query()
				.add("start_key", "org.couchdb.user")
				.add(Query.INCLUDE_DOCS)
				.toString());
		JsonNode root = Serialise.parseToJsonTree(json);

		int rowCount = root.get("total_rows").asInt(); // slightly more than needed due to design docs.
		List<User> users = new ArrayList<>(rowCount);

		JsonNode rows = root.get("rows");
		assert (rows.isArray());
		for (JsonNode row : rows) {
			JsonNode doc = row.get("doc");
			User user = Serialise.unmarshalFromJson(doc, User.class);
			users.add(user);
		}
		return users;
	}

	/**
	 * @param database
	 * @param security
	 */
	public void setDabaseSecurity(String database, Security security) throws Exception{
		String document = Serialise.marshalToJSON(security);
		couch.database().putDocument(database, "_security", document);
	}

	public CouchDB getCouch() {
		return couch;
	}
	
	/**
	 * Hold basic information about the CouchDB cluster.
	 * @author bruce_porteous
	 *
	 */
	public static class Info {

		private String version;
		private String gitSha;
		private String uuid;
		private String vendor;
		
		/**
		 * @param version
		 * @param sha
		 * @param uuid
		 * @param vendor
		 */
		public Info(String version, String sha, String uuid, String vendor) {
			this.version = version;
			this.gitSha = sha;
			this.uuid = uuid;
			this.vendor = vendor;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * @return the gitSha
		 */
		public String getGitSha() {
			return gitSha;
		}

		/**
		 * @return the uuid
		 */
		public String getUuid() {
			return uuid;
		}

		/**
		 * @return the vendor
		 */
		public String getVendor() {
			return vendor;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Version: ");
			builder.append(version);
			builder.append("\n Git sha: ");
			builder.append(gitSha);
			builder.append("\n uuid: ");
			builder.append(uuid);
			builder.append("\n Vendor: ");
			builder.append(vendor);
			builder.append("");
			return builder.toString();
		}
	}
	
	/**
	 * A user in couch.
	 * @author bruce_porteous
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class User {
		private String id;
		private String rev;
		private String name;
		private String password;
		private List<String> roles = new LinkedList<>();
		private String type;
		private String fullName;
		private String contactPhone;
		private String contactEmail;
	
		public User() {
			this.type = "user";
		}
		
		public User(String name, String password) {
			this.name = name;
			this.password = password;
			this.type = "user";
		}
		
		void addRole(String role) {
			roles.add(role);
		}

		
		
		/**
		 * @return the id
		 */
		@JsonProperty("_id")
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the rev
		 */
		@JsonProperty("_rev")
		public String getRev() {
			return rev;
		}

		/**
		 * @param rev the rev to set
		 */
		public void setRev(String rev) {
			this.rev = rev;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * @return the roles
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * @param roles the roles to set
		 */
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * @return the fullName
		 */
		public String getFullName() {
			return fullName;
		}

		/**
		 * @param fullName the fullName to set
		 */
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		/**
		 * @return the contactPhone
		 */
		public String getContactPhone() {
			return contactPhone;
		}

		/**
		 * @param contactPhone the contactPhone to set
		 */
		public void setContactPhone(String contactPhone) {
			this.contactPhone = contactPhone;
		}

		/**
		 * @return the contactEmail
		 */
		public String getContactEmail() {
			return contactEmail;
		}

		/**
		 * @param contactEmail the contactEmail to set
		 */
		public void setContactEmail(String contactEmail) {
			this.contactEmail = contactEmail;
		}
		
		public String toString() {
			return name + " (" + fullName + ")";
		}
		
	}
	
	/**
	 * Class to serialise the security object for a database.
	 * @author bruce_porteous
	 *
	 */
	@JsonInclude(Include.ALWAYS)
	public static class Security {
		private Users admins = new Users();
		private Users members = new Users();
		
		public static class Users {
			private List<String> names = new LinkedList<>();
			private List<String> roles = new LinkedList<>();
			/**
			 * @return the names
			 */
			@JsonProperty
			@JsonInclude(Include.ALWAYS)
			public List<String> getNames() {
				return names;
			}
			/**
			 * @param names the names to set
			 */
			public void setNames(List<String> names) {
				this.names = names;
			}
			/**
			 * @return the roles
			 */
			@JsonProperty
			@JsonInclude(Include.ALWAYS)
			public List<String> getRoles() {
				return roles;
			}
			/**
			 * @param roles the roles to set
			 */
			public void setRoles(List<String> roles) {
				this.roles = roles;
			}
			
		}

		/**
		 * @return the admins
		 */
		@JsonInclude(Include.ALWAYS)
		public Users getAdmins() {
			return admins;
		}

		/**
		 * @param admins the admins to set
		 */
		public void setAdmins(Users admins) {
			this.admins = admins;
		}

		/**
		 * @return the members
		 */
		@JsonInclude(Include.ALWAYS)
		public Users getMembers() {
			return members;
		}

		/**
		 * @param members the members to set
		 */
		public void setMembers(Users members) {
			this.members = members;
		}
	}
	
	public static enum Roles {
		READER,
		CONTRIBUTOR,
		DESIGNER,
		DB_ADMIN;
		
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

}
