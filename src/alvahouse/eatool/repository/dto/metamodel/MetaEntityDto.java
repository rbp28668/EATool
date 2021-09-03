/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlRootElement(name = "metaEntity")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "baseJson", "displayHint", "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class MetaEntityDto extends MetaPropertyContainerDto implements VersionedDto {
    private UUID base;
    private boolean isAbstract;
    private MetaEntityDisplayHintDto displayHint;
    private VersionDto version;
    private String rev; // Version revision returned by couchbase.
    public MetaEntityDto(){
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
	public MetaEntityDisplayHintDto getDisplayHint() {
		return displayHint;
	}

	/**
	 * @param displayHint the displayHint to set
	 */
	public void setDisplayHint(MetaEntityDisplayHintDto displayHint) {
		this.displayHint = displayHint;
	}

	/**
	 * @return the version
	 */
	@XmlElement
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
