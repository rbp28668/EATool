/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.util.LinkedList;
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

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "timeDiagram")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "properties","typeKeyJson","timeAxis"})
public class TimeDiagramDto extends DiagramDto {

    private List<TimeDiagramEntryDto> properties; 
    private UUID typeKey;
    private float timeAxis;

	/**
	 * 
	 */
	public TimeDiagramDto() {
	}

	/**
	 * @return the properties
	 */
	@XmlElementWrapper(name="propertyEntries")
	@XmlElement(name="propertyEntry")
	public List<TimeDiagramEntryDto> getProperties() {
		if(properties == null) {
			properties = new LinkedList<>();
		}
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<TimeDiagramEntryDto> properties) {
		this.properties = properties;
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
	@JsonProperty("typeKey")
	@XmlElement(name="typeKey")
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
	 * @return the timeAxis
	 */
	@XmlElement
	public float getTimeAxis() {
		return timeAxis;
	}

	/**
	 * @param timeAxis the timeAxis to set
	 */
	public void setTimeAxis(float timeAxis) {
		this.timeAxis = timeAxis;
	}

	
	
}
