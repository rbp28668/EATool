/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;

/** Proxy class for recording dependent -entities
 */
public class EntityDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	private final Model model;

	/** Creates a new proxy for deleting dependent entities
     * @param model TODO
     * @param me is the dependent entity
     */        
    public EntityDeleteProxy(Model model, Entity e) {
        this.model = model;
		entity = e;
    }
    
    /** gets the name of the dependent  entity
     * @return name for the dependent entity
     */        
    public String toString() {
        return "Entity " + entity.toString();
    }

    /** deletes the dependent entity
     */
    public void delete() throws Exception {
        this.model.deleteEntity(entity.getKey());
    }
    
    /** gets the dependent entity
     * @return the dependent entity
     */        
    public Object getTarget() {
        return entity;
    }
    
    private Entity entity;
}