package info.limpet.rcp.data_provider.data;

import java.util.Date;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.samples.StockTypes;

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
		IObjectCollection<String> dummyOne = new ObjectCollection<String>("one");
		dummyOne.setDescription("description for " + dummyOne.getName());
		dummyOne.add("aaa");
		dummyOne.add("bbb");
		dummyOne.add("ccc");
		IObjectCollection<String> dummyTwo = new ObjectCollection<String>("two");
		dummyTwo.setDescription("description for " + dummyTwo.getName());
		dummyTwo.add("ddd");
		dummyTwo.add("eee");
		dummyTwo.add("fff");
		IObjectCollection<String> dummyThree = new ObjectCollection<String>("three");
		dummyThree.setDescription("description for " + dummyThree.getName());
		dummyThree.add("fff");
		dummyThree.add("ggg");
		dummyThree.add("hhh");
		dummyThree.add("iii");
		dummyThree.add("jjj");
		dummyThree.add("kkk");
		// return new CollectionWrapper[]
		// { new CollectionWrapper(dummyOne), new CollectionWrapper(dummyTwo), new
		// CollectionWrapper(dummyThree)};

		// // collate our data series
		StockTypes.Temporal.Speed_MSec speedSeries1 = new StockTypes.Temporal.Speed_MSec(
				"Speed One");
		StockTypes.Temporal.Speed_MSec speedSeries2 = new StockTypes.Temporal.Speed_MSec(
				"Speed Two");
		StockTypes.Temporal.Speed_MSec speedSeries3 = new StockTypes.Temporal.Speed_MSec(
				"Speed Three");
		StockTypes.Temporal.Length_M length1 = new StockTypes.Temporal.Length_M(
				"Length One");
		StockTypes.Temporal.Length_M length2 = new StockTypes.Temporal.Length_M(
				"Length Two");
		ObjectCollection<String> string1 = new ObjectCollection<String>(
				"String one");
		ObjectCollection<String> string2 = new ObjectCollection<String>(
				"String two");

		for (int i = 1; i <= 10; i++)
		{
			long thisTime = new Date().getTime() + i * 500 * 60;
			
			speedSeries1.add(thisTime, i);
			speedSeries2.add(thisTime, Math.sin(i));
			speedSeries3.add(thisTime, 3 * Math.cos(i));
			length1.add(thisTime, i % 3);
			length2.add(thisTime, i % 5);
			string1.add("item " + i);
			string2.add("item " + (i % 3));
		}


		return new CollectionWrapper[]
		{ new CollectionWrapper(speedSeries1), new CollectionWrapper(speedSeries2),
				new CollectionWrapper(speedSeries3), new CollectionWrapper(length1),
				new CollectionWrapper(length2), new CollectionWrapper(string1),
				new CollectionWrapper(string2) };
	}

	public static class CollectionWrapper implements IAdaptable
	{
		private final ICollection _collection;

		public CollectionWrapper(ICollection collection)
		{
			_collection = collection;
		}

		public String toString()
		{
			return _collection.getName() + " (" + _collection.size() + " items)";
		}

		public ICollection getCollection()
		{
			return _collection;
		}

		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			if (adapter == IPropertySource.class)
			{
				return new CollectionPropertySource(this);
			}
			else if (adapter == ICollection.class)
			{
				return _collection;
			}
			return null;
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
		private final CollectionWrapper _collection;

		/**
		 * Creates a new ButtonElementPropertySource.
		 * 
		 * @param element
		 *          the element whose properties this instance represents
		 */
		public CollectionPropertySource(CollectionWrapper element)
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

			if (prop.equals(PROPERTY_NAME))
				return _collection.getCollection().getName();
			else if (prop.equals(PROPERTY_SIZE))
				return _collection.getCollection().size();
			else if (prop.equals(PROPERTY_DESCRIPTION))
				return _collection.getCollection().getDescription();

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

			if (prop.equals(PROPERTY_NAME))
				_collection.getCollection().setName((String) value);
			else if (prop.equals(PROPERTY_SIZE))
				throw new RuntimeException("Can't set size, silly");
			else if (prop.equals(PROPERTY_DESCRIPTION))
				_collection.getCollection().setDescription((String) value);
		}

	}

}