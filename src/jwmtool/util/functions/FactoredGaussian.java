package jwmtool.util.functions;

import ac.essex.statistics.functions.Gaussian;
import ac.essex.statistics.functions.GraphableFunction;

/**
 * Decorator for Gaussian GraphableFunction. It provides the same functionality,
 * but allows the user to obtain a factored result (i.e., stretch gauss curve).
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class FactoredGaussian implements GraphableFunction {
	
	/**
	 * Creates a  new FactoredGaussian GraphableFunction.
	 * 
	 * @param stdDeviation Standard deviation for Gauss function.
	 * @param mean Mean for Gauss function.
	 * @param factor Factor to multiply Gauss function result for.
	 */
	public FactoredGaussian(double stdDeviation, double mean, double factor) {
		_unfactored = new Gaussian(stdDeviation, mean);
		_factor = factor;
	}
	
	/**
	 * Obtains value of configured Gauss function, factored.
	 * 
	 * @param x Point to obtain Gauss function value.
	 * @return Value of configured factored Gauss function at <code>x</code>.
	 */
	public double getY(double x) {
		return _factor * _unfactored.getY(x);
	}
	
	/**
	 * Returns GraphableFunction name.
	 * 
	 * @return Function name string.
	 */
	public String getName() {
		return "Factored " + _unfactored.getName();
	}
	
	/**
	 * Obtains factor which is being applied in FactoredGaussian function.
	 * 
	 * @return Value which is begin multiplied by Gaussian results to
	 *         obtain FactoredGaussian values.
	 */
	public double getFactor() {
		return _factor;
	}
	
	/**
	 * Obtains standard deviation of this FactoredGaussian function.
	 * 
	 * @return Standard deviation value.
	 */
	public double getStdDeviation() {
		return _unfactored.getStdDeviation();
	}
	
	/**
	 * Obtains mean of this FactoredGaussian function.
	 * 
	 * @return Mean value.
	 */
	public double getMean() {
		return _unfactored.getMean();
	}
	
	/**
	 * Obtains variance of this FactoredGaussian function.
	 * 
	 * @return Variance value.
	 */
	public double getVariance() {
		return _unfactored.getVariance();
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Factor which is being applied in FactoredGaussian function to
	 * obtain FactoredGaussian values from Gaussian ones.
	 */
	private double _factor;
	/**
	 * Decorated Gaussian GraphableFunction.
	 */
	private Gaussian _unfactored;
}
