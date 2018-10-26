/*
 * TypeEventListener.java
 * Project: EATool
 * Created on 13-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

/**
 * TypeEventListener is a listener interface for anything that wants to
 * respond to changes in the type system.
 * 
 * @author rbp28668
 */
public interface TypeEventListener {

    /**
     * Signals that a new type has been created.
     * @param event contains the type.
     */
    public void typeAdded(TypeEvent event);
    
    /**
     * Signals that a type has been edited.  Users of the type need to 
     * check validity of any properties.
     * @param event contains the type.
     */
    public void typeChanged(TypeEvent event);
    
    /**
     * Signals that a type has been deleted.  Users of the type need to 
     * remove any references to the type.
     * @param event contains the type.
     */
    public void typeDeleted(TypeEvent event);
}
