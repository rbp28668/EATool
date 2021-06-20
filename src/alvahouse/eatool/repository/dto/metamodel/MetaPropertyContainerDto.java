/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;

/**
 * @author bruce_porteous
 *
 */
public abstract class MetaPropertyContainerDto extends NamedRepositoryItemDto {

	private List<MetaPropertyDto> properties;

	/**
	 * @return the properties
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "properties")
	@JsonProperty(value = "properties")
	public List<MetaPropertyDto> getProperties() {
		if (properties == null) {
			properties = new LinkedList<MetaPropertyDto>();
		}
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<MetaPropertyDto> properties) {
		this.properties = properties;
	}
}
