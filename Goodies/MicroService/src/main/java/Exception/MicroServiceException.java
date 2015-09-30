package net.javagoodies.MicroService.Exception;

/**
 * Any exception from a MicroService starts here
 */
public class MicroServiceException extends Exception {

	/**
	 * Default constructor
	 */
	public MicroServiceException() {
		super();
	}

	/**
	 * Constructor with just a text message
	 *
	 * @param message String of readable text that describes the nature of the cause
	 */
	public MicroServiceException(String message) {
		super(message);
	}

	/**
	 * Constructor with a text message and a cause
	 *
	 * @param message String of readable text that describes the nature of the cause
	 * @param cause Throwable root cause for this exception
	 */
	public MicroServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with just a cause
	 *
	 * @param cause Throwable root cause for this exception
	 */
	public MicroServiceException(Throwable cause) {
		super(cause);
	}
}

