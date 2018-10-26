/*
 * MetaRelationshipRestriction.java
 * Project: EATool
 * Created on 22-Nov-2005
 *
 */
package alvahouse.eatool.repository.metamodel.impl;

import java.util.LinkedList;
import java.util.List;

import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction;
import alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction.IntermediateState;

//import alvahouse.eatool.repository.model.Entity;

/**
 * MetaRelationshipRestriction is a placeholder class to put restrictions on a MetaRelationship
 * over and above those implied by the Roles and cardinality.  For example a one-to-many
 * relationship may be a hierarchy, but a true hierarchy requires that no children of
 * an entity can be that entities parent. 
 * 
 * @author rbp28668
 */
public abstract class MetaRelationshipRestrictionImpl implements MetaRelationshipRestriction {

    /** This restriction adds no extra restrictions to a relationship and is the default*/
    public final static MetaRelationshipRestriction NONE = new NullRestriction();
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction#getName()
     */
    public abstract String getName();
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction#createState()
     */
    public MetaRelationshipRestriction.IntermediateState createState(){
        return null;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction#isValid(alvahouse.eatool.repository.metamodel.MetaRelationshipRestrictionImpl.IntermediateState, alvahouse.eatool.repository.metamodel.MetaRelationship, java.lang.Object, java.lang.Object)
     */
    public boolean isValid(IntermediateState state, MetaRelationship mr, Object start, Object finish){
        return true;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction#finishValidation(alvahouse.eatool.repository.metamodel.MetaRelationshipRestrictionImpl.IntermediateState, alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public List<String> finishValidation(IntermediateState state, MetaRelationship mr){
        List<String> faults = new LinkedList<String>();
        return faults;
    }
    
    /**
     * NullRestriction provides a default restriction that doesn't restrict.
     * 
     * @author rbp28668
     */
    private static class NullRestriction extends MetaRelationshipRestrictionImpl {

        /* (non-Javadoc)
         * @see alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction#getName()
         */
        public String getName() {
            return "NONE";
        }
        
    }
    
}
