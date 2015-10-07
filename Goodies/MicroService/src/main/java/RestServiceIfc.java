package net.javagoodies.MicroService;

import org.eclipse.jetty.server.Server;

/**
 * A MicroService that uses Jetty & Jersey to host REST API's
 *
 * @todo Consider dependency injection for logging and monitoring services
 */
public interface RestServiceIfc {

	/**
	 * Depenency Injector
	 *
	 * @param server Jetty Server instance that we're going to use
	 *
	 * @todo See if there is a way to use @Inject notation with this; we want to be able to make
	 * our own but replace it with a mock if necessary so that the caller doesn't have to do all
	 * the work of setting up the Server instance as seen in the run() method below - that's the
	 * whole point of this class!
	 */
	public void injectDependencies(Server server);

	/**
	 * Service runner
	 */
	public void run() throws MicroServiceException;
}

