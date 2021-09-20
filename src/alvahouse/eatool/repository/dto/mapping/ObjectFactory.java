/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

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
	
	public ExportMappingDto createExportMappingDto() { return new ExportMappingDto(); }
	public ImportMappingDto createImportMappingDto() { return new ImportMappingDto(); }
	public EntityTranslationDto createEntityTranslationDto() { return new EntityTranslationDto(); }
	public RelationshipTranslationDto createRelationshipTranslationDto() { return new RelationshipTranslationDto();}
	public RoleTranslationDto createRoleTranslationDto() { return new RoleTranslationDto(); }
	public PropertyTranslationDto createPropertyTranslationDto() { return new PropertyTranslationDto();}
	
}
