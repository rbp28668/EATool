/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;

/**
 * @author bruce_porteous
 *
 */
public class DiagramTypeDto extends NamedRepositoryItemDto {

	private EventMapDto eventMap;
	private VersionDto version;

	/**
	 * 
	 */
	public DiagramTypeDto() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the eventMap
	 */
	public EventMapDto getEventMap() {
		return eventMap;
	}

	/**
	 * @param eventMap the eventMap to set
	 */
	public void setEventMap(EventMapDto eventMap) {
		this.eventMap = eventMap;
	}

	/**
	 * @return the version
	 */
	public VersionDto getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}

	
	
}
