package info.limpet.rcp.data_provider.data;

import info.limpet.ICommand;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CommandWrapper implements IAdaptable
{
	private final ICommand _command;

	public CommandWrapper(ICommand prec)
	{
		_command = prec;
	}

	public String toString()
	{
		return _command.getTitle();
	}

	public ICommand getCommand()
	{
		return _command;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if (adapter == IPropertySource.class)
		{
			return new CommandPropertySource(this);
		}
		else if (adapter == ICommand.class)
		{
			return _command;
		}
		return null;
	}
}