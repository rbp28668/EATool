/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.VersionedDto;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "importMapping")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "parserName", "transformPath", "entityTranslations", "relationshipTranslations", "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class ImportMappingDto extends NamedRepositoryItemDto implements VersionedDto{

    private String parserName="XML";
    private String transformPath = null;
    private List<EntityTranslationDto> entityTranslations = new LinkedList<>();
    private List<RelationshipTranslationDto> relationshipTranslations = new LinkedList<>();
    private VersionDto version;
    private String rev;
	/**
	 * @return the parserName
	 */
    @XmlElement
	public String getParserName() {
		return parserName;
	}
	/**
	 * @param parserName the parserName to set
	 */
	public void setParserName(String parserName) {
		this.parserName = parserName;
	}
	
	/**
	 * @return the transformPath
	 */
	@XmlElement
	public String getTransformPath() {
		return transformPath;
	}
	/**
	 * @param transformPath the transformPath to set
	 */
	public void setTransformPath(String transformPath) {
		this.transformPath = transformPath;
	}
	/**
	 * @return the entityTranslations
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "entityTranslations")
	@JsonProperty(value = "entityTranslations")
	public List<EntityTranslationDto> getEntityTranslations() {
		if(entityTranslations == null) {
			entityTranslations = new LinkedList<EntityTranslationDto>();
		}
		return entityTranslations;
	}
	/**
	 * @param entityTranslations the entityTranslations to set
	 */
	public void setEntityTranslations(List<EntityTranslationDto> entityTranslations) {
		this.entityTranslations = entityTranslations;
	}
	/**
	 * @return the relationshipTranslations
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "relationshipTranslations")
	@JsonProperty(value = "relationshipTranslations")
	public List<RelationshipTranslationDto> getRelationshipTranslations() {
		if(relationshipTranslations == null) {
			relationshipTranslations = new LinkedList<RelationshipTranslationDto>();
		}
		return relationshipTranslations;
	}
	/**
	 * @param relationshipTranslations the relationshipTranslations to set
	 */
	public void setRelationshipTranslations(List<RelationshipTranslationDto> relationshipTranslations) {
		this.relationshipTranslations = relationshipTranslations;
	}
	
	/**
	 * @return the version
	 */
	@XmlElement
	@Override
	public VersionDto getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}

	/**
	 * revision information for CouchDB
	 * @return the rev
	 */
	@JsonProperty("_rev")
	@Override
	public String getRev() {
		return rev;
	}

	/**
	 * @param rev the rev to set
	 */
	@Override
	public void setRev(String rev) {
		this.rev = rev;
	}
	

	

}
