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
package info.limpet.ui.data_provider.data;

import info.limpet.IDocument;
import info.limpet.IStoreItem;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class DocumentWrapper implements IAdaptable, LimpetWrapper
{
  private final IDocument _document;
  private final LimpetWrapper _parent;

  public DocumentWrapper(final LimpetWrapper parent,
      final IDocument collection)
  {
    _parent = parent;
    _document = collection;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((_document == null) ? 0 : _document.hashCode());
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
    DocumentWrapper other = (DocumentWrapper) obj;
    if (_document == null)
    {
      if (other._document != null)
      {
        return false;
      }
    }
    else if (!_document.equals(other._document))
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
      return new ReflectivePropertySource(_document);
    }
    else if (adapter == IStoreItem.class)
    {
      return _document;
    }
    else if (adapter == IDocument.class)
    {
      return _document;
    }
    return null;
  }
  public IDocument getDocument()
  {
    return _document;
  }

  @Override
  public LimpetWrapper getParent()
  {
    return _parent;
  }

  @Override
  public IStoreItem getSubject()
  {
    return _document;
  }

  @Override
  public String toString()
  {
    final String msg;
    msg = _document.getName() + " (" + (_document).size() + " items)";
    return msg;
  }
}

