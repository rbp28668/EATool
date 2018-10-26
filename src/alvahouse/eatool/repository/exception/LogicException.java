/*
 * LogicException.java
 *
 * Created on 17 January 2002, 21:34
 */

package alvahouse.eatool.repository.exception;

/**
 *
 * @author  rbp28668
 */
public class LogicException extends java.lang.Exception {

    /**
     * Creates new <code>LogicException</code> without detail message.
     */
    public LogicException() {
    }


    /**
     * Constructs an <code>LogicException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LogicException(String msg) {
        super(msg);
    }
    
	/**
	 * @see java.lang.Throwable#Throwable(String, Throwable)
	 */
    public LogicException(String msg, Throwable cause) {
    	super(msg,cause);
    }
}


