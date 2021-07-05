/**
 * 
 */
package alvahouse.eatool.repository.dto.mapping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "exportMapping")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "components", "transformPath",  "version"})
public class ExportMappingDto extends NamedRepositoryItemDto {

    private int components = 0;
    private String transformPath = null;
    private VersionDto version = null;

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
	public VersionDto getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}
	
	

}
