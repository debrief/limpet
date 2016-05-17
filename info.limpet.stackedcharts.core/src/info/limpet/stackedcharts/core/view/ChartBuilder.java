package info.limpet.stackedcharts.core.view;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.Styling;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
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
      switch (sharedAxisModel.getAxisType())
      {
      case NUMBER:
        helper = new NumberHelper();
        break;
      case TIME:
        helper = new TimeHelper();
        break;
      default:
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        helper = new NumberHelper();
      }
      sharedAxis = helper.createAxis(sharedAxisModel.getName());
    }

    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(sharedAxis);

    // now loop through the charts
    EList<Chart> charts = chartsSet.getCharts();
    for (final Chart chart : charts)
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

        List<AbstractAnnotation> annotations =
            new ArrayList<>(selectiveAnnotations.size());
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
        addAnnotationToPlot(subplot, annotations,false);

      }

      // add chart to stack
      plot.add(subplot);
    }

    plot.setGap(5.0);
    plot.setOrientation(chartsSet.getOrientation() == Orientation.VERTICAL
        ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL);

    return new JFreeChart(plot);
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
    int indexSeries = 0;
    ChartHelper axeshelper = null;
    switch (dependentAxis.getAxisType())
    {
    case NUMBER:
      axeshelper = new NumberHelper();
      break;
    case TIME:
      axeshelper = new TimeHelper();
      break;
    default:
      System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
      axeshelper = new NumberHelper();
    }
    final XYDataset collection = axeshelper.createCollection();

    final ValueAxis chartAxis = new NumberAxis(dependentAxis.getName());
    addDatasetToAxis(axeshelper, dependentAxis.getDatasets(), collection,
        renderer, indexSeries);

    EList<AbstractAnnotation> annotations = dependentAxis.getAnnotations();
    addAnnotationToPlot(subplot, annotations,true);
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
    for (AbstractAnnotation annotation : annotations)
    {

      // convert hex Color to awt
      final String hexColor = annotation.getColor();

      Color awtColor = null;
      if (hexColor != null)
      {
        awtColor = hex2Rgb(hexColor);
      }

      // build value Marker
      if (annotation instanceof info.limpet.stackedcharts.model.Marker)
      {
        info.limpet.stackedcharts.model.Marker marker =
            (info.limpet.stackedcharts.model.Marker) annotation;

        Marker mrk = new ValueMarker(marker.getValue());
        mrk.setLabel(annotation.getName());

        mrk.setPaint(awtColor == null ? Color.GRAY : awtColor);

        // move Text Anchor
        mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        mrk.setLabelAnchor(RectangleAnchor.TOP);

        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
        if(isRangeAnnotation)
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        else
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
      }
      // build Zone
      else if (annotation instanceof info.limpet.stackedcharts.model.Zone)
      {
        info.limpet.stackedcharts.model.Zone zone =
            (info.limpet.stackedcharts.model.Zone) annotation;

        Marker mrk = new IntervalMarker(zone.getStart(), zone.getEnd());
        mrk.setLabel(annotation.getName());

        if (awtColor != null)
          mrk.setPaint(awtColor);

        // move Text & Label Anchor
        mrk.setLabelTextAnchor(TextAnchor.CENTER);
        mrk.setLabelAnchor(RectangleAnchor.CENTER);
        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));

        if(isRangeAnnotation)
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        else
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
      }

      // TODO: ScatterSet Marker impl in JfreeChart

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
    for (Dataset dataset : datasets)
    {
      final Series series = helper.createSeries(dataset.getName());
      final Styling styling = dataset.getStyling();
      if (styling != null)
      {
        if (styling instanceof PlainStyling)
        {
          final PlainStyling ps = (PlainStyling) styling;
          final String hexColor = ps.getColor();

          Color thisColor = null;
          if (hexColor != null)
          {
            thisColor = hex2Rgb(hexColor);
          }
          renderer.setSeriesPaint(seriesIndex, thisColor);
        }
        else
        {
          System.err.println("Linear colors not implemented");
        }

        double size = styling.getMarkerSize();
        if (size == 0)
        {
          size = 2;// default
        }

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
      EList<DataItem> measurements = dataset.getMeasurements();
      for (DataItem dataItem : measurements)
      {
        helper.addItem(series, dataItem);
      }
    }
  }

  /**
   * 
   * @param colorStr
   *          e.g. "#FFFFFF"
   * @return awt.Color Object
   */
  private static Color hex2Rgb(String colorStr)
  {
    return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer
        .valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr
        .substring(5, 7), 16));
  }
}
