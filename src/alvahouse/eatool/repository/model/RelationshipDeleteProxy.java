/**
 * 
 */
package alvahouse.eatool.repository.model;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.util.UUID;

/** Proxy class for recording dependent -relationship
 */
public class RelationshipDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	public final static String NAME = "relationship";
	private final UUID targetKey;
	private final String name;
	private final Model model;
	private final String version;

	/** Creates a new proxy for deleting a dependent relationship
     * @param model TODO
     * @param m is the dependent relationship
     */        
    public RelationshipDeleteProxy(Model model, UUID targetKey, String name, String version) {
        this.model = model;
		this.targetKey = targetKey;
		this.name = name;
		this.version = version;
    }
    
    public RelationshipDeleteProxy(Model model, DeleteProxyDto dto) {
    	if(!NAME.equals(dto.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dto.getItemType());
    	}
    	this.model = model;
    	this.targetKey = dto.getItemKey();
    	this.name = dto.getName();
    	this.version = dto.getVersion();
    }
    
    /** gets the name of the dependent  relationship
     * @return name for the dependent relationship
     */        
    public String toString() {
        return "Relationship " + name;
    }

    /** deletes the dependent relationship
     */
    public void delete() throws Exception {
        this.model.deleteRelationship(targetKey, version);
    }
    
    /** gets the dependent -relationship
     * @return the dependent -relationship
     */
    public UUID getTargetKey() {
        return targetKey;
    }
}