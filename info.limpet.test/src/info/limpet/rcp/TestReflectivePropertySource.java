package info.limpet.rcp;

import info.limpet.IBaseQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.UIProperty;
import info.limpet.ui.data_provider.data.PropertyTypeHandler;
import info.limpet.ui.data_provider.data.ReflectivePropertySource;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

import junit.framework.TestCase;

public class TestReflectivePropertySource extends TestCase
{

  private TestData testData;
  private ReflectivePropertySource propertySource;

  @Override
  protected void setUp() throws Exception
  {
    testData = new TestData();
    testData.setName("annotated value");
    testData.setFlag(true);
    testData.setQuantity(12);

    Unit<Velocity> unit = Velocity.UNIT;
    testData.setUnits(unit);

    Measure<Double, Velocity> min = Measure.valueOf(0d, unit);
    Measure<Double, Velocity> max = Measure.valueOf(100d, unit);
    testData.setRange(new QuantityRange<Velocity>(min, max));

    propertySource = new ReflectivePropertySource(testData);
    propertySource.getPropertyDescriptors();

  }

  public void testGetValue()
  {
    Object propertyValue = propertySource.getPropertyValue(TestData.PROP_NAME);
    assertEquals("annotated value", propertyValue);

    propertyValue = propertySource.getPropertyValue(TestData.PROP_QUANTITY);
    assertEquals(12, propertyValue);

    propertyValue = propertySource.getPropertyValue(TestData.PROP_FLAG);
    assertEquals(true, propertyValue);

    propertyValue = propertySource.getPropertyValue(TestData.PROP_UNIT);
    assertEquals(Velocity.UNIT.toString(), propertyValue);

    propertyValue = propertySource.getPropertyValue(TestData.PROP_RANGE);

    Object cellEditorValue =
        PropertyTypeHandler.QUANTITY_RANGE.toCellEditorValue(testData
            .getRange(), testData);
    assertEquals(cellEditorValue, propertyValue);
  }

  public void testSetValue()
  {
    propertySource.setPropertyValue(TestData.PROP_NAME, "modified value");
    assertEquals("modified value", testData.getName());

    propertySource.setPropertyValue(TestData.PROP_QUANTITY, 50);
    assertEquals(50, testData.getQuantity());

    propertySource.setPropertyValue(TestData.PROP_FLAG, false);
    assertEquals(false, testData.getFlag());

    propertySource.setPropertyValue(TestData.PROP_UNIT, Temperature.UNIT);
    assertEquals(Temperature.UNIT, testData.getUnits());

    propertySource.setPropertyValue(TestData.PROP_UNIT, Temperature.UNIT);
    assertEquals(Temperature.UNIT, testData.getUnits());

    propertySource.setPropertyValue(TestData.PROP_RANGE, "10 : 50");
    Measure<Double, Velocity> min =
        (Measure<Double, Velocity>) testData.getRange().getMinimum();
    Measure<Double, Velocity> max =
        (Measure<Double, Velocity>) testData.getRange().getMaximum();
    assertEquals(10, min.intValue(min.getUnit()));
    assertEquals(50, max.intValue(max.getUnit()));

  }

  public void testResetValue()
  {
    propertySource.resetPropertyValue(TestData.PROP_NAME);
    assertEquals("default name", testData.getName());

    propertySource.resetPropertyValue(TestData.PROP_QUANTITY);
    assertEquals(50, testData.getQuantity());

    testData.setFlag(false);
    propertySource.resetPropertyValue(TestData.PROP_FLAG);
    assertEquals(true, testData.getFlag());

    propertySource.resetPropertyValue(TestData.PROP_UNIT);
    assertEquals(Unit.ONE, testData.getUnits());
  }

  public static class TestData implements IBaseQuantityCollection<Velocity>
  {

    public static final String PROP_NAME = "name";
    public static final String PROP_QUANTITY = "quantity";
    public static final String PROP_FLAG = "flag";
    public static final String PROP_UNIT = "units";
    public static final String PROP_RANGE = "range";

    private String name;
    private QuantityRange<Velocity> range;
    private int quantity;
    private Unit<Velocity> unit;
    private boolean flag;

    @UIProperty(name = PROP_NAME, category = "category",
        defaultString = "default name")
    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    @UIProperty(name = PROP_QUANTITY, category = "category", min = 10,
        max = 100, defaultInt = 50)
    public int getQuantity()
    {
      return quantity;
    }

    public void setQuantity(int quantity)
    {
      this.quantity = quantity;
    }

    @UIProperty(name = PROP_UNIT, category = "category")
    public Unit<Velocity> getUnits()
    {
      return unit;
    }

    public void setUnits(Unit<Velocity> unit)
    {
      this.unit = unit;
    }

    @UIProperty(name = PROP_FLAG, category = "category", defaultBoolean = true)
    public boolean getFlag()
    {
      return flag;
    }

    public void setFlag(boolean flag)
    {
      this.flag = flag;
    }

    @UIProperty(name = PROP_RANGE, category = "category")
    public QuantityRange<Velocity> getRange()
    {
      return range;
    }

    public void setRange(QuantityRange<Velocity> range)
    {
      this.range = range;
    }

    @Override
    public Measurable<Velocity> min()
    {
      return null;
    }

    @Override
    public Measurable<Velocity> max()
    {
      return null;
    }

    @Override
    public Measurable<Velocity> mean()
    {
      return null;
    }

    @Override
    public Measurable<Velocity> variance()
    {
      return null;
    }

    @Override
    public Measurable<Velocity> sd()
    {
      return null;
    }

    @Override
    public Dimension getDimension()
    {
      return null;
    }

  }
}
