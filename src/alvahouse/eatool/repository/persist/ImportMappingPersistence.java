/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface ImportMappingPersistence {

	/**
	 * Get a complete collection of import mapping.
	 * @return collection of import mapping. Maybe empty should not be null.
	 * @throws Exception
	 */
	Collection<ImportMapping> getMappings() throws Exception;

	/**
	 * Adds an import mapping to the repository.  The mapping
	 * as identified by its key, should not exist in the repository.
	 * @param mapping is the mapping to add.
	 */
	void addMapping(ImportMapping mapping) throws Exception;

	/**
	 * Updates an existing import mapping in the repository.  The mapping
	 * as identified by its key, must exist in the repository.
	 * @param mapping is the mapping to update.
	 */
	void updateMapping(ImportMapping mapping) throws Exception;

	/**
	 * Deletes the import mapping corresponding to the given key.
	 * @param key is the key of the mapping to delete.
	 */
	void deleteMapping(UUID key) throws Exception;

	/**
	 * Deletes all the import mappings.
	 */
	void deleteAll() throws Exception;

}
