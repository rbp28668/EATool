/**
 * 
 */
package alvahouse.eatool.repository.dto.scripting;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * JAXB Object factory to allow JAXB to create concrete classes.
 * @author bruce_porteous
 *
 */
@XmlRegistry
public class ObjectFactory {
	
	public ObjectFactory() {
	}
	
	public ScriptDto createScriptDto() { return new ScriptDto(); }
}
