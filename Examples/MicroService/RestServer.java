package net.javagoodies.examples.MicroService.RestServer;

import net.javagoodies.MicroService.RestService;

/**
 * Example of primitive RESTful MicroService
 */
public class RestServer {

	/**
	 *
	 */
	public static void main(String[] args) {
		RestService service = new RestService(
			"net.javagoodies.examples.MicroService.RestServer",
			80,
			"/",
			10
		);
		service.run();
	}
}

