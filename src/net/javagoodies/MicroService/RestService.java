package net.javagoodies.MicroService;

import net.javagoodies.MicroService.Exception.MicroServiceException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * A MicroService that uses Jetty & Jersey to host REST API's
 *
 * @todo Consider dependency injection for logging and monitoring services
 */
public class RestService extends MicroService {

	protected Server server;
	protected String resourcePackageName;
	protected int port;
	protected String contextPath;
	protected int threadPoolSize;

	/**
	 * Constructor
	 *
	 * @param resourcePackageName String Java package name where the Jersey resources can be found
	 * @param port integer port to bind this service to
	 * @param contextPath String path that this service's resources will all be relative to (typically "/")
	 * @param threadPoolSize integer count of threads to maintain in our pool (10 is a good start)
	 */
	public RestService(String resourcePackageName, int port, String contextPath, int threadPoolSize) throws MicroServiceException {
		super();

		this.resourcePackageName = resourcePackageName;
		this.port = port;
		this.contextPath = contextPath;
		this.threadPoolSize = threadPoolSize;

		injectDependencies(new Server(port));
	}

	/**
	 * Depenency Injector
	 *
	 * @param server Jetty Server instance that we're going to use
	 *
	 * @todo See if there is a way to use @Inject notation with this; we want to be able to make
	 * our own but replace it with a mock if necessary so that the caller doesn't ahve to do all
	 * the work of setting up the Server instance as seen in the run() method below - that's the
	 * whole point of this class!
	 */
	public void injectDependencies(Server server) {
		this.server = server;
	}

	/**
	 * Service runner
	 */
	public void run() throws MicroServiceException {

		// Check our declared settings
		if ((null == resourcePackageName) || (resourcePackageName.isEmpty())) {
			throw new MicroServiceException("Invalid resrouce package name supplied: [" + resourcePackageName + "]");
		}
		if ((null == contextPath) || (contextPath.isEmpty())) {
			throw new MicroServiceException("Invalid context path supplied: [" + contextPath + "]");
		}
		if (threadPoolSize < 1) {
			throw new MicroServiceException("Thread pool size must be >= 1; what we got was: " + threadPoolSize);
		}

		try {
			ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
			servletHolder.setInitParameter(
				"com.sun.jersey.config.property.resourceConfigClass",
				"com.sun.jersey.api.core.PackagesResourceConfig"
			);
			servletHolder.setInitParameter(
				"com.sun.jersey.config.property.packages",
				resourcePackageName
			);
			//servletHolder.setInitParameter("com.sun.jersey.config.feature.Debug", "true");
			//servletHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true");
			servletHolder.setInitParameter(
				"com.sun.jersey.spi.container.ContainerRequestFilters",
				"com.sun.jersey.api.container.filter.LoggingFilter"
			);
			servletHolder.setInitParameter(
				"com.sun.jersey.spi.container.ContainerResponseFilters",
				"com.sun.jersey.api.container.filter.LoggingFilter"
			);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath(contextPath);
			context.addServlet(servletHolder, "/*");
			context.setBaseResource(Resource.newSystemResource("./"));
			server.setHandler(context);

			QueuedThreadPool queuedThreadPool = new QueuedThreadPool(threadPoolSize);
			queuedThreadPool.setName("RestService");
			server.setThreadPool(queuedThreadPool);

			server.start();
			server.join();
		}
		catch (Exception e) {
			throw new MicroServiceException("Failed to initialize MicroService", e);
		}
	}
}

