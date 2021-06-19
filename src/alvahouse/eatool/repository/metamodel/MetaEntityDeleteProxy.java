/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dao.DeleteProxyDao;
import alvahouse.eatool.util.UUID;

/** Proxy class for recording dependent meta-entities. This allows 
 * MetaEntities to be "marked for deletion" then the delete method
 * tells the repository to delete them when (or if) called.
 */
public class MetaEntityDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	public final static String NAME = "metaEntity";
	private final MetaModel metaModel;
	private final String name;
	private final UUID targetKey;

	/** Creates a new proxy for deleting dependent meta-entities
     * @param me is the dependent meta-entity
     * @param metaModel is the meta model that the meta entity belongs to.
     */        
    public MetaEntityDeleteProxy(MetaModel metaModel, UUID targetKey, String name) {
        this.metaModel = metaModel;
        this.targetKey = targetKey;
		this.name = name;
    }
    
    public MetaEntityDeleteProxy(MetaModel metaModel, DeleteProxyDao dao) {
    	if(!NAME.equals(dao.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dao.getItemType());
    	}
    	this.metaModel = metaModel;
    	this.targetKey = dao.getItemKey();
    	this.name = dao.getName();
    }
    
    /** gets the name of the dependent meta entity
     * @return name for the dependent meta-entity
     */        
    public String toString() {
        return "Meta-Entity " + name;
    }

    /** deletes the dependent meta-entity
     */
    public void delete() throws Exception {
        this.metaModel.deleteMetaEntity(targetKey);
    }
    
    /** gets the dependent meta-entity
     * @return the dependent meta-entity
     */        
    public UUID getTargetKey() {
        return targetKey;
    }
    
}