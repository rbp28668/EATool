/**
 * 
 */
package alvahouse.eatool.repository.security;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bruce.porteous
 *
 */
public class Permission {

	private final String name;
	private final int hash;
	private final static Map<String,Permission> lookup = new HashMap<String, Permission>();
	
	public final static Permission READ = new Permission("Read",3);
	public final static Permission WRITE = new Permission("Write",5);
	public final static Permission DELETE = new Permission("Delete",7);
	public final static Permission ALL = new Permission("All",11);
	
	static {
		lookup.put(READ.toString(), READ);
		lookup.put(WRITE.toString(), WRITE);
		lookup.put(DELETE.toString(), DELETE);
		lookup.put(ALL.toString(), ALL);
	}
	
	private Permission(String name, int hash){
		this.name = name;
		this.hash = hash;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object other){
		return this == other; 
	}
	
	public int hashCode(){
		return hash;
	}

	/**
	 * Lookup permission by name.
	 * @param tok
	 * @return
	 */
	public static Permission lookup(String name) {
		Permission p = lookup.get(name);
		if(p == null){
			throw new IllegalArgumentException("No permission " + name);
		}
		return p;
	}
	
}
