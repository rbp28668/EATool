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

import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "relationship")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "metaRelationshipKeyJson", "start", "finish", "version"})
public class RelationshipDto extends PropertyContainerDto {
	private UUID metaRelationshipKey;
	private RoleDto start;
	private RoleDto finish;
	private VersionDto version;
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
	@XmlElement(name="metaKey", required=true)
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
	 * @return the start
	 */
	@XmlElement(required=true)
	public RoleDto getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(RoleDto start) {
		this.start = start;
	}
	/**
	 * @return the finish
	 */
	@XmlElement(required=true)
	public RoleDto getFinish() {
		return finish;
	}
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(RoleDto finish) {
		this.finish = finish;
	}
	/**
	 * @return the version
	 */
	@XmlElement(required=true)
	public VersionDto getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}
	
	
}
