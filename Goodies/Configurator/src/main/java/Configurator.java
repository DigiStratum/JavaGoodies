package net.javagoodies.Configurator;

/**
 * A general purpose configuration class
 *
 * This class blends settings from properties file(s) with ones that are programmatically set. This
 * allows us to bury configs that don't change all that often within the application initialization
 * code, keep the settings that either change often or need to be able to change quickly without
 * rebuilding in an external properties file, and abstracts both away from the application so that
 * access to configuration data is uniform no matter where it comes from.
 */
public class Configurator {

	/**
	 * Constructor
	 */
	public Configurator() [
	}

	/**
	 *
	 */
	public boolean importPropsResource(String filename) {
		return importProps(getClass().getClassLoader().getResourceAsStream(filename));
	}

	public boolean importPropsFile(String path) {
		return importProps(new FileInputStream(path));
	}

	protected boolean importProps(InputStream inputStream) {

		// Nothing to do if a bad inputStream was supplied
		if (null == inputStream) return false;

		Properties props = new Properties();
		props.load(inputStream);
		inputStream.close();

		// TODO: do something useful with the properties data 
		// ref: http://docs.oracle.com/javase/7/docs/api/java/util/Properties.html
		return true;
	}
}

