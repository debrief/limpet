package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.Styling;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

public class ChartBuilder
{

  /**
   * helper class that can handle either temporal or non-temporal datasets
   * 
   * @author ian
   * 
   */
  private static interface ChartHelper
  {
    /**
     * add this item to this series
     * 
     * @param series
     * @param item
     */
    void addItem(Series series, DataItem item);

    /**
     * clear the contents of the series
     * 
     */
    void clear(Series series);

    /**
     * create the correct type of axis
     * 
     * @param name
     * @return
     */
    ValueAxis createAxis(String name);

    /**
     * create a new collection of datasets (seroes)
     * 
     * @return
     */
    XYDataset createCollection();

    /**
     * create a series with the specified name
     * 
     * @param name
     * @return
     */
    Series createSeries(String name);

    /**
     * put the series into the collection
     * 
     * @param collection
     * @param series
     */
    void storeSeries(XYDataset collection, Series series);
  }

  /**
   * support generation of a stacked chart with a shared number axis
   * 
   * @author ian
   * 
   */
  private static class NumberHelper implements ChartHelper
  {

    @Override
    public void addItem(final Series series, final DataItem item)
    {
      final XYSeries ns = (XYSeries) series;
      ns.add(item.getIndependentVal(), item.getDependentVal());
    }

    @Override
    public void clear(final Series series)
    {
      final XYSeries ns = (XYSeries) series;
      ns.clear();
    }

    @Override
    public ValueAxis createAxis(final String name)
    {
      return new NumberAxis(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new XYSeriesCollection();
    }

    @Override
    public Series createSeries(final String name)
    {
      return new XYSeries(name);
    }

    @Override
    public void storeSeries(final XYDataset collection, final Series series)
    {
      final XYSeriesCollection cc = (XYSeriesCollection) collection;
      cc.addSeries((XYSeries) series);
    }

  }

  /**
   * provide new version of domain plot, that is able to render a time bar on the independent axis
   * 
   * @author ian
   * 
   */
  public static class TimeBarPlot extends CombinedDomainXYPlot
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    Date _currentTime = null;
    boolean _showLine = true;
    boolean _showLabels = true;
    final java.awt.Color _orange = new java.awt.Color(247, 153, 37);
    final java.awt.Font _markerFont;

    public TimeBarPlot(final ValueAxis sharedAxis)
    {
      super(sharedAxis);
      _markerFont = new Font("Arial", Font.PLAIN, 8);
    }

    /**
     * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer), together
     * with a current time marker
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

        if (_showLine)
        {
          plotStepperLine(g2, dataArea, vertical, domainAxis, theTime);
        }

        // ok, have a got at the time values
        if (_showLabels)
        {
          plotStepperMarkers(g2, dataArea, vertical, domainAxis, theTime,
              renderInfo);
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
     */
    protected void plotStepperLine(final Graphics2D g2,
        final Rectangle2D dataArea, final boolean vertical, final Axis axis,
        final long theTime)
    {
      // hmm, see if we are wroking with a date or number axis
      double linePosition = 0;
      if (axis instanceof DateAxis)
      {
        // ok, now scale the time to graph units
        final DateAxis dateAxis = (DateAxis) axis;

        // find the new x value
        linePosition =
            dateAxis.dateToJava2D(new Date(theTime), dataArea, this
                .getDomainAxisEdge());
      }
      else
      {
        if (axis instanceof NumberAxis)
        {
          final NumberAxis numberAxis = (NumberAxis) axis;
          linePosition =
              numberAxis.valueToJava2D(theTime, dataArea, this
                  .getDomainAxisEdge());
        }
      }

      // prepare to draw
      final Stroke oldStroke = g2.getStroke();

      g2.setColor(_orange);

      // thicken up the line
      g2.setStroke(new BasicStroke(3));

      if (vertical)
      {
        // draw the line
        g2.drawLine((int) linePosition - 1, (int) dataArea.getY() + 1,
            (int) linePosition - 1, (int) dataArea.getY()
                + (int) dataArea.getHeight() - 1);
      }
      else
      {
        // draw the line
        g2.drawLine((int) dataArea.getX() + 1, (int) linePosition - 1,
            (int) dataArea.getX() + (int) dataArea.getWidth() - 1,
            (int) linePosition - 1);

      }

      // and restore everything
      g2.setStroke(oldStroke);
      g2.setPaintMode();
    }

    protected void
        plotStepperMarkers(final Graphics2D g2, final Rectangle2D dataArea,
            final boolean vertical, final Axis domainAxis, final long theTime,
            final PlotRenderingInfo info)
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

          if (dataset instanceof XYSeriesCollection)
          {
            final XYSeriesCollection coll = (XYSeriesCollection) dataset;
            final int num = coll.getSeriesCount();
            for (int j = 0; j < num; j++)
            {
              final XYSeries series = coll.getSeries(j);

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
                if (thisItem.getXValue() < tNow)
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
              if (previousY != null && nextY != null)
              {
                // ok, now for the coordinate on the x axis
                final Double markerX;
                if (domainAxis instanceof DateAxis)
                {
                  final DateAxis dateAxis = (DateAxis) domainAxis;

                  // find the new x value
                  markerX =
                      dateAxis.dateToJava2D(new Date(theTime), dataArea, this
                          .getDomainAxisEdge());
                }
                else if (domainAxis instanceof NumberAxis)
                {
                  final NumberAxis numberAxis = (NumberAxis) domainAxis;

                  // find the new x value
                  markerX =
                      numberAxis.valueToJava2D(theTime, dataArea, this
                          .getDomainAxisEdge());
                }
                else
                {
                  // can't help - drop out
                  markerX = null;
                }

                // have we found our x value?
                if (markerX != null)
                {
                  // ok, interpolate the time (X)
                  final double proportion =
                      (theTime - previousX) / (nextX - previousX);

                  // ok, now generate interpolate the value to use
                  final double interpolated =
                      previousY + proportion * (nextY - previousY);

                  // and find the y coordinate of this data value
                  final NumberAxis rangeA =
                      (NumberAxis) plot.getRangeAxisForDataset(i);
                  final float markerY =
                      (float) rangeA.valueToJava2D(interpolated, thisPlotArea,
                          this.getRangeAxisEdge());

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
                  paintThisMarker(g2, label, paint, markerX.floatValue(),
                      markerY, vertical, _markerFont);
                }

              }

            }
          }
        }
      }
    }

    protected static void paintThisMarker(final Graphics2D g2,
        final String label, final Color color, final float markerX,
        final float markerY, final boolean vertical, Font markerFont)
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
        yPos = 2f + markerX;
        xPos = (float) markerY;
      }
      else
      {
        xPos = 4f + (float) markerX;
        yPos = 2f + (float) markerY;
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

    public void setTime(final Date time)
    {
      _currentTime = time;
    }
  }

  /**
   * support generation of a stacked chart with a shared time axis
   * 
   * @author ian
   * 
   */
  private static class DateHelper implements ChartHelper
  {

    @Override
    public void addItem(final Series series, final DataItem item)
    {
      final TimeSeries ns = (TimeSeries) series;
      final long time = (long) item.getIndependentVal();
      final Millisecond milli = new Millisecond(new Date(time));
      ns.add(milli, item.getDependentVal());
    }

    @Override
    public void clear(final Series series)
    {
      final TimeSeries ns = (TimeSeries) series;
      ns.clear();
    }

    @Override
    public ValueAxis createAxis(final String name)
    {
      return new DateAxis(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new TimeSeriesCollection();
    }

    @Override
    public Series createSeries(final String name)
    {
      return new TimeSeries(name);
    }

    @Override
    public void storeSeries(final XYDataset collection, final Series series)
    {
      final TimeSeriesCollection cc = (TimeSeriesCollection) collection;
      cc.addSeries((TimeSeries) series);
    }
  }

  /**
   * 
   * @param subplot
   *          target plot for annotation
   * @param annotations
   *          annotation list to be added to plot eg: Marker,Zone
   * @param isRangeAnnotation
   *          is annotation added to Range or Domain
   */

  private static void addAnnotationToPlot(final XYPlot subplot,
      final List<AbstractAnnotation> annotations,
      final boolean isRangeAnnotation)
  {
    for (final AbstractAnnotation annotation : annotations)
    {
      final Color color = annotation.getColor();

      if (annotation instanceof info.limpet.stackedcharts.model.Marker)
      {
        // build value Marker
        final info.limpet.stackedcharts.model.Marker marker =
            (info.limpet.stackedcharts.model.Marker) annotation;

        final Marker mrk = new ValueMarker(marker.getValue());
        mrk.setLabel(annotation.getName());

        mrk.setPaint(color == null ? Color.GRAY : color);

        // move Text Anchor
        mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        mrk.setLabelAnchor(RectangleAnchor.TOP);

        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
        if (isRangeAnnotation)
        {
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        }
        else
        {
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
        }
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.Zone)
      {
        // build Zone
        final info.limpet.stackedcharts.model.Zone zone =
            (info.limpet.stackedcharts.model.Zone) annotation;

        final Marker mrk = new IntervalMarker(zone.getStart(), zone.getEnd());
        mrk.setLabel(annotation.getName());

        if (color != null)
        {
          mrk.setPaint(color);
        }

        // move Text & Label Anchor
        mrk.setLabelTextAnchor(TextAnchor.CENTER);
        mrk.setLabelAnchor(RectangleAnchor.CENTER);
        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));

        if (isRangeAnnotation)
        {
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        }
        else
        {
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
        }
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.ScatterSet)
      {
        // build ScatterSet
        final info.limpet.stackedcharts.model.ScatterSet marker =
            (info.limpet.stackedcharts.model.ScatterSet) annotation;

        final EList<Datum> datums = marker.getDatums();
        boolean addLabel = true;
        for (final Datum datum : datums)
        {
          final Marker mrk = new ValueMarker(datum.getVal());
          // only add label for first Marker
          if (addLabel)
          {
            mrk.setLabel(annotation.getName());
            addLabel = false;
          }

          final Color thisColor = datum.getColor();
          final Color colorToUse = thisColor == null ? color : thisColor;

          // apply some transparency to the color
          if (colorToUse != null)
          {
            final Color transColor =
                new Color(colorToUse.getRed(), colorToUse.getGreen(),
                    colorToUse.getBlue(), 120);

            mrk.setPaint(transColor);
          }

          // move Text Anchor
          mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
          mrk.setLabelAnchor(RectangleAnchor.TOP);

          mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
          if (isRangeAnnotation)
          {
            subplot.addRangeMarker(mrk, Layer.FOREGROUND);
          }
          else
          {
            subplot.addDomainMarker(mrk, Layer.FOREGROUND);
          }
        }

      }

    }

  }

  /**
   * 
   * @param helper
   *          Helper object to map between axis types
   * @param datasets
   *          list of datasets to add to this axis
   * @param collection
   *          XYDataset need to be fill with Series
   * @param renderer
   *          axis renderer
   * @param seriesIndex
   *          counter for current series being assigned
   * 
   *          build axis dataset via adding series to Series & config renderer for styles
   */
  private static void addDatasetToAxis(final ChartHelper helper,
      final EList<Dataset> datasets, final XYDataset collection,
      final XYLineAndShapeRenderer renderer, int seriesIndex)
  {
    for (final Dataset dataset : datasets)
    {
      final Series series = helper.createSeries(dataset.getName());
      final Styling styling = dataset.getStyling();
      if (styling != null)
      {
        if (styling instanceof PlainStyling)
        {
          final PlainStyling ps = (PlainStyling) styling;
          renderer.setSeriesPaint(seriesIndex, ps.getColor());
        }
        else
        {
          System.err.println("Linear colors not implemented");
        }

        // legend visibility
        final boolean isInLegend = styling.isIncludeInLegend();
        renderer.setSeriesVisibleInLegend(seriesIndex, isInLegend);

        // line thickness
        // line style
        final LineType lineType = styling.getLineStyle();
        if (lineType != null)
        {
          final float thickness = (float) styling.getLineThickness();
          Stroke stroke;
          float[] pattern;
          switch (lineType)
          {
          case NONE:
            renderer.setSeriesLinesVisible(seriesIndex, false);
            break;
          case SOLID:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            stroke = new BasicStroke(thickness);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          case DOTTED:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            pattern = new float[]
            {3f, 3f};
            stroke =
                new BasicStroke(thickness, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, pattern, 0);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          case DASHED:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            pattern = new float[]
            {8.0f, 4.0f};
            stroke =
                new BasicStroke(thickness, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, pattern, 0);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          }
        }

        // marker size
        double size = styling.getMarkerSize();
        if (size == 0)
        {
          size = 2;// default
        }

        // marker style
        final MarkerStyle marker = styling.getMarkerStyle();
        if (marker != null)
        {
          switch (marker)
          {
          case NONE:
            renderer.setSeriesShapesVisible(seriesIndex, false);
            break;
          case SQUARE:
            renderer.setSeriesShape(seriesIndex, new Rectangle2D.Double(0, 0,
                size, size));
            break;
          case CIRCLE:
            renderer.setSeriesShape(seriesIndex, new Ellipse2D.Double(0, 0,
                size, size));
            break;
          case TRIANGLE:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createUpTriangle((float) size));
            break;
          case CROSS:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createRegularCross((float) size, (float) size));
            break;
          case DIAMOND:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createDiamond((float) size));
            break;
          default:
            renderer.setSeriesShapesVisible(seriesIndex, false);
          }
        }
        seriesIndex++;
      }
      helper.storeSeries(collection, series);

      // store the data in the collection
      populateCollection(helper, dataset, series);

      // also register as a listener
      final Adapter adapter = new AdapterImpl()
      {
        @Override
        public void notifyChanged(final Notification notification)
        {
          populateCollection(helper, dataset, series);
        }
      };
      dataset.eAdapters().add(adapter);
    }
  }

  /**
   * create a chart from chart object for preview
   * 
   * @param chart
   * 
   * @return
   */
  public static JFreeChart build(final Chart chart)
  {

    final IndependentAxis sharedAxisModel = chart.getParent().getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      final AxisType axisType = sharedAxisModel.getAxisType();
      if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.AngleAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.DateAxis)
      {
        helper = new DateHelper();
      }
      else
      {
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        helper = new NumberHelper();
      }
      sharedAxis = helper.createAxis(sharedAxisModel.getName());
      if (sharedAxisModel.getDirection() == AxisDirection.DESCENDING)
      {
        sharedAxis.setInverted(true);
      }
    }
    sharedAxis.setVisible(false);
    final CombinedDomainXYPlot plot = new TimeBarPlot(sharedAxis);

    // create this chart
    final XYPlot subplot = createChart(sharedAxisModel, chart);

    // add chart to stack
    plot.add(subplot);

    final JFreeChart jFreeChart = new JFreeChart(plot);
    jFreeChart.getLegend().setVisible(false);
    return jFreeChart;
  }

  /**
   * create a chart from our dataset
   * 
   * @param chartsSet
   * 
   * @return
   */
  public static JFreeChart build(final ChartSet chartsSet)
  {
    final IndependentAxis sharedAxisModel = chartsSet.getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      final AxisType axisType = sharedAxisModel.getAxisType();
      if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.AngleAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.DateAxis)
      {
        helper = new DateHelper();
      }
      else
      {
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        helper = new NumberHelper();
      }
      sharedAxis = helper.createAxis(sharedAxisModel.getName());
      if (sharedAxisModel.getDirection() == AxisDirection.DESCENDING)
      {
        sharedAxis.setInverted(true);
      }
    }

    final CombinedDomainXYPlot plot = new TimeBarPlot(sharedAxis);

    // now loop through the charts
    final EList<Chart> charts = chartsSet.getCharts();
    for (final Chart chart : charts)
    {
      // create this chart
      final XYPlot subplot = createChart(sharedAxisModel, chart);

      // add chart to stack
      plot.add(subplot);
    }

    plot.setGap(5.0);
    plot.setOrientation(chartsSet.getOrientation() == Orientation.VERTICAL
        ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL);

    return new JFreeChart(plot);
  }

  protected static XYPlot createChart(final IndependentAxis sharedAxisModel,
      final Chart chart)
  {
    final XYPlot subplot = new XYPlot(null, null, null, null);

    // keep track of how many axes we create
    int indexAxis = 0;

    // min axis create on bottom or left
    final EList<DependentAxis> minAxes = chart.getMinAxes();
    for (final DependentAxis axis : minAxes)
    {
      createDependentAxis(subplot, indexAxis, axis);
      subplot.setRangeAxisLocation(indexAxis, AxisLocation.BOTTOM_OR_LEFT);
      indexAxis++;
    }

    // max axis create on top or right
    final EList<DependentAxis> maxAxes = chart.getMaxAxes();
    for (final DependentAxis axis : maxAxes)
    {
      createDependentAxis(subplot, indexAxis, axis);
      subplot.setRangeAxisLocation(indexAxis, AxisLocation.TOP_OR_RIGHT);
      indexAxis++;
    }

    if (sharedAxisModel != null)
    {
      // build selective annotations to plot
      final EList<SelectiveAnnotation> selectiveAnnotations =
          sharedAxisModel.getAnnotations();

      final List<AbstractAnnotation> annotations = new ArrayList<>();
      for (final SelectiveAnnotation selectiveAnnotation : selectiveAnnotations)
      {
        final EList<Chart> appearsIn = selectiveAnnotation.getAppearsIn();
        // check selective option to see is this applicable to current chart
        if (appearsIn == null || appearsIn.isEmpty()
            || appearsIn.contains(chart))
        {
          annotations.add(selectiveAnnotation.getAnnotation());
        }
      }
      addAnnotationToPlot(subplot, annotations, false);
    }

    // TODO: sort out how to position this title
    // XYTitleAnnotation title = new XYTitleAnnotation(0, 0, new TextTitle(chart.getName()));
    // subplot.addAnnotation(title);

    return subplot;
  }

  /**
   * 
   * @param subplot
   *          target plot for index
   * @param indexAxis
   *          index of new axis
   * @param dependentAxis
   *          model object of DependentAxis
   */
  private static void createDependentAxis(final XYPlot subplot,
      final int indexAxis, final DependentAxis dependentAxis)
  {
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setDrawSeriesLineAsPath(true);
    final int indexSeries = 0;
    final ChartHelper axeshelper;

    final AxisType axisType = dependentAxis.getAxisType();
    if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
    {
      axeshelper = new NumberHelper();
    }
    else if (axisType instanceof info.limpet.stackedcharts.model.AngleAxis)
    {
      axeshelper = new NumberHelper();
    }
    else if (axisType instanceof info.limpet.stackedcharts.model.DateAxis)
    {
      axeshelper = new DateHelper();
    }
    else
    {
      System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
      axeshelper = new NumberHelper();
    }
    final XYDataset collection = axeshelper.createCollection();

    final ValueAxis chartAxis = new NumberAxis(dependentAxis.getName());

    if (dependentAxis.getDirection() == AxisDirection.DESCENDING)
    {
      chartAxis.setInverted(true);
    }
    addDatasetToAxis(axeshelper, dependentAxis.getDatasets(), collection,
        renderer, indexSeries);

    final EList<AbstractAnnotation> annotations =
        dependentAxis.getAnnotations();
    addAnnotationToPlot(subplot, annotations, true);
    subplot.setDataset(indexAxis, collection);
    subplot.setRangeAxis(indexAxis, chartAxis);
    subplot.setRenderer(indexAxis, renderer);
    subplot.mapDatasetToRangeAxis(indexAxis, indexAxis);
  }

  protected static void populateCollection(final ChartHelper helper,
      final Dataset dataset, final Series series)
  {
    helper.clear(series);
    final EList<DataItem> measurements = dataset.getMeasurements();
    for (final DataItem dataItem : measurements)
    {
      helper.addItem(series, dataItem);
    }
  }

  private ChartBuilder()
  {
  }
}
