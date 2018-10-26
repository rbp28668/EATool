/*
 * MetaRelationshipRestriction.java
 * Project: EATool
 * Created on 24 Dec 2007
 *
 */
package alvahouse.eatool.repository.metamodel;

import java.util.List;

public interface MetaRelationshipRestriction {

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
    public abstract IntermediateState createState();

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
    public abstract boolean isValid(IntermediateState state,
            MetaRelationship mr, Object start, Object finish);

    /**
     * Should be called at the end of validation to allow the IntermediateState to be checked.
     * @param state is the state to be checked.
     * @param mr is the MetaRelationship being checked against.
     * @return List of fault Strings (may be multiple errors in the model).  Empty if OK, not null.
     */
    public abstract List<String> finishValidation(IntermediateState state,
            MetaRelationship mr);

    
    /**
     * IntermediateState is a marker interface for the validation state.
     * 
     * @author rbp28668
     */
    public interface IntermediateState{
    }

}