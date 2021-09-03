/**
 * 
 */
package alvahouse.eatool.repository.dto;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.NONE)
public class VersionDto {

	private Date createDate;
    private String createUser;
    private Date modifyDate;
    private String modifyUser;
    private String version;
    private String originalVersion;

	/**
	 * @return the createDate
	 */
    @JsonIgnore
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@XmlAttribute(name="createDate", required = true)
	@JsonProperty(value = "createDate")
	public String getCreateDateJson() {
		return Formatting.fromDate(createDate);
	}
	
	/**
	 * @param createDate the createDate to set
	 * @throws ParseException 
	 */
	public void setCreateDateJson(String createDate) throws ParseException {
		this.createDate = Formatting.toDate(createDate);
	}
	
	/**
	 * @return the createUser
	 */
	@XmlAttribute(required = true)
	public String getCreateUser() {
		return createUser;
	}
	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	/**
	 * @return the modifyDate
	 */
	@JsonIgnore
	public Date getModifyDate() {
		return modifyDate;
	}
	/**
	 * @param modifyDate the modifyDate to set
	 */
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	/**
	 * @return the modifyDate
	 */
	@XmlAttribute(name="modifyDate", required = true)
	@JsonProperty(value = "modifyDate")
	public String getModifyDateJson() {
		return Formatting.fromDate(modifyDate);
	}
	/**
	 * @param modifyDate the modifyDate to set
	 * @throws ParseException 
	 */
	public void setModifyDateJson(String modifyDate) throws ParseException {
		this.modifyDate = Formatting.toDate(modifyDate);
	}

	
	/**
	 * @return the modifyUser
	 */
	@XmlAttribute(required = true)
	public String getModifyUser() {
		return modifyUser;
	}
	/**
	 * @param modifyUser the modifyUser to set
	 */
	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}
	
	/**
	 * @return the version
	 */
	@XmlAttribute(name="current", required = true)
	@JsonProperty(value = "current")
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return the originalVersion
	 */
	@XmlAttribute(name="previous", required = true)
	@JsonProperty(value = "previous")
	public String getOriginalVersion() {
		return originalVersion;
	}
	/**
	 * @param originalVersion the originalVersion to set
	 */
	public void setOriginalVersion(String originalVersion) {
		this.originalVersion = originalVersion;
	}
	
	/**
	 * @param string
	 */
	public String update(String newVersion) {
		this.originalVersion = version;
		this.version = newVersion;
		return newVersion;
	}
	
	/**
	 * Mainly intended for in memory stores we we want to make sure that the
	 * version that's being updated is valid.
	 * @param other
	 * @return
	 */
	public boolean sameVersionAs(VersionDto other) {
		if(this.version == null && other.version == null) return true;
		if(this.version == null || other.version == null) return false;
		return this.version.equals(other.version);
	}

}
