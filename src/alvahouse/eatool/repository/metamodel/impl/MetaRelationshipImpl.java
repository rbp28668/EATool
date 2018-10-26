/*
 * MetaRelationship.java
 *
 * Created on 10 January 2002, 21:41
 */

package alvahouse.eatool.repository.metamodel.impl;

import java.io.IOException;
import java.util.Comparator;

import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;
/**
 * MetaRelationship describes a class of Relationships in a model.  
 * @author  rbp28668
 */
public class MetaRelationshipImpl extends MetaPropertyContainerImpl implements Versionable, MetaRelationship {

    /** The 2 ends of the relationship */
    private MetaRole m_ends[] = new MetaRoleImpl[2];
    /** The metamodel this belongs to */
    private MetaModel model = null;
    /** Any additional restrictions this MetaRelationship imposes */
    private MetaRelationshipRestriction restriction = MetaRelationshipRestrictionImpl.NONE;

    private VersionImpl version = new VersionImpl();
    
    /** Creates new MetaRelationship with a given UUID*/
    public MetaRelationshipImpl(UUID uuid) {
        super(uuid);
        m_ends[0] = null;
        m_ends[1] = null;
    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#clone()
//     */
//    public Object clone() {
//        MetaRelationship copy = new MetaRelationship(getKey());
//        cloneTo(copy);
//        return copy;
//    }
//    
//    /**
//     * Converse of clone, this updates this MetaRelationship from a copy.
//     * @param copy is the copy to update from.
//     */
//    public void updateFromCopy(MetaRelationship copy) {
//        MetaModel parent = getModel();
//        copy.cloneTo(this);
//        setModel(parent);
//        
//        if(model != null) {
//            model.fireMetaRelationshipChanged(this);
//        }
//    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationship#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("MetaRelationship");
        super.writeAttributesXML(out);
        version.writeXML(out);
        start().writeXML(out);
        finish().writeXML(out);
        super.writeXML(out);
        out.stopEntity();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationship#start()
     */
    public MetaRole start() {
        return m_ends[0];
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationship#finish()
     */
    public MetaRole finish() {
        return m_ends[1];
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationship#getMetaRole(alvahouse.eatool.util.UUID)
     */
    public MetaRole getMetaRole(UUID uuid) {
        // Return any match of existing role
        for(int i=0; i<2; ++i) {
            if(m_ends[i] != null) {
                if(m_ends[i].getKey().equals(uuid))
                    return m_ends[i];
            } 
        }

        // Now look for free slot & create new 
        for(int i=0; i<2; ++i) {
            if(m_ends[i] == null) {
                m_ends[i] = new MetaRoleImpl(uuid);
                m_ends[i].setMetaRelationship(this);
                return m_ends[i];
            }
        }
        
        throw new IllegalArgumentException("Unable to add new role to relationship in meta model");
    }

//    /**
//     * Clones this MetaRelationship to an existing MetaRelationship overwriting the
//     * copy's fields.  Note that the copy is disconnected from the MetaModel to 
//     * prevent duplicate MetaRelationships in the same MetaModel.
//     * @param copy is the MetaRelationship to copy to.
//     */
//    protected void cloneTo(MetaRelationship copy) {
//        copy.setModel(null); // disconnect any copy
//        super.cloneTo(copy);
//        copy.m_ends[0] = (MetaRole)m_ends[0].clone();
//        copy.m_ends[1] = (MetaRole)m_ends[1].clone();
//        copy.m_ends[0].setMetaRelationship(copy);
//        copy.m_ends[1].setMetaRelationship(copy);
//        copy.restriction = restriction;
//        version.cloneTo(copy.version);
//
//    }

    /**
     * Sets the MetaModel this MetaRelationship belongs to.
     * @param m is the parent MetaModel.
     */
    public void setModel(MetaModel m) {
        model = m;
    }
    
    /**
     * Gets the parent MetaModel.
     * @return the parent meta model.
     */
    public MetaModel getModel() {
        return model;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationship#getRestriction()
     */
    public MetaRelationshipRestriction getRestriction() {
        return restriction;
    }
    
    /**
     * Compare allows comparing of 2 meta relationships for sorting (by name).
     * 
     * @author rbp28668
     */
    public static class Compare implements Comparator<MetaRelationship> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(MetaRelationship mr0, MetaRelationship mr1) {
             return mr0.getName().compareToIgnoreCase(mr1.getName());
        }
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getVersion()
     */
    public Version getVersion() {
        return version;
    }
}
