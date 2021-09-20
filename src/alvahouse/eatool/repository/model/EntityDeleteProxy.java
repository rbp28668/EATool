/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
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
	private final String version;

	/** Creates a new proxy for deleting dependent entities
     * @param model TODO
     * @param me is the dependent entity
     */        
    public EntityDeleteProxy(Model model, UUID targetKey, String name, String version) {
        this.model = model;
		this.targetKey = targetKey;
		this.name = name;
		this.version = version;
    }
    
    public EntityDeleteProxy(Model model, DeleteProxyDto dto) {
    	if(!NAME.equals(dto.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dto.getItemType());
    	}
    	this.model = model;
    	this.targetKey = dto.getItemKey();
    	this.name = dto.getName();
    	this.version = dto.getVersion();
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
        this.model.deleteEntity(targetKey, version);
    }
    
    /** gets the dependent entity
     * @return the dependent entity
     */        
    public UUID getTargetKey() {
        return targetKey;
    }
    
 }