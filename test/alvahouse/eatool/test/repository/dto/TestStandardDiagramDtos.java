/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.graphical.CircularSymbolDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndArrowHeadDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndNullDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorTypeDto;
import alvahouse.eatool.repository.dto.graphical.CubicConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.DiamondSymbolDto;
import alvahouse.eatool.repository.dto.graphical.ImageDisplayDto;
import alvahouse.eatool.repository.dto.graphical.QuadraticConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.RectangularSymbolDto;
import alvahouse.eatool.repository.dto.graphical.RoundedSymbolDto;
import alvahouse.eatool.repository.dto.graphical.StandardDiagramDto;
import alvahouse.eatool.repository.dto.graphical.StandardDiagramTypeDto;
import alvahouse.eatool.repository.dto.graphical.StraightConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.SymbolDto;
import alvahouse.eatool.repository.dto.graphical.SymbolTypeDto;
import alvahouse.eatool.repository.dto.graphical.TextBoxDto;
import alvahouse.eatool.repository.dto.graphical.TextualObjectDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.repository.graphical.standard.CubicConnectorStrategy;
import alvahouse.eatool.repository.graphical.symbols.RectangularSymbol;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestStandardDiagramDtos {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * @return
	 */
	private StandardDiagramDto createDto() {
		StandardDiagramDto dto = new StandardDiagramDto();
		dto.setKey(new UUID());
		dto.setName("Standard diagram");
		dto.setDescription("A Standard diagram");
		dto.setBackColour(Color.cyan);
		dto.setDynamic(false);
		dto.setTypeKey(new UUID());
		
		SymbolDto rounded = createRoundedSymbol();
		SymbolDto rectangular = createRectangularSymbol();
		SymbolDto diamond = createDiamondSymbol();
		SymbolDto circular = createCircularSymbol();
		
		dto.getSymbols().add(rounded);
		dto.getSymbols().add(rectangular);
		dto.getSymbols().add(diamond);
		dto.getSymbols().add(circular);
		
		ConnectorDto straight = createConnector(new StraightConnectorStrategyDto(), rounded, rectangular);
		ConnectorDto quadratic = createConnector(new QuadraticConnectorStrategyDto(), rectangular, diamond);
		ConnectorDto cubic = createConnector(new CubicConnectorStrategyDto(), diamond, circular);
		
		dto.getConnectors().add(straight);
		dto.getConnectors().add(quadratic);
		dto.getConnectors().add(cubic);

		ConnectorDto arrows = createConnector(new StraightConnectorStrategyDto(), rounded, rectangular);
		ConnectorEndDto startEnd = new ConnectorEndArrowHeadDto();
		ConnectorEndDto finishEnd = new ConnectorEndArrowHeadDto();
		startEnd.setKey(new UUID());
		finishEnd.setKey(new UUID());
		arrows.setStartEnd(startEnd);
		arrows.setFinishEnd(finishEnd);
		dto.getConnectors().add(arrows);
		
		TextBoxDto text = new TextBoxDto();
		setTextualObjectProperties(text);
		text.setText("Text Box");
		text.setUrl("http://bogus.com");
		dto.getTextBoxes().add(text);
		
		ImageDisplayDto image = new ImageDisplayDto();
		image.setKey(new UUID());
		image.setImageKey(new UUID());
		image.setHeight(200);
		image.setWidth(300);
		image.setX(42);
		image.setY(73);
		dto.getImages().add(image);
		
		dto.setEventMap(createEventMap());
		dto.setVersion(createVersion());
		return dto;
	}

	/**
	 * @param start
	 * @param finish
	 */
	private ConnectorDto createConnector(ConnectorStrategyDto strategy, SymbolDto start, SymbolDto finish) {
		ConnectorDto dto = new ConnectorDto();
		dto.setKey(new UUID());
		dto.setReferencedItemKey(new UUID());
		dto.setConnectorTypeKey(new UUID());
		dto.setDrawingStrategy(strategy);
		dto.setStartSymbolKey(finish.getKey());
		dto.setFinishSymbolKey(start.getKey());

		ConnectorEndDto startEnd = new ConnectorEndNullDto();
		startEnd.setKey(new UUID());
		dto.setStartEnd(startEnd);
		
		ConnectorEndDto finishEnd = new ConnectorEndNullDto();
		finishEnd.setKey(new UUID());
		dto.setFinishEnd(finishEnd);
		
		return dto;
	}

	/**
	 * @return
	 */
	private SymbolDto createCircularSymbol() {
		CircularSymbolDto dto = new CircularSymbolDto();
		setSymbolProperties(dto);
		return dto;
	}

	/**
	 * @return
	 */
	private SymbolDto createDiamondSymbol() {
		DiamondSymbolDto dto = new DiamondSymbolDto();
		setSymbolProperties(dto);
		return dto;
	}

	/**
	 * @return
	 */
	private SymbolDto createRectangularSymbol() {
		RectangularSymbolDto dto = new RectangularSymbolDto();
		setSymbolProperties(dto);
		return dto;
	}

	/**
	 * @return
	 */
	private SymbolDto createRoundedSymbol() {
		RoundedSymbolDto dto = new RoundedSymbolDto();
		setSymbolProperties(dto);
		return dto;
	}

	/**
	 * @param dto
	 */
	private void setSymbolProperties(SymbolDto dto) {
		setTextualObjectProperties(dto);
		dto.setSymbolTypeKey(new UUID());
		dto.setReferencedItemKey(new UUID());
	}

	private void setTextualObjectProperties(TextualObjectDto dto) {
		dto.setKey(new UUID());
		dto.setBackColour(Color.darkGray);
		dto.setBorderColour(Color.black);
		dto.setTextColour(Color.red);
		dto.setFont(new Font("SansSerif", Font.PLAIN,10));
		dto.setWidth(300);
		dto.setHeight(200);
		dto.setX(42);
		dto.setHeight(73);
	}
	
	
	/**
	 * @return
	 */
	private EventMapDto createEventMap() {
		EventMapDto dto = new EventMapDto();
		
		EventMapDto.EventMapHandlerDto onDisplay = new EventMapDto.EventMapHandlerDto();
		onDisplay.setEvent("OnDisplay");
		onDisplay.setHandler(new UUID());
		dto.getHandlers().add(onDisplay);
		
		EventMapDto.EventMapHandlerDto onClose = new EventMapDto.EventMapHandlerDto();
		onClose.setEvent("OnClose");
		onClose.setHandler(new UUID());
		dto.getHandlers().add(onClose);

		dto.setVersion(createVersion());
		return dto;
	}

	/**
	 * @return
	 */
	private VersionDto createVersion() {
		VersionDto version = new VersionDto();
		version.setCreateDate(new Date());
		version.setCreateUser("fred");
		version.setOriginalVersion(new UUID());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID());
		return version;
	}
	
	/**
	 * @return
	 */
	private StandardDiagramTypeDto createTypeDto() {
		StandardDiagramTypeDto dto = new StandardDiagramTypeDto();
		dto.setKey(new UUID());
		dto.setName("SDT");
		dto.setDescription("A standard diagram type");
		dto.setFamilyKey(new UUID());
		
		SymbolTypeDto stdto = new SymbolTypeDto();
		stdto.setKey(new UUID());
		stdto.setName("symbol type");
		stdto.setDescription("A symbol type");
		stdto.setSymbolClass(RectangularSymbol.class.getName());
		stdto.setMetaEntityKey(new UUID());
		stdto.setTextColour(Color.black);
		stdto.setBackColour(Color.white);
		stdto.setBorderColour(Color.red);
		stdto.setFont(new Font("SansSerif", Font.PLAIN,10));
		dto.getSymbolTypes().add(stdto);
		
		ConnectorTypeDto ctdto = new ConnectorTypeDto();
		ctdto.setKey(new UUID());
		ctdto.setName("connector type");
		ctdto.setDescription("A connector type");
		ctdto.setConnectorClass(CubicConnectorStrategy.class.getName());
		ctdto.setMetaRelationshipKey(new UUID());
		dto.getConnectorTypes().add(ctdto);
		
		
		dto.setEventMap(createEventMap());
		dto.setVersion(createVersion());
		return dto;
	}


	@Test
	void testStandardDiagramViaXML() throws Exception{
		StandardDiagramDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		StandardDiagramDto copy = (StandardDiagramDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


	@Test
	void testStandardDiagramViaJson() throws Exception{
		StandardDiagramDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		System.out.println(asJson);
		StandardDiagramDto copy = (StandardDiagramDto) Serialise.unmarshalFromJson(asJson, StandardDiagramDto.class);
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	@Test
	void testStandardDiagramTypeViaXML() throws Exception{
		StandardDiagramTypeDto dto = createTypeDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		StandardDiagramTypeDto copy = (StandardDiagramTypeDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


	@Test
	void testStandardDiagramTypeViaJson() throws Exception{
		StandardDiagramTypeDto dto = createTypeDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		System.out.println(asJson);
		StandardDiagramTypeDto copy = (StandardDiagramTypeDto) Serialise.unmarshalFromJson(asJson, StandardDiagramTypeDto.class);
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


}
