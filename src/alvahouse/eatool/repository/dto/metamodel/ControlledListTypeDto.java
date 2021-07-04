/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "controlledListType")
@XmlAccessorType(XmlAccessType.NONE)
public class ControlledListTypeDto extends ExtensibleMetaPropertyTypeDto {

    private LinkedList<String> values = new LinkedList<String>();
    private String defaultValue = "";
	/**
	 * @return the values
	 */
    @XmlElement
	public LinkedList<String> getValues() {
    	if(values == null) {
    		values = new LinkedList<String>();
    	}
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(LinkedList<String> values) {
		this.values = values;
	}
	
	@XmlElement
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

    
}
