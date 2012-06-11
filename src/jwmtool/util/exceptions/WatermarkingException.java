package jwmtool.util.exceptions;

import jwmtool.util.I18N;

/**
 * This <code>WatermarkingException</code> is thrown by
 * {@link jwmtool.lib.Watermarking Watermarking} utility class, when an error
 * during watermarking process arises.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class WatermarkingException extends Exception {
	
	/**
	 * Creates new generic <code>WatermarkingException</code>.
	 */
	public WatermarkingException() {
		super(I18N.getInstance().getString("exceptions.watermarking"));
	}
	
	/**
	 * Creates new <code>WatermarkingException</code>, with an associated
	 * message.
	 * 
	 * @param message Watermarking error description.
	 */
	public WatermarkingException(String message) {
		super(I18N.getInstance().getString(message));
	}
}
