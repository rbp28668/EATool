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

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "standardDiagram")
@XmlAccessorType(XmlAccessType.NONE)
public class StandardDiagramDto extends DiagramDto {

	private List<SymbolDto> symbols = new LinkedList<>();
	private List<ConnectorDto> connectors = new LinkedList<>();
	private List<TextBoxDto> textBoxes = new LinkedList<>();
	private List<ImageDisplayDto> images = new LinkedList<>();

	/**
	 * 
	 */
	public StandardDiagramDto() {
	}

	/**
	 * @return the symbols
	 */
	@XmlElementWrapper(name="symbols")
	@XmlElementRef
	public List<SymbolDto> getSymbols() {
		if(symbols == null) {
			symbols = new LinkedList<SymbolDto>();
		}
		return symbols;
	}

	/**
	 * @param symbols the symbols to set
	 */
	public void setSymbols(List<SymbolDto> symbols) {
		this.symbols = symbols;
	}

	/**
	 * @return the connectors
	 */
	@XmlElementWrapper(name="connectors")
	@XmlElementRef
	public List<ConnectorDto> getConnectors() {
		if(connectors == null) {
			connectors = new LinkedList<ConnectorDto>();
		}
		return connectors;
	}

	/**
	 * @param connectors the connectors to set
	 */
	public void setConnectors(List<ConnectorDto> connectors) {
		this.connectors = connectors;
	}

	/**
	 * @return the textBoxes
	 */
	@XmlElementWrapper(name="textBoxes")
	@XmlElementRef
	public List<TextBoxDto> getTextBoxes() {
		if(textBoxes == null) {
			textBoxes = new LinkedList<TextBoxDto>();
		}
		return textBoxes;
	}

	/**
	 * @param textBoxes the textBoxes to set
	 */
	public void setTextBoxes(List<TextBoxDto> textBoxes) {
		this.textBoxes = textBoxes;
	}

	/**
	 * @return the images
	 */
	@XmlElementWrapper(name="images")
	@XmlElementRef
	public List<ImageDisplayDto> getImages() {
		if(images == null) {
			images = new LinkedList<ImageDisplayDto>();
		}
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(List<ImageDisplayDto> images) {
		this.images = images;
	}

	
	
}
