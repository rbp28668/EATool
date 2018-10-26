/*
 * MetaPropertyContainer.java
 * Project: EATool
 * Created on 04-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * MetaPropertyContainer provides common implementation and signatures for
 * all the items in the meta model that contain meta-properties.
 * 
 * @author rbp28668
 */
public abstract class MetaPropertyContainerImpl extends NamedRepositoryItem implements MetaPropertyContainer{

    private HashMap<UUID,MetaProperty> m_properties = new HashMap<UUID,MetaProperty>(); // keyed by property uuid
    private LinkedList<MetaProperty> propertyList = new LinkedList<MetaProperty>(); // to keep appropriate order

    public MetaPropertyContainerImpl(UUID uuid) {
        super(uuid);
    }

    /** This adds a new MetaProperty to the MetaEntity. If an existing meta
     * property with the same UUID already exists then it is replaced.
     * @param mp is the meta-property to be added.
     */
    public MetaProperty addMetaProperty(MetaProperty mp)
    {
        if(m_properties.containsKey(mp.getKey())) { // need to replace existing one
            MetaProperty mpOld = m_properties.get(mp.getKey());
            propertyList.remove(mpOld);
        }
        m_properties.put(mp.getKey(), mp);
        propertyList.addLast(mp);
        mp.setContainer(this);
        return mp;
    }
    
    /** Gets a child meta-property given its key (UUID)
     * @param uuid is the key for the meta-property
     * @return the meta-property corresponding to the key
     */
    public MetaProperty getMetaProperty(UUID uuid) {
       return m_properties.get(uuid);
    }
    
    /** This deletes a named MetaProperty from the MetaEntity.
     * @param name is the name of the property to delete
     * @returns the delete MetaProperty or null if no match for the name
     */
    public MetaProperty deleteMetaProperty(UUID uuid) {
        MetaProperty mp = m_properties.remove(uuid);
        propertyList.remove(mp);
        return mp;
    }


    /** gets a collection of all the meta-properties 
     * for this container.
     * @return a collection of meta-properties.
     */
    public Collection<MetaProperty> getMetaProperties() {
        return propertyList;
    }
    
    /**
     * Sets the list of declared MetaProperties.
     * @param metaProperties
     */
    public void setMetaProperties(MetaProperty[] metaProperties) {
        propertyList.clear();
        for(MetaProperty mp : metaProperties){
            propertyList.addLast(mp);
        }
    }

//    /**
//     * Moves a given MetaProperty up the order list.
//     * @param entry is the MetaProperty to be moved.
//     */
//    public void moveUp(MetaProperty entry) {
//        int idx = propertyList.indexOf(entry);
//        if(idx > 0){
//            propertyList.remove(entry);
//            propertyList.add(idx-1,entry);
//        }
//        
//    }
//
//    /**
//     * Moves a given MetaProperty down the order list.
//     * @param entry
//     */
//    public void moveDown(MetaProperty entry) {
//        int idx = propertyList.indexOf(entry);
//        if(idx != -1  && idx < propertyList.size()-1){
//            propertyList.remove(entry);
//            propertyList.add(idx+1,entry);
//        }
//    }


    /**
     * Writes the MetaEntity out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        for(MetaProperty mp : propertyList){
            mp.writeXML(out);
        }
    }

//    /** copies this meta-entity to a copy
//     * @param copy is the meta entity to copy to
//     */
//    protected void cloneTo(MetaPropertyContainer copy) {
//        super.cloneTo(copy);
//        copy.propertyList = new LinkedList();
//        copy.m_properties = new HashMap();
//        Iterator iter = propertyList.iterator();
//        while(iter.hasNext()) {
//            MetaProperty mp = (MetaProperty)(((MetaProperty)iter.next()).clone());
//            copy.propertyList.addLast(mp);
//            copy.m_properties.put(mp.getKey(), mp);
//            mp.setContainer(copy);
//        }
//    }

}
