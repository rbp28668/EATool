/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent meta-relationship
 */
public class MetaRelationshipDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final MetaModel metaModelImpl;

	/** Creates a new proxy for deleting a dependent meta-relationshi
     * @param metaModelImpl TODO
     * @param m is the dependent meta-relationship
     */        
    public MetaRelationshipDeleteProxy(MetaModel metaModelImpl, MetaRelationship mr) {
        this.metaModelImpl = metaModelImpl;
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
        this.metaModelImpl.deleteMetaRelationship(relationship.getKey());
    }
    
    /** gets the dependent meta-relationship
     * @return the dependent meta-relationship
     */
    public Object getTarget() {
        return relationship;
    }
    
    private MetaRelationship relationship;
}