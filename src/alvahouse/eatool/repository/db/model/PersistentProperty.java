/*
 * Property.java
 *
 * Created on 11 January 2002, 19:45
 */

package alvahouse.eatool.repository.db.model;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.db.metamodel.PersistentMetaProperty;
import alvahouse.eatool.util.UUID;

/**
 * A single property of an Entity, Relationship or Role.  Note that as the value field has to be mapped to a fixed length field
 * this includes a list of property extension which can contain the spillover if the value is more than the alloted size.
 * @author  rbp28668
 */
public class PersistentProperty extends RepositoryItem  implements Cloneable  {

	/** Size of the value field */
	public static final int VALUE_SIZE = 255;
	
    private PersistentPropertyContainer container; //Entity entityParent;
    private PersistentMetaProperty meta;
    private String value;
    private List<PersistentPropertyExtension> extensions = new LinkedList<PersistentPropertyExtension>();
    
    /**
     * Default constructor for hibernate.
     */
    public PersistentProperty() {
    	super(null);
    }
    
    /** Creates new Property */
    public PersistentProperty(UUID uuid, PersistentMetaProperty mp) {
        super(uuid);
        meta = mp;
        value = mp.getMetaPropertyType().initialise();
    }

    public String getValueExtended(){
    	StringBuilder builder = new StringBuilder(255);
    	builder.append(value);
    	for(PersistentPropertyExtension x : extensions){
    		builder.append(x.getValue());
    	}
    	return builder.toString();
    }
    
    
    public void setValueExtended(String value){
    	extensions.clear();
    	if(value.length() < VALUE_SIZE){
    		this.value = value;
    	} else {  // too long, need to use extensions
    		this.value = value.substring(0, VALUE_SIZE);
    		int pos = VALUE_SIZE;
    		int len = value.length();
    		
    		while(pos < len){
    			String piece = value.substring(pos, pos+VALUE_SIZE);
    			extensions.add(new PersistentPropertyExtension(piece));
    			pos += VALUE_SIZE;
    		}
    	}
    }
    
    /**
     * Sets the value of the property providing it is valid according to the meta-model.
     * @param val is the value to set.
     * @throws IllegalArgumentException if invalid.
     */
    public void setValue(String val) throws IllegalArgumentException {
        value = val;
     }
    
    /**
     * Get the value of this property.
     * @return the Property's value.
     */
    public String getValue() {
        return value;
    }
    
    
    /** gets the meta property that this property is an instance of
     * @return the associated meta property
     */
    public PersistentMetaProperty getMeta() {
        return meta;
    }
    
    /**
	 * @param meta the meta to set
	 */
	public void setMeta(PersistentMetaProperty meta) {
		this.meta = meta;
	}

    /**
	 * @return the extensions
	 */
	public List<PersistentPropertyExtension> getExtensions() {
		return extensions;
	}

	/**
	 * @param extensions the extensions to set
	 */
	public void setExtensions(List<PersistentPropertyExtension> extensions) {
		this.extensions = extensions;
	}


	/** sets the parent entity for this property
     * e is the parent entity
     */
    void setContainer(PersistentPropertyContainer container) {
        this.container = container;
    }
    
    /** gets the parent entity for this property
     * @return the parent entity
     */
    public PersistentPropertyContainer getContainer() {
        return container;
    }
    
     
}
