/**
 * 
 */
package alvahouse.eatool.repository.dto.scripting;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "eventMap")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "handlers", "version"})


public class EventMapDto {

	private List<EventMapHandlerDto> handlers;
    private VersionDto version;


    
    /**
	 * @return the handlers
	 */
    @XmlElement(name="handler")
    @XmlElementWrapper(name="handlers")
	public List<EventMapHandlerDto> getHandlers() {
    	if(handlers == null) {
    		handlers = new LinkedList<EventMapHandlerDto>();
    	}
		return handlers;
	}



	/**
	 * @param handlers the handlers to set
	 */
 	public void setHandlers(List<EventMapHandlerDto> handlers) {
		this.handlers = handlers;
	}



	/**
	 * @return the version
	 */
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


	@XmlRootElement(name = "eventHandler")
	@XmlAccessorType(XmlAccessType.NONE)
	public static class EventMapHandlerDto{
    	private String event;
    	private UUID handler;
    	
		/**
		 * @return the event
		 */
    	@XmlElement
		public String getEvent() {
			return event;
		}
		/**
		 * @param event the event to set
		 */
		public void setEvent(String event) {
			this.event = event;
		}
		
		/**
		 * @return the handler
		 */
		@JsonIgnore
		public UUID getHandler() {
			return handler;
		}
		/**
		 * @param handler the handler to set
		 */
		public void setHandler(UUID handler) {
			this.handler = handler;
		}

		
	    @XmlElement(name="handler",required=true)
		@JsonProperty(value="handler")
		public String getHandlerJson() {
			if(handler == null) return null;
			return handler.asJsonId();
		}
		/**
		 * @param handler the handler to set
		 */
		public void setHandlerJson(String handler) {
			this.handler = (handler != null) ? UUID.fromJsonId(handler) : null;
		}

    	
    }
}
