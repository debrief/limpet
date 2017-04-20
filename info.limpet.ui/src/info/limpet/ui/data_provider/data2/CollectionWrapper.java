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
package info.limpet.ui.data_provider.data2;

import info.limpet2.Document;
import info.limpet2.IStoreItem;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CollectionWrapper implements IAdaptable, LimpetWrapper
{
  private final Document _collection;
  private final LimpetWrapper _parent;

  public CollectionWrapper(final LimpetWrapper parent,
      final Document collection)
  {
    _parent = parent;
    _collection = collection;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((_collection == null) ? 0 : _collection.hashCode());
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
    CollectionWrapper other = (CollectionWrapper) obj;
    if (_collection == null)
    {
      if (other._collection != null)
      {
        return false;
      }
    }
    else if (!_collection.equals(other._collection))
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
      return new ReflectivePropertySource(_collection);
    }
    else if (adapter == IStoreItem.class)
    {
      return _collection;
    }
    else if (adapter == Document.class)
    {
      return _collection;
    }
    return null;
  }
  public Document getCollection()
  {
    return _collection;
  }

  @Override
  public LimpetWrapper getParent()
  {
    return _parent;
  }

  @Override
  public IStoreItem getSubject()
  {
    return _collection;
  }

  @Override
  public String toString()
  {
    final String msg;
    msg = _collection.getName() + " (" + (_collection).size() + " items)";
    return msg;
  }
}
