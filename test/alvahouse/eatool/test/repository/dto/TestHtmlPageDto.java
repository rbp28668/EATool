/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static alvahouse.eatool.test.Comparator.objectsEqual;


import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.html.HTMLPageDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestHTMLPageDto {

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

	@Test
	void testViaXML() throws Exception{
		HTMLPageDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		HTMLPageDto copy = (HTMLPageDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat( copy, samePropertyValuesAs(dto, "eventMap","version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	@Test
	void testViaJson() throws Exception{
		HTMLPageDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		HTMLPageDto copy = (HTMLPageDto) Serialise.unmarshalFromJson(asJson, HTMLPageDto.class);
		assertThat( copy, samePropertyValuesAs(dto, "eventMap","version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertTrue(objectsEqual(copy,dto));
	}

	/**
	 * @return
	 */
	private HTMLPageDto createDto() {
		HTMLPageDto dto = new HTMLPageDto();
		dto.setKey(new UUID());
		dto.setName("A page");
		dto.setDescription("This is a page");
		dto.setDynamic(true);
		dto.setHtml("<html><head></head><body><h1>Hello World</h1></body></html>");
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
		version.setOriginalVersion(new UUID().asJsonId());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID().asJsonId());
		return version;
	}

}
