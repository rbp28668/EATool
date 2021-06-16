/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "displayHint")
@XmlAccessorType(XmlAccessType.NONE)

public class MetaEntityDisplayHintDao {
	private List<UUID> keys;

	/**
	 * @return the keys
	 */
	@JsonIgnore
	public List<UUID> getKeys() {
		if(keys == null) {
			keys = new LinkedList<UUID>();
		}
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeys(List<UUID> keys) {
		this.keys = keys;
	}

	/**
	 * @return the keys
	 */
	@JsonProperty("keys")
	public List<String> getKeysJson() {
		if(keys == null) return null;
		
		List<String> asString = new LinkedList<>();
		keys.forEach( key -> asString.add(key.asJsonId()));
		return asString;
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeysJson(List<String> keys) {
		if(keys == null) {
			this.keys = null;
			return;
		}
		
		this.keys = new LinkedList<UUID>();
		keys.forEach(key -> this.keys.add(UUID.fromJsonId(key)));
	}

}
