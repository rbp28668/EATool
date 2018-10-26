/*
 * ImageChangeEvent.java
 * Project: EATool
 * Created on 04-Jul-2007
 *
 */
package alvahouse.eatool.repository.images;

/**
 * ImageChangeEvent is used to signal changes to an Image or
 * Images collection.
 * 
 * @author rbp28668
 */
public class ImageChangeEvent extends java.util.EventObject{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an ImageChangeEvent from the given source.
     * @param source is the source of the event.
     */
    public ImageChangeEvent(Object source) {
        super(source);
    }

}
