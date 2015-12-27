package info.limpet.rcp;

import info.limpet.UIProperty;
import info.limpet.rcp.data_provider.data.ReflectivePropertySource;
import junit.framework.TestCase;

public class TestReflectivePropertySource extends TestCase
{
	
	private AnnotatedValue annotatedValue;
	private ReflectivePropertySource propertySource;

	@Override
	protected void setUp() throws Exception
	{
		annotatedValue = new AnnotatedValue();
		annotatedValue.setName("annotated value");
		annotatedValue.setFlag(true);
		annotatedValue.setQuantity(12);
		
		propertySource = new ReflectivePropertySource(annotatedValue);
		propertySource.getPropertyDescriptors();

	}
	
	public void testGetValue()
	{		
		Object propertyValue = propertySource.getPropertyValue(AnnotatedValue.PROP_NAME);
		assertEquals("annotated value", propertyValue);
		
		propertyValue = propertySource.getPropertyValue(AnnotatedValue.PROP_QUANTITY);
		assertEquals(12, propertyValue);
		
		propertyValue = propertySource.getPropertyValue(AnnotatedValue.PROP_FLAG);
		assertEquals(true, propertyValue);
	}

	public void testSetValue()
	{		
		propertySource.setPropertyValue(AnnotatedValue.PROP_NAME, "modified value");
		assertEquals("modified value", annotatedValue.getName());
		
		propertySource.setPropertyValue(AnnotatedValue.PROP_QUANTITY, 50);
		assertEquals(50, annotatedValue.getQuantity());
		
		propertySource.setPropertyValue(AnnotatedValue.PROP_FLAG, false);
		assertEquals(false, annotatedValue.getFlag());
	}
	
	public void testResetValue()
	{				
		propertySource.resetPropertyValue(AnnotatedValue.PROP_NAME);
		assertEquals("default name",annotatedValue.getName());
		
		propertySource.resetPropertyValue(AnnotatedValue.PROP_QUANTITY);
		assertEquals(50, annotatedValue.getQuantity());
		
		annotatedValue.setFlag(false);
		propertySource.resetPropertyValue(AnnotatedValue.PROP_FLAG);
		assertEquals(true, annotatedValue.getFlag());
	}
	
	public static class AnnotatedValue {
		
		public static final String PROP_NAME = "name";
		public static final String PROP_QUANTITY = "quantity";
		public static final String PROP_FLAG = "flag";
		
		private String name;
		private int quantity;
		private boolean flag;

		@UIProperty(name=PROP_NAME,category="category", defaultString="default name")
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		
		@UIProperty(name=PROP_QUANTITY,category="category", min=10, max=100, defaultInt = 50)
		public int getQuantity()
		{
			return quantity;
		}
		public void setQuantity(int quantity)
		{
			this.quantity = quantity;
		}
		
		@UIProperty(name=PROP_FLAG,category="category", defaultBoolean = true)
		public boolean getFlag()
		{
			return flag;
		}
		public void setFlag(boolean flag)
		{
			this.flag = flag;
		}
	}
}
