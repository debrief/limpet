package info.limpet.stackedcharts.ui.view;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.LineUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

public class WrappingRenderer extends XYLineAndShapeRenderer
{
  /**
   * /**
   * An interface for creating custom logic for drawing lines between
   * points for XYLineAndShapeRenderer.
   */
  public static interface OverflowCondition {

    /**
     * Custom logic for detecting overflow between points.
     *
     * @param y0 previous y
     * @param x0 previous x
     * @param y1 current y
     * @param x1 current x
     * @return true, if you there is an overflow detected.
     * Otherwise, return false
     */
    public boolean isOverflow(double y0, double x0, double y1, double x1);
  }

  /**
  *
  */
  private static final long serialVersionUID = 1L;
  final double min = 0;
  final double max = 360;
  LinearInterpolator interpolator = new LinearInterpolator();
  final OverflowCondition overflowCondition;
  
  public WrappingRenderer(double min, double max)
  {
    overflowCondition = new OverflowCondition() {
      @Override
      public boolean isOverflow(double y0, double x0, double y1, double x1) {
        return Math.abs(y1 - y0) < 180;
      }
    };
  }

  @Override
  protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2,
      XYPlot plot, XYDataset dataset, int pass, int series, int item,
      ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea)
  {
    if (item == 0)
    {
      return;
    }

    // get the data point...
    double x1 = dataset.getXValue(series, item);
    double y1 = dataset.getYValue(series, item);
    if (Double.isNaN(y1) || Double.isNaN(x1))
    {
      return;
    }

    double x0 = dataset.getXValue(series, item - 1);
    double y0 = dataset.getYValue(series, item - 1);
    if (Double.isNaN(y0) || Double.isNaN(x0))
    {
      return;
    }

    if (!overflowCondition.isOverflow(y0, x0, y1, x1))
    {
      boolean overflowAtMax = y1 < y0;
      if (overflowAtMax)
      {
        PolynomialSplineFunction psf = interpolator.interpolate(new double[]
        {y0, y1 + max}, new double[]
        {x0, x1});
        double xmid = psf.value(max);
        drawPrimaryLine(state, g2, plot, x0, y0, xmid, max, pass, series, item,
            domainAxis, rangeAxis, dataArea);
        drawPrimaryLine(state, g2, plot, xmid, min, x1, y1, pass, series, item,
            domainAxis, rangeAxis, dataArea);
      }
      else
      {
        PolynomialSplineFunction psf = interpolator.interpolate(new double[]
        {y1 - max, y0}, new double[]
        {x1, x0});
        double xmid = psf.value(min);
        drawPrimaryLine(state, g2, plot, x0, y0, xmid, min, pass, series, item,
            domainAxis, rangeAxis, dataArea);
        drawPrimaryLine(state, g2, plot, xmid, max, x1, y1, pass, series, item,
            domainAxis, rangeAxis, dataArea);
      }
    }
    else
    {
      drawPrimaryLine(state, g2, plot, x0, y0, x1, y1, pass, series, item,
          domainAxis, rangeAxis, dataArea);
    }

  }

  private void drawPrimaryLine(XYItemRendererState state, Graphics2D g2,
      XYPlot plot, double x0, double y0, double x1, double y1, int pass,
      int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis,
      Rectangle2D dataArea)
  {
    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
    double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
    double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
    double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
    double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
    // only draw if we have good values
    if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1)
        || Double.isNaN(transY1))
    {
      return;
    }
    PlotOrientation orientation = plot.getOrientation();
    boolean visible;
    if (orientation == PlotOrientation.HORIZONTAL)
    {
      state.workingLine.setLine(transY0, transX0, transY1, transX1);
    }
    else if (orientation == PlotOrientation.VERTICAL)
    {
      state.workingLine.setLine(transX0, transY0, transX1, transY1);
    }
    visible = LineUtilities.clipLine(state.workingLine, dataArea);
    if (visible)
    {
      drawFirstPassShape(g2, pass, series, item, state.workingLine);
    }
  }
}
