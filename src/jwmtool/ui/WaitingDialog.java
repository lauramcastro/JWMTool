package jwmtool.ui;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import jwmtool.util.I18N;

/**
 * Dialog to be displayed when waiting for some long-time-taking operation to
 * finish (such as the watermarking process), as an activity feedback for the
 * user.
 *
 * @author Laura Castro
 * @version 0.6
 */

public class WaitingDialog extends JDialog {
	
	/**
	 * The <code>WaitingDialog</code> is just an informative window
         * to show some activity feedback while waiting for some
	 * time-consuming activity (i.e. the watermarking process itselft) to
	 * complete. The activity feedback is a (default indetermined) progress
	 * bar.
         *
         * @param owner Parent window ({@link javax.swing.JFrame frame}) for the
         *              dialog.
	 */
	public WaitingDialog(JFrame owner) {
		super(owner, I18N.getInstance().getString("label.waiting"));
		setDefaultLookAndFeelDecorated(false);
		
		JPanel _waiting = new JPanel(new BorderLayout(10, 10));
		JLabel _waitingImage     = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
		JLabel _waitingMessage   = new JLabel(I18N.getInstance().getString("label.watermarking"));
		_waitingBar = new JProgressBar(0, 100);
		_waitingBar.setIndeterminate(true);
		
		_waiting.add(_waitingImage, BorderLayout.WEST);
		_waiting.add(_waitingMessage, BorderLayout.CENTER);
		_waiting.add(_waitingBar, BorderLayout.SOUTH);
		_waiting.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		setContentPane(_waiting);
		setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}
	
	/**
	 * Establish upper limit for progress bar. In case it is set, the
	 * progress bar in this dialog will no longer be indetermined.
	 *
	 * @param n Upper limit for progress bar.
	 */
	public void setMaximum(int n) {
		_waitingBar.setMaximum(n);
	}
	
	/**
	 * Reset upper limit progress bar, so that is is an indeterminated
	 * progress bar again. This is the default behaviour.
	 *
	 * @param newValue Whether the progress bar is to be indetermined or
	 *                 not.
	 */
	public void setIndeterminate(boolean newValue) {
		_waitingBar.setIndeterminate(newValue);
	}
	
	/**
	 * Establish actual position in progress bar.
	 *
	 * @param n Position where the progress bar completion will be set.
	 */
	public void setValue(int n) {
		_waitingBar.setValue(n);
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * 
	 */
	private JProgressBar _waitingBar = null;
	
}
