/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.dto.graphical.DiagramDto;
import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramPersistenceMemory implements DiagramPersistence {

	// All diagrams keyed by diagram key.
	private Map<UUID,DiagramDto> diagrams = new HashMap<>();
	
	// All diagrams but split by diagram type key.
	private Map<UUID,Map<UUID,DiagramDto>> diagramsByType = new HashMap<UUID,Map<UUID,DiagramDto>>(); 

	/**
	 * 
	 */
	public DiagramPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagramsByType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public Collection<DiagramDto> getDiagramsByType(UUID typeKey) throws Exception {
		Map<UUID,DiagramDto> ofType = diagramsByType.get(typeKey);
		if(ofType == null) ofType = Collections.emptyMap();
		ArrayList<DiagramDto> copy = new ArrayList<>(ofType.size());
		copy.addAll(ofType.values());
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagrams()
	 */
	@Override
	public Collection<DiagramDto> getDiagrams() throws Exception {
		ArrayList<DiagramDto> copy = new ArrayList<>(diagrams.size());
		copy.addAll(diagrams.values());
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#contains(alvahouse.eatool.util.UUID)
	 */
	@Override
	public boolean contains(UUID uuid) throws Exception {
		return diagrams.containsKey(uuid);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#lookup(alvahouse.eatool.util.UUID)
	 */
	@Override
	public DiagramDto lookup(UUID uuid) throws Exception {
		DiagramDto d = diagrams.get(uuid);
		if(d == null) {
			throw new IllegalArgumentException("Diagram with key " + uuid + " is not known to repository");
		}
		return d;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#addDiagram(alvahouse.eatool.repository.graphical.Diagram)
	 */
	@Override
	public void addDiagram(DiagramDto diagram) throws Exception {
		UUID key = diagram.getKey();
		if(diagrams.containsKey(key)) {
			throw new IllegalArgumentException("Can't add Diagram with key " + key + " it is already in the repository");
		}
		UUID typeKey = diagram.getTypeKey();
		Map<UUID,DiagramDto> ofType = diagramsByType.get(typeKey);
		if(ofType == null) {  // no existing diagrams of this type so...
			ofType = new HashMap<UUID,DiagramDto>();
			diagramsByType.put(typeKey, ofType);
		}
		diagrams.put(key, diagram);
		ofType.put(key, diagram);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#updateDiagram(alvahouse.eatool.repository.graphical.Diagram)
	 */
	@Override
	public void updateDiagram(DiagramDto diagram) throws Exception {
		UUID key = diagram.getKey();
		if(!diagrams.containsKey(key)) {
			throw new IllegalArgumentException("Can't update Diagram with key " + key + " is not known to the repository");
		}
		UUID typeKey = diagram.getTypeKey();
		Map<UUID,DiagramDto> ofType = diagramsByType.get(typeKey);
		diagrams.put(key, diagram);
		ofType.put(key, diagram);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#deleteDiagram(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteDiagram(UUID key) throws Exception {
		DiagramDto d = diagrams.get(key);
		if(d == null) {
			throw new IllegalArgumentException("Can't delete Diagram with key " + key + " as is not known to the repository");
		}
		diagrams.remove(key);
		Map<UUID,DiagramDto> ofType = diagramsByType.get(d.getTypeKey());
		ofType.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		diagrams.clear();
		diagramsByType.clear();
	}

}
