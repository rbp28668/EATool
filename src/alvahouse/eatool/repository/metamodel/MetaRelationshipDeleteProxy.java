/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent meta-relationship.
 * Allows meta relationships to be marked for possible deletion then deleted by
 * the delete method. 
 * @see IDeleteDependenciesProxy.
 */
public class MetaRelationshipDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final MetaModel metaModel;

	/** Creates a new proxy for deleting a dependent meta-relationshi
     * @param metaModel TODO
     * @param m is the dependent meta-relationship
     */        
    public MetaRelationshipDeleteProxy(MetaModel metaModel, MetaRelationship mr) {
        this.metaModel = metaModel;
		relationship = mr;
    }
    
    /** gets the name of the dependent meta relationship
     * @return name for the dependent meta-relationshp
     */        
    public String toString() {
        return "Meta-Relationship " + relationship.toString();
    }

    /** deletes the dependent meta-relationship
     */
    public void delete() throws Exception {
        this.metaModel.deleteMetaRelationship(relationship.getKey());
    }
    
    /** gets the dependent meta-relationship
     * @return the dependent meta-relationship
     */
    public Object getTarget() {
        return relationship;
    }
    
    private MetaRelationship relationship;
}