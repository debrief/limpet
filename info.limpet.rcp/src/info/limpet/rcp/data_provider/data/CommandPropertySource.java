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

	private static final String COMMAND_NAME = "limpet.operation.name";
	private static final String COMMAND_DESCRIPTION = "limpet.operation.description";
	private static final String COMMAND_DYNAMIC = "limpet.operation.dynamic";

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
					COMMAND_NAME, "Name");
			textDescriptor.setCategory("Label");
			final PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					COMMAND_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");
			final PropertyDescriptor dynamicDescriptor = new CheckboxPropertyDescriptor(
					COMMAND_DYNAMIC, "Dynamic updates");
			descriptionDescriptor.setCategory("Label");

			propertyDescriptors = new IPropertyDescriptor[]
			{ textDescriptor, descriptionDescriptor, dynamicDescriptor };
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(COMMAND_NAME))
			return _operation.getCommand().getTitle();
		else if (prop.equals(COMMAND_DESCRIPTION))
			return _operation.getCommand().getDescription();
		else if (prop.equals(COMMAND_DYNAMIC))
			return _operation.getCommand().getDynamic();

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
		if(prop.equals(COMMAND_DYNAMIC))
		{
			boolean res = (boolean) value;
			_operation.getCommand().setDynamic(res);
		}
	}

}