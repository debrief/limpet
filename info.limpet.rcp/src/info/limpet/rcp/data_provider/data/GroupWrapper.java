/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.rcp.data_provider.data;

import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.data.store.IGroupWrapper;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class GroupWrapper implements IAdaptable, LimpetWrapper, IGroupWrapper
{
	private final IStoreGroup _group;
	private final LimpetWrapper _parent;

	public GroupWrapper(final LimpetWrapper parent, final IStoreGroup prec)
	{
		_parent = parent;
		_group = prec;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_group == null) ? 0 : _group.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		GroupWrapper other = (GroupWrapper) obj;
		if (_group == null)
		{
			if (other._group != null)
			{
				return false;
			}
		}
		else if (!_group.equals(other._group))
		{
			return false;
		}
		return true;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
	{
		if (adapter == IPropertySource.class)
		{
			return new ReflectivePropertySource(this.getGroup());
		}
		else if (adapter == IStoreItem.class)
		{
			return _group;
		}
		return null;
	}

	public IStoreGroup getGroup()
	{
		return _group;
	}

	@Override
	public LimpetWrapper getParent()
	{
		return _parent;
	}

	@Override
	public IStoreItem getSubject()
	{
		return _group;
	}

	@Override
	public String toString()
	{
		return _group.getName();
	}
}
