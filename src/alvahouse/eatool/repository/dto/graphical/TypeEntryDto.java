/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

@XmlRootElement(name = "typeEntry")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "targetTypeKeyJson","targetPropertyKeyJson", "coloursJson"})
/**
 * DTO for TimeDiagramType.TypeEntry
 * @author bruce_porteous
 *
 */
public class TypeEntryDto {

    private UUID targetTypeKey;  // of MetaEntity
    private UUID targetPropertyKey; // of MetaProperty
    private List<Color> colours;

	/**
	 * 
	 */
	public TypeEntryDto() {
	}

	/**
	 * @return the targetTypeKey
	 */
	@JsonIgnore
	public UUID getTargetTypeKey() {
		return targetTypeKey;
	}

	/**
	 * @param targetTypeKey the targetTypeKey to set
	 */
	public void setTargetTypeKey(UUID targetTypeKey) {
		this.targetTypeKey = targetTypeKey;
	}

	/**
	 * @return the targetTypeKey
	 */
	@JsonProperty("targetTypeKey")
	@XmlElement(name="targetTypeKey")
	public String getTargetTypeKeyJson() {
		return targetTypeKey.asJsonId();
	}

	/**
	 * @param targetTypeKey the targetTypeKey to set
	 */
	public void setTargetTypeKeyJson(String targetTypeKey) {
		this.targetTypeKey = UUID.fromJsonId(targetTypeKey);
	}

	
	/**
	 * @return the targetPropertyKey
	 */
	@JsonIgnore
	public UUID getTargetPropertyKey() {
		return targetPropertyKey;
	}

	/**
	 * @param targetPropertyKey the targetPropertyKey to set
	 */
	public void setTargetPropertyKey(UUID targetPropertyKey) {
		this.targetPropertyKey = targetPropertyKey;
	}

	/**
	 * @return the targetPropertyKey
	 */
	@JsonProperty("targetPropertyKey")
	@XmlElement(name="targetPropertyKey")
	public String getTargetPropertyKeyJson() {
		return targetPropertyKey.asJsonId();
	}

	/**
	 * @param targetPropertyKey the targetPropertyKey to set
	 */
	public void setTargetPropertyKeyJson(String targetPropertyKey) {
		this.targetPropertyKey = UUID.fromJsonId(targetPropertyKey);
	}

	/**
	 * @return the colours
	 */
	@JsonIgnore
	public List<Color> getColours() {
		if(colours == null) {
			colours = new ArrayList<>();
		}
		return colours;
	}

	/**
	 * @param colours the colours to set
	 */
	public void setColours(List<Color> colours) {
		this.colours = colours;
	}

	/**
	 * @return the colours
	 */
	@XmlElementWrapper(name="colours")
	@XmlElement(name="colour")
	public List<ColourDto> getColoursJson() {
		if(this.colours == null) {
			return new ArrayList<ColourDto>(0);
		}
		
		List<ColourDto> colours = new ArrayList<>(this.colours.size());
		for(Color c : this.colours) {
			colours.add(new ColourDto(c));
		}
		return colours;
	}

	/**
	 * @param colours the colours to set
	 */
	public void setColoursJson(List<ColourDto> colours) {
		int size = (colours == null) ? 0 : colours.size();
		if(this.colours == null) {
			this.colours = new ArrayList<>(size);
		}
		this.colours.clear();
		for(ColourDto c : colours) {
			this.colours.add(c.toColor());
		}
	}

}
