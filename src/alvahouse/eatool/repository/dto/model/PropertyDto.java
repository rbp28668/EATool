/**
 * 
 */
package alvahouse.eatool.repository.dto.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.RepositoryItemDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "metaPropertyKeyJson", "value"})
public class PropertyDto extends RepositoryItemDto {
	private UUID metaPropertyKey;
	private String value;
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
	public void setMetaPropertyKey(UUID metaPropertyKey) {
		this.metaPropertyKey = metaPropertyKey;
	}

	/**
	 * @return the metaPropertyKey
	 */
    @XmlElement(name="metaKey", required = true)
	@JsonProperty(value = "metaKey")
	public String getMetaPropertyKeyJson() {
		return metaPropertyKey.asJsonId();
	}
	
	/**
	 * @param metaPropertyKey the metaPropertyKey to set
	 */
	public void setMetaPropertyKeyJson(String metaPropertyKey) {
		this.metaPropertyKey = UUID.fromJsonId(metaPropertyKey);
	}

	/**
	 * @return the value
	 */
    @XmlElement(name="value", required = true)
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
