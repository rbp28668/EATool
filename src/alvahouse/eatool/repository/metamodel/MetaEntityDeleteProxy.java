/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent meta-entities
 */
public class MetaEntityDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final MetaModel metaModelImpl;

	/** Creates a new proxy for deleting dependent meta-entities
     * @param me is the dependent meta-entity
     * @param metaModelImpl TODO
     */        
    public MetaEntityDeleteProxy(MetaModel metaModelImpl, MetaEntity me) {
        this.metaModelImpl = metaModelImpl;
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
        this.metaModelImpl.deleteMetaEntity(entity.getKey());
    }
    
    /** gets the dependent meta-entity
     * @return the dependent meta-entity
     */        
    public Object getTarget() {
        return entity;
    }
    
    private MetaEntity entity;
}