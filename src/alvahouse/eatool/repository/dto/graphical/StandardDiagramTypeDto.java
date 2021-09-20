/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "standardDiagramType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"symbolTypes", "connectorTypes"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class StandardDiagramTypeDto extends DiagramTypeDto {

	private List<SymbolTypeDto> symbolTypes; 
	private List<ConnectorTypeDto> connectorTypes;

	/**
	 * 
	 */
	public StandardDiagramTypeDto() {
	}

	/**
	 * @return the symbolTypes
	 */
	@XmlElementWrapper(name = "symbolTypes")
	@XmlElementRef
	public List<SymbolTypeDto> getSymbolTypes() {
		if(symbolTypes == null) {
			symbolTypes = new LinkedList<>();
		}
		return symbolTypes;
	}

	/**
	 * @param symbolTypes the symbolTypes to set
	 */
	public void setSymbolTypes(List<SymbolTypeDto> symbolTypes) {
		this.symbolTypes = symbolTypes;
	}

	/**
	 * @return the connectorTypes
	 */
	@XmlElementWrapper(name = "connectorTypes")
	@XmlElementRef
	public List<ConnectorTypeDto> getConnectorTypes() {
		if(connectorTypes == null) {
			connectorTypes = new LinkedList<>();
		}
		return connectorTypes;
	}

	/**
	 * @param connectorTypes the connectorTypes to set
	 */
	public void setConnectorTypes(List<ConnectorTypeDto> connectorTypes) {
		this.connectorTypes = connectorTypes;
	}

	
	
}
