/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.util.UUID;

/**
 * Interface to persist DiagramTypes (and their sub-types).
 * @author bruce_porteous
 *
 */
public interface DiagramTypePersistence {

	/**
	 * Gets the diagram type corresponding to a given key.
	 * @param typeID is the key to retrieve
	 * @throws Exception if the diagram type does not exist. Maybe
	 * other exceptions if there are comms errors etc.
	 * @return the DiagramTypeDto corresponding to the key.
	 */
	DiagramTypeDto getDiagramType(UUID typeID) throws Exception;

	/**
	 * Adds a diagram type to the repository.
	 * @param dt is the diagram type DTO to add.
	 * @throws Exception if the diagram type already exists. Maybe
	 * other exceptions if there are comms errors etc.
	 */
	void addDiagramType(DiagramTypeDto dt) throws Exception;

	/**
	 * Updates a diagram type in the repository.
	 * @param dt is the diagram type to update.
	 * @throws Exception if the diagram type does not exist. Maybe
	 * other exceptions if there are comms errors etc.
	 */
	void updateDiagramType(DiagramTypeDto dt) throws Exception;

	/**
	 * Deletes the given diagram type.
	 * @param key identifies the diagram type
	 */
	void delete(UUID key) throws Exception;

	/**
	 * Gets all the diagram types irrespective of family.
	 * @return a collection of all the types.
	 */
	Collection<DiagramTypeDto> getTypes() throws Exception;

	/**
	 * 
	 */
	void dispose() throws Exception;

	/**
	 * Gets all the diagram types for a given type family.
	 * @param diagramTypeFamily identifies the family. 
	 * E.g. StandardDiagramTypeFamily.FAMILY_KEY
	 * @return collection of diagram types.
	 */
	Collection<DiagramTypeDto> getDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception;

	/**
	 * Deletes all the diagram types for a given diagram type family.
	 * @param diagramTypeFamilyKey identifies the DiagramTypeFamily that should have
	 * its DiagramTypes deleted. 
	 */
	void deleteDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception;

}
