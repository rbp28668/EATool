/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
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
	private final String version;

	/** Creates a new proxy for deleting dependent meta-entities
     * @param me is the dependent meta-entity
     * @param metaModel is the meta model that the meta entity belongs to.
     */        
    public MetaEntityDeleteProxy(MetaModel metaModel, UUID targetKey, String name, String version) {
        this.metaModel = metaModel;
        this.targetKey = targetKey;
		this.name = name;
		this.version = version;
    }
    
    public MetaEntityDeleteProxy(MetaModel metaModel, DeleteProxyDto dao) {
    	if(!NAME.equals(dao.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dao.getItemType());
    	}
    	this.metaModel = metaModel;
    	this.targetKey = dao.getItemKey();
    	this.name = dao.getName();
    	this.version = dao.getVersion();
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
        this.metaModel.deleteMetaEntity(targetKey, version);
    }
    
    /** gets the dependent meta-entity
     * @return the dependent meta-entity
     */        
    public UUID getTargetKey() {
        return targetKey;
    }
    
}