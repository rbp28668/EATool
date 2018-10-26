/*
 * Property.java
 *
 * Created on 11 January 2002, 19:45
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * A single property of an Entity, Relationship or Role.
 * @author  rbp28668
 */
public class Property extends RepositoryItem  implements Cloneable  {

    private PropertyContainer container; //Entity entityParent;
    private MetaProperty meta;
    private String value;
    
    /** Creates new Property */
    public Property(UUID uuid, MetaProperty mp) {
        super(uuid);
        meta = mp;
        value = mp.getMetaPropertyType().initialise();
    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#clone()
//     */
//    public Object clone() {
//        Property copy = new Property(getKey(),meta);
//        cloneTo(copy);
//        return copy;
//    }
//
//    /**
//     * Update this Property from a copy while retaining any original
//     * parent entity.
//     * @param copy is the copy to update from.
//     */
//    public void updateFromCopy(Property copy) {
//        // copy back maintaining orignal parent
//        PropertyContainer parent = container;
//        copy.cloneTo(this);
//        container = parent;
//    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getMeta().getName() + " : " + getValue();
    }
    
    /**
     * Sets the value of the property providing it is valid according to the meta-model.
     * @param val is the value to set.
     * @throws IllegalArgumentException if invalid.
     */
    public void setValue(String val) throws IllegalArgumentException {
        MetaPropertyType type = meta.getMetaPropertyType();
        if(val.trim().length()==0){
            if(meta.isMandatory()){
                throw new IllegalArgumentException("Property " + meta.getName() + " is mandatory");
            }
        } else {
            type.validate(val);
        }
        value = val;
     }
    
    /**
     * Get the value of this property.
     * @return the Property's value.
     */
    public String getValue() {
        return value;
    }
    
//    /** sets the meta property that this property is an instance of
//     * @param me is the associated meta property
//     */
//    public void setMeta(MetaProperty mp) {
//        meta = mp;
//    }
    
    /** gets the meta property that this property is an instance of
     * @return the associated meta property
     */
    public MetaProperty getMeta() {
        return meta;
    }
    
    /** sets the parent entity for this property
     * e is the parent entity
     */
    void setContainer(PropertyContainer container) {
        this.container = container;
    }
    
    /** gets the parent entity for this property
     * @return the parent entity
     */
    public PropertyContainer getContainer() {
        return container;
    }
    
     /**
     * Writes the Property out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Property");
        out.addAttribute("uuid",getKey().toString());
        out.addAttribute("instanceof",meta.getKey().toString());
        out.text(value);
        out.stopEntity();
    }

//    /** copies the values of one instance to a copy
//     * @param copy is the property to copy the values to
//     */
//    protected void cloneTo(Property copy) {
//        super.cloneTo(copy);
//        copy.container = null; // disconnect copy
//        copy.meta = meta;
//        copy.value = value; // no clone as strings immutable
//     }
    
    /**
     * Process this property assuming the data type has changed.
     * Either it validates or, if not, it should be set to the
     * default value.
     * @return true if changed.
     */
     boolean revalidate(){
         boolean changed = false;
        try {
            value = meta.getMetaPropertyType().validate(value);
        } catch(IllegalArgumentException x){
            value = meta.getMetaPropertyType().initialise();
            changed = true;
        }
        return changed;
    }
    
}
