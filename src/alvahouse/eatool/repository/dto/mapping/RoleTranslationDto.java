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
@XmlRootElement(name = "roleTranslation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "type", "metaRoleKeyJson"})
public class RoleTranslationDto extends PropertyTranslationCollectionDto {

    private String type="";          // what the "type" attribute of the input record is
    private UUID metaRoleKey = null;    // maps to this meta role 
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
	 * @return the metaRoleKey
	 */
	@JsonIgnore
	public UUID getMetaRoleKey() {
		return metaRoleKey;
	}
	/**
	 * @param metaRoleKey the metaRoleKey to set
	 */
	public void setMetaRoleKey(UUID metaRoleKey) {
		this.metaRoleKey = metaRoleKey;
	}
	/**
	 * @return the metaRoleKey
	 */
	@XmlElement(name="metaRoleKey")
	@JsonProperty("metaRoleKey")
	public String getMetaRoleKeyJson() {
		return metaRoleKey.asJsonId();
	}
	/**
	 * @param metaRoleKey the metaRoleKey to set
	 */
	public void setMetaRoleKeyJson(String metaRoleKey) {
		this.metaRoleKey = UUID.fromJsonId(metaRoleKey);
	}

}
