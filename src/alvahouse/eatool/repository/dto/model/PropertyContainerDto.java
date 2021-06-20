/**
 * 
 */
package alvahouse.eatool.repository.dto.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.RepositoryItemDto;

/**
 * @author bruce_porteous
 *
 */
public abstract class PropertyContainerDto extends RepositoryItemDto {

	private List<PropertyDto> properties;

	/**
	 * @return the properties
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "properties")
	@JsonProperty(value = "properties")
	public List<PropertyDto> getProperties() {
		if (properties == null) {
			properties = new LinkedList<PropertyDto>();
		}
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<PropertyDto> properties) {
		this.properties = properties;
	}

}
