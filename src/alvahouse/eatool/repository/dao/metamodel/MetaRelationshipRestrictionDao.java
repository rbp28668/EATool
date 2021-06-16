/**
 * 
 */
package alvahouse.eatool.repository.dao.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "restriction")
@XmlAccessorType(XmlAccessType.NONE)

public class MetaRelationshipRestrictionDao {
	
	private String name;

	/**
	 * @return the name
	 */
	@XmlElement(required=true)
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
