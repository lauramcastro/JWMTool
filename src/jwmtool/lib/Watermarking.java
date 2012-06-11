package jwmtool.lib;

import ac.essex.statistics.functions.GraphableFunction;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.Random;

import jwmtool.util.FloatDCT;
import jwmtool.util.exceptions.WatermarkingException;
import jwmtool.util.functions.*;

/**
 * Watermarking class is a stateful element which encapsulates access and
 * manipulation of two videostream data, YUV format, and allows a
 * synchronized playback, performed step by step using
 * {@link jwmtool.lib.JWMFrame JWMFrames}. <br/>
 *
 * In JWMTool, this is used as a tool both to generate a watermarked version
 * of a given source videostream, and then to syncronize the playback of this
 * source videostream and its generated watermarked version.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class Watermarking {
	
	// ----- ----- ----- PUBLIC CLASS VARIABLES ----- ----- -----
	
	/**
	 * Unrecognized type of modification. This value is used in this class
	 * just as initialization value, but is provided also to be useful as an
	 * error -i.e. misconfiguration - value.
	 */
	public static final int MODIFICATION_TYPE_UNKNOWN    = -1;
	/**
	 * Absolute-value type of modification (watermarking) to be performed.
	 */
	public static final int MODIFICATION_TYPE_ABSOLUTE   =  1;
	/**
	 * Percentage (relative) type of modification (watermarking) to be
	 * performed.
	 */
	public static final int MODIFICATION_TYPE_PERCENTAGE =  2;
	
	/**
	 * Unrecognized kind of modification step. This value is used in this
	 * class just as initialization value, but is provided also to be
	 * useful as an error -i.e. misconfiguration - value) to be applied.
	 */
	public static final int MODIFICATION_STEP_UNKNOWN     = -1;
	/**
	 * Incremental-value type of modification (watermarking).
	 */
	public static final int MODIFICATION_STEP_INCREMENTAL =  1;
	/**
	 * Uniform-value modification (watermarking).
	 */
	public static final int MODIFICATION_STEP_UNIFORM     =  2;
	/**
	 * Random-modification type of alteration (watermarking).
	 */
	public static final int MODIFICATION_STEP_RANDOM      =  3;
	
	/**
	 * Unrecognized YUV video stream format.
	 */
	public static final int YUV_FORMAT_UNKNOWN = -1;
	/**
	 * YUV 4:4:4 video stream format.
	 */
	public static final int YUV_FORMAT_444 = 0;
	/**
	 * YUV 4:2:2 video stream format.
	 */
	public static final int YUV_FORMAT_422 = 1;
	/**
	 * YUV 4:2:0 video stream format.
	 */
	public static final int YUV_FORMAT_420 = 2;
	
	/**
	 * Matrix dimension to consider during frame processing. A value of N
	 * means that each frame will be processed taking fragments of
	 * NxN at a time. Thus, {@link jwmtool.lib.Watermarking#LIMIT LIMIT}
	 * must be a factor of the image size (both height and width).
	 */
	public static final int LIMIT = 8;
	
	// ----- ----- ----- METHODS -----  ----- -----
	
	/**
	 * Creates a Watermarking object, providing the filename of a source
	 * YUV format videostream, and the desired name of the watermarked
	 * YUV format videostream to be generated.
	 *
	 * @param filename The name of the input videostream file.
	 * @param outputFilename The name of the output (watermarked)
	 *                       videostream file to be produced.
	 */
	public Watermarking(String filename, String outputFilename) {
		_filename       = filename;
		_outputFilename = outputFilename;
		_quantizationMatrix = new float[LIMIT][LIMIT];
		for (int i = 0; i < LIMIT; i++)
			for (int j = 0; j < LIMIT; j++)
				_quantizationMatrix[i][j] = 1f;
		FloatDCT.scaleQuantizationTable(_quantizationMatrix);
	}
	
	/**
	* Creates a {@link jwmtool.lib.JWMFrame JWMFrame} that will contain the
	* first frame of the source videostream and the first frame of the
	* watermarked generated videostream.
	*
	* @return A {@link jwmtool.lib.JWMFrame JWMFrame} with the first frame
	*         of the original videostream and its watermarked counterpart.
	*/
	public JWMFrame getFirstImage() {
		try {
			_scFile = new RandomAccessFile(_filename, "r");
			_wmFile = new RandomAccessFile(_outputFilename, "r");
			
			_scFile.readLine(); // discard stream header
			_wmFile.readLine(); // discard stream header
			
			return getNextImage();
			
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Creates a {@link jwmtool.lib.JWMFrame JWMFrame} that will contain
	 * the next frame of the source videostream and the corresponding frame
	 * of the watermarked generated videostream.
	 *
	 * @return A {@link jwmtool.lib.JWMFrame JWMFrame} with the next frame
	 *         of the original videostream and its watermarked counterpart.
	 */
	public JWMFrame getNextImage() {
		try {
			if ((_scFile == null) && (_wmFile == null)) { // we didn't even open the files yet
				return getFirstImage();
			}
			else {
				_scFile.readLine(); // discard frame header
				_wmFile.readLine(); // discard frame header
				
				_yRead = _scFile.read(_yBuffer); // read as much data as Y buffer size
				_uRead = _scFile.read(_uBuffer); // read as much data as U buffer size
				_vRead = _scFile.read(_vBuffer); // read as much data as V buffer size
				
				// check we read enough data in each buffer
				if ((_yRead == -1) || (_uRead == -1) || (_vRead == -1)) {
					_scFrame = null;
				}
				else {
					// if we did read enough data, make frame out of it
					_scFrame = getRGBImage(_yBuffer, _uBuffer, _vBuffer);
				}
				
				// repeat process for watermarked counterpart
				_yRead = _wmFile.read(_yBuffer);
				_uRead = _wmFile.read(_uBuffer);
				_vRead = _wmFile.read(_vBuffer);
				if ((_yRead == -1) || (_uRead == -1) || (_vRead == -1)) {
					_wmFrame = null;
				}
				else {
					_wmFrame = getRGBImage(_yBuffer, _uBuffer, _vBuffer);
				}
				
				// build and return JWMFrame
				return new JWMFrame(_scFrame, _wmFrame);
			}
		} catch (IOException e) {
			return null;
		} catch (WatermarkingException e) {
			return null;
		}
	}
	
	/**
	 * Creates a {@link jwmtool.lib.JWMFrame JWMFrame} that will contain
	 * the previous frame of the source videostream and the corresponding
	 * frame of the watermarked generated videostream.
	 *
	 * @return A {@link jwmtool.lib.JWMFrame JWMFrame} with the previous
	 *         frame of the original videostream and its watermarked
	 *         counterpart.
	 */
	public JWMFrame getPreviousImage() {
		rewind(2);
		return getNextImage();
	}
	
	/**
	 * Sets current position in both original and watermarked videostreams
	 * back to initial position (beginning of files).
	 */
	public void rewind() {
		try {
			if (!((_scFile == null) && (_wmFile == null))) { // make sure we have opened the files
				_scFile.seek(0);
				_scFile.readLine(); // discard stream header
				_wmFile.seek(0);
				_wmFile.readLine(); // discard stream header
			}
		} catch (IOException e) {}
	}
	
	/**
	 * Sets current position in both original and watermarked videostreams
	 * <code>nframes</code> back.
	 *
	 * @param nframes Number of frames to go back in streams.
	 */
	public void rewind(int nframes) {
		try {
			if (!((_scFile == null) && (_wmFile == null))) { // make sure we have opened the files
				long actualPosition = _scFile.getFilePointer(); // same for both, we don't need to check on _wmFile
				_scFile.seek(actualPosition - nframes * _frameLength);
				_wmFile.seek(actualPosition - nframes * _frameLength);
			}
		} catch (IOException e) {}
	}
	
	/**
	 * Produces watermarked videostream file applying provided watermarking
	 * settings. Generated videostream is saved to disk as 
	 * {@link jwmtool.lib.Watermarking#_outputFilename outputFilename}.
	 *
	 * @param rangeInit First coefficient to watermark in each
	 *                  {@link jwmtool.lib.Watermarking#LIMIT LIMIT} x
	 *                  {@link jwmtool.lib.Watermarking#LIMIT LIMIT} frame
	 *                  fragment.
	 * @param rangeEnd Last coefficient to watermark in each
	 *                 {@link jwmtool.lib.Watermarking#LIMIT LIMIT} x
	 *                 {@link jwmtool.lib.Watermarking#LIMIT LIMIT} frame
	 *                 fragment.
	 * @param modificationType Type of modification
	 *                         ({@link jwmtool.lib.Watermarking#MODIFICATION_TYPE_ABSOLUTE MODIFICATION_TYPE_ABSOLUTE},
	 *                         {@link jwmtool.lib.Watermarking#MODIFICATION_TYPE_PERCENTAGE MODIFICATION_TYPE_PERCENTAGE})
	 *                         to be performed.
	 * @param modificationStep Type of modification
	 *                         ({@link jwmtool.lib.Watermarking#MODIFICATION_STEP_INCREMENTAL MODIFICATION_STEP_INCREMENTAL},
	 *                         {@link jwmtool.lib.Watermarking#MODIFICATION_STEP_RANDOM MODIFICATION_STEP_RANDOM}, or
	 *                         {@link jwmtool.lib.Watermarking#MODIFICATION_STEP_UNIFORM MODIFICATION_STEP_UNIFORM})
	 *                         to be performed each step.
	 * @param lowLimit Lower value for an incremental modification.
	 * @param upperLimit Upper value for an incremental modification.
	 * @param modificationValue Value of modification to be performed when
	 *                          selected
	 *                          {@link jwmtool.lib.Watermarking#_modificationStep modificationStep}
	 *                          is uniform.
	 * @param modificationFunction Function to obtain values when selected
	 *                          {@link jwmtool.lib.Watermarking#_modificationStep modificationStep}
	 *                          is random.
	 * @param modifyY Whether Y component (luminance) is to be modified or
	 *                not.
	 * @param modifyU Whether U component (blue chrominance) is to be
	 *                modified or not.
	 * @param modifyV Whether V component (red chrominance) is to be
	 *                modified or not.
	 */
	public void watermark(int rangeInit, int rangeEnd,   int modificationType, int modificationStep,
			      int lowLimit,  int upperLimit, int modificationValue, GraphableFunction modificationFunction,
			      boolean modifyY, boolean modifyU, boolean modifyV) throws WatermarkingException {
		try {
			RandomAccessFile  _input = new RandomAccessFile(_filename, "r");
			RandomAccessFile _output = new RandomAccessFile(_outputFilename, "rw");
			
			_rangeInit = rangeInit;
			_rangeEnd  = rangeEnd;
			_modificationType = modificationType;
			_modificationStep = modificationStep;
			_lowLimit   = lowLimit;
			_upperLimit = upperLimit;
			_modificationValue    = modificationValue;
			_modificationFunction = modificationFunction;
			
			long _fileLength = _input.length();
			String _header = _input.readLine(); // read stream header
			_output.writeBytes(_header + "\n"); // write same stream header to output file
			
			String[] info = _header.split(" ");
			_width  = (new Integer(info[1].substring(1))).intValue(); // obtain width  dimension from file header
			_height = (new Integer(info[2].substring(1))).intValue(); // obtain height dimension from file header
			
			_ySize   = _height * _width;
			_yBuffer = new byte[_ySize];
			_yData   = new byte[_height][_width];
			
			// Guess if which YUV format we are dealing with
			// First, we have to substract header size (_header.length() + 1)
			// Then, we divide _fileLength by estimated frame size + (frame header size) to find a 0 remainder
			
			_fileLength -= _header.length() + 1;
			_header = _input.readLine(); // read first frame header (all frame headers are the same)
			
			if   (( _fileLength % (_width * _height + (_width * _height / 2) + _header.length() + 1) ) == 0) {    // YUV 4:2:2
				_frameLength = _width * _height + (_width * _height / 2) + _header.length() + 1;
				_uvSize  = _ySize / 4;
				_uData   = new byte[_height/2][_width/2];
				_vData   = new byte[_height/2][_width/2];
				_YUVFormat = YUV_FORMAT_422;
			}
			else if (( _fileLength % (_width * _height + (_width * _height / 8) + _header.length() + 1) ) == 0) { // YUV 4:2:0
				_frameLength    = _width * _height + (_width * _height / 8) + _header.length() + 1;
				_uvSize  = _ySize / 16;
				_uData   = new byte[_height/4][_width/4];
				_vData   = new byte[_height/4][_width/4];
				_YUVFormat = YUV_FORMAT_420;
			}
			else if (( _fileLength % (_width * _height * 3 + _header.length() + 1) ) == 0) {                      // YUV 4:4:4
				_frameLength    = _width * _height * 3 + _header.length() + 1;
				_uvSize  = _ySize;
				_uData   = new byte[_height][_width];
				_vData   = new byte[_height][_width];
				_YUVFormat = YUV_FORMAT_444;
			}
			else {
				throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
			}
			
			_uBuffer = new byte[_uvSize];
			_vBuffer = new byte[_uvSize];
			byte[] _data = new byte[_ySize + 2 * _uvSize];
			
			while (_header != null) { // check if we have reached EOF
				_output.writeBytes(_header + "\n");     // read stream header
				_input.read(_data);   // read frame data
				_data = watermarkFrame(_data, modifyY, modifyU, modifyV); // watermark frame
				_output.write(_data); // write watermarked frame data to output file
				_header = _input.readLine(); // read next frame header
			}
			_input.close();
			_output.close();
		} catch (IOException e) { }
	}
	
	// ----- ----- ----- UTILITY STUFF ----- ----- -----
	
	/**
	 * Obtains an {@link java.awt.Image image} frame (RGB format) out of the
	 * data in three buffers representing Y, U and V components.
	 *
	 * @param yData Luminance component data to be merged into an RGB image.
	 * @param uData Blue chrominance component data to be merged into an RGB
	 *              image.
	 * @param vData Red chrominance component data to be merged into an RGB
	 *              image.
	 * @return An RGB {@link java.awt.Image image} frame.
	 */
	private Image getRGBImage(byte[] yData, byte[] uData, byte[] vData)
		throws WatermarkingException {
		int[] rgbData = new int[_width * _height]; // frame dimensions are those of original video
		int pos, spos, cy, cb, cr, r, g, b;
		for (int i = 0; i < _height; i++) {
			for (int j = 0; j < _width; j++) {
				pos = _width * i + j;
				
				switch (_YUVFormat) {
					case YUV_FORMAT_422: spos = (_width/2) * (i/2) + (j/2); break; // YUV 4:2:2
				        case YUV_FORMAT_420: spos = (_width/4) * (i/4) + (j/4); break; // YUV 4:2:0
				        case YUV_FORMAT_444: spos = pos;                        break; // YUV 4:4:4
					default: throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
			        }
				
				// transform YUV frame (YCbCr, actually) to RGB
				cy = (yData[pos]  & 255) -  16;
				cb = (uData[spos] & 255) - 128;
				cr = (vData[spos] & 255) - 128;
				
				r = clip(0, 255, (298 * cy            + 409 * cr + 128) >> 8);
				g = clip(0, 255, (298 * cy - 100 * cb - 208 * cr + 128) >> 8);
				b = clip(0, 255, (298 * cy + 516 * cb            + 128) >> 8);
				
				rgbData[pos] = ( r << 16) | ( g << 8) | b;
			}
		}
		
		BufferedImage _rgbImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
		_rgbImage.setRGB(0, 0, _width, _height, rgbData, 0, _width);
		return _rgbImage;
	}
	
	/**
	 * Normalizes <code>value</code> to be between <code>infLimit</code> and
	 * <code>supLimit</code>.
	 *
	 * @param infLimit Lower value allowed for <code>value</code>.
	 * @param supLimit Upper value allowed for <code>value</code>.
	 * @param value Value to be normalized.
	 * @return Normalized <code>value</code>: it will retain its original
	 *         value if it was already greater or equal to
	 *         <code>infLimit</code> and less or equal to
	 *         <code>supLimit</code>; it will be equal to
	 *         <code>infLimit</code> if it was lower, and it will be equal
	 *         to <code>supLimit</code> if it was greater.
	 */
	private int clip(int infLimit, int supLimit, int value) {
		return Math.min(supLimit, Math.max(infLimit, value));
	}
	
	// ----- ----- ----- WATERMARKING STUFF ----- ----- -----
	
	/**
	 * Generate watermarked frame from given source frame data.
	 *
	 * @param frameData Source data to be watermarked.
	 * @param modifyY Whether or not the luminance component has to be
	 *                altered.
	 * @param modifyU Whether or not the blue chrominance component has to
	 *                be altered.
	 * @param modifyV Whether or not the red chrominance component has to be
	 *                altered.
	 * @return Byte array containing properly watermarked data.
	 */
	private byte[] watermarkFrame(byte[] frameData, boolean modifyY, boolean modifyU, boolean modifyV)
		throws WatermarkingException {
		// extract y component from frameData array into yData matrix
		for (int i = 0; i < _height; i++)
			for (int j = 0; j < _width; j++)
				_yData[i][j] = frameData[_width*i+j];
		// extract u & v components from frameData array into uData/vData matrix
		switch (_YUVFormat) {
		    case YUV_FORMAT_422:
			for (int i = 0; i < _height/2; i++)
				for (int j = 0; j < _width/2; j++)
					_uData[i][j] = frameData[_ySize+(_width/2)*i+j];
			for (int i = 0; i < _height/2; i++)
				for (int j = 0; j < _width/2; j++)
					_vData[i][j] = frameData[_ySize+_uvSize+(_width/2)*i+j];
			break;
		    case YUV_FORMAT_420:
			for (int i = 0; i < _height/4; i++)
				for (int j = 0; j < _width/4; j++)
					_uData[i][j] = frameData[_ySize+(_width/4)*i+j];
			for (int i = 0; i < _height/4; i++)
				for (int j = 0; j < _width/4; j++)
					_vData[i][j] = frameData[_ySize+_uvSize+(_width/4)*i+j];
			break;
		    case YUV_FORMAT_444:
			for (int i = 0; i < _height; i++)
				for (int j = 0; j < _width; j++)
					_uData[i][j] = frameData[_ySize+_width*i+j];
			for (int i = 0; i < _height; i++)
				for (int j = 0; j < _width; j++)
					_vData[i][j] = frameData[_ySize+_uvSize+_width*i+j];
			break;
		    default:
		        throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
		}
		
		if (modifyY) { // if luminance is to be modified, proceed
			_yData = modifyFrame( _width/LIMIT,  _height/LIMIT, _yData);
		}
		if (modifyU) { // if blue chrominance is to be modified, proceed
			switch (_YUVFormat) {
			    case YUV_FORMAT_422:
				_uData = modifyFrame(_width/(2*LIMIT), _height/(2*LIMIT), _uData); break;
			    case YUV_FORMAT_420:
				_uData = modifyFrame(_width/(4*LIMIT), _height/(4*LIMIT), _uData); break;
			    case YUV_FORMAT_444:
				_uData = modifyFrame(_width/LIMIT, _height/LIMIT, _uData);         break;
			    default:
			        throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
			}
		}
		if (modifyV) { // if red chrominance is to be modified, proceed
			switch (_YUVFormat) {
			    case YUV_FORMAT_422:
				_vData = modifyFrame(_width/(2*LIMIT), _height/(2*LIMIT), _vData); break;
			    case YUV_FORMAT_420:
				_vData = modifyFrame(_width/(4*LIMIT), _height/(4*LIMIT), _vData); break;
			    case YUV_FORMAT_444:
				_vData = modifyFrame(_width/LIMIT, _height/LIMIT, _vData);         break;
			    default:
			    	throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
			}
		}
		
		// merge modified y component into new watermarked data array
		for (int i = 0; i < _height; i++)
			for (int j = 0; j < _width; j++)
				frameData[_width*i+j] = _yData[i][j];
		// merge modified u & v components into watermarked data array
		switch (_YUVFormat) {
		    case YUV_FORMAT_422:
			for (int i = 0; i < _height/2; i++)
				for (int j = 0; j < _width/2; j++)
					frameData[_ySize+(_width/2)*i+j] = _uData[i][j];
			for (int i = 0; i < _height/2; i++)
				for (int j = 0; j < _width/2; j++)
					frameData[_ySize+_uvSize+(_width/2)*i+j] = _vData[i][j];
			break;
		    case YUV_FORMAT_420:
			for (int i = 0; i < _height/4; i++)
				for (int j = 0; j < _width/4; j++)
					frameData[_ySize+(_width/4)*i+j] = _uData[i][j];
			for (int i = 0; i < _height/4; i++)
				for (int j = 0; j < _width/4; j++)
					frameData[_ySize+_uvSize+(_width/4)*i+j] = _vData[i][j];
			break;
		    case YUV_FORMAT_444:
			for (int i = 0; i < _height; i++)
				for (int j = 0; j < _width; j++)
					frameData[_ySize+_width*i+j] = _uData[i][j];
			for (int i = 0; i < _height; i++)
				for (int j = 0; j < _width; j++)
					frameData[_ySize+_uvSize+_width*i+j] = _vData[i][j];
			break;
		    default:
		        throw new WatermarkingException("exceptions.watermarking.unknown_yuv_format");
		}
		
		return frameData;
	}
	
	/**
	 * Watermark frame, in as many steps as <code>wblocks</code> x
	 * <code>hblocks</code>.
	 *
	 * @param wblocks Result of splitting frame
	 *                {@link jwmtool.lib.Watermarking#_width width} /
	 *                {@link jwmtool.lib.Watermarking#LIMIT LIMIT}, which is
	 *                the number of blocks of
	 *                {@link jwmtool.lib.Watermarking#LIMIT LIMIT} width in
	 *                the frame.
	 * @param hblocks Result of splitting frame
	 *                {@link jwmtool.lib.Watermarking#_height height} /
	 *                {@link jwmtool.lib.Watermarking#LIMIT LIMIT}, which is
	 *                the number of blocks of
	 *                {@link jwmtool.lib.Watermarking#LIMIT LIMIT} height in
	 *                the frame.
	 * @param frameData Original frame data matrix.
	 * @return Watermarked frame data matrix.
	 */
	private byte[][] modifyFrame(int wblocks, int hblocks, byte[][] frameData) throws WatermarkingException {
 		float[][] itable = new float[LIMIT][LIMIT];
		float[][] otable = new float[LIMIT][LIMIT];
		int i = 0, j = 0, x = 0, n = 1, inc = 0;
		
		for (int hb = 0 ; hb < hblocks ; hb++)
			for (int wb = 0 ; wb < wblocks ; wb++) {
				for (i = 0 ; i < LIMIT ; i++)  // read data in 8x8 steps
					for (j = 0; j < LIMIT ; j++)
						itable[i][j] = (new Byte(frameData[hb*LIMIT+i][wb*LIMIT+j])).floatValue();
				
				i = 0; j = 0; n = 1;
				FloatDCT.FDCT(itable, otable); // perform DCT
				inc = _lowLimit;
				
				// WATERMARK INSERTION: modify values in 8x8 array (zigzag process)
				for (i = 1 ; i < LIMIT ; i++) {
					if (i % 2 != 0)
						for (x = j ; x <= i ; x++) {
							if ((n >= _rangeInit) && (n <= _rangeEnd)) {
								otable[x][i-x] = modifyValue(otable[x][i-x], inc);
								inc = (inc + 1 > _upperLimit) ? _lowLimit : inc + 1;
							}
							n++;
						}
					else
						for (x = i ; x >= j ; x--) {
							if ((n >= _rangeInit) && (n <= _rangeEnd)) {
								otable[x][i-x] = modifyValue(otable[x][i-x], inc);
								inc = (inc + 1 > _upperLimit) ? _lowLimit : inc + 1;
							}
							n++;
						}
				}
				--i;
				for (j = 1 ; j < LIMIT ; j++) {
					if (j % 2 != 0)
						for (x = i ; x >= j ; x--) {
							if ((n >= _rangeInit) && (n <= _rangeEnd)) {
								otable[x][i-x] = modifyValue(otable[x][j-x+i], inc);
								inc = (inc + 1 > _upperLimit) ? _lowLimit : inc + 1;
							}
							n++;
						}
					else
						for (x = j ; x <= i ; x++) {
							if ((n >= _rangeInit) && (n <= _rangeEnd)) {
								otable[x][i-x] = modifyValue(otable[x][j-x+i], inc);
								inc = (inc + 1 > _upperLimit) ? _lowLimit : inc + 1;
							}
							n++;
						}
				}
				// WATERMARK INSERTED
				
				FloatDCT.IDCT(otable, _quantizationMatrix, itable); // perform inverse DCT
				
				for (i = 0 ; i < LIMIT ; i++)  // read 8x8 array values into watermarked frame data
					for (j = 0; j < LIMIT ; j++)
						frameData[hb*LIMIT+i][wb*LIMIT+j] = (new Integer(Math.round(itable[i][j]))).byteValue();
			}
		
		return frameData;
	}
	
	/**
	 * Modify coefficient value according to watermarking settings.
	 *
	 * @param value Coefficient value (to be modified).
	 * @param inc Increment to be added.
	 * @return New coefficient value (watermarked coefficient).
	 */
	private float modifyValue(float value, int inc) throws WatermarkingException {
		float ovalue = value;
		
		switch (_modificationType) {
			case MODIFICATION_TYPE_ABSOLUTE:   // absolute modification
				switch (_modificationStep) {
					case MODIFICATION_STEP_INCREMENTAL: // if modification is incremental, just add 'inc'
						ovalue += inc;
						break;
					case MODIFICATION_STEP_UNIFORM:     // if modification is uniform, use global 'modificationValue'
						ovalue += _modificationValue;
						break;
					case MODIFICATION_STEP_RANDOM:      // if modification is random, use global 'modificationFunction'
						if (_modificationFunction instanceof DiscreteFactoredGaussian) { // only modificationFunction supported at the moment!
							DiscreteFactoredGaussian f = (DiscreteFactoredGaussian) _modificationFunction;
							ovalue += f.getDiscreteY(nextGaussian(f.getInf(), f.getSup()));
						}
						break;
					default:
						throw new WatermarkingException("exceptions.watermarking.unknown_modification_step");
				}
				break;
			case MODIFICATION_TYPE_PERCENTAGE: // percentage (relative) modification
				switch (_modificationStep) {
					case MODIFICATION_STEP_UNIFORM:     // if modification is uniform, use global 'modificationValue'
						ovalue += Math.round(value * _modificationValue / 100);
						break;
					default:
						throw new WatermarkingException("exceptions.watermarking.modification_not_allowed");
				}
				break;
			default:
				throw new WatermarkingException("exceptions.watermarking.unknown_modification_type");
		}
		return ovalue;
	}
	
	/**
	 * Obtain a random value according to a gaussian function.
	 *
	 * @param inf Lower limit for generated gaussian random number.
	 * @param sup Upper limit for generated gaussian random number.
	 * @return Random gaussian function value.
	 */
	private double nextGaussian(int inf, int sup) {
		double n = 0.0;
		do {
			n = _randomGenerator.nextGaussian();
		} while ((n < inf) || (n > sup));
		return n;
	}
	
	// ----- ----- ----- ATTRIBUTES -----  ----- -----
	
	/**
	 * The source {@link java.io.RandomAccessFile file}.
	 */
	private RandomAccessFile _scFile = null;
	/**
	 * The generated watermarked {@link java.io.RandomAccessFile file}.
	 */
	private RandomAccessFile _wmFile = null;
	/**
	 * The name of the source file.
	 */
	private String _filename = null;
	/**
	 * The name of the output file.
	 */
	private String _outputFilename = null;
	/**
	 * Height dimension of input and output videostream files.
	 */
	private int _height = 0;
	/**
	 * Width dimension of input and output videostream files.
	 */
	private int _width = 0;
	/**
	 * Number of bytes needed to store each frame in the videostream
	 * (includes frame header size).
	 */
	private int _frameLength = 0;
	/**
	 * Video stream YUV format.
	 */
	private int _YUVFormat = YUV_FORMAT_UNKNOWN;
	/**
	 * Length of Y (luminance) component stream data. Its value is equal to
	 * the product of height and width video dimensions.
	 */
	private int _ySize = 0;
	/**
	 * Length of U (blue chrominance) and V (red chrominance) components
	 * stream data. Its value will be a quarter of
	 * {@link jwmtool.lib.Watermarking#_ySize _ySize} attribute.
	 */
	private int _uvSize = 0;
	/**
	 * Buffer of length {@link jwmtool.lib.Watermarking#_ySize _ySize} to
	 * store Y component data.
	 */
	private byte[] _yBuffer = null;
	/**
	 * Buffer of length {@link jwmtool.lib.Watermarking#_uvSize _uvSize} to
	 * store U component data.
	 */
	private byte[] _uBuffer = null;
	/**
	 * Buffer of length {@link jwmtool.lib.Watermarking#_uvSize _uvSize} to
	 * store V component data.
	 */
	private byte[] _vBuffer = null;
	/**
	 * Matrix of {@link jwmtool.lib.Watermarking#_height _height} x
	 * {@link jwmtool.lib.Watermarking#_width _width} dimensions to store
	 * Y component data.
	 */
	private byte[][] _yData = null;
	/**
	 * Matrix of {@link jwmtool.lib.Watermarking#_height _height} x
	 * {@link jwmtool.lib.Watermarking#_width _width} / 2 dimensions to
	 * store U component data.
	 */
	private byte[][] _uData = null;
	/**
	 * Matrix of {@link jwmtool.lib.Watermarking#_height _height} x
	 * {@link jwmtool.lib.Watermarking#_width _width} / 2 dimensions to
	 * store V component data.
	 */
	private byte[][] _vData = null;
	/**
	 * Number of bytes read for component Y (luminance).
	 */
	private int _yRead = 0;
	/**
	 * Number of bytes read for component U (blue chrominance).
	 */
	private int _uRead = 0;
	/**
	 * Number of bytes read for component V (red chrominance).
	 */
	private int _vRead = 0;
	/**
	 * A singular frame of the source videostream.
	 */
	private Image _scFrame = null;
	/**
	 * A particular watermarked frame.
	 */
	private Image _wmFrame = null;
	
	/**
	 * First coefficient to be altered.
	 */
	private	int _rangeInit = 0;
	/**
	 * Last coefficient to be altered.
	 */
	private	int _rangeEnd  = 0;
	/**
	 * Type of modification to be performed (absolute/percentage).
	 */
	private	int _modificationType = MODIFICATION_TYPE_UNKNOWN;
	/**
	 * Type of modification to be performed each step (incremental/uniform/random).
	 */
	private	int _modificationStep = MODIFICATION_STEP_UNKNOWN;
	/**
	 * In case of an incremental modification step, this value represents
	 * the lower value of the modification sequence.
	 */
	private	int _lowLimit   = 0;
	/**
	 * In case of an incremental modification step, this value represents
	 * the upper limit of the modification sequence.
	 */
	private	int _upperLimit = 0;
	/**
	 * In case of an absolute modification, value to add to each coefficient
	 * in selected range.
	 */
	private	int _modificationValue = 0;
	/**
	 * In case of a random modification, function to select values to
	 * apply to coefficients.
	 */
	private	GraphableFunction _modificationFunction = null;
	
	/**
	 * Auxiliary matrix ({@link jwmtool.lib.Watermarking#LIMIT LIMIT} x
	 * {@link jwmtool.lib.Watermarking#LIMIT LIMIT} dimensions).
	 */
	private float[][] _quantizationMatrix = null;
	
	/**
	 * Random numbers generator.
	 */
	private Random _randomGenerator = new Random(System.currentTimeMillis());
	
}
