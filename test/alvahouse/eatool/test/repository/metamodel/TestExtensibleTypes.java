/**
 * 
 */
package alvahouse.eatool.test.repository.metamodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static alvahouse.eatool.test.Comparator.objectsEqual;

import java.util.Collection;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import alvahouse.eatool.repository.metamodel.types.ControlledListType;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypeList;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.RegexpCheckedType;
import alvahouse.eatool.repository.metamodel.types.TimeSeriesType;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.memory.MetaModelPersistenceMemory;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestExtensibleTypes {

	MetaModelPersistence persistence;
	ExtensibleTypes types;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
		persistence = new MetaModelPersistenceMemory();
		types = new ExtensibleTypes(persistence);
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
	private RegexpCheckedType createRegexpType() {
		RegexpCheckedType type = new RegexpCheckedType();
		type.setKey(new UUID());
		type.setName("letters");
		type.setDescription("one or more letters");
		type.setPattern("[a-z]+");
		type.setDefaultValue("xxxx");
		type.setFieldLength(42);
		setVersionInfo(type.getVersion());
		return type;
	}

	/**
	 * @return
	 */
	private TimeSeriesType createTimeSeriesType() {
		TimeSeriesType type = new TimeSeriesType();
		type.setKey(new UUID());
		type.setName("phases");
		type.setDescription("three phases");
		type.add("first");
		type.add("second");
		type.add("third");
		setVersionInfo(type.getVersion());
		return type;
	}

	/**
	 * @return
	 */
	private ControlledListType createControlledListType() {
		ControlledListType type = new ControlledListType();
		type.setKey(new UUID());
		type.setName("three");
		type.setDescription("one_of_three");
		type.add("first_one");
		type.add("second_one");
		type.add("third_one");
		setVersionInfo(type.getVersion());
		return type;
	}

	/**
	 * @return
	 */
	private void setVersionInfo(Version version) {
		version.setCreateDate(new Date());
		version.setCreateUser("fred");
		version.setOriginalVersion(new UUID());
		version.setModifyDate(new Date());
		version.setModifyUser("jim");
		version.setVersion(new UUID());
	}

	@Test
	void testEmpty() throws Exception{
		assertEquals(3,types.getTypeCount());
		
		Collection<ExtensibleTypeList> etls = types.getTypes();
		assertEquals(3, etls.size());
		for(ExtensibleTypeList etl : etls) {
			assertEquals(0, etl.getTypes().size());
		}
	}

	@Test
	void testCounts() throws Exception{
		types.addType(createControlledListType());
		types.addType(createRegexpType());
		types.addType(createTimeSeriesType());
		types.addType(createTimeSeriesType());
		
		assertEquals(3,types.getTypeCount());
		
		assertEquals(1,types.lookupList(ControlledListType.class).getTypes().size());
		assertEquals(1,types.lookupList(RegexpCheckedType.class).getTypes().size());
		assertEquals(2,types.lookupList(TimeSeriesType.class).getTypes().size());
	}
	
	@Test
	void testSaveRestore() throws Exception{
		ControlledListType clt = createControlledListType();
		RegexpCheckedType rct = createRegexpType();
		TimeSeriesType tst = createTimeSeriesType();
		
		types.addType(clt);
		types.addType(rct);
		types.addType(tst);
	
		assertTrue(objectsEqual(clt,clt));

		
		types.lookupList(ControlledListType.class).getTypes().forEach( copy -> {
			assertTrue(objectsEqual(clt,copy));
			assertThat( copy.getVersion(), samePropertyValuesAs(clt.getVersion()));
		});
		
		types.lookupList(RegexpCheckedType.class).getTypes().forEach( copy -> {
			assertTrue(objectsEqual(rct,copy));
			assertThat( copy.getVersion(), samePropertyValuesAs(rct.getVersion()));
		});

		types.lookupList(TimeSeriesType.class).getTypes().forEach( copy -> {
			assertTrue(objectsEqual(tst,copy));
			assertThat( copy.getVersion(), samePropertyValuesAs(tst.getVersion()));
		});
		
		
	}

}
