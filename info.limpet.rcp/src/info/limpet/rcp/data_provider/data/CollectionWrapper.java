package info.limpet.rcp.data_provider.data;

import info.limpet.ICollection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CollectionWrapper implements IAdaptable, LimpetWrapper
{
	private final ICollection _collection;
	private final LimpetWrapper _parent;

	public CollectionWrapper(LimpetWrapper parent, ICollection collection)
	{
		_parent = parent;
		_collection = collection;
	}

	public String toString()
	{
		return _collection.getName() + " (" + _collection.size() + " items)";
	}

	public ICollection getCollection()
	{
		return _collection;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if (adapter == IPropertySource.class)
		{
			return new CollectionPropertySource(this);
		}
		else if (adapter == ICollection.class)
		{
			return _collection;
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
		return _collection;
	}
}