/*
 * MetaProperty.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.db.metamodel;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedItem;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.util.XMLWriter;

public interface PersistentMetaProperty extends NamedItem{

    /**
     * Gets the data type of this MetaProperty. 
     * @return the MetaPropertyType giving the data type.
     */
    public abstract MetaPropertyType getMetaPropertyType();

    /**
     * Sets the data type of this MetaProperty.
     * @param type is the MetaPropertyType to set.
     */
    public abstract void setMetaPropertyType(MetaPropertyType type);

    /**
     * Get whether Properties of this type are mandatory.
     * @return true if mandatory.
     */
    public abstract boolean isMandatory();

    /**
     * Sets whether Properties of this type are mandatory.
     * @param isMandatory is true if mandatory.
     */
    public abstract void setMandatory(boolean isMandatory);

    /**
     * Gets whether properties of this type should be read-only.
     * @return Returns the read-only status.
     */
    public abstract boolean isReadOnly();

    /**
     * Sets whether properties of this type should be read-only.
     * @param readOnly sets the read-only status.
     */
    public abstract void setReadOnly(boolean readOnly);

    /**
     * Gets whether properties of this type should be included in a summary of the entity.
     * @return Returns the summary status.
     */
    public abstract boolean isSummary();

    /**
     * Sets whether properties of this type should be included in a summary of the entity.
     * @param summary sets the summary status.
     */
    public abstract void setSummary(boolean summary);

    /**
     * Get the default value for any new Properties of this type.
     * @return the default value.
     */
    public abstract String getDefaultValue();

    /**
     * Sets the default value for any new Properties of this type.
     * @param def is the default value.
     */
    public abstract void setDefaultValue(String def);

    /**
     * Writes the MetaProperty out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public abstract void writeXML(XMLWriter out) throws IOException;

    /** gets the parent meta entity for this property
     * @return the parent meta entity
     */
    public abstract PersistentMetaPropertyContainer getContainer();

    /** sets the parent meta entity for this property
     * package scope so that the meta-model can maintain its integrety
     * @param me is the parent meta-entity for this meta-property
     */
    public abstract  void setContainer(PersistentMetaPropertyContainer container);

}