/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "connectorType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"connectorClass", "metaRelationshipKeyJson"})
public class ConnectorTypeDto extends NamedRepositoryItemDto {

	private String connectorClass;
	private UUID metaRelationshipKey;

	/**
	 * 
	 */
	public ConnectorTypeDto() {
	}

	/**
	 * @return the connectorClass
	 */
	@XmlElement
	public String getConnectorClass() {
		return connectorClass;
	}

	/**
	 * @param connectorClass the connectorClass to set
	 */
	public void setConnectorClass(String connectorClass) {
		this.connectorClass = connectorClass;
	}

	/**
	 * @return the metaRelationshipKey
	 */
	@JsonIgnore
	public UUID getMetaRelationshipKey() {
		return metaRelationshipKey;
	}

	/**
	 * @param metaRelationshipKey the metaRelationshipKey to set
	 */
	public void setMetaRelationshipKey(UUID metaRelationshipKey) {
		this.metaRelationshipKey = metaRelationshipKey;
	}

	/**
	 * @return the metaRelationshipKey
	 */
	@JsonProperty("metaRelationshipKey")
	@XmlElement(name="metaRelationshipKey")
	public String getMetaRelationshipKeyJson() {
		return metaRelationshipKey.asJsonId();
	}

	/**
	 * @param metaRelationshipKey the metaRelationshipKey to set
	 */
	public void setMetaRelationshipKeyJson(String metaRelationshipKey) {
		this.metaRelationshipKey = UUID.fromJsonId(metaRelationshipKey);
	}

	
}
