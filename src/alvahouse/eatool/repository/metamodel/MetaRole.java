/*
 * MetaRole.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.io.IOException;

import alvahouse.eatool.util.XMLWriter;

public interface MetaRole extends MetaPropertyContainer{

    /**
     * Gets the allowed multiplicity for this meta-role.
     * @return the multiplicity.
     */
    public abstract Multiplicity getMultiplicity();

    /**
     * Sets the allowed multiplicity for this meta-role.
     * @param m is the multiplicity to set.
     */
    public abstract void setMultiplicity(Multiplicity m);

    /**
     * Connects this MetaRole to a MetaEntity.  
     * @param connection is the MetaEntity to connect to.
     */
    public abstract void setConnection(MetaEntity connection);

    /**
     * Gets the MetaEntity this MetaRole connects to.
     * @return the associated MetaEntity.
     */
    public abstract MetaEntity connectsTo();

    /**
     * Writes the MetaRole out as XML.
     * @param out is the XMLWriterDirect to write the XML to.
     */
    public abstract void writeXML(XMLWriter out) throws IOException;

    /**
     * Gets the MetaRelationship this MetaRole belongs to. 
     * @return the parent MetaRelationship.
     */
    public abstract MetaRelationship getMetaRelationship();
    
    /**
     * Sets the parent MetaRelationhip.
     * @param mr is the parent to set.
     */
    public abstract void setMetaRelationship(MetaRelationship mr);


}