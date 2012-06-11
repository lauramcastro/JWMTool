package jwmtool.util.exceptions;

import jwmtool.util.I18N;

/**
 * This <code>IllegalStateChangeException</code> is thrown by
 * {@link jwmtool.ui.JWMPanel JWMPanel} when a forbidden state change is
 * attempted.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class IllegalStateChangeException extends Exception {
	
	/**
	 * Creates new <code>IllegalStateChangeException</code>, as a result
	 * of the not allowed state change attempt from <code>oldState</code>
	 * to <code>newState</code> by {@link jwmtool.ui.JWMPanel JWMPanel}.
	 * 
	 * @param oldState Original state.
	 * @param newState Desired (but forbidden from <code>oldState</code>
	 *                 final state.
	 */
	public IllegalStateChangeException(int oldState, int newState) {
		super(I18N.getInstance().getString("exceptions.illegalStateChange") + " '" + oldState + "' -> '" + newState + "'");
		this._oldState = oldState;
		this._newState = newState;
	}
	
	/**
	 * Returns original state from which forbidden state transition to
	 * {@link jwmtool.util.exceptions.IllegalStateChangeException#_newState newState}
	 * was attempted.
	 * 
	 * @return Original state identifier.
	 */
	public int getOldState() {
		return _oldState;
	}

	/**
	 * Returns intended destination state of forbidden state transition
	 * from {@link jwmtool.util.exceptions.IllegalStateChangeException#_oldState oldState}.
	 * 
	 * @return Destination state identifier.
	 */
	public int getNewState() {
		return _newState;
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Original state identifier.
	 */
	private int _oldState;
	/**
	 * Destination state indentifier.
	 */
	private int _newState;
}
