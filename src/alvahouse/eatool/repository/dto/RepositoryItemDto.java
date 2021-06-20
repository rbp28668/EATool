/**
 * 
 */
package alvahouse.eatool.repository.dto;

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
public class RepositoryItemDto {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryItemDto other = (RepositoryItemDto) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	
}
