package info.limpet.rcp.data_provider.data;

import info.limpet.IStoreItem;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

public class StoreItemWrapper implements LimpetWrapper, IAdaptable
{

  private final IStoreItem _storeItem;
  private final LimpetWrapper _parent;

  public StoreItemWrapper(IStoreItem _storeItem, LimpetWrapper _parent)
  {
    this._storeItem = _storeItem;
    this._parent = _parent;
  }

  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
  {
    if (adapter == IPropertySource.class)
    {
      return new ReflectivePropertySource(_storeItem);
    }
    else if (adapter == IStoreItem.class)
    {
      return _storeItem;
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
    return _storeItem;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((_storeItem == null) ? 0 : _storeItem.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StoreItemWrapper other = (StoreItemWrapper) obj;
    if (_storeItem == null)
    {
      if (other._storeItem != null)
        return false;
    }
    else if (!_storeItem.equals(other._storeItem))
      return false;
    return true;
  }

  @Override
  public String toString()
  {    
    return _storeItem.getName();
  }
}
