/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface DiagramPersistence {

	
	/**
	 * @param type
	 * @return
	 */
	public Collection<Diagram> getDiagramsByType(DiagramType type) throws Exception ;

	/**
	 * @return
	 */
	public Collection<Diagram> getDiagrams() throws Exception;

	/**
	 * @param uuid
	 * @return
	 */
	public Diagram lookup(UUID uuid) throws Exception;

	/**
	 * @param diagram
	 */
	public void addDiagram(Diagram diagram) throws Exception;

	/**
	 * @param diagram
	 */
	public void updateDiagram(Diagram diagram) throws Exception;

	/**
	 * @param key
	 */
	public void deleteDiagram(UUID key) throws Exception;

	/**
	 * 
	 */
	public void dispose() throws Exception;
}
