package jwmtool.ui;

import ac.essex.statistics.functions.GraphableFunction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwmtool.util.ConfigurationParametersManager;
import jwmtool.util.I18N;
import jwmtool.util.exceptions.MissingConfigurationParameterException;
import jwmtool.util.functions.*;
import jwmtool.util.graphics.DoubleGraphPanel;

/**
 * Specific JWMTool configuration dialog for certain watermarking preferences,
 * namely those related to random modification, which is based on the use of a
 * statistic distribution function to obtain the values to add to the
 * coefficients in Y, U, V components.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class RandomConfigDialog extends JDialog {
	
	/**
	 * Creates and displays additional settings window, with relevant
	 * options to be used in case a random modification watermarking
	 * procedure has been selected.
	 *
	 * @param owner Parent window ({@link javax.swing.JFrame frame}) for the
         *              dialog.
	 */
	public RandomConfigDialog(JFrame owner) {
		super(owner, I18N.getInstance().getString("menu.watermarking.configure.random.description"));
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Create main panel
		JPanel _config = new JPanel(new FlowLayout());
		
		// Create plot subpanel
		JPanel _plotPanel = new JPanel(new BorderLayout());
		GraphableFunction f = new DiscreteFactoredGaussian(1.0, 0.0, 1.0, -5, 5, 5);
		_graphPanel = new DoubleGraphPanel(f, 5);
		_graphPanel.setMinX(-5);
		_graphPanel.setMaxX(5);
		_graphPanel.setMinY(0);
		_graphPanel.setMaxY(5);
		_graphPanel.setNumberFormatter(new java.text.DecimalFormat("0.0"));
		_graphPanel.setNotchCountX(10);
		_plotPanel.add(_graphPanel, BorderLayout.CENTER);
		_plotPanel.setPreferredSize(new Dimension(400, 300));
		
		// Create options subpanel
		JPanel _optionsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints _constraints = new GridBagConstraints();
		
		String[] _choices = {};
		try {
			_choices = (new String(ConfigurationParametersManager.getInstance().getParameter(CONFIGURATION_PARAMETER_FUNCTIONS))).split(" ");
		} catch (MissingConfigurationParameterException e) {}
		
		_function = new JComboBox(_choices);
		_function.setPreferredSize(new Dimension(175, 22));
		_function.addActionListener(new FunctionActionListener());
		AcceptAction _acceptAction = new AcceptAction();
		_acceptAction.putValue(Action.NAME, I18N.getInstance().getString("label.accept"));
		JButton _acceptButton = new JButton();
		_acceptButton.setAction(_acceptAction);
		_acceptButton.setPreferredSize(new Dimension(100, 22));
		
		JPanel _valuesPanel = new JPanel(new GridBagLayout());
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 1;
		_constraints.anchor = GridBagConstraints.WEST;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.mean")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 2;
		_constraints.anchor = GridBagConstraints.WEST;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.std")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 3;
		_constraints.anchor = GridBagConstraints.WEST;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.factor")), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 4;
		_constraints.anchor = GridBagConstraints.WEST;
		_valuesPanel.add(new JLabel(I18N.getInstance().getString("config.nbars")), _constraints);
		_meanValue.setPreferredSize(new Dimension(50, 20));
		_meanValue.addChangeListener(new ValueChangeListener());
		_meanValue.setToolTipText(I18N.getInstance().getString("config.mean.description"));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 1, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 1;
		_valuesPanel.add(_meanValue, _constraints);
		_stdValue.setPreferredSize(new Dimension(50, 20));
		_stdValue.addChangeListener(new ValueChangeListener());
		_stdValue.setToolTipText(I18N.getInstance().getString("config.std.description"));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 1, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 2;
		_valuesPanel.add(_stdValue, _constraints);
		_factorValue.setPreferredSize(new Dimension(50, 20));
		_factorValue.addChangeListener(new ValueChangeListener());
		_factorValue.setToolTipText(I18N.getInstance().getString("config.factor.description"));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 1, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 3;
		_valuesPanel.add(_factorValue, _constraints);
		_barsValue.setPreferredSize(new Dimension(50, 20));
		_barsValue.addChangeListener(new ValueChangeListener());
		_barsValue.setToolTipText(I18N.getInstance().getString("config.nbars.description"));
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(1, 0, 1, 0);
		_constraints.gridx = 1;
		_constraints.gridy = 4;
		_valuesPanel.add(_barsValue, _constraints);
		
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 50, 0);
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_optionsPanel.add(_function, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 0, 10);
		_constraints.gridx = 0;
		_constraints.gridy = 1;
		_optionsPanel.add(_valuesPanel, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.insets = new Insets(0, 0, 50, 0);
		_constraints.gridx = 0;
		_constraints.gridy = 2;
		_optionsPanel.add(new JSeparator(), _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 0;
		_constraints.gridy = 3;
		_optionsPanel.add(_acceptButton, _constraints);
		
		// Populate main panel
		_config.add(_plotPanel);
		_config.add(_optionsPanel);
		
		setContentPane(_config);
		setResizable(false);
		setLocationRelativeTo(owner);
		pack();
	}
	
	/**
	 * Returns statistical distribution function to obtain modification
         * values when selected modification step is random.
         *
         * @return {@link ac.essex.statistics.functions.GraphableFunction Function}
         *         object to be used to calculate modification values.
	 */
	public GraphableFunction getFunction() {
		if (_graphPanel != null) {
			return _graphPanel.getFunction();
		}
		else {
			return null;
		}
	}
	
	// ----- ----- ----- ACTIONS AND LISTENERS ----- ----- -----
	
	private class AcceptAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			setVisible(false);
		}
	}
	
	private class ValueChangeListener implements ChangeListener {
                public void stateChanged(ChangeEvent e) {
			// this is hardcoded instead of using Class.forName().newInstance() for there is no unparametrised constructor
			String function = (String) _function.getSelectedItem();
			if (function.equals("DiscreteFactoredGaussian")) {
				GraphableFunction f = new DiscreteFactoredGaussian(((Double) _stdValue.getValue()).doubleValue(),
										   ((Double) _meanValue.getValue()).doubleValue(),
										   ((Double) _factorValue.getValue()).doubleValue(), -5, 5,
										   ((Integer) _barsValue.getValue()).intValue());
				_graphPanel.setFunction(f);
				_graphPanel.setNBars(((Integer) _barsValue.getValue()).intValue());
			}
			_graphPanel.repaint();
		}
	}
	
	private class FunctionActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GraphableFunction f = null;
			String function = (String) _function.getSelectedItem();
			// this is hardcoded instead of using Class.forName().newInstance() for there is no unparametrised constructor
			if (function.equals("DiscreteFactoredGaussian")) {
				_meanValue.setEnabled(true);
				_factorValue.setEnabled(true);
				_barsValue.setEnabled(true);
				f = new DiscreteFactoredGaussian(((Double) _stdValue.getValue()).doubleValue(),
								 ((Double) _meanValue.getValue()).doubleValue(),
								 ((Double) _factorValue.getValue()).doubleValue(),  -5, 5,
								 ((Integer) _barsValue.getValue()).intValue());
				_graphPanel.setFunction(f);
				_graphPanel.setNBars(((Integer) _barsValue.getValue()).intValue());
			}
			_graphPanel.repaint();
		}
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Name of configuration parameter (to be read from configuration file)
	 * which will provide available statistic distribution functions.
	 */
	private final static String CONFIGURATION_PARAMETER_FUNCTIONS = "functions";
	
	/**
	 * Function {@link javax.swing.JComboBox options} to be selected.
	 */
	private JComboBox _function = null;
	/**
	 * {@link javax.swing.JSpinner Counter} to select stantard deviation
	 * factor, to be used with FactoredGaussian function. It allows the user
	 * to apply a flatter gaussian curve.
	 */
	private	JSpinner _stdValue  = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 3.0, 0.1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select mean factor, to be
	 * used with FactoredGaussian function. It allows the user to shift
	 * gaussian curve either to the left or to the right.
	 */
	private	JSpinner _meanValue    = new JSpinner(new SpinnerNumberModel(0.0, -3.0, 3.0, 0.1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select function value factor,
	 * to be used with FactoredGaussian function. It allows the user to
	 * increase or decrease the absolute result (gaussian function value).
	 */
	private	JSpinner _factorValue  = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 5.0, 0.1));
	/**
	 * {@link javax.swing.JSpinner Counter} to select number of discrete
	 * segments to be considered when discretizating a continuous function.
	 */
	private	JSpinner _barsValue    = new JSpinner(new SpinnerNumberModel(5, 2, 10, 1));
	/**
	 * Function display panel. It shows both the continuous function (i.e.
	 * gaussian function) and its discrete equivalent using as many discrete
	 * intervals as selected in
	 * {@link jwmtool.ui.RandomConfigDialog#_barsValue barsValue}.
	 */
	private DoubleGraphPanel _graphPanel = null;

}
