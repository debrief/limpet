package info.limpet.rcp.data_provider.data;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class provides property sheet properties for ButtonElement.
 */
public class CollectionPropertySource implements IPropertySource
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