/**
 * 
 */
package alvahouse.eatool.repository.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "repositoryProperties")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "properties", "version"})
public class RepositoryPropertiesDto {

	private String id;
	private List<KeyValue> properties = new LinkedList<KeyValue>();
	private VersionDto version;
	private String rev;
	/**
	 * 
	 */
	public RepositoryPropertiesDto() {
	}

	/**
	 * @param key
	 * @param value
	 */
	public void add(String key, String value) {
		properties.add(new KeyValue(key,value));
	}

	
	
	/**
	 * For couchDB serialisation.
	 * @return the id
	 */
	@JsonProperty("_id")
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the properties
	 */
	@JsonProperty
	@XmlElement(name="property")
	@XmlElementWrapper(name="properties")
	public List<KeyValue> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<KeyValue> properties) {
		this.properties = properties;
	}

	/**
	 * @return the version
	 */
	@JsonProperty
	@XmlElement
	public VersionDto getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}

	/**
	 * @return the rev
	 */
	@JsonProperty("_rev")
	public String getRev() {
		return rev;
	}

	/**
	 * @param rev the rev to set
	 */
	public void setRev(String rev) {
		this.rev = rev;
	}



	public static class KeyValue {
		private String key;
		private String value;
		
		KeyValue(){
			
		}
		
		KeyValue(String key, String value){
			this.key = key;
			this.value = value;
		}

		/**
		 * @return the key
		 */
		@JsonProperty
		@XmlAttribute
		public String getKey() {
			return key;
		}

		/**
		 * @param key the key to set
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * @return the value
		 */
		@JsonProperty
		@XmlAttribute
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
		
	}
}
