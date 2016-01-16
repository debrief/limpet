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
package info.limpet.data.store;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.UIProperty;
import info.limpet.data.impl.ListenerHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InMemoryStore extends ArrayList<IStoreItem> implements IStore,
    IChangeListener, IStoreGroup
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private transient List<StoreChangeListener> _listeners = new ArrayList<StoreChangeListener>();

  private UUID uuid;

  private String _name = "Limpet Store";

  public static class StoreGroup extends ArrayList<IStoreItem> implements
      IStoreItem, IStoreGroup, IChangeListener
  {
    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    private String _name;
    private IStoreGroup _parent;
    private transient UUID uuid;

    // note: we make the change support listeners transient, since
    // they refer to UI elements that we don't persist
    private transient ListenerHelper _changeSupport;

    public StoreGroup(String name)
    {
      _name = name;
    }

    public List<IStoreItem> children()
    {
      return this;
    }

    @Override
    public UUID getUUID()
    {
      if (uuid == null)
      {
        uuid = UUID.randomUUID();
      }
      return uuid;
    }

    @Override
    public boolean add(IStoreItem e)
    {
      e.setParent(this);

      // ok, start listening to this item
      e.addChangeListener(this);

      boolean res = super.add(e);

      fireDataChanged();

      return res;
    }

    @Override
    public boolean remove(Object o)
    {
      if (o instanceof IStoreItem)
      {
        IStoreItem si = (IStoreItem) o;
        si.setParent(null);

        si.removeChangeListener(this);

      }

      boolean res = super.remove(o);

      // ok, fire an update.
      fireDataChanged();

      return res;
    }

    @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
    @Override
    public String getName()
    {
      return _name;
    }

    @UIProperty(name = "Children", category = UIProperty.CATEGORY_METADATA)
    public int getSize()
    {
      return super.size();
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getUUID().hashCode();
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
      StoreGroup other = (StoreGroup) obj;
      if (!getUUID().equals(other.getUUID()))
      {
        return false;
      }
      return true;
    }

    @Override
    public boolean hasChildren()
    {
      return size() > 0;
    }

    public void setName(String value)
    {
      _name = value;

      // and tell any listeners
      fireDataChanged();
    }

    protected void initListeners()
    {
      if (_changeSupport == null)
      {
        _changeSupport = new ListenerHelper();
      }
    }

    @Override
    public void addChangeListener(IChangeListener listener)
    {
      initListeners();

      _changeSupport.add(listener);
    }

    @Override
    public void removeChangeListener(IChangeListener listener)
    {
      initListeners();

      _changeSupport.remove(listener);
    }

    @Override
    public void fireDataChanged()
    {
      if (_changeSupport != null)
      {
        // tell any standard listeners
        _changeSupport.fireDataChange(this);
      }
    }

    @Override
    public IStoreGroup getParent()
    {
      return _parent;
    }

    @Override
    public void setParent(IStoreGroup parent)
    {
      _parent = parent;
    }

    @Override
    public void dataChanged(IStoreItem subject)
    {
      fireDataChanged();
    }

    @Override
    public void metadataChanged(IStoreItem subject)
    {
      fireDataChanged();
    }

    @Override
    public void collectionDeleted(IStoreItem subject)
    {
      fireDataChanged();
    }

    @Override
    public IStoreItem get(String name)
    {
      IStoreItem res = null;
      Iterator<IStoreItem> iter = iterator();
      while (iter.hasNext())
      {
        IStoreItem item = iter.next();
        if (item instanceof IStoreGroup)
        {
          IStoreGroup group = (IStoreGroup) item;
          Iterator<IStoreItem> iter2 = group.iterator();
          while (iter2.hasNext())
          {
            IStoreItem thisI = (IStoreItem) iter2.next();
            if (name.equals(thisI.getName()))
            {
              res = thisI;
              break;
            }
          }
        }
        if (name.equals(item.getName()))
        {
          res = item;
          break;
        }
      }
      return res;
    }
  }

  private Object readResolve()
  {
    _listeners = new ArrayList<StoreChangeListener>();
    return this;
  }

  public interface StoreChangeListener
  {
    void changed();
  }

  public void addChangeListener(StoreChangeListener listener)
  {
    _listeners.add(listener);
  }

  public void removeChangeListener(StoreChangeListener listener)
  {
    _listeners.remove(listener);
  }

  protected void fireModified()
  {
    Iterator<StoreChangeListener> iter = _listeners.iterator();
    while (iter.hasNext())
    {
      InMemoryStore.StoreChangeListener listener = (InMemoryStore.StoreChangeListener) iter
          .next();
      listener.changed();
    }
  }

  @Override
  public void addAll(List<IStoreItem> results)
  {
    // add the items individually, so we can register as a listener
    Iterator<IStoreItem> iter = results.iterator();
    while (iter.hasNext())
    {
      IStoreItem iCollection = iter.next();
      add(iCollection);
    }

    fireModified();
  }

  @Override
  public boolean add(IStoreItem results)
  {
    boolean res = super.add(results);

    // register as a listener with the results object
    if (results instanceof ICollection)
    {
      ICollection coll = (ICollection) results;
      coll.addChangeListener(this);
    }
    else if (results instanceof IStoreGroup)
    {
      IStoreGroup group = (IStoreGroup) results;
      group.addChangeListener(this);
    }

    fireModified();

    return res;
  }

  @Override
  public IStoreItem get(String name)
  {
    IStoreItem res = null;
    Iterator<IStoreItem> iter = iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        Iterator<IStoreItem> iter2 = group.iterator();
        while (iter2.hasNext())
        {
          IStoreItem thisI = (IStoreItem) iter2.next();
          if (name.equals(thisI.getName()))
          {
            res = thisI;
            break;
          }
        }
      }
      if (name.equals(item.getName()))
      {
        res = item;
        break;
      }
    }
    return res;
  }

  @Override
  public IStoreItem get(UUID uuid)
  {
    IStoreItem res = null;
    Iterator<IStoreItem> iter = iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        Iterator<IStoreItem> iter2 = group.iterator();
        while (iter2.hasNext())
        {
          IStoreItem thisI = (IStoreItem) iter2.next();
          if (uuid.equals(thisI.getUUID()))
          {
            res = thisI;
            break;
          }
        }
      }
      if (uuid.equals(item.getUUID()))
      {
        res = item;
        break;
      }
    }
    return res;
  }

  public void clear()
  {
    // stop listening to the collections individually
    // - defer the clear until the end,
    // so we don't get concurrent modification
    Iterator<IStoreItem> iter = super.iterator();
    while (iter.hasNext())
    {
      IStoreItem iC = iter.next();
      if (iC instanceof ICollection)
      {
        ICollection coll = (ICollection) iC;
        coll.removeChangeListener(this);
      }
    }

    super.clear();
    fireModified();
  }

  public boolean remove(Object item)
  {
    boolean res = super.remove(item);

    // stop listening to this one
    if (item instanceof ICollection)
    {
      ICollection collection = (ICollection) item;
      collection.removeChangeListener(this);

      // ok, also tell it that it's being deleted
      collection.beingDeleted();
    }

    fireModified();

    return res;
  }

  @Override
  public void dataChanged(IStoreItem subject)
  {
    fireModified();
  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    dataChanged(subject);
  }

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + getUUID().hashCode();
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
    InMemoryStore other = (InMemoryStore) obj;
    if (!getUUID().equals(other.getUUID()))
    {
      return false;
    }
    return true;
  }

  @Override
  public boolean hasChildren()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public IStoreGroup getParent()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setParent(IStoreGroup parent)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public String getName()
  {
    return _name;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void fireDataChanged()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public UUID getUUID()
  {
    if (uuid == null)
    {
      uuid = UUID.randomUUID();
    }
    return uuid;
  }

  @Override
  public void setName(String value)
  {
    _name = value;
  }

}
