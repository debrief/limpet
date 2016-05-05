package info.limpet.stackedcharts.model.tests;

import info.limpet.stackedcharts.model.Axis;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertEquals(1, chartsSet.getCharts().size());

    Chart chart = chartsSet.getCharts().get(0);
    Assert.assertEquals("Geothermal Gradient", chart.getName());

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
    Chart chart = factory.createChart();
    chart.setName("Geothermal Gradient");
    chartsSet.getCharts().add(chart);

    Axis xAxis = factory.createAxis();
    xAxis.setName("Depth");
    chart.getAxes().add(xAxis);

    Axis yAxis = factory.createAxis();
    yAxis.setName("Temperature");
    chart.getAxes().add(yAxis);

    Dataset temperatureVsDepth = factory.createDataset();
    temperatureVsDepth.setAxis(yAxis);
    chart.getDatasets().add(temperatureVsDepth);

    DataItem item = factory.createDataItem();
    item.setIndependentVal(1000);
    item.setDependentVal(30);
    temperatureVsDepth.getItems().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(2000);
    item.setDependentVal(50);
    temperatureVsDepth.getItems().add(item);

    item = factory.createDataItem();
    item.setIndependentVal(3000);
    item.setDependentVal(70);
    temperatureVsDepth.getItems().add(item);

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
