/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author bruce_porteous
 *
 */
@XmlSeeAlso ({
	StraightConnectorStrategyDto.class,
	QuadraticConnectorStrategyDto.class,
	CubicConnectorStrategyDto.class
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StraightConnectorStrategyDto.class, name = "Straight"),
    @JsonSubTypes.Type(value = QuadraticConnectorStrategyDto.class, name = "Quadratic"),
    @JsonSubTypes.Type(value = CubicConnectorStrategyDto.class, name = "Cubic")
    })
public class ConnectorStrategyDto {

	/**
	 * 
	 */
	public ConnectorStrategyDto() {
	}

}
