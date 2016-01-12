package info.limpet.rcp;

import javax.measure.Measure;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import info.limpet.QuantityRange;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.SpeedMSec;
import info.limpet.rcp.data_provider.data.PropertyTypeHandler;
import info.limpet.rcp.data_provider.data.ReflectivePropertySource;
import junit.framework.TestCase;

/**
 * Test setting/getting properties of a {@link SpeedMSec} object via
 * {@link ReflectivePropertySource} .
 */
public class TestReflectivePropertySourceSpeedMSec extends TestCase
{

  private SpeedMSec testData;
  private ReflectivePropertySource propertySource;

  @Override
  protected void setUp() throws Exception
  {
    testData = new SpeedMSec();

    Unit<Velocity> unit = testData.getUnits();
    Measure<Double, Velocity> min = Measure.valueOf(0d, unit);
    Measure<Double, Velocity> max = Measure.valueOf(100d, unit);
    testData.setRange(new QuantityRange<Velocity>(min, max));
    testData.add(1);

    propertySource = new ReflectivePropertySource(testData);
    propertySource.getPropertyDescriptors();

  }

  public void testGetValue()
  {
    Object propertyValue = propertySource.getPropertyValue("units");
    assertEquals(testData.getUnits(), propertyValue);

    propertyValue = propertySource.getPropertyValue("range");

    Object cellEditorValue = PropertyTypeHandler.QUANTITY_RANGE
        .toCellEditorValue(testData.getRange(), testData);
    assertEquals(cellEditorValue, propertyValue);

    propertyValue = propertySource.getPropertyValue("singletonValue");
    assertEquals("1.0", propertyValue);
  }

  public void testSetValue()
  {
    propertySource.setPropertyValue("range", "10 : 50");
    Measure<Double, Velocity> min = (Measure<Double, Velocity>) testData
        .getRange().getMinimum();
    Measure<Double, Velocity> max = (Measure<Double, Velocity>) testData
        .getRange().getMaximum();
    assertEquals(10, min.intValue(min.getUnit()));
    assertEquals(50, max.intValue(max.getUnit()));

    propertySource.setPropertyValue("singletonValue", "2.0");
    assertEquals(2.0, testData.getSingletonValue());
  }

  public void testPropertyNotVisible()
  {
    // the test data is not singleton collection anymore
    testData.add(2);
    IPropertyDescriptor[] descriptors = new ReflectivePropertySource(testData)
        .getPropertyDescriptors();
    for (IPropertyDescriptor descriptor : descriptors)
    {
      if ("range".equals(descriptor.getId()) || "singletonValue".equals(
          descriptor.getId()))
      {
        fail(
            "Range/singletonValue property should only be visible for singleton collections");
      }
    }
  }

}
