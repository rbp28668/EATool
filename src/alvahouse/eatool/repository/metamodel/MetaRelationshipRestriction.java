/*
 * MetaRelationshipRestriction.java
 * Project: EATool
 * Created on 22-Nov-2005
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.util.LinkedList;
import java.util.List;

//import alvahouse.eatool.repository.model.Entity;

/**
 * MetaRelationshipRestriction is a placeholder class to put restrictions on a MetaRelationship
 * over and above those implied by the Roles and cardinality.  For example a one-to-many
 * relationship may be a hierarchy, but a true hierarchy requires that no children of
 * an entity can be that entities parent. 
 * Note that these should be immutable once created.
 * 
 * @author rbp28668
 */
public abstract class MetaRelationshipRestriction {

    /** This restriction adds no extra restrictions to a relationship and is the default*/
    public final static MetaRelationshipRestriction NONE = new NullRestriction();
    
    /**
     * Each restriction is named for reporting purposes.
     * @return the name of the restriction.
     */
    public abstract String getName();
    
    /**
     * Creates an external state for collecting information across many validation calls.  E.g. to 
     * check for a true hierarchy it would be necessary to build up the intended hierarchy to see if
     * it was valid.
     * @return a new IntermediateState.
     */
    public MetaRelationshipRestriction.IntermediateState createState(){
        return null;
    }
    
    /**
     * Checks whether a single linkage is valid.  This may not necessarily validate the (potential) 
     * relationship but may build up the IntermediateState instead.  Note that start and finish
     * are Object to remove dependency on model.
     * @param state is the IntermediateState for building up any required model during validation.
     * @param mr is the MetaRelationship that the potential links are being validated against.
     * @param start is the potential start of the relationship.
     * @param finish is the potential end of the relationship.
     * @return true if valid, false if not.
     */
    public boolean isValid(IntermediateState state, MetaRelationship mr, Object start, Object finish){
        return true;
    }
    
    /**
     * Should be called at the end of validation to allow the IntermediateState to be checked.
     * @param state is the state to be checked.
     * @param mr is the MetaRelationship being checked against.
     * @return List of fault Strings (may be multiple errors in the model).  Empty if OK, not null.
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
    private static class NullRestriction extends MetaRelationshipRestriction {

         public String getName() {
            return "NONE";
        }
        
    }
    
    /**
     * IntermediateState is a marker interface for the validation state.
     * 
     * @author rbp28668
     */
    public interface IntermediateState{
    }

}
