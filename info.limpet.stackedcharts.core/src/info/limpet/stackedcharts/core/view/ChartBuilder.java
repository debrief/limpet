package info.limpet.stackedcharts.core.view;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.Orientation;
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
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class ChartBuilder
{

  private final ChartSet chartsSet;

  public ChartBuilder(ChartSet chartsSet)
  {
    this.chartsSet = chartsSet;

  }

  public JFreeChart build()
  {

    IndependentAxis sharedAxis = chartsSet.getSharedAxis();
    final CombinedDomainXYPlot plot;

    if (sharedAxis == null)
    {
      DateAxis parentAxis = new DateAxis("Time");
      plot = new CombinedDomainXYPlot(parentAxis);
    }
    else
    {
      final ValueAxis axis;
      switch (sharedAxis.getAxisType())
      {
      case NUMBER:
        axis = new NumberAxis(sharedAxis.getName());
        break;
      case TIME:
        axis = new DateAxis(sharedAxis.getName());
        break;
      default:
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        axis = null;
      }

      plot = new CombinedDomainXYPlot(axis);
    }
    EList<Chart> charts = chartsSet.getCharts();
    for (Chart chart : charts)
    {
      // sub plots
      
      // TODO - we have to build up multiple axes, according to the minAxes
      // & maxAxes.  See the above code that creates the correct axis type
      // for the axis in the model

      final XYItemRenderer renderer = new StandardXYItemRenderer();
      final NumberAxis chartAxis = new NumberAxis(chart.getName());
      final TimeSeriesCollection collection = new TimeSeriesCollection();
      EList<DependentAxis> minAxes = chart.getMinAxes();
      for (DependentAxis dependentAxis : minAxes)
      {
        final TimeSeries series = new TimeSeries(dependentAxis.getName());
        collection.addSeries(series);
        buildAxsis(dependentAxis, series);
      }
      
      EList<DependentAxis> maxAxes = chart.getMaxAxes();
      for (DependentAxis dependentAxis : maxAxes)
      {
        final TimeSeries series = new TimeSeries(dependentAxis.getName());
        collection.addSeries(series);
        buildAxsis(dependentAxis, series);
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

  private void buildAxsis(DependentAxis dependentAxis, final TimeSeries series)
  {
    EList<Dataset> datasets = dependentAxis.getDatasets();
    for (Dataset dataset : datasets)
    {
      EList<DataItem> measurements = dataset.getMeasurements();
      for (DataItem dataItem : measurements)
      {
        // sort out the time
        Millisecond time = new Millisecond(new Date((long)dataItem.getIndependentVal()));
        
        series
            .add(time, dataItem.getDependentVal());
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
    chartsSet.getDatasets().add(temperatureVsDepth1);

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
    chartsSet.getDatasets().add(salinityVsDepth1);


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
    shared.getAnnotations().add(marker);
    Zone zone = factory.createZone();
    zone.setStart(2100);
    zone.setEnd(2500);
    zone.setName("A Zone");    
    shared.getAnnotations().add(zone);

    DependentAxis yAxis = factory.createDependentAxis();
    yAxis.setName("Pressure");
    chart.getMinAxes().add(yAxis);

    Dataset pressureVsDepth = factory.createDataset();
    pressureVsDepth.setName("Pressure vs Depth");
    yAxis.getDatasets().add(pressureVsDepth);
    chartsSet.getDatasets().add(pressureVsDepth);

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
