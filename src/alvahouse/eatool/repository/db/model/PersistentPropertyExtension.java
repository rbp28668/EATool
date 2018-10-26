/**
 * 
 */
package alvahouse.eatool.repository.db.model;

/**
 * @author bruce.porteous
 *
 */
public class PersistentPropertyExtension {
	String value;

	
	/**
	 * @param value
	 */
	public PersistentPropertyExtension(String value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
