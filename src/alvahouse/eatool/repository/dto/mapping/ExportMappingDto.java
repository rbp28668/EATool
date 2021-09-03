/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name = "exportMapping")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "components", "transformPath",  "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class ExportMappingDto extends NamedRepositoryItemDto implements VersionedDto{

    private int components = 0;
    private String transformPath = null;
    private VersionDto version = null;
    private String rev;
    
	/**
	 * 
	 */
	public ExportMappingDto() {
	}

	/**
	 * @return the components
	 */
	@XmlElement
	public int getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(int components) {
		this.components = components;
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
