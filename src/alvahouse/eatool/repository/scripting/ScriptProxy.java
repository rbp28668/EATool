/**
 * 
 */
package alvahouse.eatool.repository.scripting;

import alvahouse.eatool.util.UUID;

/**
 * Proxy for a script that allows lazy loading so that things like EventMaps don't need to
 * load the scripts until needed.
 * @author bruce_porteous
 *
 */
public class ScriptProxy {

	private UUID key;
	private boolean isNull;
	private Script script;
	
	/**
	 * 
	 */
	public ScriptProxy() {
		set(null);
	}

	public ScriptProxy(UUID key) {
		setKey(key);
	}
	
	/**
	 * Sets a new key for this script.  As the key is being
	 * set we assume that any existing script is now invalid
	 * so mark it as null for future loading.
	 * @param key
	 */
	void setKey(UUID key) {
		if(key == null || key == UUID.NULL) {
			isNull = true;
			key = UUID.NULL;
			script = null;
		} else {
			isNull = false;
			this.key = key;
			script = null;
		}
	}
	
	/**
	 * Attaches a given  script to this proxy.
	 * @param me
	 */
	void set(Script s) {
		if(s == null) {
			isNull = true;
			key = UUID.NULL;
			script = null;
		} else {
			isNull = false;
			key = s.getKey();
			script = s;
		}
	}
	
	/**
	 * Allows checking whether something is null without necessarily having
	 * to fetch the object.
	 * @return true if object is null (i.e. there is no attached  script.
	 */
	public boolean isNull() {
		return isNull;
	}
	
	/**
	 * Gets the script managed by this proxy.  If null then simply
	 * returns null but if not, will try to fetch it from the scripts collection
	 * if not already loaded.
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public Script get(Scripts scripts) throws Exception{
		if(isNull) {
			return null;
		} else {
			if(script == null) {
				script = scripts.lookupScript(key);
			}
			return script;
		}
	}
	
	/**
	 * Gets the key that identifies this  script.
	 * It's an error to try to get the key of a null  script.
	 * @return the key.
	 */
	public UUID getKey() {
		if(isNull) {
			throw new IllegalStateException("Can't get key of null Script");
		}
		return key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		ScriptProxy copy = new ScriptProxy();
		if(!isNull) {
			copy.setKey(key);
		}
		return copy;
	}


}
