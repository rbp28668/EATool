/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import alvahouse.eatool.repository.dao.VersionDao;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "metaRelationship")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "start", "finish", "restriction", "version"})
public class MetaRelationshipDao extends MetaPropertyContainerDao{
	private MetaRoleDao start;
	private MetaRoleDao finish;
	private MetaRelationshipRestrictionDao restriction;
	private VersionDao version;
	
	/**
	 * @return the start
	 */
	@XmlElement
	public MetaRoleDao getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(MetaRoleDao start) {
		this.start = start;
	}
	/**
	 * @return the finish
	 */
	@XmlElement
	public MetaRoleDao getFinish() {
		return finish;
	}
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(MetaRoleDao finish) {
		this.finish = finish;
	}
	
	/**
	 * @return the restriction
	 */
	@XmlElement(required = false)
	public MetaRelationshipRestrictionDao getRestriction() {
		return restriction;
	}
	/**
	 * @param restriction the restriction to set
	 */
	public void setRestriction(MetaRelationshipRestrictionDao restriction) {
		this.restriction = restriction;
	}
	/**
	 * @return the version
	 */
	@XmlElement
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
