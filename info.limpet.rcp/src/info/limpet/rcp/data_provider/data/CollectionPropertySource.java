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
	public CollectionPropertySource(final CollectionWrapper element)
	{
		_collection = element;
	}

	@Override
	public Object getEditableValue()
	{
		return _collection;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (propertyDescriptors == null)
		{
			// Create a descriptor and set a category
			final PropertyDescriptor textDescriptor = new TextPropertyDescriptor(
					PROPERTY_NAME, "Name");
			textDescriptor.setCategory("Label");
			final PropertyDescriptor sizeDescriptor = new PropertyDescriptor(
					PROPERTY_SIZE, "Size");
			sizeDescriptor.setCategory("Metadata");
			final PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					PROPERTY_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");

			propertyDescriptors = new IPropertyDescriptor[]
			{ textDescriptor, sizeDescriptor, descriptionDescriptor };
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			return _collection.getCollection().getName();
		else if (prop.equals(PROPERTY_SIZE))
			return _collection.getCollection().size();
		else if (prop.equals(PROPERTY_DESCRIPTION))
			return _collection.getCollection().getDescription();

		return null;
	}

	@Override
	public boolean isPropertySet(final Object id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(final Object id)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValue(final Object id, final Object value)
	{
		final String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			_collection.getCollection().setName((String) value);
		else if (prop.equals(PROPERTY_SIZE))
			throw new RuntimeException("Can't set size, silly");
		else if (prop.equals(PROPERTY_DESCRIPTION))
			_collection.getCollection().setDescription((String) value);
	}

}