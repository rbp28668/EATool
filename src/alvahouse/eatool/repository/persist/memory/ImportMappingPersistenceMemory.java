/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.dto.mapping.ImportMappingDto;
import alvahouse.eatool.repository.persist.ImportMappingPersistence;
import alvahouse.eatool.repository.persist.StaleDataException;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ImportMappingPersistenceMemory implements ImportMappingPersistence {

	private Map<UUID, ImportMappingDto> mappings = new HashMap<>();
	
	/**
	 *  
	 */
	public ImportMappingPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#getMappings()
	 */
	@Override
	public Collection<ImportMappingDto> getMappings() throws Exception {
		ArrayList<ImportMappingDto> copy = new ArrayList<>(mappings.size());
		copy.addAll(mappings.values());
		return copy;
	}

	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#lookupMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ImportMappingDto lookupMapping(UUID key) throws Exception {
		ImportMappingDto mapping = mappings.get(key);
		if(mapping == null) {
			throw new IllegalArgumentException("Mapping with key " + key + " not known to the repository");
		}
		return mapping;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#addMapping(alvahouse.eatool.repository.mapping.ImportMapping)
	 */
	@Override
	public String addMapping(ImportMappingDto mapping) throws Exception {
		UUID key = mapping.getKey();
		if(mappings.containsKey(key)) {
			throw new IllegalArgumentException("Cannot add import mapping with key " + key + " as mapping already exists");
		}
		String version = null;
		if(mapping.getVersion().notVersioned()) {
			version = mapping.getVersion().update(new UUID().asJsonId());
		}
		mappings.put(key, mapping);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#updateMapping(alvahouse.eatool.repository.mapping.ImportMapping)
	 */
	@Override
	public String updateMapping(ImportMappingDto mapping) throws Exception {
		UUID key = mapping.getKey();
		ImportMappingDto original = mappings.get(key);
		if(original == null) {
			throw new IllegalArgumentException("Cannot update import mapping - key " + key + " not known to the repository");
		}
		
		if(!mapping.getVersion().sameVersionAs(original.getVersion())) {
			throw new StaleDataException("Unable to update import mapping due to stale data");
		}
		String version = mapping.getVersion().update(new UUID().asJsonId());
		mappings.put(key, mapping);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#deleteMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteMapping(UUID key, String version) throws Exception{
		
		ImportMappingDto mapping = mappings.get(key);
		if(mapping == null) {
			throw new IllegalArgumentException("Cannot delete import mapping - key " + key + " not known to the repository");
		}
		if(!mapping.getVersion().getVersion().equals(version)) {
			throw new StaleDataException("Unable to delete import mapping due to stale data");
		}

		mappings.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ImportMappingPersistence#deleteAll()
	 */
	@Override
	public void deleteAll() throws Exception{
		mappings.clear();
	}

}
