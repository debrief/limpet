package info.limpet.stackedcharts.core.view;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.Zone;

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
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartBuilder
{

  private final ChartSet chartsSet;

  public ChartBuilder(ChartSet chartsSet)
  {
    this.chartsSet = chartsSet;

  }

  /** helper class that can handle either temporal or non-temporal datasets
   * 
   * @author ian
   *
   */
  private static interface ChartHelper
  {
    /** create the correct type of axis
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
      cc.addSeries((XYSeries)series);
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
      cc.addSeries((TimeSeries)series);
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
      // TODO - we have to build up multiple axes, according to the minAxes
      // & maxAxes  
      //
      // so, we don't just create one chartAxis. We will loop through the 
      // minAxes & maxAxes, and we will create JFreeChart axes on the left/right
      // side as necessary
      //
      // we also need to tell JFreeChart which dataset (series) goes on which axis.
      //
      // But, we don't have a plot object yet.  So, I think we'll have to build
      // up a list of axes, then add them to the plot at the end.
      
      final XYItemRenderer renderer = new StandardXYItemRenderer();
      final ValueAxis chartAxis = new NumberAxis(chart.getName());
      final XYDataset collection = helper.createCollection();
      EList<DependentAxis> minAxes = chart.getMinAxes();
      for (DependentAxis dependentAxis : minAxes)
      {
        buildAxis(helper, dependentAxis, collection);
      }
      
      EList<DependentAxis> maxAxes = chart.getMaxAxes();
      for (DependentAxis dependentAxis : maxAxes)
      {
        buildAxis(helper, dependentAxis, collection);
      }

      final XYPlot subplot = new XYPlot(collection, null, chartAxis, renderer);
      subplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
      plot.add(subplot);
    }

    plot.setGap(5.0);
    plot.setOrientation(chartsSet.getOrientation() == Orientation.VERTICAL
        ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL);

    return new JFreeChart(plot);
  }

  private void buildAxis(ChartHelper helper, DependentAxis axis, XYDataset collection)
  {
    EList<Dataset> datasets = axis.getDatasets();
    for (Dataset dataset : datasets)
    {
      final Series series = helper.createSeries(axis.getName());
      helper.storeSeries(collection, series);
      EList<DataItem> measurements = dataset.getMeasurements();
      for (DataItem dataItem : measurements)
      {
        helper.addItem(series,  dataItem);
      }
    }
  }
  
  
   static ChartSet createDummyModel()
  {
    StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

    ChartSet chartsSet = factory.createChartSet();
    chartsSet.setOrientation(Orientation.VERTICAL);

    // set the common x axis
    IndependentAxis depthAxis = factory.createIndependentAxis();
    depthAxis.setName("Depth");
    chartsSet.setSharedAxis(depthAxis);

    // first chart
    Chart chart1 = factory.createChart();
    chart1.setName("Temperature & Salinity");
    chartsSet.getCharts().add(chart1);

    DependentAxis yAxis1 = factory.createDependentAxis();
    yAxis1.setName("Temperature");
    chart1.getMinAxes().add(yAxis1);

    Dataset temperatureVsDepth1 = factory.createDataset();
    temperatureVsDepth1.setName("Temp vs Depth");
    yAxis1.getDatasets().add(temperatureVsDepth1);

    DataItem item1 = factory.createDataItem();
    item1.setIndependentVal(1000);
    item1.setDependentVal(1030);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(2000);
    item1.setDependentVal(2050);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(3000);
    item1.setDependentVal(3060);
    temperatureVsDepth1.getMeasurements().add(item1);
    
    // second axis/dataset on this first graph
    DependentAxis yAxis2 = factory.createDependentAxis();
    yAxis2.setName("Salinity");
    chart1.getMaxAxes().add(yAxis2);

    Dataset salinityVsDepth1 = factory.createDataset();
    salinityVsDepth1.setName("Salinity Vs Depth");
    yAxis2.getDatasets().add(salinityVsDepth1);


    item1 = factory.createDataItem();
    item1.setIndependentVal(1000);
    item1.setDependentVal(3000);
    salinityVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(2000);
    item1.setDependentVal(5000);
    salinityVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(3000);
    item1.setDependentVal(9000);
    salinityVsDepth1.getMeasurements().add(item1);
    
    // create a second chart
    // first chart
    Chart chart = factory.createChart();
    chart.setName("Pressure Gradient");
    chartsSet.getCharts().add(chart);
    
    // have a go at an annotation on the x axis
    IndependentAxis shared = chartsSet.getSharedAxis();
    Marker marker = factory.createMarker();
    marker.setValue(1200);
    marker.setName("A marker");
    SelectiveAnnotation sel = factory.createSelectiveAnnotation();
    sel.setAnnotation(marker);
    shared.getAnnotations().add(sel);
    Zone zone = factory.createZone();
    zone.setStart(2100);
    zone.setEnd(2500);
    zone.setName("A Zone");    
    sel = factory.createSelectiveAnnotation();
    sel.setAnnotation(zone);
    shared.getAnnotations().add(sel);

    DependentAxis yAxis = factory.createDependentAxis();
    yAxis.setName("Pressure");
    chart.getMinAxes().add(yAxis);

    Dataset pressureVsDepth = factory.createDataset();
    pressureVsDepth.setName("Pressure vs Depth");
    yAxis.getDatasets().add(pressureVsDepth);

    DataItem item = factory.createDataItem();
    item.setIndependentVal(1000);
    item.setDependentVal(400);
    pressureVsDepth.getMeasurements().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(2000);
    item.setDependentVal(500);
    pressureVsDepth.getMeasurements().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(3000);
    item.setDependentVal(100);
    pressureVsDepth.getMeasurements().add(item);
    
    return chartsSet;
  }

}
