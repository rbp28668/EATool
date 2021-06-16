/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dao.NamedRepositoryItemDao;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "metaProperty")
@XmlAccessorType(XmlAccessType.NONE)
public class MetaPropertyDao extends NamedRepositoryItemDao {
	private UUID    typeKey;
	private boolean isMandatory;
	private boolean isReadOnly;
	private boolean isSummary;
	private String defaultValue;
	
	
	
	/**
	 * @return the typeKey
	 */
	@JsonIgnore
	public UUID getTypeKey() {
		return typeKey;
	}
	/**
	 * @param typeKey the typeKey to set
	 */
	public void setTypeKey(UUID typeKey) {
		this.typeKey = typeKey;
	}

	/**
	 * @return the typeKey
	 */
	@JsonProperty(value="type")
	@XmlAttribute(name="type", required = true)
	public String getTypeKeyJson() {
		if(typeKey == null) return null;
		return typeKey.asJsonId();
	}
	/**
	 * @param typeKey the typeKey to set
	 */
	public void setTypeKeyJson(String typeKey) {
		this.typeKey = UUID.fromJsonId(typeKey);
	}

	
	/**
	 * @return the isMandatory
	 */
	@XmlAttribute
	public boolean isMandatory() {
		return isMandatory;
	}
	/**
	 * @param isMandatory the isMandatory to set
	 */
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	/**
	 * @return the isReadOnly
	 */
	@XmlAttribute
	public boolean isReadOnly() {
		return isReadOnly;
	}
	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	/**
	 * @return the isSummary
	 */
	@XmlAttribute
	public boolean isSummary() {
		return isSummary;
	}
	/**
	 * @param isSummary the isSummary to set
	 */
	public void setSummary(boolean isSummary) {
		this.isSummary = isSummary;
	}
	/**
	 * @return the defaultValue
	 */
	@XmlElement(required = false)
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
