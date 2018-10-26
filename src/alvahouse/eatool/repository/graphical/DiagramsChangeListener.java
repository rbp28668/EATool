/*
 * DiagramTypesChangeListener.java
 * Created on 20-Jan-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

/**
 * DiagramsChangeListener provides an event listener interface
 * for changes to diagram types and diagrams.
 * @author Bruce.Porteous
 *
 */
public interface DiagramsChangeListener {
	
	public void typesUpdated(DiagramsChangeEvent e);
	
	public void diagramTypeAdded(DiagramsChangeEvent e);
	
	public void diagramTypeChanged(DiagramsChangeEvent e);
	
	public void diagramTypeDeleted(DiagramsChangeEvent e);

	public void diagramsUpdated(DiagramsChangeEvent e);

	public void diagramAdded(DiagramsChangeEvent e);
	
	public void diagramChanged(DiagramsChangeEvent e);
	
	public void diagramDeleted(DiagramsChangeEvent e);

}
