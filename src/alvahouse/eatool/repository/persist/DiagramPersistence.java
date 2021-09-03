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
	 * @return the revision number of the new record.
	 */
	public String addDiagram(DiagramDto diagram) throws Exception;

	/**
	 * @param diagram
	 * @return the revision number of the updated record.
	 */
	public String updateDiagram(DiagramDto diagram) throws Exception;

	/**
	 * @param key identifies the diagram to delete.
	 * @param version is the revision number of the diagram to delete.
	 */
	public void deleteDiagram(UUID key, String version) throws Exception;

	/**
	 * 
	 */
	public void dispose() throws Exception;
}
