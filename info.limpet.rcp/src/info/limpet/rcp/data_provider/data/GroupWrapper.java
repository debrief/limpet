package info.limpet.rcp.data_provider.data;

import info.limpet.IStore.IStoreItem;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class GroupWrapper implements IAdaptable, LimpetWrapper
{
	private final StoreGroup _group;
	private final LimpetWrapper _parent;

	public GroupWrapper(final LimpetWrapper parent, final StoreGroup prec)
	{
		_parent = parent;
		_group = prec;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
	{
		if (adapter == IPropertySource.class)
		{
			return new GroupPropertySource(this);
		}
		else if (adapter == IStoreItem.class)
		{
			return _group;
		}
		return null;
	}

	public StoreGroup getGroup()
	{
		return _group;
	}

	@Override
	public LimpetWrapper getParent()
	{
		return _parent;
	}

	@Override
	public Object getSubject()
	{
		return _group;
	}

	@Override
	public String toString()
	{
		return _group.getName();
	}
}