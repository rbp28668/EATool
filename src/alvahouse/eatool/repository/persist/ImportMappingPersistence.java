/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.mapping.ImportMappingDto;
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
	public Collection<ImportMappingDto> getMappings() throws Exception;

	/**
	 * Looks up a mapping by key.
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public ImportMappingDto lookupMapping(UUID key) throws Exception;

	/**
	 * Adds an import mapping to the repository.  The mapping
	 * as identified by its key, should not exist in the repository.
	 * @return the revision number of the new mapping record.
	 * @param mapping is the mapping to add.
	 */
	public String addMapping(ImportMappingDto mapping) throws Exception;

	/**
	 * Updates an existing import mapping in the repository.  The mapping
	 * as identified by its key, must exist in the repository.
	 * @param mapping is the mapping to update.
	 * @return the revision number of the updated mapping record.
	 */
	public String updateMapping(ImportMappingDto mapping) throws Exception;

	/**
	 * Deletes the import mapping corresponding to the given key.
	 * @param key is the key of the mapping to delete.
	 * @param version is the revision number of the record to delete.
	 */
	void deleteMapping(UUID key, String version) throws Exception;

	/**
	 * Deletes all the import mappings.
	 */
	void deleteAll() throws Exception;

}
