/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent meta-entities. This allows 
 * MetaEntities to be "marked for deletion" then the delete method
 * tells the repository to delete them when (or if) called.
 */
public class MetaEntityDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final MetaModel metaModel;

	/** Creates a new proxy for deleting dependent meta-entities
     * @param me is the dependent meta-entity
     * @param metaModel is the meta model that the meta entity belongs to.
     */        
    public MetaEntityDeleteProxy(MetaModel metaModel, MetaEntity me) {
        this.metaModel = metaModel;
		entity = me;
    }
    
    /** gets the name of the dependent meta entity
     * @return name for the dependent meta-entity
     */        
    public String toString() {
        return "Meta-Entity " + entity.toString();
    }

    /** deletes the dependent meta-entity
     */
    public void delete() throws Exception {
        this.metaModel.deleteMetaEntity(entity.getKey());
    }
    
    /** gets the dependent meta-entity
     * @return the dependent meta-entity
     */        
    public Object getTarget() {
        return entity;
    }
    
    private MetaEntity entity;
}