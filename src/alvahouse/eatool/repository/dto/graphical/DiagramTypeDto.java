/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"familyKeyJson", "eventMap", "version"})
public class DiagramTypeDto extends NamedRepositoryItemDto {

	private UUID familyKey;
	private EventMapDto eventMap;
	private VersionDto version;

	/**
	 * 
	 */
	public DiagramTypeDto() {
	}

	
	
	/**
	 * @return the familyKey
	 */
	@JsonIgnore
	public UUID getFamilyKey() {
		return familyKey;
	}



	/**
	 * @param familyKey the familyKey to set
	 */
	public void setFamilyKey(UUID familyKey) {
		this.familyKey = familyKey;
	}

	/**
	 * @return the familyKey
	 */
	@XmlElement(name="familyKey")
	@JsonProperty("familyKey")
	public String getFamilyKeyJson() {
		return familyKey.asJsonId();
	}



	/**
	 * @param familyKey the familyKey to set
	 */
	public void setFamilyKeyJson(String familyKey) {
		this.familyKey = UUID.fromJsonId(familyKey);
	}



	/**
	 * @return the eventMap
	 */
	@XmlElement
	public EventMapDto getEventMap() {
		return eventMap;
	}

	/**
	 * @param eventMap the eventMap to set
	 */
	public void setEventMap(EventMapDto eventMap) {
		this.eventMap = eventMap;
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
