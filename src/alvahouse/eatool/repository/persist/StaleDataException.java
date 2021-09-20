/**
 * 
 */
package alvahouse.eatool.repository.persist;

/**
 * @author bruce_porteous
 *
 */
public class StaleDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public StaleDataException() {
	}

	/**
	 * @param message
	 */
	public StaleDataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StaleDataException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StaleDataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public StaleDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
