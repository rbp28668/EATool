package alvahouse.eatool.test;

import static org.junit.Assert.fail;

import java.lang.reflect.Array;

/**
 * 
 */


import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Comparator {

	public static boolean objectsEqual(Object obj1, Object obj2) {
		return objectsEqual(obj1,obj2,"");
	}

	public static boolean objectsEqual(Object obj1, Object obj2, String where) {
		
		// If both null then must be equals
		if(obj1 == null && obj2 == null) return true;
		
		// but then if not both null but one is then can't be equal.
		if(obj1 == null || obj2 == null) {
			fail("Objects differing null state at " + where);
			return false;
		}
		
		// Primitive classes can just be compared directly
		if(obj1.getClass().isPrimitive() && obj2.getClass().isPrimitive()) {
			if(!obj1.equals(obj2)) {
				fail( obj1.toString() + " != " + obj2.toString() + " at " + where);
				return false;
			}
			return true;
		}

		// Treat string as primitive
		if(obj1.getClass() == String.class && obj2.getClass() == String.class) {
			if(!obj1.equals(obj2)) {
				fail( obj1.toString() + " != " + obj2.toString() + " at " + where);
				return false;
			}
			return true;
		}
		
		// Array?
		if(obj1.getClass().isArray() && obj2.getClass().isArray()) {
			return compareArrays(obj1, obj2, where);
		}

		// Do lists, maps, sets and collections so that we check the semantics and contents
		// but without worrying about the exact type of a list.  E.g. treat ArrayList
		// as equivalent to LinkedList.
		if(List.class.isAssignableFrom(obj1.getClass()) &&
				List.class.isAssignableFrom(obj2.getClass())) {
			return compareLists(obj1, obj2, where);
		}

		if(Map.class.isAssignableFrom(obj1.getClass())
				&& Map.class.isAssignableFrom(obj2.getClass())) {
			return compareMaps(obj1, obj2, where);
		}

		if(Collection.class.isAssignableFrom(obj1.getClass())) {
			return compareCollections(obj1, obj2, where);
		}
		

		
		
		// Not a primitive type, array, collection or map
		// Must be an object!
		// Clearly different objects if not same class.
		if(! obj1.getClass().equals(obj2.getClass())) {
			fail("Objects have different classes: " + obj1.getClass().getName() +
					", " + obj2.getClass().getName() + 
					" at " + where);
			return false;
		}

		// So run through getters checking values for equality.
		Object[] nullParams = new Object[0];
		
		try {
			for(Method m : obj1.getClass().getMethods()) {
				String name = m.getName();
				if(name.startsWith("get") && m.getParameterCount() == 0) {
					name = name.substring(3);
					name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

					// Ignore getClass as recurses to infinity
					if(name.equals("class")) {
						continue;
					}
					
					Object prop1 = m.invoke(obj1, nullParams);
					Object prop2 = m.invoke(obj2, nullParams);
					
					if(!objectsEqual(prop1, prop2, where + "," + name)) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			fail("Unable to inspect object due to " + e.getMessage() + " at " + where);
			return false;
		}
		
		return true;

	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param where
	 * @return
	 */
	private static boolean compareCollections(Object obj1, Object obj2, String where) {
		Collection<Object> c1 = (Collection<Object>) obj1;
		Collection<Object> c2 = (Collection<Object>) obj2;
		Iterator<Object> iter1 = c1.iterator();
		Iterator<Object> iter2 = c2.iterator();
		while(iter1.hasNext() && iter2.hasNext()) {
			Object o1 = iter1.next();
			Object o2 = iter2.next();
			if(!objectsEqual(o1,o2, where + " collection ")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param where
	 * @return
	 */
	private static boolean compareMaps(Object obj1, Object obj2, String where) {
		Map<Object,Object> m1 = (Map<Object,Object>) obj1;
		Map<Object,Object> m2 = (Map<Object,Object>) obj2;
		
		if(m1.size() != m2.size()) {
			fail("Maps different sizes at " + where);
			return false;
		}
		
		for(Object key : m1.keySet()) {
			Object o1 = m1.get(key);
			Object o2 = m2.get(key);
			if(!objectsEqual(o1,o2, where + " in map for key " + key.toString())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param where
	 * @return
	 */
	private static boolean compareLists(Object obj1, Object obj2, String where) {
		List<Object> c1 = (List<Object>) obj1;
		List<Object> c2 = (List<Object>) obj2;
		Iterator<Object> iter1 = c1.iterator();
		Iterator<Object> iter2 = c2.iterator();
		while(iter1.hasNext() && iter2.hasNext()) {
			Object o1 = iter1.next();
			Object o2 = iter2.next();
			if(!objectsEqual(o1,o2, where + " list entry ")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param where
	 */
	private static boolean compareArrays(Object obj1, Object obj2, String where) {
		
		int len1 = Array.getLength(obj1);
		if( len1 != Array.getLength(obj2)) {
			fail("Arrays have different lengths");
			return false;
		}
		for(int i=0; i<len1; ++i) {
			if(!objectsEqual( Array.get(obj1, i), Array.get(obj2, i), where + "[" + i + "]")){
				return false;
			}
		}
		return true;
	}

}