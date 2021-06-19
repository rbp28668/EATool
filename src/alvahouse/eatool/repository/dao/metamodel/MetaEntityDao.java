/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dao.VersionDao;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "metaEntity")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "baseJson", "displayHint", "version"})
public class MetaEntityDao extends MetaPropertyContainerDao {
    private UUID base;
    private boolean isAbstract;
    private MetaEntityDisplayHintDao displayHint;
    private VersionDao version;

    public MetaEntityDao(){
    }

	/**
	 * @return the base
	 */
    @JsonIgnore
	public UUID getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(UUID base) {
		this.base = base;
	}

	/**
	 * @return the base
	 */
    @XmlElement(name="base", required = false)
	@JsonProperty(value = "base")
	public String getBaseJson() {
    	if(base == null) return null;
		return base.asJsonId();
	}

	/**
	 * @param base the base to set
	 */
	public void setBaseJson(String base) {
		this.base = UUID.fromJsonId(base);
	}

	/**
	 * @return the isAbstract
	 */
	@XmlAttribute
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * @return the displayHint
	 */
	@XmlElement(required = false)
	public MetaEntityDisplayHintDao getDisplayHint() {
		return displayHint;
	}

	/**
	 * @param displayHint the displayHint to set
	 */
	public void setDisplayHint(MetaEntityDisplayHintDao displayHint) {
		this.displayHint = displayHint;
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
