/**
 * 
 */
package alvahouse.eatool.test.repository.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.dto.Serialise;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.metamodel.ControlledListTypeDto;
import alvahouse.eatool.repository.dto.metamodel.RegexpCheckedTypeDto;
import alvahouse.eatool.repository.dto.metamodel.TimeSeriesTypeDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestExtensibleTypeDtos {

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
	private RegexpCheckedTypeDto createRegexpTypeDto() {
		RegexpCheckedTypeDto dto = new RegexpCheckedTypeDto();
		dto.setKey(new UUID());
		dto.setName("letters");
		dto.setDescription("one or more letters");
		Pattern pattern = Pattern.compile("[a-z]+");
		dto.setPattern(pattern);
		dto.setDefaultValue("xxxx");
		dto.setFieldLength(42);
		dto.setVersion(createVersion());
		return dto;
	}

	/**
	 * @return
	 */
	private TimeSeriesTypeDto createTimeSeriesTypeDto() {
		TimeSeriesTypeDto dto = new TimeSeriesTypeDto();
		dto.setKey(new UUID());
		dto.setName("phases");
		dto.setDescription("three phases");
		dto.getIntervals().add("first");
		dto.getIntervals().add("second");
		dto.getIntervals().add("third");
		dto.setVersion(createVersion());
		return dto;
	}

	/**
	 * @return
	 */
	private ControlledListTypeDto createControlledListTypeDto() {
		ControlledListTypeDto dto = new ControlledListTypeDto();
		dto.setKey(new UUID());
		dto.setName("three");
		dto.setDescription("one_of_three");
		dto.getValues().add("first_one");
		dto.getValues().add("second_one");
		dto.getValues().add("third_one");
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

	@Test
	void testRegexpTypeAsXML() throws Exception{
		
		RegexpCheckedTypeDto dto = createRegexpTypeDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		RegexpCheckedTypeDto copy = (RegexpCheckedTypeDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dto, "version", "pattern"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertEquals(copy.getPatternJson(), dto.getPatternJson());
	}


	@Test
	void testRegexpTypeAsJson() throws Exception{
		
		RegexpCheckedTypeDto dto = createRegexpTypeDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		RegexpCheckedTypeDto copy = (RegexpCheckedTypeDto) Serialise.unmarshalFromJson(asJson, RegexpCheckedTypeDto.class);

		assertThat( copy, samePropertyValuesAs(dto, "version", "pattern"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		assertEquals(copy.getPatternJson(), dto.getPatternJson());
		
	}

	@Test
	void testTimeSeriesTypeAsXML() throws Exception{
		
		TimeSeriesTypeDto dto = createTimeSeriesTypeDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		TimeSeriesTypeDto copy = (TimeSeriesTypeDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dto, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
	}


	@Test
	void testTimeSeriesTypeAsJson() throws Exception{
		
		TimeSeriesTypeDto dto = createTimeSeriesTypeDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		TimeSeriesTypeDto copy = (TimeSeriesTypeDto) Serialise.unmarshalFromJson(asJson, TimeSeriesTypeDto.class);

		assertThat( copy, samePropertyValuesAs(dto, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		
	}

	@Test
	void testControlledListTypeAsXML() throws Exception{
		
		ControlledListTypeDto dto = createControlledListTypeDto();
		
		String asXml = Serialise.marshalToXML(dto);
		//System.out.println(asXml);
		ControlledListTypeDto copy = (ControlledListTypeDto) Serialise.marshalFromXML(new ByteArrayInputStream(asXml.getBytes(Charset.forName("UTF-8"))));
		
		assertThat( copy, samePropertyValuesAs(dto, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
	}


	@Test
	void testControlledListTypeAsJson() throws Exception{
		
		ControlledListTypeDto dto = createControlledListTypeDto();
		
		String asJson = Serialise.marshalToJSON(dto);
		//System.out.println(asJson);
		ControlledListTypeDto copy = (ControlledListTypeDto) Serialise.unmarshalFromJson(asJson, ControlledListTypeDto.class);

		assertThat( copy, samePropertyValuesAs(dto, "version"));
		assertThat( copy.getVersion(), samePropertyValuesAs(dto.getVersion()));
		
	}

}
