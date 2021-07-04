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
	
	public MetaEntityDto createMetaEntityDto() { return new MetaEntityDto(); }
	public MetaRelationshipDto createMetaRelationshipDto() { return new MetaRelationshipDto(); }
	public MetaRoleDto createMetaRoleDto() { return new MetaRoleDto(); }
	public VersionDto createVersionDto() { return new VersionDto(); }
	public RegexpCheckedTypeDto createRegexpCheckedTypeDto() { return new RegexpCheckedTypeDto();}
	public ControlledListTypeDto createControlledListTypeDto() { return new ControlledListTypeDto();}
	public TimeSeriesTypeDto createTimeSeriesTypeDto() { return new TimeSeriesTypeDto();}
	
}
