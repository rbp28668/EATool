/**
 * 
 */
package alvahouse.eatool.repository.dto.model;

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
	
	public EntityDto createEntityDao() { return new EntityDto(); }
	public RelationshipDto createRelationshipDao() { return new RelationshipDto(); }
}
