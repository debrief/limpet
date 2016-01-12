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
package info.limpet.data.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.UIProperty;

public abstract class AbstractCommand<T extends IStoreItem> implements
    ICommand<T>
{

  private final String title;
  private final String description;
  private final boolean canUndo;
  private final boolean canRedo;
  private final IStore store;

  private final List<T> inputs;
  private final List<T> outputs;

  private IStoreGroup _parent;

  /**
   * whether the command should recalculate if its children change
   * 
   */
  private boolean dynamic = true;
  private transient UUID uuid;
  private final transient IContext context;

  public AbstractCommand(String title, String description, IStore store,
      boolean canUndo, boolean canRedo, List<T> inputs, IContext context)
  {
    this.title = title;
    this.description = description;
    this.store = store;
    this.canUndo = canUndo;
    this.canRedo = canRedo;
    this.context = context;

    this.inputs = new ArrayList<T>();
    this.outputs = new ArrayList<T>();

    // store any inputs, if we have any
    if (inputs != null)
    {
      this.getInputs().addAll(inputs);
    }
  }

  /**
   * provide access to the context object
   * 
   * @return the context object
   */
  protected final IContext getContext()
  {
    return context;
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
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + getUUID().hashCode();
    return result;
  }

  @Override
  public final boolean equals(Object obj)
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
    AbstractCommand<?> other = (AbstractCommand<?>) obj;
    if (!getUUID().equals(other.getUUID()))
    {
      return false;
    }
    return true;
  }

  /**
   * provide a name for the single output dataset
   * 
   * @return a string to use, or null to cancel the operation
   */
  protected abstract String getOutputName();

  /**
   * convenience function, to return the datasets as a comma separated list
   * 
   * @return
   */
  protected String getSubjectList()
  {
    StringBuffer res = new StringBuffer();

    @SuppressWarnings("unchecked")
    Iterator<IStoreItem> iter = (Iterator<IStoreItem>) getInputs().iterator();
    int ctr = 0;
    while (iter.hasNext())
    {
      IStoreItem storeItem = (IStoreItem) iter.next();
      if (ctr++ > 0)
      {
        res.append(", ");
      }
      res.append(storeItem.getName());
    }

    return res.toString();
  }

  protected int getNonSingletonArrayLength(List<IStoreItem> inputs)
  {
    int size = 0;

    Iterator<IStoreItem> iter = inputs.iterator();
    while (iter.hasNext())
    {
      IQuantityCollection<?> thisC = (IQuantityCollection<?>) iter.next();
      if (thisC.getValuesCount() >= 1)
      {
        size = thisC.getValuesCount();
        break;
      }
    }

    return size;
  }

  @UIProperty(name = "Dynamic updates", category = UIProperty.CATEGORY_LABEL)
  @Override
  public boolean getDynamic()
  {
    return dynamic;
  }

  @Override
  public void setDynamic(boolean dynamic)
  {
    this.dynamic = dynamic;
  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    // TODO: do a more intelligent/informed processing of metadata changed
    dataChanged(subject);
  }

  @Override
  public IStoreGroup getParent()
  {
    return _parent;
  }

  @Override
  public final void setParent(IStoreGroup parent)
  {
    _parent = parent;
  }

  @Override
  public final void dataChanged(IStoreItem subject)
  {
    // are we doing live updates?
    if (dynamic)
    {
      // do the recalc
      recalculate();

      // now tell the outputs they have changed
      Iterator<T> iter = getOutputs().iterator();
      while (iter.hasNext())
      {
        T t = (T) iter.next();
        t.fireDataChanged();
      }
    }
  }

  protected abstract void recalculate();

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
  }

  public final IStore getStore()
  {
    return store;
  }

  @UIProperty(name = "Description", category = UIProperty.CATEGORY_LABEL)
  @Override
  public final String getDescription()
  {
    return description;
  }

  @Override
  public void execute()
  {
    // ok, register as a listener with the input files
    Iterator<T> iter = getInputs().iterator();
    while (iter.hasNext())
    {
      T t = (T) iter.next();
      t.addChangeListener(this);
    }
  }

  @Override
  public void undo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, undo not provided");
  }

  @Override
  public void redo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, redo not provided");
  }

  @Override
  public final boolean canUndo()
  {
    return canUndo;
  }

  @Override
  public final boolean canRedo()
  {
    return canRedo;
  }

  @Override
  public final List<T> getInputs()
  {
    return inputs;
  }

  @Override
  public final List<T> getOutputs()
  {
    return outputs;
  }

  public final void addOutput(T output)
  {
    getOutputs().add(output);
  }

  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  @Override
  public String getName()
  {
    return title;
  }

  @Override
  public boolean hasChildren()
  {
    return true;
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
}
