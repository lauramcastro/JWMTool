package jwmtool.ui;

import ac.essex.statistics.functions.GraphableFunction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import jwmtool.lib.Watermarking;

import jwmtool.util.I18N;
import jwmtool.util.exceptions.InvalidParameterException;
import jwmtool.util.exceptions.WatermarkingException;

/**
 * Main window for JWMTool.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class MainWindow extends JFrame {
	
	/**
	 * Creates and displays the main JWMTool application window, which gives
	 * access to all other windows and functionalities. This class
	 * implements Singleton design pattern, so constructor's visibility is
	 * private. Access to the single instance is granted by means of
	 * {@link jwmtool.ui.MainWindow#getInstance getInstance()} method.
	 */
	private MainWindow() {
		super(I18N.getInstance().getString("application.name"));
		setDefaultLookAndFeelDecorated(true);
		setIconImage(new ImageIcon(getClass().getResource("/jwmtool/images/16x16/logo.png")).getImage());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Create application menu
		JMenuBar  _menuBar = new JMenuBar();
		JMenu     _appMenu = new JMenu(I18N.getInstance().getString("menu.application"));
		JMenuItem _exitMenuItem = new JMenuItem();
		Action    _exitAction   = new ExitAction();
		_exitAction.putValue(Action.NAME, I18N.getInstance().getString("menu.application.exit"));
		_exitAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/exit.png")));
		_exitAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("menu.application.exit.description"));
		_exitMenuItem.setAction(_exitAction);
		
		// Create watermarking menu
		JMenu     _wmMenu = new JMenu(I18N.getInstance().getString("menu.watermarking"));
		JMenuItem _configMenuItem = new JMenuItem();
		Action    _configAction   = new ConfigAction();
		_configAction.putValue(Action.NAME, I18N.getInstance().getString("menu.watermarking.configure"));
		_configAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/configure.png")));
		_configAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("menu.watermarking.configure.description"));
		_configMenuItem.setAction(_configAction);
		JMenuItem _execMenuItem = new JMenuItem();
		Action    _execAction   = new ExecAction();
		_execAction.putValue(Action.NAME, I18N.getInstance().getString("menu.watermarking.execute"));
		_execAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/execute.png")));
		_execAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("menu.watermarking.execute.description"));
		_execMenuItem.setAction(_execAction);
		
		// Create help menu
		JMenu     _helpMenu = new JMenu(I18N.getInstance().getString("menu.help"));
		JMenuItem _aboutMenuItem = new JMenuItem();
		Action    _aboutAction   = new AboutAction();
		_aboutAction.putValue(Action.NAME, I18N.getInstance().getString("menu.help.about"));
		_aboutAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/logo.png")));
		_aboutAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("menu.help.about.description"));
		_aboutMenuItem.setAction(_aboutAction);
		
		// Key shortcuts
		_appMenu.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.application")).intValue());
		_exitMenuItem.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.application.exit")).intValue());
		_wmMenu.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.watermarking")).intValue());
		_configMenuItem.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.watermarking.configure")).intValue());
		_execMenuItem.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.watermarking.execute")).intValue());
		_helpMenu.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.help")).intValue());
		_aboutMenuItem.setMnemonic(Integer.valueOf(I18N.getInstance().getString("shortcut.menu.help.about")).intValue());
		
		_appMenu.add(_exitMenuItem);
		_wmMenu.add(_configMenuItem);
		_wmMenu.add(_execMenuItem);
		_helpMenu.add(_aboutMenuItem);
		_menuBar.add(_appMenu);
		_menuBar.add(_wmMenu);
		// _menuBar.setHelpMenu(_helpMenu); // not yet implemented!
		_menuBar.add(_helpMenu);
		
		// Create statusbar
		JPanel _statusBar = new JPanel();
		_statusBar.setLayout(new BorderLayout());
		_statusBar.setBorder(new EtchedBorder());
		JLabel _date = new JLabel(I18N.getInstance().getString("application.name"));
		_statusBar.add(_date, BorderLayout.EAST);
		
		// Create main component
		JPanel _mainPanel  = new JPanel();
		_mainPanel.setLayout(new BorderLayout());
		JLabel _background = new JLabel(new ImageIcon(getClass().getResource("/jwmtool/images/logo.png")));
		_mainPanel.add(_background, BorderLayout.CENTER);
		
		// Set layout
		BorderLayout _layout = new BorderLayout();
		setLayout(_layout);
		getContentPane().add(_mainPanel, BorderLayout.CENTER);
		getContentPane().add(_statusBar, BorderLayout.SOUTH);
		
		setJMenuBar(_menuBar);
		setResizable(false);
		setLocation(75, 75);
		pack();
	}
	
	/**
	 * Provides access to {@link jwmtool.ui.MainWindow MainWindow} single
	 * instance.
	 *
	 * @return Single reference to {@link jwmtool.ui.MainWindow MainWindow}
	 *         instance.
	 */
	public static MainWindow getInstance() {
		return _window;
	}
	
	// ----- ----- ----- ACTIONS ----- ----- -----
	
	private class ExitAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if (_configDialog != null) {
				_configDialog.dispose();
			}
			dispose();
			System.exit(0);
		}
	}
	
	private class ConfigAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if (_configDialog == null) {
				_configDialog = new ConfigDialog(MainWindow.getInstance());
			}
			_configDialog.setModal(true);
			_configDialog.setVisible(true);
		}
	}
	
	private class ExecAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if (_configDialog == null) {
				JDialog dialog = new WarningDialog(MainWindow.getInstance(), I18N.getInstance().getString("message.configuration.needed"));
				dialog.setModal(true);
				dialog.setVisible(true);
			} else {
				// Recover settings from configuration dialog
				_filename       = _configDialog.getFilename();
				_outputFilename = _configDialog.getOutputFilename();
				_blindProcess   = _configDialog.isBlind();
				_rangeInit      = _configDialog.getRangeInit();
				_rangeEnd       = _configDialog.getRangeEnd();
				_modificationType = _configDialog.getModificationType();
				_modificationStep = _configDialog.getModificationStep();
				_modifyY = _configDialog.isYmodified();
				_modifyU = _configDialog.isUmodified();
				_modifyV = _configDialog.isVmodified();
				if ((_filename == null) || (_outputFilename == null) || (_filename.equals("")) || (_outputFilename.equals(""))) {
					JDialog dialog = new WarningDialog(MainWindow.getInstance(), I18N.getInstance().getString("message.configuration.incomplete"));
					dialog.setModal(true);
					dialog.setVisible(true);
				}
				else {
					try {
						switch (_configDialog.getModificationStep()) {
							case Watermarking.MODIFICATION_STEP_INCREMENTAL: 
								_lowLimit   = _configDialog.getModificationLowLimit();
								_upperLimit = _configDialog.getModificationUpperLimit();
							break;
						case Watermarking.MODIFICATION_STEP_UNIFORM:
								_modificationValue = _configDialog.getModificationValue();
							break;
						case Watermarking.MODIFICATION_STEP_RANDOM:
								_modificationFunction = _configDialog.getModificationFunction();
							break;
						}
					} catch (InvalidParameterException e) {
						e.printStackTrace();
					}
					// Show display panel
					JWMInitializer _initializer = new JWMInitializer();
					_waitingDialog = new WaitingDialog(MainWindow.getInstance());
					_initializer.start();
				}
			}
		}
	}
	
	private class AboutAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			JDialog dialog = new AboutDialog(MainWindow.getInstance());
			dialog.setModal(false);
			dialog.setVisible(true);
		}
	}
	
	// ----- ----- ----- WORKING THREAD ----- ----- -----
	
	private class JWMInitializer extends Thread {
		public void run() {
			try {
				_waitingDialog.setVisible(true);
				// time-consuming operation
				_displayPanel = new JWMPanel(_filename, _outputFilename,
							     _rangeInit, _rangeEnd, _modificationType, _modificationStep,
							     _lowLimit, _upperLimit, _modificationValue, _modificationFunction,
							     _modifyY, _modifyU, _modifyV, _blindProcess);
				// previous operation can take quite some time
				MainWindow.getInstance().getContentPane().removeAll();
				MainWindow.getInstance().getContentPane().add(_displayPanel, BorderLayout.CENTER);
				MainWindow.getInstance().setLocation(75, 75);
				MainWindow.getInstance().pack();
				_waitingDialog.setVisible(false);
			} catch (WatermarkingException e) {
				_waitingDialog.setVisible(false);
				JDialog dialog = new ErrorDialog(MainWindow.getInstance(), e.getMessage());
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		}
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Single instance of {@link jwmtool.ui.MainWindow MainWindow}.
	 */
	private static MainWindow _window = new MainWindow();
	
	/**
	 * Reference to {@link jwmtool.ui.ConfigDialog ConfigDialog} window.
	 */
	private ConfigDialog _configDialog  = null;
	/**
	 * Reference to {@link jwmtool.ui.WaitingDialog WaitingDialog} window.
	 */
	private WaitingDialog _waitingDialog = null;
	/**
	 * Display panel where source and generated watermarked videostreams
	 * will be shown after watermarking process.
	 */
	private JWMPanel _displayPanel = null;
	
	/**
	 * Input (source videostream) filename.
	 */
	private String _filename = null;
	/**
	 * Output (generated, watermarked videostream) filename.
	 */
	private String _outputFilename = null;
	/**
	 * Boolean value that stores user preference about whether or not the
         * playback after the watermarking process will be blind.
	 */
	private boolean _blindProcess = false;
	/**
	 * First coefficient in 1-63 zigzag sequence to be modified by
	 * watermarking process.
	 */
	private int _rangeInit = 0;
	/**
	 * Last coefficient to be modified in 1-63 zigzag sequence.
	 */
	private int _rangeEnd  = 0;
	/**
	 * Type of modification selected to be performed as part of the
	 * watermarking process (namely,
	 * {@link jwmtool.lib.Watermarking#MODIFICATION_TYPE_ABSOLUTE absolute}, or
	 * {@link jwmtool.lib.Watermarking#MODIFICATION_TYPE_PERCENTAGE relative}).
	 */
	private int _modificationType = -1;
	/**
	 * Step modification change selected to be applied as part of the
	 * watermarking process (namely,
	 * {@link jwmtool.lib.Watermarking#MODIFICATION_STEP_INCREMENTAL incremental},
	 * {@link jwmtool.lib.Watermarking#MODIFICATION_STEP_UNIFORM uniform}, or
	 * {@link jwmtool.lib.Watermarking#MODIFICATION_STEP_RANDOM random}).
	 */
	private int _modificationStep = -1;
	/**
	 * Lowest value to add to coefficients.
	 */
	private int _lowLimit   = Integer.MIN_VALUE;
	/**
	 * Highest value to add to coefficients.
	 */
	private int _upperLimit = Integer.MAX_VALUE;
	/**
	 * Constant value to add to coefficients.
	 */
	private int _modificationValue = 0;
	/**
	 * Statistic distribution function to generate random modification
	 * values to add to coefficients.
	 */
	private GraphableFunction _modificationFunction = null;
	/**
	 * Boolean value that stores user preference about whether or not the
         * Y component (luminance) is to be altered during watermarking process.
	 */
	private boolean _modifyY = false;
	/**
	 * Boolean value that stores user preference about whether or not the
         * U component (blue chrominance) is to be altered during watermarking
	 * process.
	 */
	private boolean _modifyU = false;
	/**
	 * Boolean value that stores user preference about whether or not the
         * V component (red chrominance) is to be altered during watermarking
	 * process.
	 */
	private boolean _modifyV = false;

}
