/*
 * DiagramTypesChangeEvent.java
 * Created on 20-Jan-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

/**
 * DiagramsChangeEvent is an event object for signalling changes
 * to available diagram types and their components.
 * @author Bruce.Porteous
 *
 */
public class DiagramsChangeEvent {

	private Object source;
	
	/**
	 * 
	 */
	public DiagramsChangeEvent(Object cause) {
		super();
		this.source = cause;
	}

	public Object getSource() {
		return source;
	}
}
