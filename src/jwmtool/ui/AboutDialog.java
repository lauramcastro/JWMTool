package jwmtool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jwmtool.util.I18N;

/**
 * Information dialog.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class AboutDialog extends JDialog {
	
	/**
	 * The <code>AboutDialog</code> is just an informative window
	 * with no further functionality. It only displays the JWMTool logo.
	 *
	 * @param owner Parent window ({@link javax.swing.JFrame frame}) for the
	 *              dialog.
	 */
	public AboutDialog(JFrame owner) {
		super(owner, I18N.getInstance().getString("menu.help.about.description"));
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JPanel _about = new JPanel(new BorderLayout());
		JLabel _logo  = new JLabel(new ImageIcon(getClass().getResource("/jwmtool/images/big-logo.png")));
		
		CloseAction _closeAction = new CloseAction();
		_closeAction.putValue(Action.NAME, I18N.getInstance().getString("label.close"));
		JButton _closeButton = new JButton();
		_closeButton.setAction(_closeAction);
		_closeButton.setMargin(new Insets(1, 5, 1, 10));
		_closeButton.setPreferredSize(new Dimension(75, 22));
		
		_about.add(_logo, BorderLayout.CENTER);
		_about.add(_closeButton, BorderLayout.SOUTH);
		
		setContentPane(_about);
		setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}
	
	// ----- ----- ----- ACTIONS ----- ----- -----
	
	private class CloseAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			dispose();
		}
	}
	
}
