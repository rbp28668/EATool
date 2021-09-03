package alvahouse.eatool.repository.graphical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.dto.graphical.DiagramDto;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeAdapter;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * This class holds the complete set of diagrams known to the system.  As
 * well as providing a central point of access for all the diagram, it
 * provides access to them by diagram type.
 * @author bruce.porteous
 *
 */
public class Diagrams extends ModelChangeAdapter {

	private final DiagramTypes types;
	private final DiagramPersistence persistence;
	private List<DiagramsChangeListener> changeListeners = new LinkedList<DiagramsChangeListener>(); // of DiagramsChangeListener
	
	/**
	 * @return
	 * @throws Exception
	 */
	private Collection<Diagram> toDiagrams(Collection<DiagramDto> dtos) throws Exception {
		List<Diagram> diagrams = new ArrayList<>(dtos.size());
		for(DiagramDto dto : dtos) {
			Diagram d = diagramFromDto(dto);
			diagrams.add(d);
		}
		return diagrams;
	}



	/**
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	private Diagram diagramFromDto(DiagramDto dto) throws Exception {
		UUID typeKey = dto.getTypeKey();
		DiagramType type = types.get(typeKey);
		Diagram d = type.newDiagram(dto);
		return d;
	}
	
	

	/**
	 * Constructor for Diagrams.
	 * @param diagramPersistence 
	 */
	public Diagrams(DiagramTypes types, DiagramPersistence diagramPersistence) {
		super();
		this.types = types;
		this.persistence = diagramPersistence;
	}

    /**
     * Does a simple lookup of a diagram by key.  Note that this is
     * a simple linear search.
     * @param uuid is the key to use for lookup.
     */
    public Diagram lookup(UUID uuid) throws Exception {
        return diagramFromDto(persistence.lookup(uuid));
    }

    /**
     * Determines whether there is a diagram with a given key.
     * @param uuid
     * @return
     * @throws Exception
     */
    public boolean contains(UUID uuid) throws Exception {
    	return persistence.contains(uuid);
    }
    
	/**
	 * This adds a diagram to the diagrams collection.
	 * @param diagram is the diagram to add.
	 */
	public void add(Diagram diagram) throws Exception{
	    if(diagram == null){
	        throw new NullPointerException("Can't add null diagram to diagrams");
	    }
 		String user = System.getProperty("user.name");
		diagram.getVersion().createBy(user);

		String version = persistence.addDiagram(diagram.toDto());
		diagram.getVersion().update(version);
		fireDiagramAdded(diagram);
	}

	/**
	 * This adds a diagram to the diagrams collection.
	 * @param diagram is the diagram to add.
	 */
	public void _add(Diagram diagram) throws Exception{
	    if(diagram == null){
	        throw new NullPointerException("Can't add null diagram to diagrams");
	    }
		String version = persistence.addDiagram(diagram.toDto());
		diagram.getVersion().update(version);
	}

	/**
	 * This adds a diagram to the diagrams collection.
	 * @param diagram is the diagram to add.
	 */
	public void update(Diagram diagram) throws Exception{
	    if(diagram == null){
	        throw new NullPointerException("Can't update null diagram to diagrams");
	    }
 		String user = System.getProperty("user.name");
		diagram.getVersion().modifyBy(user);

		String version = persistence.updateDiagram(diagram.toDto());
		diagram.getVersion().update(version);
		fireDiagramChanged(diagram);
	}

	/**
	 * Creates a new diagram of the given type.
	 * @param dt is the type of diagram to create.
	 * @return a new diagram.
	 */
	public Diagram newDiagramOfType(DiagramType dt) throws Exception{
		Diagram diagram = dt.newDiagram(new UUID());
		return diagram;
	}
	
	/**
	 * Removes a given diagram.
	 * @param diagram
	 */
	public void removeDiagram(Diagram diagram) throws Exception{
	    persistence.deleteDiagram(diagram.getKey(), diagram.getVersion().getVersion());
	    fireDiagramDeleted(diagram);
	}
	
	/**
	 * Returns a collection of all the diagrams of a given diagram type.
	 * If there is no existing list, then a new, empty, list is created for the given
	 * diagram type and stored against that type.
	 * @param type is the diagram type to get the associated diagrams for.
	 * @return List of diagrams: never null, may be empty.
	 * @param type is the DiagramType of the diagrams to be returned.
	 * @return Collection of diagrams.  May be empty, never null.
	 */
	public Collection<Diagram> getDiagramsOfType(DiagramType type) throws Exception {
		Collection<DiagramDto> dtos = persistence.getDiagramsByType(type.getKey());
		return toDiagrams(dtos);
	}

	public void addChangeListener(DiagramsChangeListener listener){
		changeListeners.add(listener);
	}

	public void removeChangeListener(DiagramsChangeListener listener){
		changeListeners.remove(listener);
	}
	
	/**
	 * Determines whether a change listener is active.
	 * @param l
	 * @return
	 */
	public boolean isActive(DiagramsChangeListener l) {
		return changeListeners.contains(l);
	}

	
	public void deleteContents() throws Exception{
		persistence.dispose();
		fireDiagramsUpdated();
	}
	
	/**
	 * Writes the Diagrams out as XML
	 * @param out is the XMLWriterDirect to write the XML to.
	 * @param entityName is the name to use for the diagrams collection.
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out, String entityName) throws IOException {
		out.startEntity(entityName);
		try {
			Collection<DiagramDto> dtos = persistence.getDiagrams();
			for( Diagram diagram : toDiagrams(dtos)) {
				diagram.writeXML(out);
			}
		} catch (Exception e) {
			throw new IOException("Unable to write diagrams to XML",e);
		}
		out.stopEntity();
	}

	private void fireDiagramsUpdated() throws Exception{
		DiagramsChangeEvent event = new DiagramsChangeEvent(null);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramsUpdated(event);
		}
	}

	private void fireDiagramAdded(Diagram diag) throws Exception{
		DiagramsChangeEvent event = new DiagramsChangeEvent(diag);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramAdded(event);
		}
	}
	
	private void fireDiagramChanged(Diagram diag) throws Exception{
		DiagramsChangeEvent event = new DiagramsChangeEvent(diag);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramChanged(event);
		}
	}
	
	private void fireDiagramDeleted(Diagram diag) throws Exception{
		DiagramsChangeEvent event = new DiagramsChangeEvent(diag);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramDeleted(event);
		}
	}

	
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#modelUpdated(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void modelUpdated(ModelChangeEvent e) throws Exception{
        // Update all diagrams.
        // Should only occur in situations where major change 
        // is occuring and that means deletion of everything.
        Model model = (Model)e.getSource();
        if(model.getEntityCount() == 0 && model.getRelationshipCount() == 0){
            deleteContents();
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */ 
    public void EntityChanged(ModelChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void EntityDeleted(ModelChangeEvent e) {
    	// TODO approach needs to be reviewed as not really appropriate for remote repository
    	// Possibly system of "tombstone" entities
//        Entity entity = (Entity)e.getSource();
//
//        // Remove any corresponding symbols from the diagram
//        for(Diagram diagram : allDiagrams) {
//            diagram.removeNodeForObject(entity);
//        }
    }



    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipChanged(ModelChangeEvent e) throws Exception{
        Relationship rel = (Relationship)e.getSource();
        validateRelationship(rel);
    }

    /**
     * This validates the relationship in all the diagrams.
     * @param rel is the Relationship to validate.
     */
    private void validateRelationship(Relationship rel) throws Exception{
//        Entity start = rel.start().connectsTo();
//        Entity finish = rel.finish().connectsTo();
//
//        // Validate on diagram from the diagram
//        for(Diagram diagram : allDiagrams){
//            diagram.validate(rel,start,finish);
//        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipDeleted(ModelChangeEvent e) {
    	// TODO - review as for entities
//        // Delete corresponding connectors
//        Relationship rel = (Relationship)e.getSource();
//
//        // Remove any corresponding symbols from the diagram
//        for(Diagram diagram : allDiagrams){
//            diagram.removeArcForObject(rel);
//        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RoleChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RoleChanged(ModelChangeEvent e) throws Exception{
        // Some connectors may be invalid.
        Role role = (Role)e.getSource();
        validateRelationship(role.getRelationship());
    }





}
