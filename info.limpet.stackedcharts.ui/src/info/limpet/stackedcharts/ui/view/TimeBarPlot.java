package info.limpet.stackedcharts.ui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * provide new version of domain plot, that is able to render a time bar on the independent axis
 * 
 * @author ian
 * 
 */
public class TimeBarPlot extends CombinedDomainXYPlot
{
  /** strategy for classes that can provide an interpolated value for 
   * JFreeChart datasets
   * @author ian
   *
   */
  protected static interface AxisHelper
  {
    Double getValueAt(Dataset dataset, int index, double time);
  }

  protected static class CacheingDateHelper implements AxisHelper
  {

    private static class IndexSet
    {
      Double previousY = null;
      Double nextY = null;
      Long previousX = null;
      Long nextX = null;
      public int index;
    }

    private final Map<TimeSeries, IndexSet> _cache;

    public CacheingDateHelper()
    {
      _cache = new HashMap<TimeSeries, IndexSet>();
    }

    @Override
    public Double getValueAt(final Dataset dataset, final int seriesIndex,
        final double tNow)
    {
      final TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
      final TimeSeries series = tsc.getSeries(seriesIndex);

      // see if we've cached this
      IndexSet set = _cache.get(series);
      if (set == null)
      {
        set = new IndexSet();
        _cache.put(series, set);
      }

      @SuppressWarnings("unchecked")
      final List<TimeSeriesDataItem> items = series.getItems();

      // just check if the time is still between the existing items
      final boolean pending = set.previousX == null;
      final boolean outOfRange =
          pending || set.previousX > tNow || set.nextX < tNow;
      if (pending || outOfRange)
      {
        final int len = items.size();

        // try the index
        final long indexVal =
            items.get(set.index).getPeriod().getFirstMillisecond();

        if (indexVal < tNow)
        {
          // walk forwards
          for (int index = set.index; index < len; index++)
          {
            final TimeSeriesDataItem thisItem = items.get(index);
            // does this class as a previous value?
            if (thisItem.getPeriod().getFirstMillisecond() < tNow)
            {
              // ok, it's before use the previous
              set.previousY = (Double) thisItem.getValue();
              set.previousX = thisItem.getPeriod().getFirstMillisecond();
              set.index = index;
            }
            else
            {
              // ok, we've passed it. stop
              set.nextY = (Double) thisItem.getValue();
              set.nextX = thisItem.getPeriod().getFirstMillisecond();
              break;
            }
          }
        }
        else
        {
          // walk backwards
          for (int index = set.index; index > 0; index--)
          {
            final TimeSeriesDataItem thisItem = items.get(index);
            // does this class as a previous value?
            if (thisItem.getPeriod().getFirstMillisecond() > tNow)
            {
              // ok, it's before use the previous
              set.nextY = (Double) thisItem.getValue();
              set.nextX = thisItem.getPeriod().getFirstMillisecond();
            }
            else
            {
              // ok, we've passed it. stop
              set.previousY = (Double) thisItem.getValue();
              set.previousX = thisItem.getPeriod().getFirstMillisecond();
              set.index = index;
              break;
            }
          }
        }

      }

      // have we found values?
      final Double interpolated;
      if (set.previousY != null && set.nextY != null)
      {
        // ok, interpolate the time (X)
        final double proportion =
            (tNow - set.previousX) / (set.nextX - set.previousX);

        // ok, now generate interpolate the value to use
        interpolated = set.previousY + proportion * (set.nextY - set.previousY);
      }
      else
      {
        interpolated = null;
      }

      return interpolated;
    }
  }

  protected static class DateHelper implements AxisHelper
  {
    public DateHelper()
    {
    }

    @Override
    public Double getValueAt(final Dataset dataset, final int index,
        final double tNow)
    {
      final TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
      final TimeSeries series = tsc.getSeries(index);

      @SuppressWarnings("unchecked")
      final List<TimeSeriesDataItem> items = series.getItems();

      // ok, get ready to interpolate the value
      Double previousY = null;
      Double nextY = null;
      Long previousX = null;
      Long nextX = null;

      // loop through the data
      for (final TimeSeriesDataItem thisItem : items)
      {
        // does this class as a previous value?
        if (thisItem.getPeriod().getFirstMillisecond() < tNow)
        {
          // ok, it's before use the previous
          previousY = (Double) thisItem.getValue();
          previousX = thisItem.getPeriod().getFirstMillisecond();
        }
        else
        {
          // ok, we've passed it. stop
          nextY = (Double) thisItem.getValue();
          nextX = thisItem.getPeriod().getFirstMillisecond();
          break;
        }
      }

      // have we found values?
      final Double interpolated;
      if (previousY != null && nextY != null)
      {
        // ok, interpolate the time (X)
        final double proportion = (tNow - previousX) / (nextX - previousX);

        // ok, now generate interpolate the value to use
        interpolated = previousY + proportion * (nextY - previousY);
      }
      else
      {
        interpolated = null;
      }

      return interpolated;
    }
  }

  protected static class NumberHelper implements AxisHelper
  {

    public NumberHelper()
    {
    }

    @Override
    public Double getValueAt(final Dataset dataset, final int index,
        final double tNow)
    {
      final XYSeriesCollection tsc = (XYSeriesCollection) dataset;
      final XYSeries series = tsc.getSeries(index);

      @SuppressWarnings("unchecked")
      final List<XYDataItem> items = series.getItems();

      // ok, get ready to interpolate the value
      Double previousY = null;
      Double nextY = null;
      Double previousX = null;
      Double nextX = null;

      // loop through the data
      for (final XYDataItem thisItem : items)
      {
        // does this class as a previous value?
        if (thisItem.getX().doubleValue() < tNow)
        {
          // ok, it's before use the previous
          previousY = thisItem.getYValue();
          previousX = thisItem.getXValue();
        }
        else
        {
          // ok, we've passed it. stop
          nextY = thisItem.getYValue();
          nextX = thisItem.getXValue();
          break;
        }
      }

      // have we found values?
      final Double interpolated;
      if (previousY != null && nextY != null)
      {
        // ok, interpolate the time (X)
        final double proportion = (tNow - previousX) / (nextX - previousX);

        // ok, now generate interpolate the value to use
        interpolated = previousY + proportion * (nextY - previousY);
      }
      else
      {
        interpolated = null;
      }

      return interpolated;
    }
  }

  private static abstract class TestRunner
  {
    private final String _title;

    TestRunner(final String title)
    {
      _title = title;
    }

    private TimeSeriesCollection collateTestData(final long len,
        final long start)
    {
      final TimeSeries series1 = new TimeSeries("Speed 1");
      final TimeSeries series2 = new TimeSeries("Speed 2");

      for (long i = 0; i < len; i += 10)
      {
        series1.add(new Millisecond(new Date(start + i)), Math.sin(Math
            .toRadians(i)));
        series2.add(new Millisecond(new Date(start + i)), Math.cos(Math
            .toRadians(i)));
      }

      final TimeSeriesCollection coll = new TimeSeriesCollection();
      coll.addSeries(series1);
      coll.addSeries(series2);

      return coll;
    }

    abstract AxisHelper getHelper();

    void test(final long start, final long len)
    {

      System.out.println(_title);

      AxisHelper helper = getHelper();
      final TimeSeriesCollection dataset = collateTestData(len, start);

      Double res = 0d;

      long t0 = System.currentTimeMillis();

      for (int i = 1; i < 10; i++)
      {
        final Double interp = helper.getValueAt(dataset, 0, start + (len / i));
        if (interp != null)
          res += interp;
      }

      // re-initiate the helper
      helper = getHelper();

      long t1 = System.currentTimeMillis();

      System.out.println("Random access:" + (t1 - t0) + ", " + res);

      res = 1d;

      t0 = System.currentTimeMillis();

      long time = start + (long) (len * 0.1);

      for (int i = 1; i < 10; i++)
      {
        final Double interp = helper.getValueAt(dataset, 0, time + i);
        if (interp != null)
          res += interp;
      }

      // re-initiate the helper
      helper = getHelper();

      t1 = System.currentTimeMillis();

      System.out.println("Sequential access (early):" + (t1 - t0) + ", " + res);

      res = 1d;

      t0 = System.currentTimeMillis();

      time = start + (long) (len * 0.9);

      for (int i = 1; i < 10; i++)
      {
        final Double interp = helper.getValueAt(dataset, 0, time + i);
        if (interp != null)
          res += interp;
      }

      // re-initiate the helper
      helper = getHelper();

      t1 = System.currentTimeMillis();

      System.out.println("Sequential access (late):" + (t1 - t0) + ", " + res);

      res = 1d;

      // re-initiate the helper
      helper = getHelper();

      t0 = System.currentTimeMillis();

      time = start + (long) (len * 0.9);

      for (int i = 10; i > 0; i--)
      {
        final Double interp = helper.getValueAt(dataset, 0, time + i);
        if (interp != null)
          res += interp;
      }

      t1 = System.currentTimeMillis();

      System.out.println("Sequential access (late desc):" + (t1 - t0) + ", "
          + res);
    }

  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static void main(final String[] args)
  {
    final long len = 1000000;
    final long start = new Date().getTime();

    TestRunner runner = new TestRunner("Brute force")
    {
      @Override
      DateHelper getHelper()
      {
        return new DateHelper();
      }
    };

    runner.test(start, len);

    runner = new TestRunner("Cache")
    {
      @Override
      AxisHelper getHelper()
      {
        return new CacheingDateHelper();
      }
    };

    runner.test(start, len);

  }

  protected static void paintThisMarker(final Graphics2D g2,
      final String label, final Color color, final float markerX,
      final float markerY, final boolean vertical, final Font markerFont)
  {
    // store old font
    final Font oldFont = g2.getFont();

    // set the new one
    g2.setFont(markerFont);

    // reflect the plot orientation
    final float xPos;
    final float yPos;
    if (vertical)
    {
      yPos = 3f + markerX;
      xPos = markerY;
    }
    else
    {
      xPos = 4f + markerX;
      yPos = 2f + markerY;
    }

    // find the size of the label, so we can draw a background
    final FontMetrics fc = g2.getFontMetrics();
    final Rectangle2D bounds = fc.getStringBounds(label, g2);
    g2.setColor(Color.white);
    g2.fill3DRect((int) yPos, (int) (xPos - bounds.getHeight()),
        3 + (int) bounds.getWidth(), 3 + (int) bounds.getHeight(), true);

    g2.setColor(color.darker());
    g2.drawString(label, yPos, xPos);

    // and restore the font
    g2.setFont(oldFont);
  }

  Date _currentTime = null;

  boolean _showLine = true;

  boolean _showLabels = true;

  final java.awt.Color _orange = new java.awt.Color(247, 153, 37);

  final java.awt.Font _markerFont;

  AxisHelper _helper;

  public TimeBarPlot(final ValueAxis sharedAxis)
  {
    super(sharedAxis);
    _markerFont = new Font("Arial", Font.PLAIN, 8);
  }

  /**
   * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer), together with
   * a current time marker
   * <P>
   * XYPlot relies on an XYItemRenderer to draw each item in the plot. This allows the visual
   * representation of the data to be changed easily.
   * <P>
   * The optional info argument collects information about the rendering of the plot (dimensions,
   * tooltip information etc). Just pass in null if you do not need this information.
   * 
   * @param g2
   *          The graphics device.
   * @param plotArea
   *          The area within which the plot (including axis labels) should be drawn.
   * @param renderInfo
   *          Collects chart drawing information (null permitted).
   */
  @Override
  public final void draw(final Graphics2D g2, final Rectangle2D plotArea,
      final Point2D anchor, final PlotState state,
      final PlotRenderingInfo renderInfo)
  {
    super.draw(g2, plotArea, anchor, state, renderInfo);

    // do we have a time?
    if (_currentTime != null)
    {
      // hmm, are we stacked vertically or horizontally?
      final boolean vertical =
          this.getOrientation() == PlotOrientation.VERTICAL;

      // find the screen area for the dataset
      final Rectangle2D dataArea = renderInfo.getDataArea();

      // determine the time we are plotting the line at
      final long theTime = _currentTime.getTime();

      final Axis domainAxis = this.getDomainAxis();

      // hmm, see if we are wroking with a date or number axis
      Double linePosition;
      if (domainAxis instanceof DateAxis)
      {
        // ok, now scale the time to graph units
        final DateAxis dateAxis = (DateAxis) domainAxis;

        // find the new x value
        linePosition =
            dateAxis.dateToJava2D(new Date(theTime), dataArea, this
                .getDomainAxisEdge());

      }
      else if (domainAxis instanceof NumberAxis)
      {
        final NumberAxis numberAxis = (NumberAxis) domainAxis;
        linePosition =
            numberAxis.valueToJava2D(theTime, dataArea, this
                .getDomainAxisEdge());
      }
      else
      {
        linePosition = null;
      }

      if (linePosition == null)
      {
        return;
      }

      // trim the linePositiion to the visible area
      double trimmedLinePosition;
      if (vertical)
      {
        trimmedLinePosition = Math.max(linePosition, dataArea.getMinX());
        trimmedLinePosition = Math.min(trimmedLinePosition, dataArea.getMaxX());
      }
      else
      {
        trimmedLinePosition = Math.max(linePosition, dataArea.getMinY());
        trimmedLinePosition = Math.min(trimmedLinePosition, dataArea.getMaxY());

      }

      // did we do any clipping?
      final boolean clipped = !linePosition.equals(trimmedLinePosition);

      if (_showLine)
      {
        plotStepperLine(g2, dataArea, vertical, domainAxis, theTime,
            (int) trimmedLinePosition, clipped);
      }

      // ok, have a got at the time values
      if (_showLabels && !clipped)
      {
        // sort out the helper
        if (_helper == null)
        {
          final CombinedDomainXYPlot comb = this;

          final ValueAxis sharedAxis = comb.getDomainAxis();
          if (sharedAxis instanceof DateAxis)
          {
            _helper = new DateHelper();
          }
          else if (sharedAxis instanceof NumberAxis)
          {
            _helper = new NumberHelper();
          }
        }

        plotStepperMarkers(g2, dataArea, vertical, domainAxis, theTime,
            renderInfo, (int) trimmedLinePosition, _helper);
      }
    }
  }

  /**
   * draw the new stepper line into the plot
   * 
   * @param g2
   * @param linePosition
   * @param dataArea
   * @param axis
   * @param theTime
   * @param linePosition
   * @param clipped
   */
  protected void plotStepperLine(final Graphics2D g2,
      final Rectangle2D dataArea, final boolean vertical, final Axis axis,
      final long theTime, final int linePosition, final boolean clipped)
  {

    // prepare to draw
    final Stroke oldStroke = g2.getStroke();

    g2.setColor(_orange);

    // thicken up the line
    final int wid;
    if (clipped)
    {
      g2.setStroke(new BasicStroke(1));
      wid = 1;
    }
    else
    {
      g2.setStroke(new BasicStroke(3));
      wid = 1;
    }

    if (vertical)
    {
      // draw the line
      g2.drawLine(linePosition - wid + 2, (int) dataArea.getY(), linePosition
          - wid + 2, (int) dataArea.getY() + (int) dataArea.getHeight());
    }
    else
    {
      // draw the line
      g2.drawLine((int) dataArea.getX() + 1, linePosition - 1, (int) dataArea
          .getX()
          + (int) dataArea.getWidth() - 1, linePosition - 1);

    }

    // and restore everything
    g2.setStroke(oldStroke);
    g2.setPaintMode();
  }

  protected void plotStepperMarkers(final Graphics2D g2,
      final Rectangle2D dataArea, final boolean vertical,
      final Axis domainAxis, final long theTime, final PlotRenderingInfo info,
      final int linePosition, final AxisHelper axisHelper)
  {
    // ok, loop through the charts
    final CombinedDomainXYPlot comb = this;
    @SuppressWarnings("unchecked")
    final List<XYPlot> plots = comb.getSubplots();

    final NumberFormat oneDP = new DecimalFormat("0.0");
    final NumberFormat noDP = new DecimalFormat("0");

    // keep track of how many series we've used for each renderer
    final Map<XYItemRenderer, Integer> seriesCounter =
        new HashMap<XYItemRenderer, Integer>();

    // what's the current time?
    final double tNow = _currentTime.getTime();

    // keep track of how many plots we've processed
    int plotCounter = 0;

    // loop through the stack of plots
    for (final XYPlot plot : plots)
    {
      // ok, get the area for this subplot
      final Rectangle2D thisPlotArea =
          info.getSubplotInfo(plotCounter++).getPlotArea();

      // how many datasets?
      final int numC = plot.getDatasetCount();

      // loop through the datasets for this plot
      for (int i = 0; i < numC; i++)
      {
        final XYDataset dataset = plot.getDataset(i);

        if (axisHelper != null)
        {

          final int num = dataset.getSeriesCount();
          for (int j = 0; j < num; j++)
          {
            final Double interpolated = axisHelper.getValueAt(dataset, j, tNow);

            if (interpolated != null)
            {

              // and find the y coordinate of this data value
              final NumberAxis rangeA =
                  (NumberAxis) plot.getRangeAxisForDataset(i);
              final float markerY =
                  (float) rangeA.valueToJava2D(interpolated, thisPlotArea, this
                      .getRangeAxisEdge());

              // prepare the label
              final String label;
              if (interpolated > 100)
              {
                label = noDP.format(interpolated);
              }
              else
              {
                label = oneDP.format(interpolated);
              }

              // and the color
              final XYItemRenderer renderer =
                  plot.getRendererForDataset(dataset);

              // find the series number
              Integer counter = seriesCounter.get(renderer);
              if (counter == null)
              {
                // first series, initialise
                counter = 0;
                seriesCounter.put(renderer, counter);
              }
              else
              {
                // already exists, increment
                seriesCounter.put(renderer, ++counter);
              }

              final Color paint = (Color) renderer.getSeriesPaint(counter);

              // done, render it
              paintThisMarker(g2, label, paint, linePosition, markerY,
                  vertical, _markerFont);
            }
          }
        }
      }
    }
  }

  public void setTime(final Date time)
  {
    _currentTime = time;
  }
}