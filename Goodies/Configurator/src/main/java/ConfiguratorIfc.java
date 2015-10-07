package net.javagoodies.Configurator;

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
public interface ConfiguratorIfc {

	/**
	 * Get the value of the named property
	 *
	 * Note that if a prefix is in use, the name supplied here should NOT include the prefix
	 *
	 * @param name String name of the property whose value we want to get
	 *
	 * @return String value of the named property or null
	 */
	public String get(String name);

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
	public String get(String name, String defaultValue);

	/**
	 * Set the value of the named property to that provided
	 *
	 * Note that prefix is NOT considered for this setter, but its usage should be consistent
	 * with that of the getter.
	 *
	 * @param name String name of the property whose value we want to set
	 * @param value String value to set for the property; (null is an option)
	 */
	public void set(String name, String value);

	/**
	 * Check whether the configuration data has a property with the specified name
	 *
	 * @param name String name of the property whose value we want to check for
	 *
	 * @return boolean true if the named property is defined, else false
	 */
	public boolean has(String name);

	/**
	 * Import properties from a resource (within our JAR?)
	 *
	 * @param filename String name of the file resource to import from
	 *
	 * @return boolean true on success, else false
	 *
	 * @throws ConfiguratorException If there is a file I/O error
	 */
	public boolean importPropsResource(String filename) throws ConfiguratorException;

	/**
	 * Import properties from a .properties file on disk
	 *
	 * @param path String filesystem path of the file to import from
	 *
	 * @return boolean true on success, else false
	 *
	 * @throws ConfiguratorException If there is a file I/O error
	 */
	public boolean importPropsFile(String path) throws ConfiguratorException;
}

