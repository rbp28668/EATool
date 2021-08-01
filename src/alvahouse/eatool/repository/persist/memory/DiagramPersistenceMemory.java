/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class DiagramPersistenceMemory implements DiagramPersistence {

	private Map<UUID,Diagram> diagrams = new HashMap<>();
	private Map<DiagramType,Map<UUID,Diagram>> diagramsByType = new HashMap<DiagramType,Map<UUID,Diagram>>(); 

	/**
	 * 
	 */
	public DiagramPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagramsByType(alvahouse.eatool.repository.graphical.DiagramType)
	 */
	@Override
	public Collection<Diagram> getDiagramsByType(DiagramType type) throws Exception {
		Map<UUID,Diagram> ofType = diagramsByType.get(type);
		if(ofType == null) ofType = Collections.emptyMap();
		ArrayList<Diagram> copy = new ArrayList<>(ofType.size());
		for(Diagram d : ofType.values()) {
			copy.add((Diagram) d.clone());
		}
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#getDiagrams()
	 */
	@Override
	public Collection<Diagram> getDiagrams() throws Exception {
		ArrayList<Diagram> copy = new ArrayList<>(diagrams.size());
		for(Diagram d : diagrams.values()) {
			copy.add((Diagram) d.clone());
		}
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
	public Diagram lookup(UUID uuid) throws Exception {
		Diagram d = diagrams.get(uuid);
		if(d == null) {
			throw new IllegalArgumentException("Diagram with key " + uuid + " is not known to repository");
		}
		return (Diagram) d.clone();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#addDiagram(alvahouse.eatool.repository.graphical.Diagram)
	 */
	@Override
	public void addDiagram(Diagram diagram) throws Exception {
		UUID key = diagram.getKey();
		if(diagrams.containsKey(key)) {
			throw new IllegalArgumentException("Can't add Diagram with key " + key + " it is already in the repository");
		}
		DiagramType type = diagram.getType();
		Diagram copy = (Diagram)diagram.clone();
		Map<UUID,Diagram> ofType = diagramsByType.get(type);
		if(ofType == null) {  // no existing diagrams of this type so...
			ofType = new HashMap<UUID,Diagram>();
			diagramsByType.put(type, ofType);
		}
		diagrams.put(key, copy);
		ofType.put(key, copy);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#updateDiagram(alvahouse.eatool.repository.graphical.Diagram)
	 */
	@Override
	public void updateDiagram(Diagram diagram) throws Exception {
		UUID key = diagram.getKey();
		if(!diagrams.containsKey(key)) {
			throw new IllegalArgumentException("Can't update Diagram with key " + key + " is not known to the repository");
		}
		DiagramType type = diagram.getType();
		Diagram copy = (Diagram)diagram.clone();
		Map<UUID,Diagram> ofType = diagramsByType.get(type);
		diagrams.put(key, copy);
		ofType.put(key, copy);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.DiagramPersistence#deleteDiagram(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteDiagram(UUID key) throws Exception {
		if(!diagrams.containsKey(key)) {
			throw new IllegalArgumentException("Can't delete Diagram with key " + key + " as is not known to the repository");
		}
		diagrams.remove(key);
		for(Map<UUID,Diagram> ofType : diagramsByType.values()) {
			ofType.remove(key);
		}
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
