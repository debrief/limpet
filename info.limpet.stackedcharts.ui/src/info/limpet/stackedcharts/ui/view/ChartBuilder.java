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
import java.util.Iterator;
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

    public TimeBarPlot(ValueAxis sharedAxis)
    {
      super(sharedAxis);
      _markerFont = new Font("Arial", Font.PLAIN, 8);
    }

    public void setTime(Date time)
    {
      _currentTime = time;
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

        final Axis axis = this.getDomainAxis();

        if (_showLine)
        {
          plotStepperLine(g2, dataArea, vertical, axis, theTime);
        }

        // ok, have a got at the time values
        if (_showLabels)
        {
          plotStepperMarkers(g2, dataArea, vertical, axis, theTime, renderInfo);
        }
      }
    }

    protected void plotStepperMarkers(final Graphics2D g2,
        final Rectangle2D dataArea, final boolean vertical,
        final Axis axis, final long theTime, final PlotRenderingInfo info)
    {
      // ok, loop through the charts
      final CombinedDomainXYPlot comb = this;
      @SuppressWarnings("unchecked")
      List<XYPlot> plots = comb.getSubplots();

      NumberFormat oneDP = new DecimalFormat("0.0");
      NumberFormat noDP = new DecimalFormat("0");

      // keep track of how many series we've used for each renderer
      Map<XYItemRenderer, Integer> seriesCounter =
          new HashMap<XYItemRenderer, Integer>();

      // what's the current time?
      double tNow = _currentTime.getTime();

      int ctr = 0;

      for (XYPlot plot : plots)
      {
        final int numC = plot.getDatasetCount();

        // ok, get the area for this subplot
        final Rectangle2D thisPlotArea =
            info.getSubplotInfo(ctr++).getPlotArea();

        for (int i = 0; i < numC; i++)
        {
          XYDataset thisD = plot.getDataset(i);

          final NumberAxis rangeA = (NumberAxis) plot.getRangeAxisForDataset(i);

          if (thisD instanceof XYSeriesCollection)
          {
            XYSeriesCollection coll = (XYSeriesCollection) thisD;
            final int num = coll.getSeriesCount();
            for (int j = 0; j < num; j++)
            {
              XYSeries thisS = coll.getSeries(j);

              // find the value at this time
              // System.out.println("this series:" + thisS.getKey());

              // ok, find the time value nearest to now
              @SuppressWarnings("unchecked")
              List<XYDataItem> items = thisS.getItems();

              Double previousValue = null;
              Double nextValue = null;
              Double previousTime = null;
              Double nextTime = null;

              for (Iterator<XYDataItem> iterator = items.iterator(); iterator
                  .hasNext();)
              {
                XYDataItem thisItem = (XYDataItem) iterator.next();

                if (thisItem.getXValue() < tNow)
                {
                  // ok, it's before use the previous
                  previousValue = thisItem.getYValue();
                  previousTime = thisItem.getXValue();
                }
                else
                {
                  nextValue = thisItem.getYValue();
                  nextTime = thisItem.getXValue();
                  break;
                }

              }

              if (previousValue != null && nextValue != null)
              {
                if (axis instanceof DateAxis)
                {

                  // ok, interpolate the time & date
                  double proportion =
                      (theTime - previousTime) / (nextTime - previousTime);

                  double nearest =
                      previousValue + proportion * (nextValue - previousValue);

                  DateAxis dateAxis = (DateAxis) axis;

                  // find the new x value
                  double markerX =
                      dateAxis.dateToJava2D(new Date(theTime), dataArea, this
                          .getDomainAxisEdge());

                  // find the new x value
                  // Double markerY = rangeA..dateToJava2D(new Date(theTime),
                  // dataArea, this.getDomainAxisEdge());

                  double markerY =
                      rangeA.valueToJava2D(nearest, thisPlotArea, this
                          .getRangeAxisEdge());

                  // prepare the label
                  final String label;
                  if (nearest > 100)
                  {
                    label = noDP.format(nearest);
                  }
                  else
                  {
                    label = oneDP.format(nearest);
                  }

                  // store old font
                  final Font oldFont = g2.getFont();

                  // set the new one
                  g2.setFont(_markerFont);

                  // reflect the plot orientation
                  final float xPos;
                  final float yPos;
                  if (vertical)
                  {
                    yPos = 2f + (float) markerX;
                    xPos = (float) markerY;
                  }
                  else
                  {
                    xPos = 4f + (float) markerX;
                    yPos = 2f + (float) markerY;
                  }

                  // find the size of the label, so we can draw a background
                  FontMetrics fc = g2.getFontMetrics();
                  Rectangle2D bounds = fc.getStringBounds(label, g2);
                  g2.setColor(Color.white);
                  g2.fill3DRect((int) yPos, (int) (xPos - bounds.getHeight()),
                      3 + (int) bounds.getWidth(),
                      3 + (int) bounds.getHeight(), true);

                  // and the label itself
                  final XYItemRenderer renderer =
                      plot.getRendererForDataset(thisD);

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
                  g2.setColor(paint.darker());
                  g2.drawString(label, yPos, xPos);

                  // and restore the font
                  g2.setFont(oldFont);
                }

              }

            }
          }
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
    protected void
        plotStepperLine(final Graphics2D g2,
            final Rectangle2D dataArea, boolean vertical, Axis axis, long theTime)
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
  }

  private ChartBuilder()
  {
  }

  /**
   * helper class that can handle either temporal or non-temporal datasets
   * 
   * @author ian
   * 
   */
  private static interface ChartHelper
  {
    /**
     * create the correct type of axis
     * 
     * @param name
     * @return
     */
    ValueAxis createAxis(String name);

    /**
     * add this item to this series
     * 
     * @param series
     * @param item
     */
    void addItem(Series series, DataItem item);

    /**
     * create a series with the specified name
     * 
     * @param name
     * @return
     */
    Series createSeries(String name);

    /**
     * create a new collection of datasets (seroes)
     * 
     * @return
     */
    XYDataset createCollection();

    /**
     * put the series into the collection
     * 
     * @param collection
     * @param series
     */
    void storeSeries(XYDataset collection, Series series);

    /**
     * clear the contents of the series
     * 
     */
    void clear(Series series);
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
    public ValueAxis createAxis(String name)
    {
      return new NumberAxis(name);
    }

    @Override
    public void addItem(Series series, DataItem item)
    {
      XYSeries ns = (XYSeries) series;
      ns.add(item.getIndependentVal(), item.getDependentVal());
    }

    @Override
    public Series createSeries(String name)
    {
      return new XYSeries(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new XYSeriesCollection();
    }

    @Override
    public void storeSeries(XYDataset collection, Series series)
    {
      XYSeriesCollection cc = (XYSeriesCollection) collection;
      cc.addSeries((XYSeries) series);
    }

    @Override
    public void clear(Series series)
    {
      XYSeries ns = (XYSeries) series;
      ns.clear();
    }

  }

  /**
   * support generation of a stacked chart with a shared time axis
   * 
   * @author ian
   * 
   */
  private static class TimeHelper implements ChartHelper
  {

    @Override
    public ValueAxis createAxis(String name)
    {
      return new DateAxis(name);
    }

    @Override
    public void addItem(Series series, DataItem item)
    {
      TimeSeries ns = (TimeSeries) series;
      long time = (long) item.getIndependentVal();
      Millisecond milli = new Millisecond(new Date(time));
      ns.add(milli, item.getDependentVal());
    }

    @Override
    public Series createSeries(String name)
    {
      return new TimeSeries(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new TimeSeriesCollection();
    }

    @Override
    public void storeSeries(XYDataset collection, Series series)
    {
      TimeSeriesCollection cc = (TimeSeriesCollection) collection;
      cc.addSeries((TimeSeries) series);
    }

    @Override
    public void clear(Series series)
    {
      TimeSeries ns = (TimeSeries) series;
      ns.clear();
    }
  }

  /**
   * create a chart from our dataset
   * 
   * @param chartsSet
   * 
   * @return
   */
  public static JFreeChart build(ChartSet chartsSet)
  {
    IndependentAxis sharedAxisModel = chartsSet.getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      AxisType axisType = sharedAxisModel.getAxisType();
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
        helper = new TimeHelper();
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
    EList<Chart> charts = chartsSet.getCharts();
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

  /**
   * create a chart from chart object for preview
   * 
   * @param chart
   * 
   * @return
   */
  public static JFreeChart build(Chart chart)
  {

    IndependentAxis sharedAxisModel = chart.getParent().getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      AxisType axisType = sharedAxisModel.getAxisType();
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
        helper = new TimeHelper();
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

    JFreeChart jFreeChart = new JFreeChart(plot);
    jFreeChart.getLegend().setVisible(false);
    return jFreeChart;
  }

  protected static XYPlot createChart(IndependentAxis sharedAxisModel,
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
      EList<SelectiveAnnotation> selectiveAnnotations =
          sharedAxisModel.getAnnotations();

      List<AbstractAnnotation> annotations = new ArrayList<>();
      for (SelectiveAnnotation selectiveAnnotation : selectiveAnnotations)
      {
        EList<Chart> appearsIn = selectiveAnnotation.getAppearsIn();
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
  private static void createDependentAxis(final XYPlot subplot, int indexAxis,
      DependentAxis dependentAxis)
  {
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setDrawSeriesLineAsPath(true);
    int indexSeries = 0;
    final ChartHelper axeshelper;

    AxisType axisType = dependentAxis.getAxisType();
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
      axeshelper = new TimeHelper();
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

    EList<AbstractAnnotation> annotations = dependentAxis.getAnnotations();
    addAnnotationToPlot(subplot, annotations, true);
    subplot.setDataset(indexAxis, collection);
    subplot.setRangeAxis(indexAxis, chartAxis);
    subplot.setRenderer(indexAxis, renderer);
    subplot.mapDatasetToRangeAxis(indexAxis, indexAxis);
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
      final List<AbstractAnnotation> annotations, boolean isRangeAnnotation)
  {
    for (final AbstractAnnotation annotation : annotations)
    {
      Color color = annotation.getColor();

      if (annotation instanceof info.limpet.stackedcharts.model.Marker)
      {
        // build value Marker
        info.limpet.stackedcharts.model.Marker marker =
            (info.limpet.stackedcharts.model.Marker) annotation;

        Marker mrk = new ValueMarker(marker.getValue());
        mrk.setLabel(annotation.getName());

        mrk.setPaint(color == null ? Color.GRAY : color);

        // move Text Anchor
        mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        mrk.setLabelAnchor(RectangleAnchor.TOP);

        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
        if (isRangeAnnotation)
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        else
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.Zone)
      {
        // build Zone
        info.limpet.stackedcharts.model.Zone zone =
            (info.limpet.stackedcharts.model.Zone) annotation;

        Marker mrk = new IntervalMarker(zone.getStart(), zone.getEnd());
        mrk.setLabel(annotation.getName());

        if (color != null)
          mrk.setPaint(color);

        // move Text & Label Anchor
        mrk.setLabelTextAnchor(TextAnchor.CENTER);
        mrk.setLabelAnchor(RectangleAnchor.CENTER);
        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));

        if (isRangeAnnotation)
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        else
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.ScatterSet)
      {
        // build ScatterSet
        info.limpet.stackedcharts.model.ScatterSet marker =
            (info.limpet.stackedcharts.model.ScatterSet) annotation;

        EList<Datum> datums = marker.getDatums();
        boolean addLabel = true;
        for (Datum datum : datums)
        {
          Marker mrk = new ValueMarker(datum.getVal());
          // only add label for first Marker
          if (addLabel)
          {
            mrk.setLabel(annotation.getName());
            addLabel = false;
          }

          Color thisColor = datum.getColor();
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
            subplot.addRangeMarker(mrk, Layer.FOREGROUND);
          else
            subplot.addDomainMarker(mrk, Layer.FOREGROUND);
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
        LineType lineType = styling.getLineStyle();
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
      Adapter adapter = new AdapterImpl()
      {
        public void notifyChanged(Notification notification)
        {
          populateCollection(helper, dataset, series);
        }
      };
      dataset.eAdapters().add(adapter);
    }
  }

  protected static void populateCollection(final ChartHelper helper,
      Dataset dataset, final Series series)
  {
    helper.clear(series);
    EList<DataItem> measurements = dataset.getMeasurements();
    for (DataItem dataItem : measurements)
    {
      helper.addItem(series, dataItem);
    }
  }
}
