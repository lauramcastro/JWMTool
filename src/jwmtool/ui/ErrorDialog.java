package jwmtool.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import jwmtool.util.I18N;

/**
 * Error dialog.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class ErrorDialog extends JDialog {
	
	/**
	 * The <code>ErrorDialog</code> is just an informative window used to
	 * display expected error messages that may arise during the execution
	 * of the JWMTool application.
	 *
	 * @param owner Parent window ({@link javax.swing.JFrame frame}) for the
         *              dialog.
	 * @param error Error message to be displayed for user information.
	 */
	public ErrorDialog(JFrame owner, String error) {
		super(owner, I18N.getInstance().getString("label.error"));
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JPanel _error = new JPanel(new GridBagLayout());
		JLabel _errorImage   = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		JLabel _errorMessage = new JLabel(error);
		
		CloseAction _closeAction = new CloseAction();
		_closeAction.putValue(Action.NAME, I18N.getInstance().getString("label.accept"));
		JButton _closeButton = new JButton();
		_closeButton.setAction(_closeAction);
		_closeButton.setPreferredSize(new Dimension(88, 22));
		
		GridBagConstraints _constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 0);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_error.add(_errorImage, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_error.add(_errorMessage, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 5, 0);
		_constraints.gridx = 0;
		_constraints.gridy = 1;
		_constraints.gridwidth = 2;
		_error.add(_closeButton, _constraints);
		
		setContentPane(_error);
		setResizable(false);
		setVisible(false);
		setModal(true);
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
