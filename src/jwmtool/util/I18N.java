package jwmtool.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Singleton class which provides an internationalization service, based on
 * the existence of internationalization files.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class I18N {
	
	/**
	 * Restricted constructor: initializes the <code>I18N</code> singleton
	 * and also prevents no one but the <code>I18N</code> class itself to
	 * create an instance of this kind.
	 */
	private I18N() {
		try {
			// retrieve language preferences from configuration file
			String language = ConfigurationParametersManager.getInstance().getParameter(CONFIGURATION_PARAMETER_LANGUAGE);
			String country  = ConfigurationParametersManager.getInstance().getParameter(CONFIGURATION_PARAMETER_COUNTRY);
			_locale = new Locale(language, country);
		} catch(Exception e) {
			_locale = new Locale("en", "US"); // default locale
		}
		_messages = ResourceBundle.getBundle("jwmtool.messages.Messages", _locale);
	}
	
	/**
	 * Provides access to <code>I18N</code> single instance.
	 */
	public static I18N getInstance() {
		return _instance;
	}
	
	/**
	 * Returns current locale selection.
	 *
	 * @return {@link java.util.Locale Locale} reference which is being
	 *         by <code>I18N</code> used at the moment.
	 */
	public Locale getLocale() {
		return _locale;
	}
	
	/**
	 * Establishes current locale selection.
	 * 
	 * @param language Two-character string containing a valid language ISO
	 *                 code.
	 * @param country Two-character string containing a valid country ISO
	 *                code.
	 */
	public void setLocale(String language, String country) {
		try {
			_locale = new Locale(language, country);
		} catch(Exception e) {
			_locale = Locale.getDefault();
		}
		_messages = ResourceBundle.getBundle("jwmtool.messages.Messages", _locale);
	}
	
	/**
	 * Provides translation (using current locale selection) for a given
	 * key-string.
	 * 
	 * @param key String reference to provide translation for. This key
	 *            string is looked up in the language file corresponding
	 *            to the currently selected locale.
	 * @return Translation found in language file for given key string. If
	 *         the key string is not found in the language file
	 *         (<code>MissingResourceException</code> is trapped), then the
	 *         key string itself is returned.
	 */
	public String getString(String key) {
		String res = key;
		try {
			res = _messages.getString(key);
		} catch (MissingResourceException ex) {
			System.out.println("[jwmtool.util.I18N] No se encuentra: " + key);
		}
		return res;
	}
	
	// ----- ----- ----- ATTRIBUTES ---- ---- ----
	
	/**
	 * Name of the language configuration parameter, to be specified in
	 * application configuration file.
	 */
	private final static String CONFIGURATION_PARAMETER_LANGUAGE = "language";
	/**
	 * Name of the country configuration parameter (to build locale
	 * preference together with language configuration parameter), to be
	 * specified in application configuration file.
	 */
	private final static String CONFIGURATION_PARAMETER_COUNTRY  = "country";
	
	/**
	 * Single instance of <code>I18N</code>.
	 */
	private static I18N _instance = new I18N();
	/**
	 * Locale preference.
	 */
	private Locale _locale;
	/**
	 * Locale translations bundle.
	 */
	private ResourceBundle _messages;
}
