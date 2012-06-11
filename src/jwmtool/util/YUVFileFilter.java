package jwmtool.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import jwmtool.util.I18N;

/**
 * Extension of {@link javax.swing.filechooser.FileFilter FileFilter} to provide
 * an specific file filter for YUV format videostream files.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class YUVFileFilter extends FileFilter {
	
	// ----- ----- ----- PUBLIC CLASS VARIABLES ----- ----- -----
	
	/**
	 * Expected extension for YUV format videostream files.
	 */
	public static final String[] YUV_EXT = {"yuv", "y4m"};
	/**
	 * Description for YUV format videostream files.
	 */
	public static final String YUV_DSC = I18N.getInstance().getString("message.yuv.description");
	/**
	 * Whether the given file is accepted by this filter.
	 * 
	 * @param f File to check acceptance.
	 * @return Whether file is acceptable or not.
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		else {
			for (int i=0; i<YUV_EXT.length; i++)
				if (YUV_EXT[i].equals(getExtension(f)))
					return true;
			return false;
		}
	}
	
	/**
	 * Description of this filter.
	 * 
	 * @return Descriptive string for this type of file filter.
	 */
	public String getDescription() {
		String extList = "";
		for (int i=0; i<YUV_EXT.length; i++)
			extList += "*." + YUV_EXT[i] + ", ";
		
		return YUV_DSC + " ("+ extList.substring(0, extList.length()-2) +")";
	}
	
	/**
	 * Returns extension of a given file.
	 * 
	 * @param f File to extract extension substring.
	 * @return Extension substring (typically, (three) letters afther (last)
	 *         dot in filename.
	 */
	private String getExtension(File f) {
		String filename = f.getName();
                return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
	}
}
