/**
 * 
 */
package alvahouse.eatool.repository.dao;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.NONE)
public class VersionDao {

	private Date createDate;
    private String createUser;
    private Date modifyDate;
    private String modifyUser;
    private UUID version;
    private UUID originalVersion;

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
	@JsonIgnore
	public UUID getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(UUID version) {
		this.version = version;
	}
	/**
	 * @return the originalVersion
	 */
	@JsonIgnore
	public UUID getOriginalVersion() {
		return originalVersion;
	}
	/**
	 * @param originalVersion the originalVersion to set
	 */
	public void setOriginalVersion(UUID originalVersion) {
		this.originalVersion = originalVersion;
	}

	/**
	 * @return the version
	 */
	@XmlAttribute(name="current", required = true)
	@JsonProperty(value = "current")
	public String getVersionJson() {
		return version.asJsonId();
	}
	/**
	 * @param version the version to set
	 */
	public void setVersionJson(String version) {
		this.version = UUID.fromJsonId(version);
	}
	/**
	 * @return the originalVersion
	 */
	@XmlAttribute(name="previous", required = true)
	@JsonProperty(value = "previous")
	public String getOriginalVersionJson() {
		return originalVersion.asJsonId();
	}
	/**
	 * @param originalVersion the originalVersion to set
	 */
	public void setOriginalVersionJson(String originalVersion) {
		this.originalVersion = UUID.fromJsonId(originalVersion);
	}

    
}
