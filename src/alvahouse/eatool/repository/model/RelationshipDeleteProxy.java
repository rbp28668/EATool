/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent -relationship
 */
public class RelationshipDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final Model model;

	/** Creates a new proxy for deleting a dependent relationship
     * @param model TODO
     * @param m is the dependent relationship
     */        
    public RelationshipDeleteProxy(Model model, Relationship r) {
        this.model = model;
		relationship = r;
    }
    
    /** gets the name of the dependent  relationship
     * @return name for the dependent relationship
     */        
    public String toString() {
        return "Relationship " + relationship.toString();
    }

    /** deletes the dependent relationship
     */
    public void delete() throws Exception {
        this.model.deleteRelationship(relationship.getKey());
    }
    
    /** gets the dependent -relationship
     * @return the dependent -relationship
     */
    public Object getTarget() {
        return relationship;
    }
    
    private Relationship relationship;
}