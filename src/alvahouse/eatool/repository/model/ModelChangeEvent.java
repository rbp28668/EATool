/*
 * ModelChangeEvent.java
 *
 * Created on 16 February 2002, 19:10
 */

package alvahouse.eatool.repository.model;

/**
 * Event object used for signalling model change events.
 * @author  rbp28668
 */
public class ModelChangeEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;

    /** Creates new ModelChangeEvent 
     * @param o is the source of the event 
     */
    public ModelChangeEvent(Object o) {
        super(o);
    }

}
