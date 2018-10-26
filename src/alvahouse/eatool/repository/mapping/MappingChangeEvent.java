/*
 * MappingChangeEvent.java
 * Project: EATool
 * Created on 10-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

import java.util.EventObject;

/**
 * MappingChangeEvent is an event object for signalling changes in the
 * import mappings.
 * 
 * @author rbp28668
 */
public class MappingChangeEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * Signals a change event.
     * @param o is the target of the event.
     */
    public MappingChangeEvent(Object target) {
        super(target);
    }

}
