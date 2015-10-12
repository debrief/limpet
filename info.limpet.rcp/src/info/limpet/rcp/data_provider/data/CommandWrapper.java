package info.limpet.rcp.data_provider.data;

import info.limpet.ICommand;
import info.limpet.IStore.IStoreItem;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CommandWrapper implements IAdaptable, LimpetWrapper
{
	private final ICommand<?> _command;
	private final LimpetWrapper _parent;

	public CommandWrapper(final LimpetWrapper parent, final ICommand<?> prec)
	{
		_parent = parent;
		_command = prec;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
	{
		if (adapter == IPropertySource.class)
		{
			return new CommandPropertySource(this);
		}
		else if (adapter == IStoreItem.class)
		{
			return _command;
		}
		return null;
	}

	public ICommand<?> getCommand()
	{
		return _command;
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

	@Override
	public String toString()
	{
		return _command.getName();
	}
}