/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import alvahouse.eatool.repository.dto.RepositoryItemDto;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ ConnectorEndNullDto.class, ConnectorEndArrowHeadDto.class})
public class ConnectorEndDto extends RepositoryItemDto {

	/**
	 * 
	 */
	public ConnectorEndDto() {
	}

}
