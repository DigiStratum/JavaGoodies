package net.javagoodies.Configurator;

/**
 * Any exception from a Configurator starts here
 */
public class ConfiguratorException extends Exception {

	/**
	 * Default constructor
	 */
	public ConfiguratorException() {
		super();
	}

	/**
	 * Constructor with just a text message
	 *
	 * @param message String of readable text that describes the nature of the cause
	 */
	public ConfiguratorException(String message) {
		super(message);
	}

	/**
	 * Constructor with a text message and a cause
	 *
	 * @param message String of readable text that describes the nature of the cause
	 * @param cause Throwable root cause for this exception
	 */
	public ConfiguratorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with just a cause
	 *
	 * @param cause Throwable root cause for this exception
	 */
	public ConfiguratorException(Throwable cause) {
		super(cause);
	}
}

