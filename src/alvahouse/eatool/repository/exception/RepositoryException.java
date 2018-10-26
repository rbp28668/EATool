/*
 * RepositoryException.java
 * Project: EATool
 * Created on 5 Dec 2007
 *
 */
package alvahouse.eatool.repository.exception;

/**
 * RepositoryException - generic exception in the repository.
 * 
 * @author rbp28668
 */
public class RepositoryException extends ChainedException {

     private static final long serialVersionUID = 1L;

    public RepositoryException() {
        super();
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
