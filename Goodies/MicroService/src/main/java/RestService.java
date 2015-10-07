package net.javagoodies.MicroService;

import net.javagoodies.MicroService.MicroServiceException;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Default implementation of RestServuceIfc interface (see Ifc for additional notes)
 */
public class RestService extends MicroService implements RestServiceIfc {

	/**
	 *
	 */
	protected Server server;

	/**
	 *
	 */
	protected String resourcePackageName;

	/**
	 *
	 */
	protected int port;

	/**
	 *
	 */
	protected String contextPath;

	/**
	 *
	 */
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

		QueuedThreadPool queuedThreadPool = new QueuedThreadPool(threadPoolSize);
		queuedThreadPool.setName("RestService");
		injectDependencies(new Server(queuedThreadPool));
	}

	public void injectDependencies(Server server) {
		this.server = server;
	}

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

			// Jetty connector configuration
			// ref: http://www.eclipse.org/jetty/documentation/current/configuring-connectors.html#jetty-connectors-http-configuration
			// ref: http://download.eclipse.org/jetty/9.3.3.v20150827/apidocs/org/eclipse/jetty/server/HttpConfiguration.html
			HttpConfiguration httpConfig = new HttpConfiguration();
			httpConfig.setRequestHeaderSize(8192);
			httpConfig.setResponseHeaderSize(8192);
			httpConfig.setOutputBufferSize(32768);

			// Set the port
			// ref: http://stackoverflow.com/questions/23329135/how-do-you-set-both-port-and-thread-pool-using-embedded-jetty-v-9-1-0
			ServerConnector http = new ServerConnector(
				server,
				new HttpConnectionFactory(httpConfig)
			);
			http.setPort(port);
			server.addConnector(http);

			server.start();
			server.join();
		}
		catch (Exception e) {
			throw new MicroServiceException("Failed to initialize MicroService", e);
		}
	}
}

