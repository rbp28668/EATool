/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static alvahouse.eatool.test.Comparator.objectsEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.graphical.TimeDiagramDto;
import alvahouse.eatool.repository.dto.graphical.TimeDiagramEntryDto;
import alvahouse.eatool.repository.dto.graphical.TimeDiagramTypeDto;
import alvahouse.eatool.repository.dto.graphical.TypeEntryDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestTimeDiagramDtos {

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
	private TimeDiagramDto createDto() {
		TimeDiagramDto dto = new TimeDiagramDto();
		dto.setKey(new UUID());
		dto.setName("Time diagram");
		dto.setDescription("A Time diagram");
		dto.setBackColour(Color.cyan);
		dto.setDynamic(false);
		dto.setTypeKey(new UUID());
		
		TimeDiagramEntryDto entry = new TimeDiagramEntryDto();
		entry.setEntityKey(new UUID());
		entry.setMetaPropertyKey(new UUID());
		dto.getProperties().add(entry);
		
		dto.setEventMap(createEventMap());
		dto.setVersion(createVersion());
		return dto;
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
	private TimeDiagramTypeDto createTypeDto() {
		TimeDiagramTypeDto dto = new TimeDiagramTypeDto();
		dto.setKey(new UUID());
		dto.setName("TDT");
		dto.setDescription("A time diagram type");
		dto.setFamilyKey(new UUID());
		
		TypeEntryDto entry = new TypeEntryDto();
		entry.setTargetTypeKey(new UUID());
		entry.setTargetPropertyKey(new UUID());
		entry.getColours().add(Color.red);
		entry.getColours().add(Color.green);
		entry.getColours().add(Color.blue);
		dto.getTargets().add(entry);
		
		dto.setEventMap(createEventMap());
		dto.setVersion(createVersion());
		return dto;
	}


	@Test
	void testTimeDiagramViaXML() throws Exception{
		TimeDiagramDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		TimeDiagramDto copy = (TimeDiagramDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


	@Test
	void testTimeDiagramViaJson() throws Exception{
		TimeDiagramDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		TimeDiagramDto copy = (TimeDiagramDto) Serialise.unmarshalFromJson(asJson, TimeDiagramDto.class);
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	@Test
	void testTimeDiagramTypeViaXML() throws Exception{
		TimeDiagramTypeDto dto = createTypeDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		TimeDiagramTypeDto copy = (TimeDiagramTypeDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


	@Test
	void testTimeDiagramTypeViaJson() throws Exception{
		TimeDiagramTypeDto dto = createTypeDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		TimeDiagramTypeDto copy = (TimeDiagramTypeDto) Serialise.unmarshalFromJson(asJson, TimeDiagramTypeDto.class);
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}


}
