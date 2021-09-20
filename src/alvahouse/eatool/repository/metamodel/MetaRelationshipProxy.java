/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.util.UUID;

/**
 * Class to support lazy loading of a meta relationship.
 * @author bruce_porteous
 *
 */
public class MetaRelationshipProxy {

	private boolean isNull = true;
	private UUID key = UUID.NULL;
	private MetaRelationship metaRelationship = null;
	
	/**
	 * Creates a new proxy to a null meta relationship.
	 */
	public MetaRelationshipProxy() {
	}

	/**
	 * Sets a new key for this relationship.  As the key is being
	 * set we assume that any existing metaRelationship is now invalid
	 * so mark it as null for future loading.
	 * @param key
	 */
	public void setKey(UUID key) {
		if(key == null || key == UUID.NULL) {
			isNull = true;
			key = UUID.NULL;
			metaRelationship = null;
		} else {
			isNull = false;
			this.key = key;
			metaRelationship = null;
		}
	}
	
	/**
	 * Attaches a given meta relationship to this proxy.
	 * @param me
	 */
	public void set(MetaRelationship me) {
		if(me == null) {
			isNull = true;
			key = UUID.NULL;
			metaRelationship = null;
		} else {
			isNull = false;
			key = me.getKey();
			metaRelationship = me;
		}
	}
	
	/**
	 * Allows checking whether something is null without necessarily having
	 * to fetch the object.
	 * @return true if object is null (i.e. there is no attached meta relationship.
	 */
	public boolean isNull() {
		return isNull;
	}
	
	/**
	 * Gets the meta relationship managed by this proxy.  If null then simply
	 * returns null but if not, will try to fetch it from the meta-model
	 * if not already loaded.
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public MetaRelationship get(MetaModel model) throws Exception{
		if(isNull) {
			return null;
		} else {
			if(metaRelationship == null) {
				metaRelationship = model.getMetaRelationship(key);
			}
			return metaRelationship;
		}
	}
	
	/**
	 * Gets the key that identifies this meta relationship.
	 * It's an error to try to get the key of a null meta relationship.
	 * @return the key.
	 */
	public UUID getKey() {
		if(isNull) {
			throw new IllegalStateException("Can't get key of null MetaRelationship");
		}
		return key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		MetaRelationshipProxy copy = new MetaRelationshipProxy();
		if(!isNull) {
			copy.setKey(key);
		}
		return copy;
	}

	
}
