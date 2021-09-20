/**
 * 
 */
package alvahouse.eatool.repository.metamodel;

import alvahouse.eatool.repository.base.IDeleteDependenciesProxy;
import alvahouse.eatool.repository.dto.DeleteProxyDto;
import alvahouse.eatool.util.UUID;

/** Proxy class for recording dependent meta-relationship.
 * Allows meta relationships to be marked for possible deletion then deleted by
 * the delete method. 
 * @see IDeleteDependenciesProxy.
 */
public class MetaRelationshipDeleteProxy implements IDeleteDependenciesProxy {
    /**
	 * 
	 */
	public final static String NAME = "metaRelationship";
	private final MetaModel metaModel;
	private final String name;
	private final UUID targetKey;
	private final String version;

	/** Creates a new proxy for deleting a dependent meta-relationshi
     * @param metaModel TODO
     * @param m is the dependent meta-relationship
     */        
    public MetaRelationshipDeleteProxy(MetaModel metaModel, UUID targetKey, String name, String version) {
        this.metaModel = metaModel;
        this.targetKey = targetKey;
		this.name = name;
		this.version = version;
    }
    
    public MetaRelationshipDeleteProxy(MetaModel metaModel, DeleteProxyDto dto) {
    	if(!NAME.equals(dto.getItemType())){
    		throw new IllegalArgumentException("Invalid Delete proxy, expected " + NAME + " was " + dto.getItemType());
    	}
    	this.metaModel = metaModel;
    	this.targetKey = dto.getItemKey();
    	this.name = dto.getName();
    	this.version = dto.getVersion();
    }
    
    /** gets the name of the dependent meta relationship
     * @return name for the dependent meta-relationshp
     */        
    public String toString() {
        return "Meta-Relationship " + name;
    }

    /** deletes the dependent meta-relationship
     */
    public void delete() throws Exception {
        this.metaModel.deleteMetaRelationship(targetKey, version);
    }
    
    /** gets the dependent meta-relationship
     * @return the dependent meta-relationship
     */
    public UUID getTargetKey() {
        return targetKey;
    }
    
 }