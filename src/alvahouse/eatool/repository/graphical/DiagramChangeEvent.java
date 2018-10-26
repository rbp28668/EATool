/*
 * DiagramChangeEvent.java
 * Created on 18-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

/**
 * DiagramChangeEvent is an event class used to signal changes in
 * a diagram to a DiagramChangeListener.
 * @author Bruce.Porteous
 *
 */
public class DiagramChangeEvent {

	private Object source;
	/**
	 * 
	 */
	public DiagramChangeEvent(Object source) {
		super();
		this.source = source;
	}

	public Object getSource(){
		return source;
	}
}
