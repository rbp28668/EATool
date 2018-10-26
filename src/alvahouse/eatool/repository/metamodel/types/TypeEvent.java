/*
 * TypeEvent.java
 * Project: EATool
 * Created on 13-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

/**
 * TypeEvent is an event class used to signal changes in the type
 * system.
 * 
 * @author rbp28668
 */
public class TypeEvent {

    private ExtensibleMetaPropertyType type;
    
    /**
     * 
     */
    public TypeEvent(ExtensibleMetaPropertyType type) {
        super();
        this.type = type;
    }

    
    /**
     * @return Returns the type.
     */
    public ExtensibleMetaPropertyType getType() {
        return type;
    }
}
