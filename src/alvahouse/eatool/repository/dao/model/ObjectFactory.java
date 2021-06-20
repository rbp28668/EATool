/**
 * 
 */
package alvahouse.eatool.repository.dao.model;

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
	
	public EntityDao createEntityDao() { return new EntityDao(); }
	public RelationshipDao createRelationshipDao() { return new RelationshipDao(); }
}
