package jwmtool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jwmtool.util.exceptions.MissingConfigurationParameterException;

/**
 * Singleton class which provides access to configuration parameters values.
 * This configuration parameters are stored in application configuration file(s)
 * and corresponding values are returned upon (by name) request.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class ConfigurationParametersManager {
	
	/**
	 * Empty constructor, with restricted visibility to prevent any class
	 * from creating <code>ConfigurationParametersManager</cod> instances,
	 * since this class implements Singleton design pattern.
	 */
	private ConfigurationParametersManager() { }
	
	/**
	 * Provides access to <code>ConfigurationParametersManager</code> single
	 * instance.
	 * 
	 * @return Single reference to
	 *         <code>ConfigurationParametersManager</code> instance.
	 */
	public static ConfigurationParametersManager getInstance() {
		return _instance;
	}
	
	/**
	 * Establishes reference configuration file from which
	 * <code>ConfigurationParametersManager</code> will obtain configuration
	 * parameters values.
	 * 
	 * @param file Name of configuration file containing known configuration
	 *             parameters and their values.
	 */
	public void setConfigFile(String file) {
		_parameters = getMappedProperties(file);
	}
	
	/**
	 * Reads configuration parameters and their values from provided
	 * configuration file into a mapping structure.
	 * 
	 * @param file Name of configuration file containing configuration
	 *             parameters and associated values.
	 * @return Mapping structure with list of known configuration parameters
	 *         and their values.
	 */
	private HashMap getMappedProperties(String file) {
		try {
			InputStream inputStream;
			
			file = System.getProperty("user.dir") + File.separator + file;
			inputStream = new FileInputStream(file);
			
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			return new HashMap(properties);
		} catch (FileNotFoundException e) {
			System.err.println("Configuration file does not exist.");
			return null;
		} catch (Exception e) {
			System.err.println("Unable to load configuration.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns value of a given parameter, according to configuration file.
	 * If configuration parameter is not known (i.e. is not present in
	 * configuration file),
	 * {@link jwmtool.util.exceptions.MissingConfigurationParameterException MissingConfigurationParameterException}
	 * is thrown.
	 * 
	 * @param name Name of parameter which value is requested.
	 * @return Value of given parameter.
	 * @throws MissingConfigurationParameterException
	 */
	public String getParameter(String name)
	        throws MissingConfigurationParameterException {
		
		String value = (String) _parameters.get(name);
		if (value == null) {
			throw new MissingConfigurationParameterException(name);
		}
		return value;
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	* Single instance of {@link jwmtool.util.ConfigurationParametersManager ConfigurationParametersManager}.
	 */
	private static ConfigurationParametersManager _instance = new ConfigurationParametersManager();
	/**
	 * Mapping structure containing parameters and their associated values.
	 */
	private Map _parameters = null;
}
