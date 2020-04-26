/*
 * MetaRelationshipEnd.java
 *
 * Created on 10 January 2002, 21:54
 */

package alvahouse.eatool.repository.metamodel.impl;

import java.io.IOException;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * This describes a role at the end of a relationship.  MetaRoles act as the glue to
 * link MetaRelationships to MetaEntities and also describe the allowed multiplicity
 * of that end of the relationship.
 * @author  rbp28668
 */
public class MetaRoleImpl extends MetaPropertyContainerImpl implements MetaRole  {

    /** The parent MetaRelationship - this MetaRole describes one end of the relationship*/
    private MetaRelationship metaRelationship = null;
    /** Multiplicity of this relationship */
    private Multiplicity multiplicity = MultiplicityImpl.fromString("one");
    /** The MetaEntity this MetaRole (and hence the parent MetaRelationship) connects to. */
    private MetaEntity connection = null;
    
    /** Creates new MetaRole with the given UUID
     * @param uuid is the identifier to use for this MetaRole. 
     */
    public MetaRoleImpl(UUID uuid) {
        super(uuid);
    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#clone()
//     */
//    public Object clone() {
//        MetaRole copy = new MetaRole(getKey());
//        cloneTo(copy);
//        return copy;
//    }
//    
//    /**
//     * Updates this MetaRole from a copy.  Typically the copy will have been
//     * cloned, edited and is now being used to update the main meta-model.
//     * This fires  MetaModelChangeEvent on the meta model.
//     * @param copy is the copy to be updated from.
//     */
//    public void updateFromCopy(MetaRole copy) {
//        // copy back maintaining same parent.
//        MetaRelationship parent = metaRelationship;
//        copy.cloneTo(this);
//        metaRelationship = parent;
//     }
    
    /**
     * @deprecated
     **/
    public Multiplicity multiplicity() {
        return multiplicity;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#getMultiplicity()
     */
    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#setMultiplicity(alvahouse.eatool.repository.metamodel.Multiplicity)
     */
    @Override
    public void setMultiplicity(Multiplicity m) {
        if(m == null) {
            throw new NullPointerException("Cannot set null multiplicity on MetaRole");
        }
        multiplicity = m;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#setConnection(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    @Override
    public void setConnection(MetaEntity connection) {
        if(connection == null) {
            throw new NullPointerException("Cannot set null connection to MetaEntity on MetaRole");
        }
        this.connection = connection;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#connectsTo()
     */
    @Override
    public MetaEntity connectsTo() {
        return connection;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    @Override
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("MetaRole");
        super.writeAttributesXML(out);
        out.addAttribute("multiplicity",multiplicity.toString());
        out.addAttribute("connects",connection.getKey().toString());
        super.writeXML(out);
        out.stopEntity();
    }

//    /**
//     * Clones this MetaRole to a copy.
//     * @param copy is the copy that will be modified to be a clone of this MetaRole.
//     */
//    protected void cloneTo(MetaRole copy) {
//        super.cloneTo(copy);
//        copy.multiplicity = multiplicity;
//        copy.connection = connection;
//        copy.metaRelationship = null;   // possible different parent.
//    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRole#getMetaRelationship()
     */
    @Override
    public MetaRelationship getMetaRelationship() {
        return metaRelationship;
    }
    
    /**
     * Sets the parent MetaRelationhip.
     * @param mr is the parent to set.
     */
    @Override
    public void setMetaRelationship(MetaRelationship mr) {
        if(mr == null) {
            throw new NullPointerException("Cannot set null MetaRelationship on MetaRole");
        }
        metaRelationship = mr;
    }
    
}

