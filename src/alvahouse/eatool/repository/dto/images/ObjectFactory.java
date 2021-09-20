/**
 * 
 */
package alvahouse.eatool.repository.dto.images;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * JAXB Object factory to allow JAXB to create concrete classes.
 * @author bruce_porteous
 *
 */
@XmlRegistry
public class ObjectFactory {
	
	public ObjectFactory() {
	}
	
	public ImageDto createImageDto() { return new ImageDto(); }
}
