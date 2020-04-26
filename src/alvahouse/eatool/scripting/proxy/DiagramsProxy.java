/*
 * Diagrams.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.util.UUID;

/**
 * Diagrams is a proxy class which provides a facade to the diagrams collection
 * for scripting. 
 * 
 * @author rbp28668
 */
public class DiagramsProxy {

    private alvahouse.eatool.repository.graphical.Diagrams diagrams;
    private Repository repository;
    /**
     * Creates a new proxy object tied to an underlying diagrams collection.
     */
    public DiagramsProxy(
            alvahouse.eatool.repository.graphical.Diagrams diagrams,
            Repository repository) {
        super();
        this.diagrams = diagrams;
        this.repository = repository;
    }

    /**
     * Gets a diagram associated with the given key.
     * @param key is the string representation of the UUID that identifies 
     * the diagram to get.
     * @return the corresponding diagram.
     */
    public StandardDiagramProxy getStandardDiagram(String key){
        return new StandardDiagramProxy(
                (alvahouse.eatool.repository.graphical.standard.StandardDiagram)diagrams.lookup(new UUID(key)),
                repository);
    }
    
    /**
     * Creates a new diagram of the given type.
     * @param type is the string representation of the UUID that identifies
     * the diagram type to use when creating the new diagram.
     * @return the corresponding diagram.
     */
    public StandardDiagramProxy newStandardDiagram(String type){
        DiagramTypes types = repository.getDiagramTypes();
        DiagramType dt = types.get(new UUID(type));
        return new StandardDiagramProxy((alvahouse.eatool.repository.graphical.standard.StandardDiagram)dt.newDiagram(new UUID()),repository);
    }

}
