/**
 * 
 */
package alvahouse.eatool.repository.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author bruce_porteous
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class NamedRepositoryItemDto extends RepositoryItemDto implements Comparable<NamedRepositoryItemDto>{
	private String name;
	private String description;
	/**
	 * @return the name
	 */
	@XmlElement
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	@XmlElement
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NamedRepositoryItemDto o) {
		if(getName() == null || o.getName() == null) {
			return getKey().compareTo(o.getKey());
		}
		if(getName().isEmpty() && o.getName().isEmpty()){
			return getKey().compareTo(o.getKey());
		}
		return getName().compareTo(o.getName());
	}

}
