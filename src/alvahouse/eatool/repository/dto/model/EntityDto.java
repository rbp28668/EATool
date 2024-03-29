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
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.VersionedDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "entity")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "metaEntityKeyJson", "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class EntityDto extends PropertyContainerDto implements VersionedDto {

	private UUID metaEntityKey;
	private VersionDto version;
	private String rev;
	
	/**
	 * @return the metaKey
	 */
	@JsonIgnore
	public UUID getMetaEntityKey() {
		return metaEntityKey;
	}
	/**
	 * @param metaKey the metaKey to set
	 */
	public void setMetaEntityKey(UUID metaEntityKey) {
		this.metaEntityKey = metaEntityKey;
	}

	/**
	 * @return the metaKey
	 */
	@XmlElement(name = "metaKey", required=true)
	@JsonProperty(value="metaKey")
	public String getMetaEntityKeyJson() {
		return metaEntityKey.asJsonId();
	}
	/**
	 * @param metaKey the metaKey to set
	 */
	public void setMetaEntityKeyJson(String metaEntityKey) {
		this.metaEntityKey = UUID.fromJsonId(metaEntityKey);
	}

	/**
	 * @return the version
	 */
	@XmlElement(name = "version", required=true)
	@Override
	public VersionDto getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}
	
	/**
	 * revision information for CouchDB
	 * @return the rev
	 */
	@JsonProperty("_rev")
	@Override
	public String getRev() {
		return rev;
	}

	/**
	 * @param rev the rev to set
	 */
	@Override
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	
	
}
