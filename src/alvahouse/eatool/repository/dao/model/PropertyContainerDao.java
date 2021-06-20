/**
 * 
 */
package alvahouse.eatool.repository.dao.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dao.RepositoryItemDao;

/**
 * @author bruce_porteous
 *
 */
public abstract class PropertyContainerDao extends RepositoryItemDao {

	private List<PropertyDao> properties;

	/**
	 * @return the properties
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "properties")
	@JsonProperty(value = "properties")
	public List<PropertyDao> getProperties() {
		if (properties == null) {
			properties = new LinkedList<PropertyDao>();
		}
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<PropertyDao> properties) {
		this.properties = properties;
	}

}
