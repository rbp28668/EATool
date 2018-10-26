/*
 * ScriptChangeEvent.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.util.EventObject;

/**
 * ScriptChangeEvent is an event object for signalling
 * changes to scripts.
 * 
 * @author rbp28668
 */
public class ScriptChangeEvent extends EventObject{

    private static final long serialVersionUID = 1L;

    /**
      * Creates a new ScriptChangeEvent.
     * @param target is the thing that is changing.
     */
    public ScriptChangeEvent(Object target) {
        super(target);
    }

}
