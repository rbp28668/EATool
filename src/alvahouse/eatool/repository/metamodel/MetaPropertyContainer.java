/*
 * MetaPropertyContainer.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.io.IOException;
import java.util.Collection;

import alvahouse.eatool.repository.base.NamedItem;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

public interface MetaPropertyContainer extends NamedItem{

    /** This adds a new MetaProperty to the MetaEntity. If an existing meta
     * property with the same UUID already exists then it is replaced.
     * @param mp is the meta-property to be added.
     * @throws Exception 
     */
    public abstract MetaProperty addMetaProperty(MetaProperty mp) throws Exception;
    
    /** Gets a child meta-property given its key (UUID)
     * @param uuid is the key for the meta-property
     * @return the meta-property corresponding to the key
     */
    public MetaProperty getMetaProperty(UUID uuid);
    
    /** This deletes a named MetaProperty from the MetaEntity.
     * @param name is the name of the property to delete
     * @throws Exception 
     * @returns the delete MetaProperty or null if no match for the name
     */
    public MetaProperty deleteMetaProperty(UUID uuid) throws Exception;


    /** gets a collection of all the meta-properties 
     * for this container.  Note that where the container implements inheritance
     * (e.g. has a base definition) this should return all the meta properties
     * in the hierarchy. 
     * @return a collection of meta-properties.
     */
    public Collection<MetaProperty> getMetaProperties();

    /**
     * gets a collection of MetaProperties declared in this Meta Property Containers
     * only ignoring any parent containers.
     * @return a collection of MetaProperty.
     */
    public abstract Collection<MetaProperty> getDeclaredMetaProperties();

    /**
     * Sets the list of declared MetaProperties.
     * @param metaProperties
     */
    public abstract void setDeclaredMetaProperties(MetaProperty[] metaProperties);


    
    /**
     * Writes the MetaEntity out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public abstract void writeXML(XMLWriter out) throws IOException;
 
}