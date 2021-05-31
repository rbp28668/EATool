/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.util.UUID;

/**
 * Class to support lazy loading of a meta entity.
 * @author bruce_porteous
 *
 */
public class MetaEntityProxy {

	private boolean isNull = true;
	private UUID key = UUID.NULL;
	private MetaEntity metaEntity = null;
	
	/**
	 * Creates a new proxy to a null meta entity.
	 */
	public MetaEntityProxy() {
	}

	/**
	 * Sets a new key for this entity.  As the key is being
	 * set we assume that any existing metaEntity is now invalid
	 * so mark it as null for future loading.
	 * @param key
	 */
	public void setKey(UUID key) {
		isNull = false;
		this.key = key;
		metaEntity = null;
	}
	
	/**
	 * Attaches a given meta entity to this proxy.
	 * @param me
	 */
	public void set(MetaEntity me) {
		if(me == null) {
			isNull = true;
			key = UUID.NULL;
			metaEntity = null;
		} else {
			isNull = false;
			key = me.getKey();
			metaEntity = me;
		}
	}
	
	/**
	 * Allows checking whether something is null without necessarily having
	 * to fetch the object.
	 * @return true if object is null (i.e. there is no attached meta entity.
	 */
	public boolean isNull() {
		return isNull;
	}
	
	/**
	 * Gets the meta entity managed by this proxy.  If null then simply
	 * returns null but if not, will try to fetch it from the meta-model
	 * if not already loaded.
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public MetaEntity get(MetaModel model) throws Exception{
		if(isNull) {
			return null;
		} else {
			if(metaEntity == null) {
				metaEntity = model.getMetaEntity(key);
			}
			return metaEntity;
		}
	}
	
	/**
	 * Gets the key that identifies this meta entity.
	 * It's an error to try to get the key of a null meta entity.
	 * @return the key.
	 */
	public UUID getKey() {
		if(isNull) {
			throw new IllegalStateException("Can't get key of null MetaEntity");
		}
		return key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		MetaEntityProxy copy = new MetaEntityProxy();
		if(!isNull) {
			copy.setKey(key);
		}
		return copy;
	}

	
}
