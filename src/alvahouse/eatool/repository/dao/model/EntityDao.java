/**
 * 
 */
package alvahouse.eatool.repository.dao.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import alvahouse.eatool.repository.dao.VersionDao;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "entity")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "metaEntityKeyJson", "version"})
public class EntityDao extends PropertyContainerDao {

	private UUID metaEntityKey;
	private VersionDao version;
	
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
	public VersionDao getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDao version) {
		this.version = version;
	}
	
	
	
	
}
