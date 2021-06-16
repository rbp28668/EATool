/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dao.NamedRepositoryItemDao;

/**
 * @author bruce_porteous
 *
 */
public abstract class MetaPropertyContainerDao extends NamedRepositoryItemDao {

	private List<MetaPropertyDao> properties;

	/**
	 * @return the properties
	 */
	@XmlElementRef
	@XmlElementWrapper(name = "properties")
	@JsonProperty(value = "properties")
	public List<MetaPropertyDao> getProperties() {
		if (properties == null) {
			properties = new LinkedList<MetaPropertyDao>();
		}
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<MetaPropertyDao> properties) {
		this.properties = properties;
	}
}
