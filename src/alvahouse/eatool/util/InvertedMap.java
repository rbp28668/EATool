/*
 * InvertedMap.java
 * Created on 31-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.util;

import java.util.HashMap;
import java.util.Map;

/**
 * InvertedMap is a variant on a HashMap for inverse lookup where
 * keys become values and vice-versa.
 * @author Bruce.Porteous
 *
 */
public class InvertedMap<K,V> extends HashMap<K,V> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param m
	 */
	public InvertedMap(Map<V,K> m) {
		super();
		invertMap(m);
	}
	
	/**
	 * Do the map inversion.
	 * @param source
	 */
	private void invertMap(Map<V,K> source){
		for(Map.Entry<V,K> entry : source.entrySet()){
			put(entry.getValue(), entry.getKey());
		}
	}


}
