/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.dto.mapping.ExportMappingDto;
import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.repository.persist.StaleDataException;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ExportMappingPersistenceMemory implements ExportMappingPersistence {

	private Map<UUID, ExportMappingDto> mappings = new HashMap<>();
	
	/**
	 *  
	 */
	public ExportMappingPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#getMappings()
	 */
	@Override
	public Collection<ExportMappingDto> getMappings() throws Exception {
		ArrayList<ExportMappingDto> copy = new ArrayList<>(mappings.size());
		copy.addAll(mappings.values());
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#addMapping(alvahouse.eatool.repository.mapping.ExportMapping)
	 */
	@Override
	public String addMapping(ExportMappingDto mapping) throws Exception {
		UUID key = mapping.getKey();
		if(mappings.containsKey(key)) {
			throw new IllegalArgumentException("Cannot add export mapping with key " + key + " as mapping already exists");
		}
		String version = null;
		if(mapping.getVersion().notVersioned()) {
			version = mapping.getVersion().update(new UUID().asJsonId());
		}
		mappings.put(key, mapping);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#updateMapping(alvahouse.eatool.repository.mapping.ExportMapping)
	 */
	@Override
	public String updateMapping(ExportMappingDto mapping) throws Exception {
		UUID key = mapping.getKey();
		ExportMappingDto original = mappings.get(key);
		if (original == null) {
			throw new IllegalArgumentException("Cannot update export mapping - key " + key + " not known to the repository");
		}
		if(!mapping.getVersion().sameVersionAs(original.getVersion())) {
			throw new StaleDataException("Unable to update entity due to stale data");
		}
		
		String version = mapping.getVersion().update(new UUID().asJsonId());
		mappings.put(key, mapping);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#deleteMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteMapping(UUID key, String version) throws Exception{
		ExportMappingDto mapping = mappings.get(key);
		if (mapping == null) {
			throw new IllegalArgumentException("Cannot delete export mapping - key " + key + " not known to the repository");
		}
		if(!mapping.getVersion().getVersion().equals(version)) {
			throw new StaleDataException("Unable to delete entity due to stale data");
		}
		mappings.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#lookupMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ExportMappingDto lookupMapping(UUID key) throws Exception {
		ExportMappingDto mapping = mappings.get(key);
		if(mappings == null) {
			throw new IllegalArgumentException("Cannot find export mapping with key " + key);
		}
		return mapping;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#deleteContents()
	 */
	@Override
	public void deleteContents() throws Exception {
		mappings.clear();
	}

}
