/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @author bruce_porteous
 *
 */
@XmlSeeAlso ({
	StraightConnectorStrategyDto.class,
	QuadraticConnectorStrategyDto.class,
	CubicConnectorStrategyDto.class
})
public class ConnectorStrategyDto {

	/**
	 * 
	 */
	public ConnectorStrategyDto() {
	}

}
