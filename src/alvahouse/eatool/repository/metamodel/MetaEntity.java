/*
 * MetaEntity.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.io.IOException;

import alvahouse.eatool.util.XMLWriter;

public interface MetaEntity extends MetaPropertyContainer{

    /**
     * Sets the base MetaEntity that this MetaEntity inherits from. This allows the
     * meta model to provide inheritance of entities.
     * @param base is the MetaEntities superclass.
     */
    public abstract void setBase(MetaEntity base);

    /** 
     * Gets the base MetaEntity this MetaEntity inherits from 
     */
    public abstract MetaEntity getBase();

    /**
     * Sets a MetaEntity as being abstract.  If abstract it cannot be instantiated
     * in a model but can be used as a base entity for other MetaEntities to
     * inherit from.
     * @param isAbstract sets this MetaEntity abstract if true, concrete if false.
     */
    public abstract void setAbstract(boolean isAbstract);

    /**
     * Determines whether this MetaEntity is abstract
     */
    public abstract boolean isAbstract();

    /** sets a display hint for this meta-entity
     * @param hint is the display hint to set.
     */
    public abstract void setDisplayHint(MetaEntityDisplayHint hint);

    /** get any display hint from this meta-entity (or it's base)
     * @returns the display hint, or null if none found.
     */
    public abstract MetaEntityDisplayHint getDisplayHint();

    /**
     * Writes the MetaEntity out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public abstract void writeXML(XMLWriter out) throws IOException;

    /** Gets the parent meta-model
     * @return the parent meta-model
     */
    public abstract MetaModel getModel();

    /** Sets the parent meta-model
     * @param m is the meta model to set
     */
    public void setModel(MetaModel m);
    
 
}