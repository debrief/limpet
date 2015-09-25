package info.limpet.rcp.data_provider.data;

import info.limpet.ICollection;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class DataModel implements IStructuredContentProvider
{
	private final IStore _store;

	public DataModel(IStore store)
	{
		_store = store;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{
	}

	public void dispose()
	{
	}

	public Object[] getElements(Object parent)
	{

		List<CollectionWrapper> list = new ArrayList<CollectionWrapper>();
		
		Iterator<ICollection> iter = _store.getAll().iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();
			list.add(new CollectionWrapper(iCollection));
		}
		
		return list.toArray();
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