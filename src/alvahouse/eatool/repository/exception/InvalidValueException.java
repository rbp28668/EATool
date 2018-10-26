/*
 * InvalidValueException.java
 *
 * Created on 12 January 2002, 22:20
 */

package alvahouse.eatool.repository.exception;

/**
 *
 * @author  rbp28668
 */
public class InvalidValueException extends java.lang.Exception {

    /**
     * Creates new <code>InvalidValueException</code> without detail message.
     */
    public InvalidValueException() {
    }


    /**
     * Constructs an <code>InvalidValueException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidValueException(String msg) {
        super(msg);
    }
}


