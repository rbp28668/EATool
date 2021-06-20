/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "metaRole")
@XmlAccessorType(XmlAccessType.NONE)
public class MetaRoleDto  extends MetaPropertyContainerDto{
	private UUID connects;
	private String multiplicity;
	
	/**
	 * @return the connects
	 */
	@JsonIgnore
	public UUID getConnects() {
		return connects;
	}
	/**
	 * @param connects the connects to set
	 */
	public void setConnects(UUID connects) {
		this.connects = connects;
	}

	/**
	 * @return the connects
	 */
	@XmlAttribute(name="connects", required = true)
	@JsonProperty("connects")
	public String getConnectsJson() {
		return connects.asJsonId();
	}
	/**
	 * @param connects the connects to set
	 */
	public void setConnectsJson(String connects) {
		this.connects = UUID.fromJsonId(connects);
	}
	
	/**
	 * @return the multiplicity
	 */
	@XmlAttribute(required = true)
	public String getMultiplicity() {
		return multiplicity;
	}
	/**
	 * @param multiplicity the multiplicity to set
	 */
	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	
}
