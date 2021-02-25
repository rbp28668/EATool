/*
 * DiagramChangeListener.java
 * Created on 18-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

/**
 * DiagramChangeListener is a listener interface to allow classes
 * to register interest in changes to a diagram.
 * @author Bruce.Porteous
 *
 */
public interface DiagramChangeListener {

	/**
	 * Indicates a symbol was added to the diagram.
	 * @param e is the change event that identifies the symbol.
	 */
	public void symbolAdded(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a symbol was deleted from the diagram.
	 * @param e is the change event that identifies the symbol.
	 */
	public void symbolDeleted(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a symbol was moved on the diagram.
	 * @param e is the change event that identifies the symbol.
	 */
	public void symbolMoved(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a connector was added to the diagram.
	 * @param e is the change event that identifies the connector.
	 */
	public void connectorAdded(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a connector was deleted from the diagram.
	 * @param e is the change event that identifies the connector.
	 */
	public void connectorDeleted(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a connector was moved on the diagram.
	 * @param e is the change event that identifies the connector.
	 */
	public void connectorMoved(DiagramChangeEvent e) throws Exception;
	
	/**
	 * Indicates a major change to the diagram rather than having
	 * multiple calls to to add/move/delete.
	 * @param e is the change event that identifies the diagram.
	 * @throws Exception 
	 */
	public void majorDiagramChange(DiagramChangeEvent e) throws Exception;
	
}
