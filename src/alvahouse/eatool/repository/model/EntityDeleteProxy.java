/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dao.DeleteProxyDao;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.UUID;

/** Proxy class for recording dependent -entities
 */
public class EntityDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	public final static String NAME = "entity";
	private final UUID targetKey;
	private final String name;
	private final Model model;

	/** Creates a new proxy for deleting dependent entities
     * @param model TODO
     * @param me is the dependent entity
     */        
    public EntityDeleteProxy(Model model, UUID targetKey, String name) {
        this.model = model;
		this.targetKey = targetKey;
		this.name = name;
    }
    
    public EntityDeleteProxy(Model model, DeleteProxyDao dao) {
    	if(!NAME.equals(dao.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dao.getItemType());
    	}
    	this.model = model;
    	this.targetKey = dao.getItemKey();
    	this.name = dao.getName();
    }
    
    /** gets the name of the dependent  entity
     * @return name for the dependent entity
     */        
    public String toString() {
        return "Entity " + name;
    }

    /** deletes the dependent entity
     */
    public void delete() throws Exception {
        this.model.deleteEntity(targetKey);
    }
    
    /** gets the dependent entity
     * @return the dependent entity
     */        
    public UUID getTargetKey() {
        return targetKey;
    }
    
 }