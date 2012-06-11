package jwmtool.util.graphics;

import ac.essex.statistics.functions.GraphableFunction;
import ac.essex.statistics.graphing.GraphPanel;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Graphical element which is able to display a
 * {@link ac.essex.statistics.functions.GraphableFunction GraphableFunction}
 * and also a number of equivalent discretization bars in the same panel.
 * 
 * @author Laura Castro
 * @version 0.6
 */

public class DoubleGraphPanel extends GraphPanel {
	
	/**
	 * Builds a <code>DoubleGraphPanel</code>.
	 * 
	 * @param function <code>GraphableFunction</code> to be provided to
	 *                 parent class for basic display.
	 */
	public DoubleGraphPanel(GraphableFunction function) {
		super(function);
	}
	
	/**
	 * Builds a <code>DoubleGraphPanel</code>.
	 * 
	 * @param function <code>GraphableFunction</code> to be provided to
	 *                 parent class for basic display.
	 * @param nBars Number of discrete bars to be displayed together with
	 *              <code>function</code>.
	 */
	public DoubleGraphPanel(GraphableFunction function, int nBars) {
		super(function);
		_nBars = nBars;
	}
	
	/**
	 * Builds a <code>DoubleGraphPanel</code>.
	 * 
	 * @param function <code>GraphableFunction</code> to be provided to
	 *                 parent class for basic display.
	 * @param xMin Minimum point to be displayed.
	 * @param xMax Maximum point to be displayed.
	 * @param yMin Minimum value of function to be displayed.
	 * @param yMax Maximum value of function to be displayed.
	 */
	public DoubleGraphPanel(GraphableFunction function, double xMin, double xMax, double yMin, double yMax) {
		super(function, xMin, xMax, yMin, yMax);
	}
	
	/**
	 * Builds a <code>DoubleGraphPanel</code>.
	 * 
	 * @param function <code>GraphableFunction</code> to be provided to
	 *                 parent class for basic display.
	 * @param xMin Minimum point to be displayed.
	 * @param xMax Maximum point to be displayed.
	 * @param yMin Minimum value of function to be displayed.
	 * @param yMax Maximum value of function to be displayed.
	 * @param nBars Number of discrete bars to be displayed together with
	 *              <code>function</code>.
	 */
	public DoubleGraphPanel(GraphableFunction function, double xMin, double xMax, double yMin, double yMax, int nBars) {
		super(function, xMin, xMax, yMin, yMax);
		_nBars = nBars;
	}
	
	/**
	 * Displays <code>GraphableFunction</code> and discrete bars on the
	 * same canvas.
	 * 
	 * @param g Graphical component to draw on.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		double plotRangeX     = Math.abs(minX) + Math.abs(maxX);
		double plotRangeY     = Math.abs(minY) + Math.abs(maxY);
		int    totalPixelsX   = getWidth() - (borderLeft + borderRight);
	        int    totalPixelsY   = getHeight() - (borderTop + borderBottom);
		double unitsPerPixelX = plotRangeX / totalPixelsX;
		double unitsPerPixelY = plotRangeY / totalPixelsY;
		int    barWidth       = totalPixelsX / _nBars;
		
		double actualX = minX + (barWidth/2 * unitsPerPixelX),
		       actualY = function.getY(actualX);
		
		int pixelY = ((int) ((actualY - minY) / unitsPerPixelY));
		
		int x = borderLeft + 1,
		    y = getHeight() - borderBottom - pixelY,
		    width  = barWidth - 1,
		    height = pixelY - 2;
		
		g.setColor(_barsColor);
		g.drawRect(x, y, width, height);
		width = barWidth;
		for (int i = 1; i < _nBars - 1; i++) {
			x = borderLeft + i * barWidth;
			actualX = minX + (x + barWidth/2 - borderLeft) * unitsPerPixelX;
			actualY = function.getY(actualX);
			pixelY = ((int) ((actualY - minY) / unitsPerPixelY));
			y = getHeight() - borderBottom - pixelY;
			height = pixelY - 2;
			g.drawRect(x, y, width, height);
		}
		x = borderLeft + (_nBars - 1) * barWidth;
		actualX = minX + (x + barWidth/2 - borderLeft) * unitsPerPixelX;
		actualY = function.getY(actualX);
		pixelY = ((int) ((actualY - minY) / unitsPerPixelY));
		y = getHeight() - borderBottom - pixelY;
		width = getWidth() - borderLeft - borderRight - (_nBars - 1) * barWidth - 2;
		height = pixelY - 2;
		g.drawRect(x, y, width, height);
        }
	
	/**
	 * Change number of discrete bars to be displayed.
	 * 
	 * @param nBars Number of discrete bars to be displayed together with
	 *              <code>function</code>.
	 */
	public void setNBars(int nBars) {
		_nBars = nBars;
	}
	
	// ----- ----- ----- ATTRIBUTES ----- ----- -----
	
	/**
	 * Number of discrete bars to be displayed.
	 */
	private int _nBars = 0;
	/**
	 * Color used to paint discrete bars.
	 */
	private Color _barsColor = Color.BLUE;
}
