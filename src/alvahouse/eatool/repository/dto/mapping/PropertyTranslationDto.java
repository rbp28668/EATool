/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlRootElement(name = "propertyTranslation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "type", "metaPropertyKeyJson"})
public class PropertyTranslationDto {

    private String type = "";           // typename for input property in input
    private UUID metaPropertyKey = null;   // corresponding meta property
    private boolean isKey = false;  	// true if forms part of identify
	/**
	 * @return the type
	 */
    @XmlElement
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	public void setMetaPropertyKey(UUID metaPropertyKey) {
		this.metaPropertyKey = metaPropertyKey;
	}

	/**
	 * Gets a json compatible string representation of the meta property key.
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
	public void setMetaPropertyKeyJson(String metaPropertyKey) {
		this.metaPropertyKey = UUID.fromJsonId(metaPropertyKey);
	}

	/**
	 * @return the isKey
	 */
	@XmlAttribute
	public boolean isKey() {
		return isKey;
	}
	/**
	 * @param isKey the isKey to set
	 */
	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}


    
}
