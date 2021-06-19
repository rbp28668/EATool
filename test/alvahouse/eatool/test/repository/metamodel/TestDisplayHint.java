/**
 * 
 */
package alvahouse.eatool.test.repository.metamodel;

import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.collection.IsIn.in;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
class TestDisplayHint {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		MetaEntity target = new MetaEntity(new UUID());
		
		MetaProperty mp1 = new MetaProperty(new UUID());
		MetaProperty mp2 = new MetaProperty(new UUID());
		
		MetaEntityDisplayHint hint = new MetaEntityDisplayHint(target);
		hint.addPropertyKey(mp1.getKey());
		hint.addPropertyKey(mp2.getKey());
		
		assertThat(mp1.getKey(), in(hint.getKeys()));
		assertThat(mp2.getKey(), in(hint.getKeys()));
	}

}
