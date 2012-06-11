package jwmtool;

/**
 * Client for JWMTool.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class JWMTool {
	
	/**
	 * Static main method to run the application.
	 * Configuration file is provided to
	 * {@link jwmtool.util.ConfigurationParametersManager ConfigurationParametersManager}
	 * and then {@link jwmtool.ui.MainWindow MainWindow} is displayed.
	 *
	 * @param args Arguments to the main function (none are expected or used).
	 */
	public static void main (final String args[]) {
		jwmtool.util.ConfigurationParametersManager.getInstance().setConfigFile("jwmtool.conf");
		jwmtool.ui.MainWindow.getInstance().setVisible(true);
	}
	
}
