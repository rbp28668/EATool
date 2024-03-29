/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.VersionedDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"typeKeyJson","backColourJson", "eventMap", "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
@JsonSubTypes({ 
	  @Type(value = StandardDiagramDto.class, name = "StandardDiagramDto"), 
	  @Type(value = TimeDiagramDto.class, name = "TimeDiagramDto") 
	})
public abstract class DiagramDto extends NamedRepositoryItemDto implements VersionedDto{

	private UUID typeKey;
	private boolean isDynamic = false; // True if content script generated so don't save.
	private Color backColour = Color.lightGray;
	private EventMapDto eventMap;
	private VersionDto version;
	private String rev;
	
	/**
	 * 
	 */
	public DiagramDto() {
	}


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
	@XmlElement(name = "typeKey")
	@JsonProperty("typeKey")
	public String getTypeKeyJson() {
		return typeKey.asJsonId();
	}

	/**
	 * @param typeKey the typeKey to set
	 */
	public void setTypeKeyJson(String typeKey) {
		this.typeKey = UUID.fromJsonId(typeKey);
	}
	/**
	 * @return the isDynamic
	 */
	@XmlAttribute
	public boolean isDynamic() {
		return isDynamic;
	}

	/**
	 * @param isDynamic the isDynamic to set
	 */
	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}

	/**
	 * @return the backColour
	 */
	@JsonIgnore
	public Color getBackColour() {
		return backColour;
	}

	/**
	 * @param backColour the backColour to set
	 */
	public void setBackColour(Color backColour) {
		this.backColour = backColour;
	}

	/**
	 * @return the backColour
	 */
	@XmlElement(name="backColour")
	@JsonProperty("backColour")
	public ColourDto getBackColourJson() {
		return new ColourDto(backColour);
	}

	/**
	 * @param backColour the backColour to set
	 */
	public void setBackColourJson(ColourDto backColour) {
		this.backColour = backColour.toColor();
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
