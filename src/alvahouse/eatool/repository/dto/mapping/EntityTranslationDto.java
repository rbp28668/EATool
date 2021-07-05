/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

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
@XmlRootElement(name = "entityTranslation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "type", "metaEntityKeyJson"})
public class EntityTranslationDto extends PropertyTranslationCollectionDto {

    private String type="";                // what the "type" attribute of the input record is
    private UUID metaEntityKey;            // maps to this meta entity 
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
	 * @return the metaEntityKey
	 */
	@JsonIgnore
	public UUID getMetaEntityKey() {
		return metaEntityKey;
	}
	/**
	 * @param metaEntityKey the metaEntityKey to set
	 */
	public void setMetaEntityKey(UUID metaEntityKey) {
		this.metaEntityKey = metaEntityKey;
	}

	/**
	 * Gets the meta entity key as a json compatible string
	 * @return the metaEntityKey
	 */
	@XmlElement(name="metaEntityKey")
	@JsonProperty("metaEntityKey")
	public String getMetaEntityKeyJson() {
		return metaEntityKey.asJsonId();
	}
	/**
	 * @param metaEntityKey the metaEntityKey to set
	 */
	public void setMetaEntityKeyJson(String metaEntityKey) {
		this.metaEntityKey = UUID.fromJsonId(metaEntityKey);
	}

    

}
