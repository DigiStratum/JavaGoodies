package net.javagoodies.Configurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Properties;

/**
 * A general purpose configuration class
 *
 * This class blends settings from properties file(s) with ones that are programmatically set. This
 * allows us to bury configs that don't change all that often within the application initialization
 * code, keep the settings that either change often or need to be able to change quickly without
 * rebuilding in an external properties file, and abstracts both away from the application so that
 * access to configuration data is uniform no matter where it comes from.
 *
 * Note that this is a read-only configuration mechanism designed to obtain configs from multiple
 * sources and supply them to the application uniformly. While some of the mechanisms integrated may
 * include writable/updatable solutions, not all of them do... and ultimately modifying the config
 * is not our intent.
 *
 * @TODO Add support for importing configuration data from a database
 */
public class Configurator {

	/**
	 * Local store for all our name-value configuration pairs
	 */
	private HashMap<String, String> configs;

	/**
	 * An optional prefix to filter configuration data to properties of interest to us
	 */
	private String namePrefix;

	/**
	 * Default constructor
	 */
	public Configurator() {
		this(null);
	}

	/**
	 * Constructor with optional name prefix
	 *
	 * For a collection of properties that looks like:
	 *
	 * myclass.setting1=value1
	 * myclass.setting2=value2
	 * otherclass.unknownsetting=something else
	 *
	 * If prefix is set to "myclass." then when the properties are read in, we will end up with:
	 *
	 * setting1=value1
	 * setting2=value2
	 *
	 * Anything not starting with "myclass." will be filtered out, and the prefix itself will be
	 * stripped off the property names.
	 *
	 * @param namePrefix String prefix that we expect to see at the start of each property name
	 */
	public Configurator(String namePrefix) {
		// Only capture namePrefix if it is a non-null, non-empty string
		this.namePrefix = ((null != namePrefix) && (! namePrefix.isEmpty())) ? namePrefix : null;
		configs = new HashMap<>();
	}

	/**
	 * Get the value of the named property
	 *
	 * Note that if a prefix is in use, the name supplied here should NOT include the prefix
	 *
	 * @param name String name of the property whose value we want to get
	 *
	 * @return String value of the named property or null
	 */
	public String get(String name) {
		return get(name, null);
	}

	/**
	 * Get the value of the named property with a default if the property is undefined
	 *
	 * Note that if a prefix is in use, the name supplied here should NOT include the prefix
	 *
	 * @param name String name of the property whose value we want to get
	 * @param defaultValue String (optional) default to use if property is undefined
	 *
	 * @return String value of the named property or null
	 */
	public String get(String name, String defaultValue) {
		if ((null == name) || name.isEmpty()) return null;
		if (has(name)) return configs.get(name);
		return defaultValue;
	}

	/**
	 * Set the value of the named property to that provided
	 *
	 * Note that prefix is NOT considered for this setter, but its usage should be consistent
	 * with that of the getter.
	 *
	 * @param name String name of the property whose value we want to set
	 * @param value String value to set for the property; (null is an option)
	 */
	public void set(String name, String value) {
		if ((null == name) || name.isEmpty()) return;
		configs.put(name, value);
	}

	/**
	 * Check whether the configuration data has a property with the specified name
	 *
	 * @param name String name of the property whose value we want to check for
	 *
	 * @return boolean true if the named property is defined, else false
	 */
	public boolean has(String name) {
		if ((null == name) || name.isEmpty()) return false;
		return configs.containsKey(name);
	}

	/**
	 * Import properties from a resource (within our JAR?)
	 *
	 * @param filename String name of the file resource to import from
	 *
	 * @return boolean true on success, else false
	 *
	 * @throws ConfiguratorException If there is a file I/O error
	 */
	public boolean importPropsResource(String filename) throws ConfiguratorException {
		return importPropsStream(getClass().getClassLoader().getResourceAsStream(filename));
	}

	/**
	 * Import properties from a .properties file on disk
	 *
	 * @param path String filesystem path of the file to import from
	 *
	 * @return boolean true on success, else false
	 *
	 * @throws ConfiguratorException If there is a file I/O error
	 */
	public boolean importPropsFile(String path) throws ConfiguratorException {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(path);
		}
		catch (FileNotFoundException e) {
			throw new ConfiguratorException(
				"Error importing properties file at path '" + path + "'",
				e
			);
		}
		return importPropsStream(fileInputStream);
	}

	/**
	 * Import properties from some input stream
	 *
	 * @param inputStream InputStream instance to read property data from
	 *
	 * @return boolean true on success, else false
	 *
	 * @throws ConfiguratorException If there is a file I/O error
	 */
	protected boolean importPropsStream(InputStream inputStream) throws ConfiguratorException {

		// Nothing to do if a bad inputStream was supplied
		if (null == inputStream) return false;

		// Read property data from the supplied file
		Properties props = new Properties();
		try {
			props.load(inputStream);
			inputStream.close();
		}
		catch (IOException e) {
			throw new ConfiguratorException(
				"Error importing properties file at path",
				e
			);
		}

		// Transfer properties that we read into our internal configuration hashmap
		for (String name : props.stringPropertyNames()) {

			// If we have no prefix, or the name starts with our expected prefix...
			if ((null == namePrefix) || (name.startsWith(namePrefix))) {

				// Get ready to set configuration data for this property...
				String tmpName = name;

				// If there is a prefix...
				if (null != namePrefix) {

					// Remove it
					tmpName = name.substring(namePrefix.length());
				}
				set(tmpName, props.getProperty(name));
			}
		}

		return true;
	}
}

