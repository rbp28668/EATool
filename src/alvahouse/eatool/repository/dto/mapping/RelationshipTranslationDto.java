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
@XmlRootElement(name = "relationshipTranslation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "type", "metaRelationshipKeyJson", "startRoleTranslation", "finishRoleTranslation"})
public class RelationshipTranslationDto extends PropertyTranslationCollectionDto {

    private String type="";             	   // what the "type" attribute of the input record is
    private UUID metaRelationshipKey = null;      // maps to this meta relationship 
    private RoleTranslationDto startRoleTranslation = null;
    private RoleTranslationDto finishRoleTranslation = null;
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
	 * @return the metaRelationshipKey
	 */
	@JsonIgnore
	public UUID getMetaRelationshipKey() {
		return metaRelationshipKey;
	}
	/**
	 * @param metaRelationshipKey the metaRelationshipKey to set
	 */
	public void setMetaRelationshipKey(UUID metaRelationshipKey) {
		this.metaRelationshipKey = metaRelationshipKey;
	}
	/**
	 * @return the metaRelationshipKey
	 */
	@XmlElement(name="metaRelationshipKey")
	@JsonProperty("metaRelationshipKey")
	public String getMetaRelationshipKeyJson() {
		return metaRelationshipKey.asJsonId();
	}
	/**
	 * @param metaRelationshipKey the metaRelationshipKey to set
	 */
	public void setMetaRelationshipKeyJson(String metaRelationshipKey) {
		this.metaRelationshipKey = UUID.fromJsonId(metaRelationshipKey);
	}
	/**
	 * @return the startRoleTranslation
	 */
	@XmlElement
	public RoleTranslationDto getStartRoleTranslation() {
		return startRoleTranslation;
	}
	/**
	 * @param startRoleTranslation the startRoleTranslation to set
	 */
	public void setStartRoleTranslation(RoleTranslationDto startRoleTranslation) {
		this.startRoleTranslation = startRoleTranslation;
	}
	/**
	 * @return the finishRoleTranslation
	 */
	@XmlElement
	public RoleTranslationDto getFinishRoleTranslation() {
		return finishRoleTranslation;
	}
	/**
	 * @param finishRoleTranslation the finishRoleTranslation to set
	 */
	public void setFinishRoleTranslation(RoleTranslationDto finishRoleTranslation) {
		this.finishRoleTranslation = finishRoleTranslation;
	}


}
