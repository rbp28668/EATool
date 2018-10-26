/*
 * MetaModelChangeEvent.java
 *
 * Created on 14 February 2002, 21:49
 */

package alvahouse.eatool.repository.metamodel;


/**
 * Event used to signal changes in the meta-model.
 * @author  rbp28668
 */
public class MetaModelChangeEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;

    /** Creates new MetaModelChangeEvent */
    public MetaModelChangeEvent(Object source) {
        super(source);
    }

}
