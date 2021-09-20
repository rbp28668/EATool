/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "displayHint")
@XmlAccessorType(XmlAccessType.NONE)

public class MetaEntityDisplayHintDto {
	private ArrayList<UUID> keys;
	/**
	 * @return the keys
	 */
	@JsonIgnore
	public List<UUID> getKeys() {
		if(keys == null) {
			keys = new ArrayList<UUID>();
		}
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeys(List<UUID> keys) {
		this.keys = new ArrayList<UUID>(keys.size());
		this.keys.addAll(keys);
	}

	/**
	 * @return the keys
	 */
	@XmlElementWrapper(name = "keys")
	@XmlElement( name = "key")
	@JsonProperty("keys")
	public KeyWrapper getKeysJson() {
		if(keys == null) return null;
		return new KeyWrapper(keys);
	}

	/**
	 * @param keys the keys to set
	 */
	public void setKeysJson(KeyWrapper keys) {
		if(keys == null) {
			this.keys = null;
			return;
		}
		
		this.keys = keys.getKeys();
	}

	/**
	 * A list class that represents the list of UUIDs as a list of
	 * string with appropriate translations.  This is so JAXB
	 * can treat this as a normal List<String>.
	 * @author bruce_porteous
	 *
	 */
	private static class KeyWrapper extends AbstractList<String> {
		private ArrayList<UUID> keys;

		@SuppressWarnings("unused")
		public KeyWrapper() {
			keys = new ArrayList<>();
		}
		
		public KeyWrapper(ArrayList<UUID> keys) {
			this.keys = keys;
		}
		
		ArrayList<UUID>  getKeys() {
			return keys;
		}
		
		/* (non-Javadoc)
		 * @see java.util.AbstractList#get(int)
		 */
		@Override
		public String get(int index) {
			return keys.get(index).asJsonId();
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractList#set(int, java.lang.Object)
		 */
		@Override
		public String set(int index, String element) {
			keys.set(index, UUID.fromJsonId(element));
			return element;
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractList#add(int, java.lang.Object)
		 */
		@Override
		public void add(int index, String element) {
			keys.add(index, UUID.fromJsonId(element));
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractList#remove(int)
		 */
		@Override
		public String remove(int index) {
			return keys.remove(index).asJsonId();
		}

		/* (non-Javadoc)
		 * @see java.util.AbstractCollection#size()
		 */
		@Override
		public int size() {
			return keys.size();
		}
		
		

	}
}
