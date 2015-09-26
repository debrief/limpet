package info.limpet.rcp.data_provider.data;

import info.limpet.ICommand;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CommandWrapper implements IAdaptable, LimpetWrapper
{
	private final ICommand _command;
	private final LimpetWrapper _parent;

	public CommandWrapper(LimpetWrapper parent, ICommand prec)
	{
		_parent = parent;
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

	@Override
	public LimpetWrapper getParent()
	{
		return _parent;
	}

	@Override
	public Object getSubject()
	{
		return _command;
	}
}