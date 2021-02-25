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
	
	public void typesUpdated(DiagramsChangeEvent e) throws Exception;
	
	public void diagramTypeAdded(DiagramsChangeEvent e) throws Exception;
	
	public void diagramTypeChanged(DiagramsChangeEvent e) throws Exception;
	
	public void diagramTypeDeleted(DiagramsChangeEvent e) throws Exception;

	public void diagramsUpdated(DiagramsChangeEvent e) throws Exception;

	public void diagramAdded(DiagramsChangeEvent e) throws Exception;
	
	public void diagramChanged(DiagramsChangeEvent e) throws Exception;
	
	public void diagramDeleted(DiagramsChangeEvent e) throws Exception;

}
