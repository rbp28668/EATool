/*
 * Entity.java
 *
 * Created on 11 January 2002, 01:36
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;
/**
 * This models a "thing" in the model.  Entities are of a given type described
 * by an attached @link alvahouse.eatool.repository.metamodel.MetaEntity.
 * @author  rbp28668
 */
/**
 * Entity
 * 
 * @author rbp28668
 */
public class Entity extends PropertyContainer implements Versionable {

    private MetaEntity meta;
    private Model model;                                // that this belongs to
    private VersionImpl version = new VersionImpl();
    
    /** Creates new Entity of a given type.
     * @param me is the  MetaEntity that describes the type of the entity.
    */
    public Entity(MetaEntity me) {
        super(new UUID());
        addDefaultProperties(me);
        meta = me;
    }
    
    /** Creates new Entity where the uuid is already known - such as when
     * de-serialising a model. 
     * @param uuid is the UUID that should be used to uniquely identify
     * this Entity.
     * @param me is the MetaEntity that describes the type of the entity.
     * */
    public Entity(UUID uuid, MetaEntity me) {
        super(uuid);
        addDefaultProperties(me);
        meta = me;
    }

//	/**
//	 * Copy constructor for clone.
//	 * @param source is the Entity to clone from.
//	 */
//	protected Entity(Entity source){
//		super(source.getKey());
//		source.cloneTo(this);
//	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        MetaEntity me = getMeta();
        MetaEntityDisplayHint dh = me.getDisplayHint();
        if(dh != null)
            return getName(dh);
        else
            return getKey().toString();
    }

    /** Uses the keys to work out what the (display) name for an
     * entity should be
     * @param e is the entity to get the name for
     * @returns the name for the entity
     */
    private String getName(MetaEntityDisplayHint dh) {
        
        StringBuffer buff = new StringBuffer();
        for(UUID key : dh.getKeys()){
            Property p = getPropertyByMeta(key);
            if(p != null) {
                if(buff.length() > 0)
                    buff.append(":");
                buff.append(p.getValue());
            }
        }
        
        if(buff.length() == 0)
            return getKey().toString();
        else
            return buff.toString();
    }

//    /** Creates a copy of the entity
//     * @return a new entity (with the same key)
//     */
//    public Object clone() {
//        Entity copy = new Entity(this);
//        return copy;
//    }
    
//    /** Updates this meta entity from a copy.  Used for editing - the 
//     * copy should be edited (use clone to get the copy) and only if the
//     * edit is successful should the original be updated by calling this
//     * method on the original meta-entity.  This ensures that any listeners
//     * are updated properly.
//     * @param copy is the meta-entity to copy from
//     */
//    public void updateFromCopy(Entity copy) {
//
//        // Update from copy - cloneTo clears model so save & restore
//        Model parent = getModel();
//        copy.cloneTo(this);
//        setModel(parent);
//       
//        // Fire change events.
//        if(model != null){
//            model.fireEntityChanged(this);
//        }
//    }
    
    /** gets the meta entity that this entity is an instance of.
     * @return the associated meta entity
     */
    public MetaEntity getMeta() {
        return meta;
    }
    
     /**
     * Writes the Entity out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Entity");
        out.addAttribute("uuid",getKey().toString());
        out.addAttribute("instanceof",meta.getKey().toString());
        version.writeXML(out);
        super.writeXML(out);
        
        out.stopEntity();
    }

//    /** This adds a new Property to the Entity. This replaces the existing property
//     * that shares the same meta-property.
//     * @param prop is the property to be added.
//     */
//    public Property addProperty(Property prop)
//    {
//        Property propOld = (Property)properties.get(prop.getMeta().uuid());
//        if(propOld == null)
//            throw new IllegalArgumentException("Can't find property to replace when updating entity");
//        
//        int idx = propertyList.indexOf(propOld);
//        propertyList.set(idx, prop);
//        properties.put(prop.getMeta().uuid(), prop);
//
//        prop.setEntity(this);
//        if(model != null)
//            model.firePropertyChanged(prop);
//        return prop;
//    }

    
//    /** This deletes a named Property from the Entity.
//     * @param name is the name of the property to delete
//     * @returns the delete Property or null if no match for the name
//     */
//    public Property deleteProperty(UUID uuid) {
//         Property prop = (Property)properties.remove(uuid);
//        propertyList.remove(prop);
//        if(model != null)
//            model.firePropertyDeleted(prop);
//        return prop;
//    }

     
     /** Sets the parent model
     * @param m is the model to set
     */
    void setModel(Model m) {
        model = m;
    }
    
    /** Gets the parent model.
     * @return the parent model.
     */
    public Model getModel() {
        return model;
    }

//    /** where properties exist in the source entity, they are copied to
//     * the destination entity.  Note that this is not the same as a clone!
//     * @param source is the source entity to copy from.
//     */
//    public void updatePropertiesFrom(Entity source) {
//        
//        if(getMeta() != source.getMeta())
//            throw new IllegalArgumentException("Cannot update Entity type " 
//                + getMeta().getName() + " from type " + source.getMeta().getName());
//        
//        Iterator iter = source.getProperties().iterator();
//        while(iter.hasNext()) {
//            Property p = (Property)iter.next();
//            
//            boolean propertyExists = false;
//
//            // Look for property to update - should share the same meta-property
//            Iterator iterProperty = getProperties().iterator();
//            while(iterProperty.hasNext()) {
//                Property dest = (Property)iter.next();
//                if(dest.getMeta() == p.getMeta()) {
//                    dest.setValue(p.getValue());
//                    propertyExists = true;
//                    break;
//                }
//            }
//            if(!propertyExists)
//                throw new IllegalArgumentException("Unable to update properties from instance of " + source.getMeta().getName());
//        }
//        if(model != null) model.fireEntityChanged(this);
//    }
    
    /** This gets the set of relationships that are connected to this entity in the model
     * that the entity belongs to.  Note - this is in no-way optimised.
     * @return the set of connected relationships.
     */
    public Set<Relationship> getConnectedRelationships() {
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        Set<Relationship> rels = new HashSet<Relationship>();
        for(Relationship rel : model.getRelationships()) {
            if(rel.start().connectsTo().equals(this) ||
                rel.finish().connectsTo().equals(this)) {
                rels.add(rel);
            }
        }
        return rels;
    }

    /** This gets the set of relationships that are connected to this entity in the model
     * that the entity belongs to.  Note - this is in no-way optimised.
     * @return the set of connected relationships.
     */
    public Set<Relationship> getConnectedRelationshipsOf(MetaRelationship meta) {
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        Set<Relationship> rels = new HashSet<Relationship>();
        for(Relationship rel :  model.getRelationships()) {
            if((rel.start().connectsTo().equals(this) ||
                rel.finish().connectsTo().equals(this)) &&
                rel.getMeta().equals(meta)) {
                rels.add(rel);
            }
        }
        return rels;
    }
    
//     /** copies this entity to a copy.
//     * @param copy is the entity to copy to.
//     */
//    protected void cloneTo(Entity copy) {
//        super.cloneTo(copy);
//        
//        copy.meta = meta;   // must be same type
//        copy.model = null;  // always disconnect copies from model
//        version.cloneTo(copy.version);
//        super.cloneTo(copy);
//    }

    

    /**
     * Compare is a Comparator class to allow sorting etc of Entities. 
     * 
     * @author rbp28668
     */
    public static class Compare implements Comparator<Entity> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Entity arg0, Entity arg1) {
            return arg0.toString().compareToIgnoreCase(arg1.toString());
        }
        
    }

   /* (non-Javadoc)
 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
 */
public Version getVersion() {
        return version;
    }
    

}
