/**
 * 
 */
package alvahouse.eatool.repository.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "deleteDependencies")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteDependenciesListDto {

	private List<DeleteProxyDto> dependencies = new LinkedList<>();
	
	/**
	 * @return the dependencies
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "dependencies")
	@JsonProperty(value = "dependencies")
	public List<DeleteProxyDto> getProperties() {
		if (dependencies == null) {
			dependencies = new LinkedList<DeleteProxyDto>();
		}
		return dependencies;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setProperties(List<DeleteProxyDto> dependencies) {
		this.dependencies = dependencies;
	}
}
