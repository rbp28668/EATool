/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import javax.xml.bind.annotation.XmlRegistry;

import alvahouse.eatool.repository.dto.VersionDto;

/**
 * JAXB Object factory to allow JAXB to create concrete classes.
 * @author bruce_porteous
 *
 */
@XmlRegistry
public class ObjectFactory {
	
	public ObjectFactory() {
	}
	
	public MetaEntityDto createMetaEntityDao() { return new MetaEntityDto(); }
	public MetaRelationshipDto createMetaRelationshipDao() { return new MetaRelationshipDto(); }
	public MetaRoleDto createMetaRoleDao() { return new MetaRoleDto(); }
	public VersionDto createVersionDao() { return new VersionDto(); }
}
