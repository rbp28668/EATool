/**
 * 
 */
package alvahouse.eatool.repository.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RepositoryItemDao {
	private UUID key;

	/**
	 * @return the key
	 */
	@JsonIgnore
	public UUID getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(UUID key) {
		this.key = key;
	}
	
	/**
	 * @return the key
	 */
	@XmlElement(name="key", required = true)
	@JsonProperty(value = "key")
	public String getKeyJson() {
		return key.asJsonId();
	}

	/**
	 * @param key the key to set
	 */
	public void setKeyJson(String key) {
		this.key = UUID.fromJsonId(key);
	}
	
}
