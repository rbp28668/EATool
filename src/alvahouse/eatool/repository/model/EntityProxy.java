/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.util.UUID;

/**
 * Proxy class to hold a reference to an entity that can then be lazy loaded.
 * Used for Roles.
 * @author bruce_porteous
 *
 */
public class EntityProxy {

	private boolean isNull = true;
	private UUID key = UUID.NULL;
	private Entity entity = null;
	
	/**
	 * Creates a new proxy to a null entity.
	 */
	public EntityProxy() {
	}

	/**
	 * Sets a new key for this entity.  As the key is being
	 * set we assume that any existing entity is now invalid
	 * so mark it as null for future loading.
	 * @param key
	 */
	void setKey(UUID key) {
		isNull = false;
		this.key = key;
		entity = null;
	}
	
	/**
	 * Attaches a given meta entity to this proxy.
	 * @param me
	 */
	void set(Entity e) {
		if(e == null) {
			isNull = true;
			key = UUID.NULL;
			entity = null;
		} else {
			isNull = false;
			key = e.getKey();
			entity = e;
		}
	}
	
	/**
	 * Allows checking whether something is null without necessarily having
	 * to fetch the object.
	 * @return true if object is null (i.e. there is no attached meta entity.
	 */
	boolean isNull() {
		return isNull;
	}
	
	/**
	 * Gets the entity managed by this proxy.  If null then simply
	 * returns null but if not, will try to fetch it from the model
	 * if not already loaded.
	 * @param model
	 * @return
	 * @throws Exception
	 */
	Entity get(Model model) throws Exception{
		if(isNull) {
			return null;
		} else {
			if(entity == null) {
				entity = model.getEntity(key);
			}
			return entity;
		}
	}
	
	/**
	 * Gets the key that identifies this meta entity.
	 * It's an error to try to get the key of a null meta entity.
	 * @return the key.
	 */
	UUID getKey() {
		if(isNull) {
			throw new IllegalStateException("Can't get key of null Entity");
		}
		return key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		EntityProxy copy = new EntityProxy();
		if(!isNull) {
			copy.setKey(key);
		}
		return copy;
	}


}
