/*
 * PropertyContainer.java
 * Project: EATool
 * Created on 05-Jul-2006
 *
 */
package alvahouse.eatool.repository.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.dao.model.PropertyContainerDao;
import alvahouse.eatool.repository.dao.model.PropertyDao;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * PropertyContainer provides a generic base class for any class with
 * a set of properties. 
 * 
 * @author rbp28668
 */
public abstract class PropertyContainer extends RepositoryItem{

    private HashMap<UUID,Property> properties = new HashMap<UUID,Property>();         // keyed by META-property uuid
    private LinkedList<Property> propertyList = new LinkedList<Property>(); // to keep appropriate order

    /**
     * 
     */
    public PropertyContainer(UUID uuid) {
        super(uuid);
    }
    
    public PropertyContainer(PropertyContainerDao dao, MetaPropertyContainer meta) throws Exception{
    	super(dao);
    	for(PropertyDao pdao : dao.getProperties()) {
    		addProperty(new Property(pdao, this, meta));
    	}
    }
    
    /** Gets a child property given its key (UUID)
     * @param uuidMeta is the key for the property
     * @return the property corresponding to the key
     */
    public Property getPropertyByMeta(UUID uuidMeta) {
        return (Property)properties.get(uuidMeta);
    }

    /** gets an iterator that can be used to retrieve all the -properties
     * for this -entity
     * @return an iterator for -properties
     */
    public Collection<Property> getProperties() {
        //return m_properties.values().iterator();
        return Collections.unmodifiableCollection(propertyList);
    }
    
    /** gets all the  properties as an array of objects
     * @return an array of the  properties
     */
    public Property[] getPropertiesAsArray() {
        return propertyList.toArray(new Property[propertyList.size()]);
    }
    
    /** gets the number properties in this entity.  
     * @return the number of properties
     */
    public int getPropertyCount() {
        return propertyList.size();
    }
    
    /**
     * Determines whether this has any properties.
     * @return true if this container has any properties, false if not.
     */
    public boolean hasProperties() {
    	return !propertyList.isEmpty();
    }
    /**
     * Adds a property - should only be called in response
     * to a change in the meta-model.
     * @param p is the property to add.
     */
    protected void addProperty(Property p){
        if(p == null){
            throw new NullPointerException("Can't add null property");
        }
        propertyList.add(p);
        properties.put(p.getMeta().getKey(), p);
    }
    
    /**
     * Removes a property.  Should only be called
     * in response to a change in the meta-model.
     * @param p is the property to remove.
     */
    protected void removeProperty(Property p){
        propertyList.remove(p);
        properties.remove(p.getMeta().getKey());
    }
    
    /** copies this entity to a copy.
     * @param copy is the entity to copy to.
     */
    protected void cloneTo(PropertyContainer copy) {
        super.cloneTo(copy);
        
        copy.propertyList.clear();
        copy.properties.clear();
        for(Property property : propertyList) {
            Property p = (Property) property.clone();
            copy.propertyList.addLast(p);
            copy.properties.put(p.getMeta().getKey(), p);
            p.setContainer(copy);
        }
    }
    
    /**
     * Copies the property container to its corresponding DAO
     * @param dao is the corresponding DAO to initialise from this container.
     */
    protected void copyTo(PropertyContainerDao dao) {
 	   super.copyTo(dao);
       for(Property property : propertyList) {
    	   dao.getProperties().add( property.toDao());
       }
    }

    /** Method for adding default properties to a new meta-entity. Note that getMetaProperties
     * recurses through any base classes therefore this doesn't need to.
     * @param m is the meta-entity to add properties from.
     */
    protected void addDefaultProperties(MetaPropertyContainer meta) throws Exception {

        for(MetaProperty mp : meta.getMetaProperties()){
            Property p = new Property(new UUID(), mp);
            p.setContainer(this);
            addProperty(p);
        }
    }
    
    /**
     * Makes sure the entity matches its meta-entity.  Call
     * after changes to the meta-model.
     * @param meta is the MetaPropertyContainer that describes which
     * properties this <b>should</b> have.
     * @return true if changed, false if not.
     */
    boolean revalidate(MetaPropertyContainer meta) throws Exception{
        Collection<MetaProperty> metaProperties = meta.getMetaProperties();
        
        Set<MetaProperty> template = new HashSet<MetaProperty>();
        template.addAll(metaProperties);
        Set<Property> current = new HashSet<Property>();
        current.addAll(getProperties());
        
        boolean changed = false;
            
        // Run through the current set of properties:
        // Any not in the template should go.
        // They should all validate against the template.
        // Any left in the template should be added....
        for(Property p : current){
            if(template.remove(p.getMeta())) {
                // ok, property should still be there - just check its type.
                changed |= p.revalidate();
            } else {
                // Not in template, remove it.
                removeProperty(p);
                changed = true;
            }
        }
        
        for(MetaProperty mp : template){
            Property p = new Property(new UUID(), mp);
            addProperty(p);
            changed = true;
        }
        return changed;
    }
    
    /**
     * Writes the properties out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        
        for(Property p : propertyList){
            p.writeXML(out);
        }
    }
    

}
