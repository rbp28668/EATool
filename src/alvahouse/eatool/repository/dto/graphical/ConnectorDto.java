/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.RepositoryItemDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "connector")
@XmlAccessorType(XmlAccessType.NONE)
public class ConnectorDto extends RepositoryItemDto {

	private UUID connectorTypeKey;
	private UUID referencedItemKey;
	private UUID startSymbolKey;
	private UUID finishSymbolKey;
	private ConnectorEndDto startEnd;
	private ConnectorEndDto finishEnd;
	private ConnectorStrategyDto drawingStrategy;
	

	/**
	 * 
	 */
	public ConnectorDto() {
	}


	/**
	 * @return the connectorTypeKey
	 */
	@JsonIgnore
	public UUID getConnectorTypeKey() {
		return connectorTypeKey;
	}


	/**
	 * @param connectorTypeKey the connectorTypeKey to set
	 */
	public void setConnectorTypeKey(UUID connectorTypeKey) {
		this.connectorTypeKey = connectorTypeKey;
	}

	/**
	 * @return the connectorTypeKey
	 */
	@XmlElement(name="type")
	@JsonProperty("type")
	public String getConnectorTypeKeyJson() {
		return connectorTypeKey.asJsonId();
	}


	/**
	 * @param connectorTypeKey the connectorTypeKey to set
	 */
	public void setConnectorTypeKeyJson(String connectorTypeKey) {
		this.connectorTypeKey = UUID.fromJsonId(connectorTypeKey);
	}


	/**
	 * @return the referencedItemKey
	 */
	@JsonIgnore
	public UUID getReferencedItemKey() {
		return referencedItemKey;
	}


	/**
	 * @param referencedItemKey the referencedItemKey to set
	 */
	public void setReferencedItemKey(UUID referencedItemKey) {
		this.referencedItemKey = referencedItemKey;
	}

	/**
	 * @return the referencedItemKey
	 */
	@XmlElement(name="referencedItem")
	@JsonProperty("referencedItem")
	public String getReferencedItemKeyJson() {
		return referencedItemKey.asJsonId();
	}


	/**
	 * @param referencedItemKey the referencedItemKey to set
	 */
	public void setReferencedItemKeyJson(String referencedItemKey) {
		this.referencedItemKey = UUID.fromJsonId(referencedItemKey);
	}


	/**
	 * @return the startSymbolKey
	 */
	@JsonIgnore
	public UUID getStartSymbolKey() {
		return startSymbolKey;
	}


	/**
	 * @param startSymbolKey the startSymbolKey to set
	 */
	public void setStartSymbolKey(UUID startSymbolKey) {
		this.startSymbolKey = startSymbolKey;
	}

	/**
	 * @return the startSymbolKey
	 */
	@XmlElement(name="startSymbol")
	@JsonProperty("startSymbol")
	public String getStartSymbolKeyJson() {
		return startSymbolKey.asJsonId();
	}


	/**
	 * @param startSymbolKey the startSymbolKey to set
	 */
	public void setStartSymbolKeyJson(String startSymbolKey) {
		this.startSymbolKey = UUID.fromJsonId(startSymbolKey);
	}

	/**
	 * @return the finishSymbolKey
	 */
	@JsonIgnore
	public UUID getFinishSymbolKey() {
		return finishSymbolKey;
	}


	/**
	 * @param finishSymbolKey the finishSymbolKey to set
	 */
	public void setFinishSymbolKey(UUID finishSymbolKey) {
		this.finishSymbolKey = finishSymbolKey;
	}

	/**
	 * @return the finishSymbolKey
	 */
	@XmlElement(name="finishSymbol")
	@JsonProperty("finishSymbol")
	public String getFinishSymbolKeyJson() {
		return finishSymbolKey.asJsonId();
	}


	/**
	 * @param finishSymbolKey the finishSymbolKey to set
	 */
	public void setFinishSymbolKeyJson(String finishSymbolKey) {
		this.finishSymbolKey = UUID.fromJsonId(finishSymbolKey);
	}


	/**
	 * @return the startEnd
	 */
	@XmlElement
	public ConnectorEndDto getStartEnd() {
		return startEnd;
	}


	/**
	 * @param startEnd the startEnd to set
	 */
	public void setStartEnd(ConnectorEndDto startEnd) {
		this.startEnd = startEnd;
	}


	/**
	 * @return the finishEnd
	 */
	@XmlElement
	public ConnectorEndDto getFinishEnd() {
		return finishEnd;
	}


	/**
	 * @param finishEnd the finishEnd to set
	 */
	public void setFinishEnd(ConnectorEndDto finishEnd) {
		this.finishEnd = finishEnd;
	}


	/**
	 * @return the drawingStrategy
	 */
	@XmlElement
	public ConnectorStrategyDto getDrawingStrategy() {
		return drawingStrategy;
	}


	/**
	 * @param drawingStrategy the drawingStrategy to set
	 */
	public void setDrawingStrategy(ConnectorStrategyDto drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
	}

	
}
