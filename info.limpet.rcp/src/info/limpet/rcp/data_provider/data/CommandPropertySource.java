package info.limpet.rcp.data_provider.data;

import info.limpet.data.operations.SimpleMovingAverageOperation.SimpleMovingAverageCommand;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

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
	private static final String AVERAGE_WINDOW = "limpet.operation.movingaverage.window";

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
			List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();

			// Create a descriptor and set a category
			final PropertyDescriptor textDescriptor = new PropertyDescriptor(
					COMMAND_NAME, "Name");
			textDescriptor.setCategory("Label");
			list.add(textDescriptor);
			final PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					COMMAND_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");
			list.add(descriptionDescriptor);
			final PropertyDescriptor dynamicDescriptor = new CheckboxPropertyDescriptor(
					COMMAND_DYNAMIC, "Dynamic updates");
			dynamicDescriptor.setCategory("Label");
			list.add(dynamicDescriptor);

			// hmm, is it our moving average?
			if (_operation.getCommand() instanceof SimpleMovingAverageCommand)
			{
				final SliderPropertyDescriptor windowDescriptor = new SliderPropertyDescriptor(
						AVERAGE_WINDOW, "Window", 1, 20);
				windowDescriptor.setCategory("Calculation");
				list.add(windowDescriptor);
			}

			propertyDescriptors = (IPropertyDescriptor[]) list.toArray(new IPropertyDescriptor[]{});
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(COMMAND_NAME))
			return _operation.getCommand().getName();
		else if (prop.equals(COMMAND_DESCRIPTION))
			return _operation.getCommand().getDescription();
		else if (prop.equals(COMMAND_DYNAMIC))
			return _operation.getCommand().getDynamic();
		else if (prop.equals(AVERAGE_WINDOW))
			return "" + ((SimpleMovingAverageCommand) _operation.getCommand())
					.getWindowSize();

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
		if (prop.equals(COMMAND_DYNAMIC))
		{
			boolean res = (boolean) value;
			_operation.getCommand().setDynamic(res);
		}
		else if (prop.equals(AVERAGE_WINDOW))
		{
			((SimpleMovingAverageCommand) _operation.getCommand())
					.setWindowSize(Integer.parseInt((String) value));
		}
	}

}