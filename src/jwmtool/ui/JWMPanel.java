package jwmtool.ui;

import ac.essex.statistics.functions.GraphableFunction;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jwmtool.lib.JWMFrame;
import jwmtool.lib.Watermarking;

import jwmtool.util.I18N;
import jwmtool.util.exceptions.IllegalStateChangeException;
import jwmtool.util.exceptions.WatermarkingException;

/**
 * Specific panel component for JWMTool. 
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class JWMPanel extends JPanel {
	
	// ----- ----- ----- PUBLIC CLASS VARIABLES ----- ----- -----
	
	/**
	 * State of the panel is not ready to perform any operation (but,
	 * probably, configuration).
	 */
	public static final int ST_NOT_READY     = -1;
	/**
	 * State of the panel is ready to synchronously play original and
	 * watermarked videostreams.
	 */
	public static final int ST_READY         =  0;
	/**
	 * State of panel is currently synchronously playing original and
	 * watermarked videostreams.
	 */
	public static final int ST_PLAYING       =  1;
	/**
	 * State of panel is user-selected standby while synchronously playing
	 * original and watermarked videostreams.
	 */
	public static final int ST_PAUSED        =  2;
	/**
	 * State of panel is currently synchronously playing (at a reduced
	 * speed) original and watermarked videostreams.
	 */
	public static final int ST_SLOW_PLAYING  =  3;
	/**
	 * State of panel is currently synchronously playing (at a high
	 * speed) original and watermarked videostreams.
	 */
	public static final int ST_SPEED_PLAYING =  4;
	
	// ----- ----- ----- METHODS ----- ----- -----
	
	/**
	 * Creates and displays synchronized playback panel.
	 *
	 * @param filename Source file name.
	 * @param outputFilename Output file name.
	 * @param rangeInit First coefficient to be modified.
	 * @param rangeEnd Last coefficient to be modified.
	 * @param modificationType Type of modification (absolute, relative).
	 * @param modificationStep Type of modification step (uniform,
	 *                         incremental, random).
	 * @param lowLimit Lowest modification value to add to coefficients.
	 * @param upperLimit Highest modification value to add to coefficients.
	 * @param modificationValue Constante value to add to coefficients.
	 * @param modificationFunction Statistic distribution function to
	 *                             obtain random modification values to add
	 *                             to coefficients.
	 * @param modifyY Boolean value expressing whether or not luminance
	 *                component will be affected by watermarking.
	 * @param modifyU Boolean value expressing whether or not blue
	 *                chrominance component will be affected by
	 *                watermarking.
	 * @param modifyV Boolean value expressing whether or not red
	 *                chrominance component will be affected by
	 *                watermarking.
	 * @param blindProcess Boolean value representing whether or not the
	 *                     user wants the playback stage to be blind.
	 * @throws WatermarkingException
	 */
	public JWMPanel(String filename, String outputFilename,
			int rangeInit, int rangeEnd,
			int modificationType, int modificationStep,
			int lowLimit, int upperLimit,
			int modificationValue, GraphableFunction modificationFunction,
			boolean modifyY, boolean modifyU, boolean modifyV,
			boolean blindProcess) throws WatermarkingException {
		super();
		_blindProcess = blindProcess;
		
		_wmtool = new Watermarking(filename, outputFilename);
		
		// INVOKE WATERMARK PROCESS
		_wmtool.watermark(rangeInit, rangeEnd, modificationType, modificationStep,
				  lowLimit, upperLimit, modificationValue, modificationFunction,
				  modifyY, modifyU, modifyV);
		JWMFrame _frame = _wmtool.getFirstImage();
		
		// UI creation
		Random _randomGenerator = new Random(System.currentTimeMillis());
		_order = _randomGenerator.nextBoolean();
		if (!blindProcess) { // if the procces is not blind, original video is on the left and watermarked video on the right, labelled
			_leftVideo = new JLabel(new ImageIcon(_frame.getSourceFrame()));
			_rightVideo = new JLabel(new ImageIcon(_frame.getWMarkedFrame()));
			_leftVideo.setBorder(new TitledBorder(new LineBorder(Color.BLUE, 1, true), I18N.getInstance().getString("label.video.original")));
			_rightVideo.setBorder(new TitledBorder(new LineBorder(Color.RED, 1, true), I18N.getInstance().getString("label.video.watermarked"), TitledBorder.RIGHT, TitledBorder.TOP));
		}
		else { // if the procces has to be blind, randomly place videos and do not label them
			if (_order) {
				_leftVideo = new JLabel(new ImageIcon(_frame.getSourceFrame()));
				_rightVideo = new JLabel(new ImageIcon(_frame.getWMarkedFrame()));
			}
			else {
				_leftVideo = new JLabel(new ImageIcon(_frame.getWMarkedFrame()));
				_rightVideo = new JLabel(new ImageIcon(_frame.getSourceFrame()));
			}
			_leftVideo.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1, true), I18N.getInstance().getString("label.video.oneblind")));
			_rightVideo.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1, true), I18N.getInstance().getString("label.video.anotherblind"), TitledBorder.RIGHT, TitledBorder.TOP));
		}
		
		JPanel _display = new JPanel(new FlowLayout());
		_display.add(_leftVideo);
		_display.add(_rightVideo);
		setLayout(new BorderLayout());
		add(_display, BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);
		
		// Set initial state
		try {
			setPlayerState(ST_READY);
		} catch (IllegalStateChangeException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns current panel state code.
	 *
	 * @return Current panel state.
	 */
	public int getPlayerState() {
		return _state;
	}
	
	/**
	 * Changes panel state. Not every state change is allowed; in case a
	 * forbidden transition is invoked, the method will throw an exception.
	 *
	 * @param newState State to change panel to.
	 * @throws IllegalStateChangeException
	 */
	private void setPlayerState(int newState) 
		throws IllegalStateChangeException {
		
		switch (_state) {
			case ST_NOT_READY:     // from NOT_READY state we can only stay or move to READY state
				switch (newState) {
					case ST_NOT_READY:
					case ST_READY: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			case ST_READY:         // from READY state we can stay or move to NOT_READY or PLAYING states
				switch (newState) {
					case ST_NOT_READY:
					case ST_READY:
					case ST_PLAYING: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			case ST_PLAYING:       // from PLAYING state we can stay or move to PAUSED, READY, SLOW_PLAYING or SPEED_PLAYING
				switch (newState) {
					case ST_PLAYING:
					case ST_PAUSED:
					case ST_READY:
					case ST_SLOW_PLAYING:
					case ST_SPEED_PLAYING: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			case ST_PAUSED:        // from PAUSED state we can stay or move to PLAYING or READY states
				switch (newState) {
					case ST_PAUSED:
					case ST_PLAYING:
					case ST_READY: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			case ST_SLOW_PLAYING:  // from SLOW_PLAYING state we can stay or move to PAUSED, PLAYING or READY states
				switch (newState) {
					case ST_SLOW_PLAYING:
					case ST_PAUSED:
					case ST_PLAYING:
					case ST_READY: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			case ST_SPEED_PLAYING: // from SPEED_PLAYING state we can stay or move to PAUSED, PLAYING or READY states
				switch (newState) {
					case ST_SPEED_PLAYING:
					case ST_PAUSED:
					case ST_PLAYING:
					case ST_READY: _state = newState; break;
					default: throw new IllegalStateChangeException(_state, newState);
				}; break;
			default: throw new IllegalStateChangeException(_state, newState);
		}
	}
	
	/**
	 * Create and arrange panel components.
	 *
	 * @return {@link javax.swing.JPanel Panel} and components properly
	 *         arranged.
	 */
	private JPanel createControlPanel() {
		JPanel _controlPanel = new JPanel(new GridBagLayout());
		GridBagConstraints _constraints;
		
		// Slow/Back button
		SlowAction _slowAction = new SlowAction();
		_slowAction.putValue(Action.NAME, I18N.getInstance().getString("label.slow"));
		_slowAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/slow.png")));
		_slowAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("label.slow.description"));
		_slowButton.setAction(_slowAction);
		_slowButton.setPreferredSize(new Dimension(125, 22));
		_slowButton.setEnabled(false);
		
		// Pause button
		PauseAction _pauseAction = new PauseAction();
		_pauseAction.putValue(Action.NAME, I18N.getInstance().getString("label.pause"));
		_pauseAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/pause.png")));
		_pauseAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("label.pause.description"));
		JButton _pauseButton = new JButton();
		_pauseButton.setAction(_pauseAction);
		_pauseButton.setPreferredSize(new Dimension(125, 22));
		
		// Play button
		PlayAction _playAction = new PlayAction();
		_playAction.putValue(Action.NAME, I18N.getInstance().getString("label.play"));
		_playAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/play.png")));
		_playAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("label.play.description"));
		JButton _playButton = new JButton();
		_playButton.setAction(_playAction);
		_playButton.setPreferredSize(new Dimension(125, 22));
		
		// Stop button
		StopAction _stopAction = new StopAction();
		_stopAction.putValue(Action.NAME, I18N.getInstance().getString("label.stop"));
		_stopAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/stop.png")));
		_stopAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("label.stop.description"));
		JButton _stopButton = new JButton();
		_stopButton.setAction(_stopAction);
		_stopButton.setPreferredSize(new Dimension(125, 22));
		
		// Forward/Speed button
		SpeedAction _speedAction = new SpeedAction();
		_speedAction.putValue(Action.NAME, I18N.getInstance().getString("label.speed"));
		_speedAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/jwmtool/images/16x16/speed.png")));
		_speedAction.putValue(Action.SHORT_DESCRIPTION, I18N.getInstance().getString("label.speed.description"));
		_speedButton.setAction(_speedAction);
		_speedButton.setPreferredSize(new Dimension(125, 22));
		_speedButton.setEnabled(false);
		
		// Set layout
		_constraints = new GridBagConstraints();
		_constraints.gridx = 0;
		_constraints.gridy = 0;
		_constraints.insets = new Insets(10, 5, 10, 5);
		_controlPanel.add(_slowButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 1;
		_constraints.gridy = 0;
		_constraints.insets = new Insets(10, 5, 10, 5);
		_controlPanel.add(_pauseButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 2;
		_constraints.gridy = 0;
		_constraints.insets = new Insets(10, 5, 10, 5);
		_controlPanel.add(_playButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 3;
		_constraints.gridy = 0;
		_constraints.insets = new Insets(10, 5, 10, 5);
		_controlPanel.add(_stopButton, _constraints);
		_constraints = new GridBagConstraints();
		_constraints.gridx = 4;
		_constraints.gridy = 0;
		_constraints.insets = new Insets(10, 5, 10, 5);
		_controlPanel.add(_speedButton, _constraints);
		
		return _controlPanel;
	}
	
	// ----- ----- ----- STATE-DEPENDENT METHODS ----- ----- -----
	// This class needs to be redesigned and reimplemented to follow State design pattern
	
	/**
	 * Speeds up playback.
	 */
	private void speedUp() {}
	
	/**
	 * Stops playback.
	 */
	private void stop() {
		_worker.interrupt();
		_worker = null;
		_wmtool.rewind();
	}
	
	/**
	 * Pauses playback.
	 */
	private void setSuspended(boolean suspend) {
		if (suspend) {
			_worker.interrupt();
		}
		else {
			_worker = new JWMWorker();
			_worker.start();
		}
	}
	
	/**
	 * Starts playback from the beginning, or resumes paused playback.
	 */
	private void start() {
		if ((_worker == null) || (!_worker.isAlive())) {
			_worker = new JWMWorker();
		}
		_worker.start();
	}
	
	/**
	 * Proceed and show next frame from both original and watermarked
	 * videostreams.
	 */
	private void readData() {
		JWMFrame _frame = _wmtool.getNextImage();
		if ((!_blindProcess) || (_order)) {
			_leftVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
			_rightVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
		}
		else {
			_leftVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
			_rightVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
		}
	}
	
	/**
	 * Show previous frame from both original and watermarked videostreams.
	 */
	private void readPreviousData() {
		JWMFrame _frame = _wmtool.getPreviousImage();
		if ((!_blindProcess) || (_order)) {
			_leftVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
			_rightVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
		}
		else {
			_leftVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
			_rightVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
		}
	}
	
	/**
	 * Slows playback down.
	 */
	private void slowDown() {}
	
	// ----- ----- ----- ACTIONS ----- ----- -----
	
	private class SlowAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				switch (getPlayerState()) {
					case ST_PLAYING:       // if PLAYING, slow down playback
						setPlayerState(ST_SLOW_PLAYING);
						slowDown();
						break;
					case ST_PAUSED:        // if PAUSED, show previous frame
						readPreviousData();
						break;
					case ST_SPEED_PLAYING: // if SPEED_PLAYING, slow down to a normal playback rate
						setPlayerState(ST_PLAYING);
						slowDown();
						break;
					default:               // or else, do nothing
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class PlayAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				switch (getPlayerState()) {
					case ST_READY:         // if READY, start playback
						setPlayerState(ST_PLAYING);
						start();
						break;
					case ST_PAUSED:        // if PAUSED, resume playback
						setPlayerState(ST_PLAYING);
						_slowButton.setEnabled(false);
						_speedButton.setEnabled(false);
						setSuspended(false);
						break;
					case ST_SPEED_PLAYING: // if SPEED_PLAYING, slow down to a normal playback rate
						setPlayerState(ST_PLAYING);
						slowDown();
						break;
					case ST_SLOW_PLAYING:  // if SLOW_PLAYING, speed up to a normal playback rate
						setPlayerState(ST_PLAYING);
						speedUp();
						break;
					default:              // or else, do nothing
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class PauseAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				switch (getPlayerState()) {
					case ST_PLAYING: 
					case ST_SLOW_PLAYING:
					case ST_SPEED_PLAYING: // if (SLOW/SPEED)PLAYING, pause playback
						setPlayerState(ST_PAUSED);
						_slowButton.setEnabled(true);
						_speedButton.setEnabled(true);
						setSuspended(true);
						break;
					case ST_PAUSED:        // if PAUSED, resume playback
						setPlayerState(ST_PLAYING);
						_slowButton.setEnabled(false);
						_speedButton.setEnabled(false);
						setSuspended(false);
						break;
					default:               // or else, do nothing
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class StopAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				switch (getPlayerState()) {
					case ST_PLAYING:
					case ST_SLOW_PLAYING:
					case ST_SPEED_PLAYING:
					case ST_PAUSED:         // if (SLOW/SPEED)PLAYING or PAUSED, stop playback
						setPlayerState(ST_READY);
						stop();
						break;
					default:                // or else, do nothing
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class SpeedAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			try {
				switch (getPlayerState()) {
					case ST_READY:        // if READY, show next frame
						setPlayerState(ST_PAUSED);
						readData();
						break;
					case ST_PLAYING:      // if PLAYING, speed up playback
						setPlayerState(ST_SPEED_PLAYING);
						speedUp();
						break;
					case ST_PAUSED:       // if PAUSED, show next frame
						readData();
						break;
					case ST_SLOW_PLAYING: // if SLOW PLAYING, speed up to a normal playback rate
						setPlayerState(ST_PLAYING);
						speedUp();
						break;
					default:              // or else, do nothing
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ----- ----- ----- WORKING THREAD ----- ----- -----
	
	private class JWMWorker extends Thread {
		public void run() {
			try {   // Playback process means showing one frame
				// after the other until EOF for both original
				// and generated watermarked videostreams
				JWMFrame _frame = _wmtool.getNextImage();
				while ( ( getPlayerState() == ST_PLAYING     ) &&
					( (_frame.getSourceFrame())  != null ) &&
					( (_frame.getWMarkedFrame()) != null ) &&
			        	( !isInterrupted()                   ) ) {
						if ((!_blindProcess) || (_order)) {
							_leftVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
							_rightVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
						}
						else {
							_leftVideo.setIcon(new ImageIcon(_frame.getWMarkedFrame()));
							_rightVideo.setIcon(new ImageIcon(_frame.getSourceFrame()));
						}
					_frame = _wmtool.getNextImage();
				}
				if ((_frame.getSourceFrame() == null) || (_frame.getWMarkedFrame() == null)) {
					_wmtool.rewind();
					setPlayerState(ST_READY);
				}
			} catch (IllegalStateChangeException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Boolean value representing whether or not the user wants the playback
	 * stage to be blind.
	 */
	private boolean _blindProcess = false;
	/**
	 * Current state of Panel. Initial state is NOT_READY (i.e.
	 * configuration is needed before watermarking process can be invoked).
	 */
	private int _state = ST_NOT_READY;
	
	/**
	 * Title of the videostream to be placed on the left side of the
	 * playback panel. It will read something like "Original video" if the
	 * playback process is selected to be non-blind, or else just "Video".
	 */
	private JLabel _leftVideo  = null;
	/**
	 * Title of the videostream to be placed on the right side of the
	 * playback panel. It will read something like "Watermarked video" if
	 * the playback process is selected to be non-blind, or else just
	 * "Video".
	 */
	private JLabel _rightVideo = null;
	/**
	 * Reference to the object in charge of performing the watermarking
	 * process and generating the output watermarked videostream file.
	 */
	private Watermarking _wmtool = null;
	/**
	 * Auxiliary thread to run playback operations without blocking the
	 * rest of the UI.
	 */
	private	JWMWorker _worker    = null;
	/**
	 * Auxiliary flag to help relocate frames when the playback is blind.
	 */
	private boolean _order = true;
	
	/**
	 * Slow/Back button.
	 */
	private JButton _slowButton = new JButton();
	/**
	 * Speed/Forward button.
	 */
	private JButton _speedButton = new JButton();

}
