package jwmtool.util.functions;

import ac.essex.statistics.functions.GraphableFunction;

/**
 * Decorator for FactoredGaussian GraphableFunction. It provides the same
 * functionality, but discretizes input into <code>nblocks</code> blocks to
 * obtain a discretized result.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class DiscreteFactoredGaussian implements GraphableFunction {
	
	/**
	 * Creates a  new DiscreteFactoredGaussian GraphableFunction.
	 * 
	 * @param stdDeviation Standard deviation for Gauss function.
	 * @param mean Mean for Gauss function.
	 * @param factor Factor to multiply Gauss function result for.
	 * @param rangeInit Lowest point to be considered for Gauss function.
	 * @param rangeEnd Highest point to be considered for Gauss function.
	 * @param nblocks Number of discrete blocks to apply.
	 */
	public DiscreteFactoredGaussian(double stdDeviation, double mean, double factor, int rangeInit, int rangeEnd, int nblocks) {
		_continuous = new FactoredGaussian(stdDeviation, mean, factor);
		_rangeInit = rangeInit;
		_rangeEnd  = rangeEnd;
		_nblocks = nblocks;
		_blockLength = (_rangeEnd - _rangeInit) / (_nblocks * 1f);
	}
	
	/**
	 * Obtains value of configured Gauss function, factored.
	 * 
	 * @param x Point to obtain Gauss function value.
	 * @return Value of configured factored Gauss function at <code>x</code>.
	 */
	public double getY(double x) {
		return _continuous.getY(x);
	}
	
	/**
	 * Obtains value of configured Gauss function, factored and discretized.
	 * 
	 * @param x Point to obtain Gauss function value.
	 * @return Value of configured discretized factored Gauss function at
	 *         <code>x</code>.
	 */
	public double getDiscreteY(double x) {
		if (x < _rangeInit) {
			return _continuous.getY(_rangeInit + _blockLength/2);
		}
		else if (x > _rangeEnd) {
			return _continuous.getY(_rangeInit + (2 * _nblocks - 1) * _blockLength/2);
		}
		else {
			int actualBlock = 0;
			double lowLimit = _rangeInit;
			double upperLimit = _rangeInit + _blockLength;
			boolean found = false;
			while (!found) {
				if ((x >= lowLimit) && (x <= upperLimit)) {
					found = true;
				}
				else {
					actualBlock++;
					lowLimit += _blockLength;
					upperLimit += _blockLength;
				}
			}
			return _continuous.getY(_rangeInit + actualBlock * _blockLength + _blockLength/2);
		}
	}
	
	/**
	 * Returns GraphableFunction name.
	 * 
	 * @return Function name string.
	 */
	public String getName() {
		return "Discrete " + _continuous.getName();
	}
	
	/**
	 * Returns lowest point to be considered to obtain Gauss function value.
	 * 
	 * @return Lowest allowed point.
	 */
	public int getInf() {
		return _rangeInit;
	}
	
	/**
	 * Returns highest point to be considered to obtain Gauss function value.
	 * 
	 * @return Highest allowed point.
	 */
	public int getSup() {
		return _rangeEnd;
	}
	
	/**
	 * Obtains factor which is being applied in decorated FactoredGaussian
	 * function.
	 * 
	 * @return Value which is begin multiplied by Gaussian results to
	 *         obtain decorated FactoredGaussian values.
	 */
	public double getFactor() {
		return _continuous.getFactor();
	}
	
	/**
	 * Obtains standard deviation of this FactoredGaussian function.
	 * 
	 * @return Standard deviation value.
	 */
	public double getStdDeviation() {
		return _continuous.getStdDeviation();
	}
	
	/**
	 * Obtains mean of this FactoredGaussian function.
	 * 
	 * @return Mean value.
	 */
	public double getMean() {
		return _continuous.getMean();
	}
	
	/**
	 * Obtains variance of this FactoredGaussian function.
	 * 
	 * @return Variance value.
	 */
	public double getVariance() {
		return _continuous.getVariance();
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Lowest point to be considered to obtain Gauss function value.
	 */
	private int _rangeInit;
	/**
	 * Highest point to be considered to obtain Gauss function value.
	 */
	private int _rangeEnd;
	/**
	 * Number of blocks to discretize factored Gauss function into.
	 */
	private int _nblocks;
	/**
	 * Decorated FactoredGaussian GraphableFunction.
	 */
	private FactoredGaussian _continuous;
	/**
	 * Length of each discretization block.
	 */
	private double _blockLength;
}
