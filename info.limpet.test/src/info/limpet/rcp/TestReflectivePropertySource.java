package info.limpet.rcp;

import info.limpet.ICommand;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.Range;
import info.limpet.impl.UIProperty;
import info.limpet.ui.data_provider.data.PropertyTypeHandler;
import info.limpet.ui.data_provider.data.ReflectivePropertySource;

import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import junit.framework.TestCase;

import org.eclipse.january.dataset.DoubleDataset;

public class TestReflectivePropertySource extends TestCase
{

  private TestData testData;
  private ReflectivePropertySource propertySource;

  @Override
  protected void setUp() throws Exception
  {
    testData = new TestData(null, null, null);
    testData.setName("annotated value");
    testData.setFlag(true);
    testData.setQuantity(12);

    Unit<Velocity> unit = Velocity.UNIT;
    testData.setUnits(unit);

    testData.setRange(new Range(0d, 100d));

    propertySource = new ReflectivePropertySource(testData);
    propertySource.getPropertyDescriptors();

  }

  public void testGetValue()
  {
    Object propertyValue = propertySource.getPropertyValue(TestData.PROP_NAME);
    assertEquals("annotated value", propertyValue);

    propertyValue = propertySource.getPropertyValue(TestData.PROP_QUANTITY);
    assertEquals(true, propertyValue);

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

//    propertySource.setPropertyValue(TestData.PROP_QUANTITY, 50);
//    assertEquals(true, testData.getQuantity());

    propertySource.setPropertyValue(TestData.PROP_FLAG, false);
    assertEquals(false, testData.getFlag());

    propertySource.setPropertyValue(TestData.PROP_UNIT, Temperature.UNIT);
    assertEquals(Temperature.UNIT, testData.getUnits());

    propertySource.setPropertyValue(TestData.PROP_UNIT, Temperature.UNIT);
    assertEquals(Temperature.UNIT, testData.getUnits());

    propertySource.setPropertyValue(TestData.PROP_RANGE, "10 : 50");
    assertEquals(10d, testData.getRange().getMinimum());
    assertEquals(50d, testData.getRange().getMaximum());

  }

  public void testResetValue()
  {
    propertySource.resetPropertyValue(TestData.PROP_NAME);
    assertEquals("default name", testData.getName());

    propertySource.resetPropertyValue(TestData.PROP_QUANTITY);
    assertEquals(12, testData.getQuantity());

    testData.setFlag(false);
    propertySource.resetPropertyValue(TestData.PROP_FLAG);
    assertEquals(true, testData.getFlag());

    propertySource.resetPropertyValue(TestData.PROP_UNIT);
    assertEquals(Unit.ONE, testData.getUnits());
  }

  public static class TestData extends NumberDocument
  {

    public TestData(DoubleDataset dataset, ICommand predecessor, Unit<?> qType)
    {
      super(dataset, predecessor, qType);
      // TODO Auto-generated constructor stub
    }

    public static final String PROP_NAME = "name";
    public static final String PROP_QUANTITY = "quantity";
    public static final String PROP_FLAG = "flag";
    public static final String PROP_UNIT = "units";
    public static final String PROP_RANGE = "range";

    private String name;
    private Range range;
    private int quantity;
    private Unit<?> unit;
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
    public Unit<?> getUnits()
    {
      return unit;
    }

    public void setUnits(Unit<?> unit)
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
    public Range getRange()
    {
      return range;
    }

    public void setRange(Range range)
    {
      this.range = range;
    }
  }
}
