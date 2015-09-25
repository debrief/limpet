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
	public CommandPropertySource(CommandWrapper operationWrapper)
	{
		_operation = operationWrapper;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (propertyDescriptors == null)
		{
			// Create a descriptor and set a category
			PropertyDescriptor textDescriptor = new PropertyDescriptor(
					PROPERTY_NAME, "Name");
			textDescriptor.setCategory("Label");
			PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					PROPERTY_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");

			propertyDescriptors = new IPropertyDescriptor[]
			{ textDescriptor, descriptionDescriptor };
		}
		return propertyDescriptors;
	}

	public Object getEditableValue()
	{
		return _operation;
	}

	public Object getPropertyValue(Object id)
	{
		String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			return _operation.getCommand().getTitle();
		else if (prop.equals(PROPERTY_DESCRIPTION))
			return _operation.getCommand().getDescription();

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
		throw new RuntimeException("cannot set properties for operation source");
	}

}