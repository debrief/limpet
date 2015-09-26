package info.limpet.rcp.data_provider.data;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class provides property sheet properties for ButtonElement.
 */
public class CommandPropertySource implements IPropertySource
{

	private static final String PROPERTY_NAME = "limpet.operation.name";
	private static final String PROPERTY_DESCRIPTION = "limpet.collection.description";

	private IPropertyDescriptor[] propertyDescriptors;
	private final CommandWrapper _operation;

	/**
	 * Creates a new ButtonElementPropertySource.
	 * 
	 * @param operationWrapper
	 *          the element whose properties this instance represents
	 */
	public CommandPropertySource(final CommandWrapper operationWrapper)
	{
		_operation = operationWrapper;
	}

	@Override
	public Object getEditableValue()
	{
		return _operation;
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
			final PropertyDescriptor textDescriptor = new PropertyDescriptor(
					PROPERTY_NAME, "Name");
			textDescriptor.setCategory("Label");
			final PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					PROPERTY_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");

			propertyDescriptors = new IPropertyDescriptor[]
			{ textDescriptor, descriptionDescriptor };
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			return _operation.getCommand().getTitle();
		else if (prop.equals(PROPERTY_DESCRIPTION))
			return _operation.getCommand().getDescription();

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
		throw new RuntimeException("cannot set properties for operation source");
	}

}