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
@XmlRootElement(name = "relationship")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "metaRelationshipKeyJson", "start", "finish", "version"})
public class RelationshipDao extends PropertyContainerDao {
	private UUID metaRelationshipKey;
	private RoleDao start;
	private RoleDao finish;
	private VersionDao version;
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
	public RoleDao getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(RoleDao start) {
		this.start = start;
	}
	/**
	 * @return the finish
	 */
	@XmlElement(required=true)
	public RoleDao getFinish() {
		return finish;
	}
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(RoleDao finish) {
		this.finish = finish;
	}
	/**
	 * @return the version
	 */
	@XmlElement(required=true)
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
