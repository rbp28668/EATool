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

	/** Creates a new proxy for deleting a dependent relationship
     * @param model TODO
     * @param m is the dependent relationship
     */        
    public RelationshipDeleteProxy(Model model, UUID targetKey, String name) {
        this.model = model;
		this.targetKey = targetKey;
		this.name = name;
    }
    
    public RelationshipDeleteProxy(Model model, DeleteProxyDto dao) {
    	if(!NAME.equals(dao.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dao.getItemType());
    	}
    	this.model = model;
    	this.targetKey = dao.getItemKey();
    	this.name = dao.getName();
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
        this.model.deleteRelationship(targetKey);
    }
    
    /** gets the dependent -relationship
     * @return the dependent -relationship
     */
    public UUID getTargetKey() {
        return targetKey;
    }
}