package jwmtool.lib;

import java.awt.Image;

/**
 * A JWMFrame represents a pair of frames, one being the
 * watermarked version of the other.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class JWMFrame {
	
	// ----- ----- ----- METHODS -----  ----- -----
	
	/**
	 * Creates a new JWMFrame from two images, representing
	 * a source frame and its watermarked version.
	 *
	 * @param sourceFrame An image frame.
	 * @param wmarkedFrame The watermarked version of the <code>sourceFrame</code>.
	 */
	public JWMFrame(Image sourceFrame, Image wmarkedFrame) {
		_sourceImage  = sourceFrame;
		_wmarkedImage = wmarkedFrame;
	}
	
	/**
	 * Access method to non-watermarked frame inside JWMFrame.
	 *
	 * @return A non-watermarked {@link java.awt.Image image} frame.
	 */
	public Image getSourceFrame() {
		return _sourceImage;
	}
	
	/**
	 * Access method to watermarked frame inside JWMFrame.
	 *
	 * @return A watermarked {@link java.awt.Image image} frame.
	 */
	public Image getWMarkedFrame() {
		return _wmarkedImage;
	}
	
	// ----- ----- ----- ATTRIBUTES -----  ----- -----
	
	/**
	 * Original (non-watermarked) {@link java.awt.Image image} frame.
	 */
	private Image _sourceImage  = null;
	/**
	 * Modified (watermarked) {@link java.awt.Image image} frame.
	 */
	private Image _wmarkedImage = null;
	
}