/*
 * PageChangeEvent.java
 * Project: EATool
 * Created on 16-Aug-2007
 *
 */
package alvahouse.eatool.repository.html;

import java.util.EventObject;

/**
 * PageChangeEvent signals the change (new,edit,delete) of a HTML page.
 * 
 * @author rbp28668
 */
public class PageChangeEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * @param source
     */
    public PageChangeEvent(Object source) {
        super(source);
    }

}
