/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramTypePersistenceMemory implements DiagramTypePersistence {

	private Map<UUID,DiagramType> types = new HashMap<>();
	/**
	 * 
	 */
	public DiagramTypePersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getDiagramType(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DiagramType getDiagramType(UUID typeID) throws Exception {
		DiagramType type = types.get(typeID);
		if(type == null) {
			throw new IllegalArgumentException("Repository does not have a diagram type with key " + typeID);
		}
		return (DiagramType) type.clone();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#addDiagramType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public void addDiagramType(DiagramType dt) throws Exception {
		UUID key = dt.getKey();
		if(types.containsKey(key)) {
			throw new IllegalArgumentException("Repository already contains a diagram type with key " + key);
		}
		types.put(key, (DiagramType) dt.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#updateDiagramType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public void updateDiagramType(DiagramType dt) throws Exception {
		UUID key = dt.getKey();
		if(!types.containsKey(key)) {
			throw new IllegalArgumentException("Repository does not contain a diagram type with key " + key);
		}
		types.put(key, (DiagramType) dt.clone());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#delete(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void delete(UUID key) throws Exception {
		if(!types.containsKey(key)) {
			throw new IllegalArgumentException("Repository does not contain a diagram type with key " + key);
		}
		types.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#getTypes()
	 */
	@Override
	public Collection<DiagramType> getTypes() throws Exception {
		ArrayList<DiagramType> list = new ArrayList<>(types.size());
		for(DiagramType t : types.values()) {
			list.add((DiagramType)t.clone());
		}
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
	public Collection<DiagramType> getDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception {
		// Brute force and ignorance search on the expectation that there are never more than a few tens of diagram types.
		// Saves book-keeping when writing.
		UUID familyKey = diagramTypeFamily.getKey();
		LinkedList<DiagramType> list = new LinkedList<>();

		for(DiagramType t : types.values()) {
			if(familyKey.equals(t.getFamilyKey())){
				list.add((DiagramType)t.clone());
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramTypePersistence#deleteDiagramTypesOfFamily(alvahouse.eatool.repository.graphical.DiagramTypeFamily)
	 */
	@Override
	public void deleteDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception {
		UUID familyKey = diagramTypeFamily.getKey();
		ArrayList<DiagramType> list = new ArrayList<>(types.values());

		for(DiagramType t : list) {
			if(familyKey.equals(t.getFamilyKey())){
				types.remove(t.getKey());
			}
		}
	}

}
