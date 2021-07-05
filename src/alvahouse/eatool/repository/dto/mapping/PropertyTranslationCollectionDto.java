/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PropertyTranslationCollectionDto {

    private List<PropertyTranslationDto> props = new LinkedList<PropertyTranslationDto>(); // property input descriptions

	/**
	 * @return the props
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "propertyTranslations")
	@JsonProperty(value = "propertyTranslations")
	public List<PropertyTranslationDto> getProps() {
		if(props == null) {
			props = new LinkedList<PropertyTranslationDto>();
		}
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(List<PropertyTranslationDto> props) {
		this.props = props;
	}


}
