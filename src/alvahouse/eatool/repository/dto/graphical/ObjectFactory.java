/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * JAXB Object factory to allow JAXB to create concrete classes.
 * @author bruce_porteous
 *
 */
@XmlRegistry
public class ObjectFactory {
	
	public ObjectFactory() {
	}
	
	public FontDto createFontDto() { return new FontDto(); }
	public ColourDto createColourDto() { return new ColourDto(); }
	
	public DiamondSymbolDto createDiamondSymbolDto() { return new DiamondSymbolDto(); }
	public RectangularSymbolDto createRectangularSymbolDto() { return new RectangularSymbolDto(); }
	public RoundedSymbolDto createRoundedSymbolDto() { return new RoundedSymbolDto(); }
	public CircularSymbolDto createCircularSymbolDto() { return new CircularSymbolDto(); }

	public ConnectorDto createConnectorDto() { return new ConnectorDto(); }
	public ConnectorEndNullDto createConnectorEndNullDto() { return new ConnectorEndNullDto();}
	public ConnectorEndArrowHeadDto createConnectorEndArrowHeadDto() { return new ConnectorEndArrowHeadDto(); }
	
	public StraightConnectorStrategyDto createStraightConnectorStrategyDto() { return new StraightConnectorStrategyDto(); }
	public QuadraticConnectorStrategyDto createQuadraticConnectorStrategyDto() { return new QuadraticConnectorStrategyDto(); }
	public CubicConnectorStrategyDto createCubicConnectorStrategyDto() { return new CubicConnectorStrategyDto(); }
	
	public ImageDisplayDto createImageDisplayDto() { return new ImageDisplayDto(); }
	
}
