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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StoreGroup extends ArrayList<IStoreItem> implements IStore,
    IChangeListener, IStoreGroup
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private transient List<StoreChangeListener> _storeListeners;
  private transient List<IChangeListener> _listeners;
  private transient List<PropertyChangeListener> _timeListeners;
  private Date _currentTime;
  
  private UUID uuid;

  private String _name;

  private IStoreGroup _parent; 
  private static final String TOP_LEVEL_NAME = "Limpet Store";
  
  public StoreGroup(String name)
  {
    _name = name;
  }

  private void checkListeners()
  {
    if(_storeListeners == null)
    {
      _storeListeners =
          new ArrayList<StoreChangeListener>();
    }
    if(_listeners == null)
    {
      _listeners =
          new ArrayList<IChangeListener>();
    }
  }
  
  public StoreGroup()
  {
    this(TOP_LEVEL_NAME);
  }

  public interface StoreChangeListener
  {
    void changed();
  }

  public void addChangeListener(StoreChangeListener listener)
  {
    checkListeners();
    
    _storeListeners.add(listener);
  }

  public void removeChangeListener(StoreChangeListener listener)
  {
    checkListeners();
    
    _storeListeners.remove(listener);
  }

  protected void fireModified()
  {
    checkListeners();

    Iterator<StoreChangeListener> iter = _storeListeners.iterator();
    while (iter.hasNext())
    {
      StoreChangeListener listener =
          iter.next();
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
    
    results.setParent(this);
    
    results.addChangeListener(this);

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
  public String getName()
  {
    return _name;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    checkListeners();
    
    _listeners.add(listener);
  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    checkListeners();
    
    _listeners.add(listener);
  }

  @Override
  public void fireDataChanged()
  {
    if(_listeners != null)
    {
      for(IChangeListener listener: _listeners)
      {
        listener.dataChanged(this);
      }
    }
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

  @Override
  public Date getTime()
  {
    return _currentTime;
  }

  @Override
  public void setTime(final Date time)
  {
    final Date oldTime = _currentTime;
    _currentTime = time;
    if(_timeListeners != null)
    {
      PropertyChangeEvent evt = new PropertyChangeEvent(this, "TIME", oldTime, time);
      for(PropertyChangeListener thisL: _timeListeners)
      {
        thisL.propertyChange(evt);
      }
    }
  }

  @Override
  public void addTimeChangeListener(PropertyChangeListener listener)
  {
    if(_timeListeners == null)
    {
      _timeListeners = new ArrayList<PropertyChangeListener>();
    }
    _timeListeners.add(listener);
  }

  @Override
  public void removeTimeChangeListener(PropertyChangeListener listener)
  {
    if(_timeListeners != null)
    {
      _timeListeners.remove(listener);
    }
  }

}
