/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "displayedProperty")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "entityKeyJson","metaPropertyKeyJson"})
public class TimeDiagramEntryDto {

	private UUID entityKey;
    private UUID metaPropertyKey;
	/**
	 * 
	 */
	public TimeDiagramEntryDto() {
	}

	/**
	 * @return the entityKey
	 */
	@JsonIgnore
	public UUID getEntityKey() {
		return entityKey;
	}
	/**
	 * @param entityKey the entityKey to set
	 */
	public void setEntityKey(UUID entityKey) {
		this.entityKey = entityKey;
	}

	/**
	 * @return the entityKey
	 */
	@XmlElement(name="entityKey")
	@JsonProperty("entityKey")
	public String getEntityKeyJson() {
		return entityKey.asJsonId();
	}
	
	/**
	 * @param entityKey the entityKey to set
	 */
	public void setEntityKeyJson(String entityKey) {
		this.entityKey = UUID.fromJsonId(entityKey);
	}

	/**
	 * @return the metaPropertyKey
	 */
	@JsonIgnore
	public UUID getMetaPropertyKey() {
		return metaPropertyKey;
	}
	/**
	 * @param metaPropertyKey the metaPropertyKey to set
	 */
	public void setMetaPropertyKey(UUID propertyKey) {
		this.metaPropertyKey = propertyKey;
	}

	/**
	 * @return the metaPropertyKey
	 */
	@XmlElement(name="metaPropertyKey")
	@JsonProperty("metaPropertyKey")
	public String getMetaPropertyKeyJson() {
		return metaPropertyKey.asJsonId();
	}
	
	/**
	 * @param metaPropertyKey the metaPropertyKey to set
	 */
	public void setMetaPropertyKeyJson(String propertyKey) {
		this.metaPropertyKey = UUID.fromJsonId(propertyKey);
	}

	

}
