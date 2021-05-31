/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.util.UUID;

/**
 * Interface to define persistence for Export Mappings.
 * @author bruce_porteous
 *
 */
public interface ExportMappingPersistence {

	/**
	 * Get a complete collection of export mapping.
	 * @return collection of export mapping. Maybe empty should not be null.
	 * @throws Exception
	 */
	Collection<ExportMapping> getMappings() throws Exception;

	/**
	 * Adds an export mapping to the repository.  The mapping
	 * as identified by its key, should not exist in the repository.
	 * @param mapping is the mapping to add.
	 */
	void addMapping(ExportMapping mapping) throws Exception;

	/**
	 * Updates an existing export mapping in the repository.  The mapping
	 * as identified by its key, must exist in the repository.
	 * @param mapping is the mapping to update.
	 */
	void updateMapping(ExportMapping mapping) throws Exception;

	/**
	 * Deletes the export mapping corresponding to the given key.
	 * @param key is the key of the mapping to delete.
	 */
	void deleteMapping(UUID key) throws Exception;
	
	
}
