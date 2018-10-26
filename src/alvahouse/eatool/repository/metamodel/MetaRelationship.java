/*
 * MetaRelationship.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.io.IOException;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

public interface MetaRelationship extends MetaPropertyContainer{

    /**
     * Writes the MetaRelationship out as XML.
     * @param out is the XMLWriterDirect to write the XML to.
     * @throws IOException if unable to write XML.
     */
    public abstract void writeXML(XMLWriter out) throws IOException;

    /**
     * Gets the MetaRole at the start end of the MetaRelationship.
     * @return MetaRole at the start end.
     */
    public abstract MetaRole start();

    /**
     * Gets the MetaRole at the finish end of the MetaRelationship.
     * @return MetaRole at the finish end.
     */
    public abstract MetaRole finish();

    /**
     * Gets a MetaRole (at either end) corresponding to the given UUID.  If neither
     * ends match then a new one is created.  If there is no space to create a new
     * end then the call fails.
     * @param uuid is the UUID identifying the MetaRole to return.
     * @return the corresponding MetaRole.
     * @throws IllegalArgumentException if the MetaRole cannot be found or created.
     */
    public abstract MetaRole getMetaRole(UUID uuid);

    /**
     * Get any additional restriction placed on this relationship.
     * @return MetaRelationshipRestriction. Should never be null.
     */
    public abstract MetaRelationshipRestriction getRestriction();

    /**
     * Sets the MetaModel this MetaRelationship belongs to.
     * @param m is the parent MetaModel.
     */
    public void setModel(MetaModel m);
    
    /**
     * Gets the parent MetaModel.
     * @return the parent meta model.
     */
    public MetaModel getModel();
    
}