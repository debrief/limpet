package info.limpet.rcp.data_provider.data;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class DataModel implements IStructuredContentProvider
{
	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{
	}

	public void dispose()
	{
	}

	public Object[] getElements(Object parent)
	{

		DummyCollection dummyOne = new DummyCollection("one", "first sample dataset");
		dummyOne.add("aaa");
		dummyOne.add("bbb");
		dummyOne.add("ccc");
		DummyCollection dummyTwo = new DummyCollection("two", "second sample dataset");
		dummyTwo.add("ddd");
		dummyTwo.add("eee");
		dummyTwo.add("fff");
		DummyCollection dummyThree = new DummyCollection("three", "third sample dataset");
		dummyThree.add("fff");
		dummyThree.add("ggg");
		dummyThree.add("hhh");
		dummyThree.add("iii");
		dummyThree.add("jjj");
		dummyThree.add("kkk");
		return new DummyCollection[]
		{ dummyOne, dummyTwo, dummyThree  };

		// // collate our data series
		// StockTypes.Temporal.Speed_MSec speedSeries1 = new
		// StockTypes.Temporal.Speed_MSec("Speed One");
		// StockTypes.Temporal.Speed_MSec speedSeries2 = new
		// StockTypes.Temporal.Speed_MSec("Speed Two");
		// StockTypes.Temporal.Speed_MSec speedSeries3 = new
		// StockTypes.Temporal.Speed_MSec("Speed Three");
		// StockTypes.Temporal.Length_M length1 = new
		// StockTypes.Temporal.Length_M("Length One");
		// StockTypes.Temporal.Length_M length2 = new
		// StockTypes.Temporal.Length_M("Length Two");
		// ObjectCollection<String> string1 = new
		// ObjectCollection<String>("String one");
		// ObjectCollection<String> string2 = new
		// ObjectCollection<String>("String one");
		//
		//
		// for (int i = 1; i <= 10; i++)
		// {
		// speedSeries1.add(i);
		// speedSeries2.add(Math.sin(i));
		// speedSeries3.add(3 * Math.cos(i));
		// length1.add(i % 3);
		// length2.add(i % 5);
		// string1.add("" + i);
		// string2.add("" + (i % 3));
		// }
		//
		// return new Object[] {speedSeries1, speedSeries2, speedSeries3, length1,
		// length2, string1, string2};
	}

	public static class DummyCollection extends ArrayList<String> implements
			IAdaptable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String _name;
		private String _description;

		public DummyCollection(String name)
		{
			_name = name;
		}

		public DummyCollection(String name, String description)
		{
			this(name);
			_description = description;
		}

		public String toString(){
			return _name + " (" + size() + " items)";
		}
		
		public String getName()
		{
			return _name;
		}

		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			if (adapter == IPropertySource.class)
			{
				return new CollectionPropertySource(this);
			}
			return null;
		}

		public String getDescription()
		{
			return _description;
		}
		
		public void setDescription(String val)
		{
			_description = val;
		}
		
		public void setName(String val)
		{
			_name = val;
		}

	}

	/**
	 * This class provides property sheet properties for ButtonElement.
	 */
	public static class CollectionPropertySource implements IPropertySource
	{

		private static final String PROPERTY_NAME = "limpet.collection.name";
		private static final String PROPERTY_SIZE = "limpet.collection.size";
		private static final String PROPERTY_DESCRIPTION = "limpet.collection.description";

		private IPropertyDescriptor[] propertyDescriptors;
		private final DummyCollection _collection;

		/**
		 * Creates a new ButtonElementPropertySource.
		 * 
		 * @param element
		 *          the element whose properties this instance represents
		 */
		public CollectionPropertySource(DummyCollection element)
		{
			_collection = element;
		}

		/**
		 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
		 */
		public IPropertyDescriptor[] getPropertyDescriptors()
		{
			if (propertyDescriptors == null)
			{
				// Create a descriptor and set a category
				PropertyDescriptor textDescriptor = new TextPropertyDescriptor(
						PROPERTY_NAME, "Name");
				textDescriptor.setCategory("Label");
				PropertyDescriptor sizeDescriptor = new PropertyDescriptor(
						PROPERTY_SIZE, "Size");
				sizeDescriptor.setCategory("Metadata");
				PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
						PROPERTY_DESCRIPTION, "Description");
				descriptionDescriptor.setCategory("Label");

				propertyDescriptors = new IPropertyDescriptor[]
				{ textDescriptor, sizeDescriptor, descriptionDescriptor };
			}
			return propertyDescriptors;
		}

		public Object getEditableValue()
		{
			return _collection;
		}

		public Object getPropertyValue(Object id)
		{
			String prop = (String) id;
			
			if(prop.equals(PROPERTY_NAME))
				return _collection.getName();
			else if(prop.equals(PROPERTY_SIZE))
				return _collection.size();
			else if(prop.equals(PROPERTY_DESCRIPTION))
				return _collection.getDescription();
			
			return null;
		}

		public boolean isPropertySet(Object id)
		{
			// TODO Auto-generated method stub
			return false;
		}

		public void resetPropertyValue(Object id)
		{
			// TODO Auto-generated method stub
			
		}

		public void setPropertyValue(Object id, Object value)
		{
			String prop = (String) id;
			
			if(prop.equals(PROPERTY_NAME))
				_collection.setName((String) value);
			else if(prop.equals(PROPERTY_SIZE))
				throw new RuntimeException("Can't set size, silly");
			else if(prop.equals(PROPERTY_DESCRIPTION))
				_collection.setDescription((String) value);
		}

	}

}