/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import javax.xml.bind.annotation.XmlRegistry;

import alvahouse.eatool.repository.dao.VersionDao;

/**
 * JAXB Object factory to allow JAXB to create concrete classes.
 * @author bruce_porteous
 *
 */
@XmlRegistry
public class ObjectFactory {
	
	public ObjectFactory() {
	}
	
	public MetaEntityDao createMetaEntityDao() { return new MetaEntityDao(); }
	public MetaRelationshipDao createMetaRelationshipDao() { return new MetaRelationshipDao(); }
	public MetaRoleDao createMetaRoleDao() { return new MetaRoleDao(); }
	public VersionDao createVersionDao() { return new VersionDao(); }
}
