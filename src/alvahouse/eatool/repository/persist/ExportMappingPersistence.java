/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.mapping.ExportMappingDto;
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
	Collection<ExportMappingDto> getMappings() throws Exception;

	/**
	 * Adds an export mapping to the repository.  The mapping
	 * as identified by its key, should not exist in the repository.
	 * @param mapping is the mapping to add.
	 * @return the revision number of the new mapping.
	 */
	String addMapping(ExportMappingDto mapping) throws Exception;

	/**
	 * Updates an existing export mapping in the repository.  The mapping
	 * as identified by its key, must exist in the repository.
	 * @param mapping is the mapping to update.
	 * @return the revision number of the updated mapping.
	 */
	String updateMapping(ExportMappingDto mapping) throws Exception;

	/**
	 * Deletes the export mapping corresponding to the given key.
	 * @param key is the key of the mapping to delete.
	 * @param version is the revision number of the record to delete.
	 */
	void deleteMapping(UUID key, String version) throws Exception;

	/**
	 * Looks up an export mapping by key
	 * @param key is the mappings key.
	 * @return the export mapping
	 * @throws Exception if not found or other error occurs
	 */
	ExportMappingDto lookupMapping(UUID key) throws Exception;
	
	/**
	 * Deletes all the export mappings.
	 * @throws Exception
	 */
	void deleteContents() throws Exception;
}
