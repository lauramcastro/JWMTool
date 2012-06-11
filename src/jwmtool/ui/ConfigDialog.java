package jwmtool.ui;

import ac.essex.statistics.functions.GraphableFunction;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import jwmtool.lib.Watermarking;

import jwmtool.util.I18N;
import jwmtool.util.YUVFileFilter;
import jwmtool.util.exceptions.InvalidParameterException;

/**
 * Main configuration dialog for JWMTool. It presents and stores the
 * watermarking process settings.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class ConfigDialog extends JDialog {
	
	/**
	 * Creates and displays configuration settings window, so that the user
	 * can select and save her/his preferences for the watermarking process.
	 *
	 * @param owner Parent window ({@link javax.swing.JFrame frame}) for the
	 *              dialog.
	 */
	public ConfigDialog(JFrame owner) {
		super(owner, I18N.getInstance().getString("menu.watermarking.configure.description"));
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		_owner = owner;
		
		// Create main panel
		JPanel _config = new JPanel(new GridBagLayout());
		GridBagConstraints _constraints;
		
		// File panel
		JPanel _filePanel = new JPanel(new GridBagLayout());
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 5, 0, 15);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_filePanel.add(new JLabel(I18N.getInstance().getString("config.file")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_fileName.setToolTipText(I18N.getInstance().getString("config.file.description"));
		_filePanel.add(_fileName, _constraints);
		ChooseFileAction _chooseFileAction = new ChooseFileAction();
		_chooseFileAction.putValue(Action.NAME, I18N.getInstance().getString("config.file.choose"));
		JButton _chooseFileButton = new JButton();
		_chooseFileButton.setAction(_chooseFileAction);
		_chooseFileButton.setPreferredSize(new Dimension(22, 22));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 1, 0);
		_constraints.gridx = 2;
		_constraints.gridy = 0;
		_chooseFileButton.setToolTipText(I18N.getInstance().getString("config.file.description"));
		_filePanel.add(_chooseFileButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 5, 0, 15);
		_constraints.gridx = 0;
		_constraints.gridy = 1;
		_filePanel.add(new JLabel(I18N.getInstance().getString("config.outputfile")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 1;
		_outputFileName.setToolTipText(I18N.getInstance().getString("config.outputfile.description"));
		_filePanel.add(_outputFileName, _constraints);
		ChooseOutputFileAction _chooseOutputFileAction = new ChooseOutputFileAction();
		_chooseOutputFileAction.putValue(Action.NAME, I18N.getInstance().getString("config.outputfile.choose"));
		JButton _chooseOutputFileButton = new JButton();
		_chooseOutputFileButton.setAction(_chooseOutputFileAction);
		_chooseOutputFileButton.setPreferredSize(new Dimension(22, 22));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 0, 0);
		_constraints.gridx = 2;
		_constraints.gridy = 1;
		_chooseOutputFileButton.setToolTipText(I18N.getInstance().getString("config.outputfile.description"));
		_filePanel.add(_chooseOutputFileButton, _constraints);
		
		// Blind process panel
		JPanel _blindPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		_blindProcess.setToolTipText(I18N.getInstance().getString("config.blind.description"));
		_blindProcess.setHorizontalTextPosition(SwingConstants.LEFT);
		_blindPanel.add(_blindProcess);
		
		// Components panel
		JPanel _componentsPanel = new JPanel(new GridBagLayout());
		_modifyY.setToolTipText(I18N.getInstance().getString("config.modifyY.description"));
		_modifyU.setToolTipText(I18N.getInstance().getString("config.modifyU.description"));
		_modifyV.setToolTipText(I18N.getInstance().getString("config.modifyV.description"));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 20);
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_componentsPanel.add(new JLabel(I18N.getInstance().getString("config.modify")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 20);
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_componentsPanel.add(_modifyY, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 20);
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.gridx = 1;
		_constraints.gridy = 1;
		_componentsPanel.add(_modifyU, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 20);
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.gridx = 1;
		_constraints.gridy = 2;
		_componentsPanel.add(_modifyV, _constraints);
		
		// Coefficients panel
		JPanel _coefsPanel = new JPanel(new GridBagLayout());
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 20);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_coefsPanel.add(new JLabel(I18N.getInstance().getString("config.coefficients")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 15, 0, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_coefsPanel.add(new JLabel(I18N.getInstance().getString("config.begin_at")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 5, 0, 5);
		_constraints.gridx = 2;
		_constraints.gridy = 0;
		_rangeInit.setToolTipText(I18N.getInstance().getString("config.begin_at.description"));
		_coefsPanel.add(_rangeInit, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 15, 0, 0);
		_constraints.gridx = 3;
		_constraints.gridy = 0;
		_coefsPanel.add(new JLabel(I18N.getInstance().getString("config.end_at")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 5, 0, 15);
		_constraints.gridx = 4;
		_constraints.gridy = 0;
		_rangeEnd.setToolTipText(I18N.getInstance().getString("config.end_at.description"));
		_coefsPanel.add(_rangeEnd, _constraints);
		
		// Alterations panel
		JPanel _alterPanel = new JPanel(new GridBagLayout());
		ButtonGroup _altRGroup = new ButtonGroup();
		_absRButton.addItemListener(new AlterationItemListener());
		_altRGroup.add(_absRButton);
		_altRGroup.add(_perRButton);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_alterPanel.add(new JLabel(I18N.getInstance().getString("config.alterations")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 10, 0, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_absRButton.setToolTipText(I18N.getInstance().getString("config.alterations.absolute.description"));
		_alterPanel.add(_absRButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 10, 0, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 1;
		_perRButton.setToolTipText(I18N.getInstance().getString("config.alterations.percentage.description"));
		_alterPanel.add(_perRButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 0);
		_constraints.gridx = 2;
		_constraints.gridy = 1;
		_alterPanel.add(new JLabel(I18N.getInstance().getString("config.alterations.modifications")), _constraints);
		
		// Values panel
		JPanel _valuesPanel = new JPanel(new GridBagLayout());
		ButtonGroup _valuesGroup = new ButtonGroup();
		_valuesGroup.add(_incRButton);
		_valuesGroup.add(_unfRButton);
		_valuesGroup.add(_ranRButton);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.values")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 10, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_constraints.gridwidth = 1;
		_incRButton.setToolTipText(I18N.getInstance().getString("config.values.incremental.description"));
		_valuesPanel.add(_incRButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 10, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 1;
		_unfRButton.setToolTipText(I18N.getInstance().getString("config.values.uniform.description"));
		_valuesPanel.add(_unfRButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.anchor = GridBagConstraints.WEST;
		_constraints.insets = new Insets(0, 10, 0, 10);
		_constraints.gridx = 1;
		_constraints.gridy = 2;
		_ranRButton.setToolTipText(I18N.getInstance().getString("config.values.random.description"));
		_valuesPanel.add(_ranRButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 2;
		_constraints.gridy = 0;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.values.from")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 2;
		_constraints.gridy = 1;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.values.of")), _constraints);
		RandomCAction _randomCAction = new RandomCAction();
		_randomCAction.putValue(Action.NAME, I18N.getInstance().getString("config.values.random.setup"));
		JButton _ranCButton = new JButton();
		_ranCButton.setAction(_randomCAction);
		_ranCButton.setPreferredSize(new Dimension(80, 20));
		_constraints = new GridBagConstraints();
		_constraints.gridx = 2;
		_constraints.gridy = 2;
		_constraints.gridwidth = 2;
		_ranCButton.setToolTipText(I18N.getInstance().getString("config.values.random.setup.description"));
		_ranCButton.setContentAreaFilled(false);
		_valuesPanel.add(_ranCButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 5, 0, 5);
		_constraints.gridx = 3;
		_constraints.gridy = 0;
		_valuesPanel.add(_lowILimit, _constraints);
		_constraints.insets = new Insets(0, 5, 0, 5);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 3;
		_constraints.gridy = 1;
		_valuesPanel.add(_mValue, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 4;
		_constraints.gridy = 0;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.values.to")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 5, 0, 5);
		_constraints.gridx = 5;
		_constraints.gridy = 0;
		_valuesPanel.add(_upperILimit, _constraints);
		
		// Buttons panel
		JPanel _butPanel = new JPanel(new GridBagLayout());
		AcceptAction _acceptAction = new AcceptAction();
		_acceptAction.putValue(Action.NAME, I18N.getInstance().getString("label.accept"));
		JButton _acceptButton = new JButton();
		_acceptButton.setAction(_acceptAction);
		_acceptButton.setPreferredSize(new Dimension(88, 22));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_butPanel.add(_acceptButton, _constraints);
		CancelAction _cancelAction = new CancelAction();
		_cancelAction.putValue(Action.NAME, I18N.getInstance().getString("label.cancel"));
		JButton _cancelButton = new JButton();
		_cancelButton.setAction(_cancelAction);
		_cancelButton.setPreferredSize(new Dimension(88, 22));	
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 10, 0, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_butPanel.add(_cancelButton, _constraints);
		
		// Populate panel
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_config.add(_filePanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.gridx = 0;
		_constraints.gridy = 1;
		_config.add(_blindPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 2;
		_config.add(_componentsPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 3;
		_config.add(_coefsPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 4;
		_config.add(_alterPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 5;
		_config.add(_valuesPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.fill = GridBagConstraints.HORIZONTAL;
		_constraints.insets = new Insets(5, 5, 5, 5);
		_constraints.gridx = 0;
		_constraints.gridy = 6;
		_config.add(_butPanel, _constraints);
		
		setContentPane(_config);
		setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}
	
	// ----- ----- ----- ACCESS METHODS ----- ----- -----
	
	/**
	 * Returns the name of the file to be used in the watermarking process
	 * as source videostream.
	 *
	 * @return Name (full or absolute path) of source videostream file.
	 */
	public String getFilename() {
		return _fileName.getText();
	}
	
	/**
	 * Returns the name of the file to be used to save the output of the
	 * watermarking process.
	 *
	 * @return Name (full or absolute path) of output videostream file.
	 */
	public String getOutputFilename() {
		return _outputFileName.getText();
	}
	
	/**
	 * Checks if the user has selected the displaying/playback of the
	 * watermarking results to be a blind process, i.e. source videostream
	 * and watermarked videostream will appear side by side but no label
	 * will mark which one is each one.
	 *
	 * @return A boolean value <code>false</code> if the source videostream
	 *         is to be labelled as the original video and the watermarked
	 *         videostream as the generated video. A boolean value
	 *         <code>true</code> if both input and output videos will have
	 *         no significant label.
	 */
	public boolean isBlind() {
		return _blindProcess.isSelected();
	}
	
	/**
	 * Checks if the user has selected the Y component (luminance) to be
	 * modified during watermarking process.
	 *
	 * @return A boolean value <code>true</code> if the luminance is to
	 *         be altered; a boolean value <code>false</code>, if not.
	 */
	public boolean isYmodified() {
		return _modifyY.isSelected();
	}
	
	/**
	 * Checks if the user has selected the U component (blue chrominance) to
	 * be modified during watermarking process.
	 *
	 * @return A boolean value <code>true</code> if the blue chrominance is
	 *         to be altered; a boolean value <code>false</code>, if not.
	 */
	public boolean isUmodified() {
		return _modifyU.isSelected();
	}
	
	/**
	 * Checks if the user has selected the V component (red chrominance) to
	 * be modified during watermarking process.
	 *
	 * @return A boolean value <code>true</code> if the red chrominance is
	 *         to be altered; a boolean value <code>false</code>, if not.
	 */
	public boolean isVmodified() {
		return _modifyV.isSelected();
	}
	
	/**
	 * Returns first coefficient (in range from 1 to 63) in zigzag sequence
	 * to be altered during watermark process.
	 *
	 * @return Position of first coefficient to be affected by watermarking
	 *         process.
	 */
	public int getRangeInit() {
		return ((Integer) _rangeInit.getValue()).intValue();
	}
	
	/**
	 * Returns last coefficient (in range from
	 * {@link jwmtool.ui.ConfigDialog#_rangeInit rangeInit} to 63) in zigzag
	 * sequence to be altered during watermark process.
	 *
	 * @return Position of last coefficient to be affected by watermarking
	 *         process.
	 */
	public int getRangeEnd() {
		return ((Integer) _rangeEnd.getValue()).intValue();
	}
	
	/**
	 * Returns the type of modification (absolute or percentage -relative-)
	 * selected to be used during watermarking process. An absolute
	 * modification means that each coefficient in range will be added or
	 * substracted an absolute value (also to be configured). A relative
	 * modification means that each coefficient in range will be added or
	 * substracted a value proportional to its own value (percentage).
	 *
	 * @return Code of selected modification type.
	 */
	public int getModificationType() {
		if (_absRButton.isSelected()) {
			return Watermarking.MODIFICATION_TYPE_ABSOLUTE;
		} else if (_perRButton.isSelected()) {
			return Watermarking.MODIFICATION_TYPE_PERCENTAGE;
		} else {
			return Watermarking.MODIFICATION_TYPE_UNKNOWN;
		}
	}
	
	/**
	 * Returns how the modification varies after each step (coefficient
	 * alteration): incrementally, uniformly, randomly. An incremental
	 * modification means that each coefficient in range will be altered in
	 * a slightly higher value to the previous one (always within
	 * modification limits, in a circular way). A uniform modification means
	 * that all coefficients in range will be altered by the same value.
	 * A random modification means that a statistical distribution function
	 * will be used to select the value to be added to each coefficient in
	 * range.
	 *
	 * @return Code of selected modification step.
	 */
	public int getModificationStep() {
		if (_incRButton.isSelected()) {
			return Watermarking.MODIFICATION_STEP_INCREMENTAL;
		} else if (_unfRButton.isSelected()) {
			return Watermarking.MODIFICATION_STEP_UNIFORM;
		} else if (_ranRButton.isSelected()) {
			return Watermarking.MODIFICATION_STEP_RANDOM;
		} else {
			return Watermarking.MODIFICATION_STEP_UNKNOWN;
		}
	}
	
	/**
	 * Returns lowest value to be added to a coefficient in range when the
	 * selected modification step is incremental.
	 *
	 * @return Lowest value to be added to coefficients during watermarking.
	 */
	public int getModificationLowLimit() 
		throws InvalidParameterException {
		switch (getModificationStep()) {
			case Watermarking.MODIFICATION_STEP_INCREMENTAL: return ((Integer) _lowILimit.getValue()).intValue();
			case Watermarking.MODIFICATION_STEP_UNIFORM:
			case Watermarking.MODIFICATION_STEP_RANDOM:
			default:
				throw new InvalidParameterException(I18N.getInstance().getString("parameters.lowLimit"));
		}
	}
	
	/**
	 * Returns highest value to be added to a coefficient in range when the
	 * selected modification step is incremental. After this highest
	 * alteration value, next coefficient will be affected by
	 * {@link jwmtool.ui.ConfigDialog#_lowILimit lowLimit}, in a circular
	 * way.
	 *
	 * @return Highest value to be added to coefficients during
	 *         watermarking.
	 */
	public int getModificationUpperLimit()
		throws InvalidParameterException {
		switch (getModificationStep()) {
			case Watermarking.MODIFICATION_STEP_INCREMENTAL: return ((Integer) _upperILimit.getValue()).intValue();
			case Watermarking.MODIFICATION_STEP_UNIFORM:
			case Watermarking.MODIFICATION_STEP_RANDOM:
			default:
				throw new InvalidParameterException(I18N.getInstance().getString("parameters.upperLimit"));
		}
	}
	
	/**
	 * Returns value to be added to coefficients in range when the selected
	 * modification step is uniform.
	 *
	 * @return Value to be added to coefficients during watermarking.
	 */
	public int getModificationValue()
		throws InvalidParameterException {
		switch (getModificationStep()) {
			case Watermarking.MODIFICATION_STEP_UNIFORM:     return ((Integer) _mValue.getValue()).intValue();
			case Watermarking.MODIFICATION_STEP_INCREMENTAL:
			case Watermarking.MODIFICATION_STEP_RANDOM:
			default:
				throw new InvalidParameterException(I18N.getInstance().getString("parameters.value"));
		}
	}
	
	/**
	 * Returns statistical distribution function to obtain modification
	 * values for coefficients in range when selected modification step is
	 * random.
	 *
	 * @return {@link ac.essex.statistics.functions.GraphableFunction Function}
	 *         object to be used to calculate modification values.
	 */
	public GraphableFunction getModificationFunction()
		throws InvalidParameterException {
		switch (getModificationStep()) {
			case Watermarking.MODIFICATION_STEP_RANDOM:      if (_randomCDialog == null) {
				                                         	_randomCDialog = new RandomConfigDialog(MainWindow.getInstance());
			                                                 }
			                                                 return _randomCDialog.getFunction();
			case Watermarking.MODIFICATION_STEP_UNIFORM:
			case Watermarking.MODIFICATION_STEP_INCREMENTAL:
			default:
				throw new InvalidParameterException(I18N.getInstance().getString("parameters.value"));
		}
	}
	
	// ----- ----- ----- ACTIONS ----- ----- -----
	
	private class ChooseFileAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				if (_fileChooser == null) {
					_fileChooser = new JFileChooser();
					_fileChooser.setFileFilter(new YUVFileFilter());
				}
				if (_fileChooser.showOpenDialog(_owner) == JFileChooser.APPROVE_OPTION) {
					_fileName.setText(_fileChooser.getSelectedFile().getCanonicalPath());
				}
			} catch (IOException e) { }
		}
	}
	
	private class ChooseOutputFileAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				if (_fileChooser == null) {
					_fileChooser = new JFileChooser();
					_fileChooser.setFileFilter(new YUVFileFilter());
				}
				if (_fileChooser.showOpenDialog(_owner) == JFileChooser.APPROVE_OPTION) {
					_outputFileName.setText(_fileChooser.getSelectedFile().getCanonicalPath());
				}
			} catch (IOException e) { }
		}
	}
	
	private class AcceptAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			setVisible(false);
		}
	}
	
	private class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if (_randomCDialog != null) {
				_randomCDialog.dispose();
			}
			dispose();
		}
	}
	
	private class RandomCAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if (_randomCDialog == null) {
				_randomCDialog = new RandomConfigDialog(MainWindow.getInstance());
			}
			_randomCDialog.setModal(true);
			_randomCDialog.setVisible(true);
		}
	}
	
	// ----- ----- ----- LISTENERS ----- ----- -----
	
	private class AlterationItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (_absRButton.isSelected()) { // enable all modification options
				_incRButton.setEnabled(true);
				_ranRButton.setEnabled(true);
				_unfRButton.setEnabled(true);
				_lowILimit.setEnabled(true);
				_upperILimit.setEnabled(true);
				((SpinnerNumberModel) _mValue.getModel()).setMinimum(new Integer(-127));
				((SpinnerNumberModel) _mValue.getModel()).setMaximum(new Integer(128));
			}
			else { // disable incremental and random modification options
				_incRButton.setEnabled(false);
				_ranRButton.setEnabled(false);
				_unfRButton.setSelected(true);
				_lowILimit.setEnabled(false);
				_upperILimit.setEnabled(false);
				int oldValue = ((Integer) _mValue.getValue()).intValue();
				((SpinnerNumberModel) _mValue.getModel()).setMinimum(new Integer(-100));
				((SpinnerNumberModel) _mValue.getModel()).setMaximum(new Integer(100));
				if (oldValue < -100) {
					_mValue.setValue(new Integer(-100));
				}
				else if (oldValue > 100) {
					_mValue.setValue(new Integer(100));
				}
			}
		}
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	* Parent {@link javax.swing.JFrame frame}.
	 */
	private JFrame _owner = null;
	/**
	 * {@link javax.swing.JFileChooser File chooser} to select input and
	 * output files.
	 */
	private JFileChooser _fileChooser = null;
	/**
	 * {@link javax.swing.JTextField Text field} to input source file name.
	 */
	private JTextField _fileName = new JTextField(16);
	/**
	 * {@link javax.swing.JTextField Text field} to input file name for
	 * generated watermarked videostream.
	 */
	private JTextField _outputFileName = new JTextField(16);
	/**
	 * {@link javax.swing.JCheckBox Checkbox} to select whether or not the
	 * playback after the watermarking process will be blind. Default value
	 * is false.
	 */
	private JCheckBox _blindProcess = new JCheckBox(I18N.getInstance().getString("config.blind"), false);
	/**
	 * {@link javax.swing.JCheckBox Checkbox} to select whether or not the
	 * Y component (luminance) is to be altered during watermarking process.
	 * Default choice is false.
	 */
	private JCheckBox _modifyY = new JCheckBox(I18N.getInstance().getString("config.modifyY"), false);
	/**
	 * {@link javax.swing.JCheckBox Checkbox} to select whether or not the
	 * U component (blue chrominance) is to be altered during watermarking
	 * process. Default choice is false.
	 */
	private JCheckBox _modifyU = new JCheckBox(I18N.getInstance().getString("config.modifyU"), false);
	/**
	 * {@link javax.swing.JCheckBox Checkbox} to select whether or not the
	 * V component (red chrominance) is to be altered during watermarking
	 * process. Default choice is false.
	 */
	private JCheckBox _modifyV = new JCheckBox(I18N.getInstance().getString("config.modifyV"), false);
	/**
	 * {@link javax.swing.JSpinner Counter} to select first coefficient to
	 * be modified in 1-63 zigzag sequence.
	 */
	private JSpinner _rangeInit = new JSpinner(new SpinnerNumberModel(1, 1, 63, 1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select last coefficient to
	 * be modified in {@link jwmtool.ui.ConfigDialog#_rangeInit rangeInit} -
	 *  63 zigzag sequence.
	 */
	private JSpinner _rangeEnd  = new JSpinner(new SpinnerNumberModel(1, 1, 63, 1));
	/**
	 * {@link javax.swing.JRadioButton Option} to select absolute value
	 * modification during watermarking.
	 */
	private JRadioButton _absRButton = new JRadioButton(I18N.getInstance().getString("config.alterations.absolute"), true);
	/**
	 * {@link javax.swing.JRadioButton Option} to select relative
	 * (percentage) value modification during watermarking.
	 */
	private JRadioButton _perRButton = new JRadioButton(I18N.getInstance().getString("config.alterations.percentage"));
	/**
	 * {@link javax.swing.JRadioButton Option} to select an incremental step
	 * for coefficient value modification.
	 */
	private	JRadioButton _incRButton = new JRadioButton(I18N.getInstance().getString("config.values.incremental"), true);
	/**
	 * {@link javax.swing.JRadioButton Option} to select a uniform step
	 * for coefficient value modification.
	 */
	private	JRadioButton _unfRButton = new JRadioButton(I18N.getInstance().getString("config.values.uniform"));
	/**
	 * {@link javax.swing.JRadioButton Option} to select a random step (i.e.
	 * use of a statistical distribution function) for coefficient value
	 * modification.
	 */
	private	JRadioButton _ranRButton = new JRadioButton(I18N.getInstance().getString("config.values.random"));
	/**
	 * {@link javax.swing.JSpinner Counter} to select lowest value to add to
	 * coefficients when using incremental modification step.
	 */
	private	JSpinner _lowILimit   = new JSpinner(new SpinnerNumberModel(0, -127, 128, 1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select highest value to add
	 * to coefficients when using incremental modification step.
	 */
	private	JSpinner _upperILimit = new JSpinner(new SpinnerNumberModel(0, -127, 128, 1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select constant value to add
	 * to coefficients when using uniform modification step.
	 */
	private	JSpinner _mValue      = new JSpinner(new SpinnerNumberModel(0, -127, 128, 1));
	/**
	 * {@link jwmtool.ui.RandomConfigDialog Specific config dialog} to 
	 * select particular user preferences for random modification step.
	 */
	private RandomConfigDialog _randomCDialog = null;

}
