package alvahouse.eatool.repository.graphical;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeAdapter;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
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

	private List<Diagram> allDiagrams = new LinkedList<Diagram>();
	private Map<DiagramType,List<Diagram>> diagramsByType = new HashMap<DiagramType,List<Diagram>>(); // map of lists of diagram, keyed by diagram type
	private List<DiagramsChangeListener> changeListeners = new LinkedList<DiagramsChangeListener>(); // of DiagramsChangeListener
	
	/**
	 * Gets a list of diagrams of a particular type.  If there is no
	 * existing list, then a new, empty, list is created for the given
	 * diagram type and stored against that type.
	 * @param type is the diagram type to get the associated diagrams for.
	 * @return List of diagrams: never null, may be empty.
	 */
	private List<Diagram> getDiagramList(DiagramType type) {
		List<Diagram> diagrams = diagramsByType.get(type);
		if(diagrams == null) {
			diagrams = new LinkedList<Diagram>();
			diagramsByType.put(type,diagrams);
		}
		return diagrams;
	}
	
	private Collection<Diagram> getDiagrams(){
		return Collections.unmodifiableCollection(allDiagrams);
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
	
//	private void fireDiagramChanged(Diagram diag){
//		DiagramsChangeEvent event = new DiagramsChangeEvent(diag);
//		for(DiagramsChangeListener listener : changeListeners){
//			listener.diagramChanged(event);
//		}
//	}
	
	private void fireDiagramDeleted(Diagram diag) throws Exception{
		DiagramsChangeEvent event = new DiagramsChangeEvent(diag);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramDeleted(event);
		}
	}

	/**
	 * Constructor for Diagrams.
	 */
	public Diagrams() {
		super();
	}

	/**
	 * This adds a diagram to the diagrams collection.
	 * @param diagram is the diagram to add.
	 */
	public void add(Diagram diagram) throws Exception{
	    if(diagram == null){
	        throw new NullPointerException("Can't add null diagram to diagrams");
	    }
		allDiagrams.add(diagram);
		List<Diagram> diagrams = getDiagramList(diagram.getType());
		diagrams.add(diagram);
		fireDiagramAdded(diagram);
	}
	
	/**
	 * Creates a new diagram of the given type.
	 * @param dt is the type of diagram to create.
	 * @return a new diagram.
	 */
	public Diagram newDiagramOfType(DiagramType dt) throws Exception{
		Diagram diagram = dt.newDiagram(new UUID());
		add(diagram);
		return diagram;
	}
	
	/**
	 * Removes a given diagram.
	 * @param diagram
	 */
	public void removeDiagram(Diagram diagram) throws Exception{
	    boolean removed = allDiagrams.remove(diagram);
		List<Diagram> diagrams = getDiagramList(diagram.getType());
		if(diagrams != null){
		    diagrams.remove(diagram);
		}
		if(removed){
		    fireDiagramDeleted(diagram);
		}
	}
	
	/**
	 * Returns a collection of all the diagrams of a given diagram type.
	 * @param type is the DiagramType of the diagrams to be returned.
	 * @return Collection of diagrams.  May be empty, never null.
	 */
	public Collection<Diagram> getDiagramsOfType(DiagramType type) {
		return getDiagramList(type);
	}

	public void addChangeListener(DiagramsChangeListener listener){
		changeListeners.add(listener);
	}

	public void removeChangeListener(DiagramsChangeListener listener){
		changeListeners.remove(listener);
	}
	
	
	
	public void deleteContents() throws Exception{
		allDiagrams.clear();
		diagramsByType.clear();
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
		for( Diagram diagram : getDiagrams()) {
			diagram.writeXML(out);
		}
		out.stopEntity();
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
        Entity entity = (Entity)e.getSource();

        // Remove any corresponding symbols from the diagram
        for(Diagram diagram : allDiagrams) {
            diagram.removeNodeForObject(entity);
        }
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
        Entity start = rel.start().connectsTo();
        Entity finish = rel.finish().connectsTo();

        // Validate on diagram from the diagram
        for(Diagram diagram : allDiagrams){
            diagram.validate(rel,start,finish);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RelationshipDeleted(ModelChangeEvent e) {
        // Delete corresponding connectors
        Relationship rel = (Relationship)e.getSource();

        // Remove any corresponding symbols from the diagram
        for(Diagram diagram : allDiagrams){
            diagram.removeArcForObject(rel);
        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.model.ModelChangeListener#RoleChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
     */
    public void RoleChanged(ModelChangeEvent e) throws Exception{
        // Some connectors may be invalid.
        Role role = (Role)e.getSource();
        validateRelationship(role.getRelationship());
    }

    /**
     * Does a simple lookup of a diagram by key.  Note that this is
     * a simple linear search.
     * @param uuid is the key to use for lookup.
     */
    public Diagram lookup(UUID uuid) {
        Diagram diagram = null;
        for(Diagram d : allDiagrams){
            if(d.getKey().equals(uuid)){
                diagram = d;
                break;
            }
        }
        return diagram;
    }

}
