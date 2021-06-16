/**
 * 
 */
package alvahouse.eatool.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class TestUUID {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		UUID.initialise(UUID.findMACAddress(), null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/*
	 * Given a null UUID when I convert to a JSON ID then I expect 22 characters of a
	 */
	@Test
	public void testZero() {
		UUID empty = new UUID("00000000-0000-0000-0000-000000000000");
		String asId = empty.asJsonId();
		assertEquals(asId, "aaaaaaaaaaaaaaaaaaaaaa");
	}
	

	/*
	 * Given any UUID when I convert to a JSON ID then I expect the result to be 22 characters long. 
	 * Note that this is a direct result of having 64 valid characters for a JSON identifier and a 128 bit UUID.
	 */
	@Test
	public void testValidJsonIdLength() {
		UUID uuid = new UUID("f05b4a20-a576-01fa-8000-9cb6d0dc3055");
		String asId = uuid.asJsonId();
		assertEquals(asId.length(), 22);
	}

	/*
	 * Given any UUID when I convert to a JSON ID then I expect the result to be a valid JSON ID. 
	 */
	@Test
	public void testValidJsonId() {
		UUID uuid = new UUID();
		String asId = uuid.asJsonId();
		assertTrue(Character.isJavaIdentifierStart(asId.charAt(0)));
		for(int idx=1; idx<asId.length(); ++idx) {
			assertTrue(Character.isJavaIdentifierPart(asId.charAt(idx)));
		}
	}

	/*
	 * Given any UUID when I convert to a JSON ID and back again then I expect the two to be equal. 
	 */
	@Test
	public void testJsonIdRoundTrip() {
		UUID uuid = new UUID("f05b4a20-a576-01fa-8000-9cb6d0dc3055");
		String asId = uuid.asJsonId();
		UUID copy = UUID.fromJsonId(asId);
		assertEquals(uuid, copy);
	}

}
