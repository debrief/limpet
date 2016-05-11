package info.limpet.stackedcharts.model.tests;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Test;

import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisOrigin;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * A JUnit Plug-in Test to demonstrate basic EMF operations, such as model manipulaton, persistnce
 * and event handling
 */
public class ModelTests
{

  @Test
  public void testReadModel()
  {
    URI resourceURI = URI.createFileURI("testRead.stackedcharts");
    Resource resource = new ResourceSetImpl().createResource(resourceURI);
    try
    {
      resource.load(new HashMap<>());
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Assert.fail("Could not read model: " + e.getMessage());
    }
    ChartSet chartsSet = (ChartSet) resource.getContents().get(0);

    Assert.assertNotNull(chartsSet);
    Assert.assertEquals(2, chartsSet.getCharts().size());

    Chart chart = chartsSet.getCharts().get(0);
    Assert.assertEquals("Temperature & Salinity", chart.getName());
    
    // have a look at the innads
    EList<DependentAxis> axes = chart.getAxes();
    Assert.assertEquals("Correct number",  2, axes.size());
    DependentAxis axis1 = axes.get(0);
    Assert.assertEquals("correct name", "Temperature", axis1.getName());
    Assert.assertEquals("correct origin", AxisOrigin.MIN, axis1.getAxisOrigin());
    Assert.assertEquals("correct direction", AxisDirection.ASCENDING, axis1.getDirection());
    DependentAxis axis2 = axes.get(1);
    Assert.assertEquals("correct name", "Salinity", axis2.getName());
    Assert.assertEquals("correct origin", AxisOrigin.MAX, axis2.getAxisOrigin());

  }

  @Test
  public void testWriteModel()
  {
    ChartSet chartsSet = createModel();
    URI resourceURI = URI.createFileURI("testWrite.stackedcharts");
    Resource resource = new ResourceSetImpl().createResource(resourceURI);
    resource.getContents().add(chartsSet);
    try
    {
      resource.save(null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Assert.fail("Could not write model: " + e.getMessage());
    }
  }

  @Test
  public void testNotifications()
  {
    Chart chart = createModel().getCharts().get(0);

    ChartNameChangeListener listener = new ChartNameChangeListener();
    chart.eAdapters().add(listener);

    Assert.assertFalse(listener.isNotified());

    chart.setName("Changed");
    Assert.assertTrue(listener.isNotified());
  }

  private ChartSet createModel()
  {
    StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

    ChartSet chartsSet = factory.createChartSet();

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
    chart1.getAxes().add(yAxis1);

    Dataset temperatureVsDepth1 = factory.createDataset();
    temperatureVsDepth1.setName("Temp vs Depth");
    yAxis1.getDatasets().add(temperatureVsDepth1);
    chartsSet.getDatasets().add(temperatureVsDepth1);

    DataItem item1 = factory.createDataItem();
    item1.setIndependentVal(1000);
    item1.setDependentVal(30);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(2000);
    item1.setDependentVal(50);
    temperatureVsDepth1.getMeasurements().add(item1);

    item1 = factory.createDataItem();
    item1.setIndependentVal(3000);
    item1.setDependentVal(60);
    temperatureVsDepth1.getMeasurements().add(item1);
    
    // second axis/dataset on this first graph
    DependentAxis yAxis2 = factory.createDependentAxis();
    yAxis2.setName("Salinity");
    yAxis2.setAxisOrigin(AxisOrigin.MAX);
    chart1.getAxes().add(yAxis2);

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

    DependentAxis yAxis = factory.createDependentAxis();
    yAxis.setName("Pressure");
    chart.getAxes().add(yAxis);

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

  /**
   * Helper class to test notifications
   */
  private static class ChartNameChangeListener implements Adapter
  {

    private Notifier notifier;
    private boolean notified;

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.CHART__NAME:
        notified = true;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return notifier;
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      this.notifier = newTarget;
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return ChartSet.class.equals(type);
    }

    public boolean isNotified()
    {
      return notified;
    }

  }

}
