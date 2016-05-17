package info.limpet.stackedcharts.core.view;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.model.Zone;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class ChartBuilder
{

  private final ChartSet chartsSet;

  public ChartBuilder(ChartSet chartsSet)
  {
    this.chartsSet = chartsSet;
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

    void addItem(Series series, DataItem item);

    Series createSeries(String name);

    XYDataset createCollection();

    void storeSeries(XYDataset collection, Series series);
  }

  
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

  public JFreeChart build()
  {

    IndependentAxis sharedAxis = chartsSet.getSharedAxis();

    final ChartHelper helper;
    final ValueAxis axis;

    if (sharedAxis == null)
    {
      axis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      switch (sharedAxis.getAxisType())
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

      axis = helper.createAxis(sharedAxis.getName());
    }

    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(axis);

    EList<Chart> charts = chartsSet.getCharts();
    for (Chart chart : charts)
    {
      final XYPlot subplot = new XYPlot(null, null, null, null);

      int indexAxis = 0;

      // min axis create on bottom or left
      EList<DependentAxis> minAxes = chart.getMinAxes();
      for (DependentAxis dependentAxis : minAxes)
      {
        createAxis(subplot, indexAxis, dependentAxis);
        subplot.setRangeAxisLocation(indexAxis, AxisLocation.BOTTOM_OR_LEFT);
        indexAxis++;
      }

      // max axis create on top or right
      EList<DependentAxis> maxAxes = chart.getMaxAxes();
      for (DependentAxis dependentAxis : maxAxes)
      {
        createAxis(subplot, indexAxis, dependentAxis);
        subplot.setRangeAxisLocation(indexAxis, AxisLocation.TOP_OR_RIGHT);
        indexAxis++;
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
   * @param subplot plot object need to be genereated 
   * @param indexAxis  axis index to be created
   * @param dependentAxis model object of DependentAxis
   */
  private void createAxis(final XYPlot subplot, int indexAxis,
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
    buildAxisDataset(axeshelper, dependentAxis, collection, renderer,
        indexSeries);
    subplot.setDataset(indexAxis, collection);
    subplot.setRangeAxis(indexAxis, chartAxis);
    subplot.setRenderer(indexAxis, renderer);
    subplot.mapDatasetToRangeAxis(indexAxis, indexAxis);
  }

  /**
   * 
   * @param helper
   *          Helper object to map between axis types
   * @param axis
   * @param collection
   *          XYDataset need to be fill with Series
   * @param renderer
   *          axis renderer
   * @param index
   * 
   *          build axis dataset via adding series to Series & config renderer for styles
   */
  private void buildAxisDataset(ChartHelper helper, DependentAxis axis,
      XYDataset collection, XYLineAndShapeRenderer renderer, int index)
  {

    EList<Dataset> datasets = axis.getDatasets();
    for (Dataset dataset : datasets)
    {
      final Series series = helper.createSeries(dataset.getName());

      Styling styling = dataset.getStyling();
      if (styling != null)
      {
        if (styling instanceof PlainStyling)
        {
          PlainStyling ps = (PlainStyling) styling;
          String hexColor = ps.getColor();

          Color thisColor = null;

          if (hexColor != null)
          {
            thisColor = hex2Rgb(hexColor);
          }
          renderer.setSeriesPaint(index, thisColor);
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
        MarkerStyle marker = styling.getMarkerStyle();

        if (marker != null)
        {

          switch (marker)
          {
          case NONE:
            renderer.setSeriesShapesVisible(index, false);
            break;
          case SQUARE:

            renderer.setSeriesShape(index, new Rectangle2D.Double(0, 0, size,
                size));

            break;
          case CIRCLE:

            renderer.setSeriesShape(index, new Ellipse2D.Double(0, 0, size,
                size));
            break;
          case TRIANGLE:

            renderer.setSeriesShape(index, ShapeUtilities
                .createUpTriangle((float) size));
            break;
          case CROSS:
            renderer.setSeriesShape(index, ShapeUtilities.createRegularCross(
                (float) size, (float) size));
            break;
          case DIAMOND:
            renderer.setSeriesShape(index, ShapeUtilities
                .createDiamond((float) size));
            break;
          default:
            renderer.setSeriesShapesVisible(index, false);
          }
        }

        index++;

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
