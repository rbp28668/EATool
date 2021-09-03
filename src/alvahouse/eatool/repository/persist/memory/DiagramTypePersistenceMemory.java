/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.repository.persist.StaleDataException;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramTypePersistenceMemory implements DiagramTypePersistence {

	private Map<UUID,DiagramTypeDto> types = new HashMap<>();
	/**
	 * 
	 */
	public DiagramTypePersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getDiagramType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DiagramTypeDto getDiagramType(UUID typeID) throws Exception {
		DiagramTypeDto type = types.get(typeID);
		if(type == null) {
			throw new IllegalArgumentException("Repository does not have a diagram type with key " + typeID);
		}
		return type;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#addDiagramType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public String addDiagramType(DiagramTypeDto dt) throws Exception {
		UUID key = dt.getKey();
		if(types.containsKey(key)) {
			throw new IllegalArgumentException("Repository already contains a diagram type with key " + key);
		}
		String version = dt.getVersion().update(new UUID().asJsonId());
		types.put(key, dt);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#updateDiagramType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public String updateDiagramType(DiagramTypeDto dt) throws Exception {
		UUID key = dt.getKey();
		DiagramTypeDto original = types.get(key);
		if (original == null) {
			throw new IllegalArgumentException("Repository does not contain a diagram type with key " + key);
		}
		if(!dt.getVersion().sameVersionAs(original.getVersion())) {
			throw new StaleDataException("Unable to update diagram type due to stale data");
		}
		
		String version = dt.getVersion().update(new UUID().asJsonId());
		types.put(key, dt);
		return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#delete(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void delete(UUID key, String version) throws Exception {
		DiagramTypeDto dt = types.get(key);
		if(dt == null) {
			throw new IllegalArgumentException("Repository does not contain a diagram type with key " + key);
		}
		if(!dt.getVersion().getVersion().equals(version)) {
			throw new StaleDataException("Unable to delete diagram type due to stale data");
		}
		types.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getTypes()
	 */
	@Override
	public Collection<DiagramTypeDto> getTypes() throws Exception {
		ArrayList<DiagramTypeDto> list = new ArrayList<>(types.size());
		list.addAll(types.values());
		return list;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		types.clear();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getDiagramTypesOfFamily(alvahouse.eatool.repository.graphical.DiagramTypeFamily)
	 */
	@Override
	public Collection<DiagramTypeDto> getDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception {
		// Brute force and ignorance search on the expectation that there are never more than a few tens of diagram types.
		// Saves book-keeping when writing.
		LinkedList<DiagramTypeDto> list = new LinkedList<>();

		for(DiagramTypeDto t : types.values()) {
			if(diagramTypeFamilyKey.equals(t.getFamilyKey())){
				list.add(t);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#deleteDiagramTypesOfFamily(alvahouse.eatool.repository.graphical.DiagramTypeFamily)
	 */
	@Override
	public void deleteDiagramTypesOfFamily(UUID diagramTypeFamilyKey) throws Exception {
		ArrayList<DiagramTypeDto> list = new ArrayList<>(types.values());

		for(DiagramTypeDto t : list) {
			if(diagramTypeFamilyKey.equals(t.getFamilyKey())){
				types.remove(t.getKey());
			}
		}
	}

}
