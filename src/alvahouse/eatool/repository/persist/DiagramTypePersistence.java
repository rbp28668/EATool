/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface DiagramTypePersistence {

	/**
	 * @param typeID
	 * @return
	 */
	DiagramType getDiagramType(UUID typeID) throws Exception;

	/**
	 * @param dt
	 */
	void addDiagramType(DiagramType dt) throws Exception;

	/**
	 * @param dt
	 */
	void updateDiagramType(DiagramType dt) throws Exception;

	/**
	 * @param key
	 */
	void delete(UUID key) throws Exception;

	/**
	 * @return
	 */
	Collection<DiagramType> getTypes() throws Exception;

	/**
	 * 
	 */
	void dispose() throws Exception;

	/**
	 * @param diagramTypeFamily
	 * @return
	 */
	Collection<DiagramType> getDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception;

	/**
	 * @param diagramTypeFamily
	 */
	void deleteDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception;

}
