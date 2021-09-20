/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.scripting.ScriptDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestScriptDto {

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
		ScriptDto dto = createDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		ScriptDto copy = (ScriptDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		assertThat( copy, samePropertyValuesAs(dto, "version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
	}

	@Test
	void testViaJson() throws Exception{
		ScriptDto dto = createDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		ScriptDto copy = (ScriptDto) Serialise.unmarshalFromJson(asJson, ScriptDto.class);
		assertThat( copy, samePropertyValuesAs(dto, "version" ));
		assertThat(copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
	}

	/**
	 * @return
	 */
	private ScriptDto createDto() {
		ScriptDto dto = new ScriptDto();
		dto.setKey(new UUID());
		dto.setName("A script");
		dto.setDescription("This is a script");
		dto.setLanguage("javascript");
		dto.setScript("var thing=42; \r\n var thing2=\"fortytwo\"; var thing3=4>5;");
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
