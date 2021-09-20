/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.repository.dto.RepositoryItemDto;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ ConnectorEndNullDto.class, ConnectorEndArrowHeadDto.class})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConnectorEndNullDto.class, name = "NullEnd"),
    @JsonSubTypes.Type(value = ConnectorEndArrowHeadDto.class, name = "ArrowEnd"),
})
public abstract class ConnectorEndDto extends RepositoryItemDto {

	/**
	 * 
	 */
	public ConnectorEndDto() {
	}

}
