/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import alvahouse.eatool.repository.dto.VersionDto;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "metaRelationship")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "start", "finish", "restriction", "version"})
public class MetaRelationshipDto extends MetaPropertyContainerDto {
	private MetaRoleDto start;
	private MetaRoleDto finish;
	private MetaRelationshipRestrictionDto restriction;
	private VersionDto version;
	
	/**
	 * @return the start
	 */
	@XmlElement
	public MetaRoleDto getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(MetaRoleDto start) {
		this.start = start;
	}
	/**
	 * @return the finish
	 */
	@XmlElement
	public MetaRoleDto getFinish() {
		return finish;
	}
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(MetaRoleDto finish) {
		this.finish = finish;
	}
	
	/**
	 * @return the restriction
	 */
	@XmlElement(required = false)
	public MetaRelationshipRestrictionDto getRestriction() {
		return restriction;
	}
	/**
	 * @param restriction the restriction to set
	 */
	public void setRestriction(MetaRelationshipRestrictionDto restriction) {
		this.restriction = restriction;
	}
	/**
	 * @return the version
	 */
	@XmlElement
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
