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

import info.limpet2.ICommand;
import info.limpet2.IStoreItem;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class CommandWrapper implements IAdaptable, LimpetWrapper
{
  private final ICommand _command;
  private final LimpetWrapper _parent;

  public CommandWrapper(final LimpetWrapper parent, final ICommand prec)
  {
    _parent = parent;
    _command = prec;
  }

  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
  {
    if (adapter == IPropertySource.class)
    {
      return new ReflectivePropertySource(_command);
    }
    else if (adapter == IStoreItem.class)
    {
      return _command;
    }
    return null;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_command == null) ? 0 : _command.hashCode());
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
    CommandWrapper other = (CommandWrapper) obj;
    if (_command == null)
    {
      if (other._command != null)
      {
        return false;
      }
    }
    else if (!_command.equals(other._command))
    {
      return false;
    }
    return true;
  }

  public ICommand getCommand()
  {
    return _command;
  }

  @Override
  public LimpetWrapper getParent()
  {
    return _parent;
  }

  @Override
  public IStoreItem getSubject()
  {
    return _command;
  }

  @Override
  public String toString()
  {
    return _command.getName();
  }
}
