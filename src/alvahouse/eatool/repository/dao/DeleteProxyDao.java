/**
 * 
 */
package alvahouse.eatool.repository.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "deleteProxy")
@XmlAccessorType(XmlAccessType.NONE)

public class DeleteProxyDao {
	private String itemType;
	private String name;
	private UUID itemKey;
	/**
	 * @return the itemType
	 */
	@XmlElement(required=true)
	public String getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	
	/**
	 * @return the name
	 */
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
	 * @return the itemKey
	 */
	@XmlElement(required=true)
	public UUID getItemKey() {
		return itemKey;
	}
	/**
	 * @param itemKey the itemKey to set
	 */
	public void setItemKey(UUID itemKey) {
		this.itemKey = itemKey;
	}
	
	
}
