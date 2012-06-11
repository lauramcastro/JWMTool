package jwmtool.util.exceptions;

import jwmtool.util.I18N;

/**
 * This <code>InvalidParameterException</code> is thrown by
 * {@link jwmtool.ui.ConfigDialog ConfigDialog} on when an invalid configuration
 * is selected.
 * 
 * @author Laura Castro
 * @version 0.6
 */
 
public class InvalidParameterException extends Exception {
	
	/**
	 * Creates new <code>InvalidParameterException</code>, providing
	 * conflictive configuration parameter name.
	 * 
	 * @param parameterName Configuration parameter name causing the
	 *                      invalid settings situation.
	 */
	public InvalidParameterException(String parameterName) {
		super(I18N.getInstance().getString("exceptions.invalidParameter") + " '" + parameterName + "'");
		this._parameterName = parameterName;
	}
	
	/**
	 * Returns name of conflictive configuration parameter.
	 * 
	 * @return Name of configuration parameter.
	 */
	public String getParameterName() {
		return _parameterName;
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Configuration parameter name.
	 */
	private String _parameterName;
}
