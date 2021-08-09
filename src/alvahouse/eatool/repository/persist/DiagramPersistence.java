/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.graphical.DiagramDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface DiagramPersistence {

	
	/**
	 * Gets diagrams of a given type
	 * @param typeKey is the UUID that identifies the diagram type.
	 * @return
	 */
	public Collection<DiagramDto> getDiagramsByType(UUID typeKey) throws Exception ;

	/**
	 * Gets all the diagrams.
	 * @return
	 */
	public Collection<DiagramDto> getDiagrams() throws Exception;

	/**
	 * @param uuid
	 * @return
	 */
	public boolean contains(UUID uuid) throws Exception;

	/**
	 * @param uuid
	 * @return
	 */
	public DiagramDto lookup(UUID uuid) throws Exception;

	/**
	 * @param diagram
	 */
	public void addDiagram(DiagramDto diagram) throws Exception;

	/**
	 * @param diagram
	 */
	public void updateDiagram(DiagramDto diagram) throws Exception;

	/**
	 * @param key
	 */
	public void deleteDiagram(UUID key) throws Exception;

	/**
	 * 
	 */
	public void dispose() throws Exception;
}
