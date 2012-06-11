package jwmtool.util.exceptions;

import jwmtool.util.I18N;

/**
 * This <code>MissingConfigurationParameterException</code> is thrown by the
 * {@link jwmtool.util.ConfigurationParametersManager ConfigurationParametersManager}
 * when a non-existent parameter (i.e. not present in configuration file) is
 * requested.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class MissingConfigurationParameterException extends Exception {
	
	/**
	 * Creates new <code>MissingConfigurationParameterException</code>,
	 * providing name of conflicting parameter.
	 *
	 * @param parameterName Parameter which request caused the exception to
	 *                      be thrown.
	 */
	public MissingConfigurationParameterException(String parameterName) {
		super(I18N.getInstance().getString("exceptions.parameterNotFound") + " '" + parameterName + "'");
		this._parameterName = parameterName;
	}
	
	/**
	 * Returns the name of the conflictive (i.e. missing from configuration
	 * file) parameter.
	 *
	 * @return Not found parameter name.
	 */
	public String getParameterName() {
		return _parameterName;
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Name of non-existent (i.e. not found in configuration file) which
	 * request caused <code>MissingConfigurationParameterException</code>
	 * to arise.
	 */
	private String _parameterName;
}
