/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ExportMappingPersistenceMemory implements ExportMappingPersistence {

	private Map<UUID, ExportMapping> mappings = new HashMap<>();
	
	/**
	 *  
	 */
	public ExportMappingPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#getMappings()
	 */
	@Override
	public Collection<ExportMapping> getMappings() throws Exception {
		ArrayList<ExportMapping> copy = new ArrayList<>(mappings.size());
		for(ExportMapping em : mappings.values()) {
			copy.add( (ExportMapping) em.clone());
		}
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#addMapping(alvahouse.eatool.repository.mapping.ExportMapping)
	 */
	@Override
	public void addMapping(ExportMapping mapping) throws Exception {
		UUID key = mapping.getKey();
		if(mappings.containsKey(key)) {
			throw new IllegalArgumentException("Cannot add export mapping with key " + key + " as mapping already exists");
		}
		mappings.put(key, (ExportMapping)mapping.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#updateMapping(alvahouse.eatool.repository.mapping.ExportMapping)
	 */
	@Override
	public void updateMapping(ExportMapping mapping) throws Exception {
		UUID key = mapping.getKey();
		if(!mappings.containsKey(key)) {
			throw new IllegalArgumentException("Cannot update export mapping - key " + key + " not known to the repository");
		}
		mappings.put(key, (ExportMapping)mapping.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ExportMappingPersistence#deleteMapping(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteMapping(UUID key) throws Exception{
		if(!mappings.containsKey(key)) {
			throw new IllegalArgumentException("Cannot delete export mapping - key " + key + " not known to the repository");
		}
		mappings.remove(key);
	}

}
