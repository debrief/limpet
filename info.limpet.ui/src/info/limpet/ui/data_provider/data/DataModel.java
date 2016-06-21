/*******************************************************************************
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
 *******************************************************************************/
package info.limpet.ui.data_provider.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.store.StoreGroup;

/**
 * make the Limpet data store suitable for displaying in a tree control
 * 
 * @author ian
 * 
 */
public class DataModel implements ITreeContentProvider
{
  public static final String DEPENDENTS = "Dependents";
  public static final String PRECEDENTS = "Precedents";

  private void addCollectionItems(final List<Object> res,
      final CollectionWrapper cw)
  {
    final ICollection coll = cw.getCollection();

    final ICommand<?> prec = coll.getPrecedent();
    if (prec != null)
    {
      final NamedList dList = new NamedList(cw, PRECEDENTS);
      dList.add(new CommandWrapper(dList, prec));
      res.add(dList);
    }

    final List<ICommand<?>> dep = coll.getDependents();
    if (dep != null)
    {
      final NamedList dList = new NamedList(cw, DEPENDENTS);
      final Iterator<ICommand<?>> dIter = dep.iterator();
      while (dIter.hasNext())
      {
        final ICommand<?> thisI = dIter.next();
        dList.add(new CommandWrapper(dList, thisI));
      }

      // did we find any?
      if (dList.size() > 0)
      {
        res.add(dList);
      }
    }
  }

  private void addCommandItems(final List<Object> res, final CommandWrapper cw)
  {
    final ICommand<?> coll = cw.getCommand();

    final List<? extends IStoreItem> inp = coll.getInputs();
    if (inp != null)
    {
      final NamedList dList = new NamedList(cw, "Inputs");
      final Iterator<? extends IStoreItem> dIter = inp.iterator();
      while (dIter.hasNext())
      {
        final IStoreItem thisI = dIter.next();
        if (thisI instanceof ICollection)
        {
          dList.add(new CollectionWrapper(dList, (ICollection) thisI));
        }
        else if (thisI instanceof IStoreGroup)
        {
          dList.add(new GroupWrapper(dList, (IStoreGroup) thisI));
        }

      }
      // did we find any?
      if (dList.size() > 0)
      {
        res.add(dList);
      }
    }

    final List<? extends IStoreItem> outp = coll.getOutputs();
    if (outp != null)
    {
      final NamedList dList = new NamedList(cw, "Outputs");
      final Iterator<? extends IStoreItem> dIter = outp.iterator();
      while (dIter.hasNext())
      {
        final IStoreItem thisI = dIter.next();
        if (thisI instanceof ICollection)
        {
          dList.add(new CollectionWrapper(dList, (ICollection) thisI));
        }
        else if (thisI instanceof IStoreGroup)
        {
          dList.add(new GroupWrapper(dList, (IStoreGroup) thisI));
        }
      }
      // did we find any?
      if (dList.size() > 0)
      {
        res.add(dList);
      }
    }
  }

  private void addGroupItems(final List<Object> res, final GroupWrapper cw)
  {
    final IStoreGroup coll = cw.getGroup();

    // final NamedList dList = new NamedList(cw, coll.getName());
    final Iterator<IStoreItem> dIter = coll.iterator();
    while (dIter.hasNext())
    {
      final IStoreItem thisI = dIter.next();
      if (thisI instanceof ICollection)
      {
        res.add(new CollectionWrapper(cw, (ICollection) thisI));
      }
      else if (thisI instanceof IStoreGroup)
      {
        res.add(new GroupWrapper(cw, (IStoreGroup) thisI));
      }
    }
    // did we find any?
    // if (dList.size() > 0)
    // {
    // res.add(dList);
    // }
  }

  /**
   * walk back up object tree, to see if the provided element is the top level element (so it has
   * null as parent)
   * 
   * @param element
   *          the object we're considering
   * @return yes/no for if it's at the top of the folder tree
   */
  protected boolean alreadyShown(final LimpetWrapper element)
  {
    final LimpetWrapper lookingFor = element;
    LimpetWrapper current = element;
    boolean found = false;
    boolean walking = true;

    while (walking)
    {
      current = current.getParent();
      if (current == null)
      {
        walking = false;
      }
      else if (current.getSubject() == lookingFor.getSubject())
      {
        found = true;
        walking = false;
      }
    }

    return found;
  }

  @Override
  public void dispose()
  {
  }

  @Override
  @SuppressWarnings(
  {"rawtypes"})
  public Object[] getChildren(final Object parentElement)
  {
    final List<Object> res = new ArrayList<Object>();

    if (parentElement instanceof CollectionWrapper)
    {
      // see if it has predecessors or successors
      addCollectionItems(res, (CollectionWrapper) parentElement);
    }
    else if (parentElement instanceof CommandWrapper)
    {
      // see if it has predecessors or successors
      addCommandItems(res, (CommandWrapper) parentElement);
    }
    else if (parentElement instanceof GroupWrapper)
    {
      // see if it has predecessors or successors
      addGroupItems(res, (GroupWrapper) parentElement);
    }
    else if (parentElement instanceof List)
    {
      final List list = (List) parentElement;
      final Iterator iter = list.iterator();
      while (iter.hasNext())
      {
        res.add(iter.next());
      }
    }

    final Object[] resArray = res.toArray();
    return resArray;
  }

  @Override
  public Object[] getElements(final Object parent)
  {
    final List<LimpetWrapper> list = new ArrayList<LimpetWrapper>();

    if (parent != null)
    {
      StoreGroup store = (StoreGroup) parent;

      final Iterator<IStoreItem> iter = store.iterator();
      while (iter.hasNext())
      {
        final IStoreItem item = iter.next();
        if (item instanceof ICollection)
        {
          list.add(new CollectionWrapper(null, (ICollection) item));
        }
        else if (item instanceof IStoreGroup)
        {
          list.add(new GroupWrapper(null, (IStoreGroup) item));
        }
      }
    }
    else
    {
      throw new RuntimeException("We don't have a data store");
    }

    return list.toArray();
  }

  @Override
  public Object getParent(final Object element)
  {
    return null;
  }

  @Override
  public boolean hasChildren(final Object element)
  {
    boolean res = false;

    if (element instanceof LimpetWrapper)
    {
      final LimpetWrapper core = (LimpetWrapper) element;

      // has it already been shown?
      if (!alreadyShown(core))
      {
        if (element instanceof CollectionWrapper)
        {
          // see if it has predecessors or successors
          final CollectionWrapper cw = (CollectionWrapper) element;

          final ICollection coll = cw.getCollection();
          final boolean hasDependents =
              coll.getDependents() != null && coll.getDependents().size() > 0;
          final boolean hasPrecedents = coll.getPrecedent() != null;
          res = (hasDependents || hasPrecedents);
        }
        else if (element instanceof CommandWrapper)
        {
          // see if it has predecessors or successors
          final CommandWrapper cw = (CommandWrapper) element;
          final ICommand<?> comm = cw.getCommand();

          res = comm.getInputs().size() > 0 || comm.getOutputs().size() > 0;
        }
        else if (element instanceof GroupWrapper)
        {
          // see if it has predecessors or successors
          final GroupWrapper cw = (GroupWrapper) element;
          final IStoreGroup comm = cw.getGroup();

          res = comm.size() > 0;
        }
        else if (element instanceof ArrayList)
        {
          final ArrayList<?> ar = (ArrayList<?>) element;
          return ar.size() > 0;
        }
      }
    }

    return res;
  }

  @Override
  public void inputChanged(final Viewer v, final Object oldInput,
      final Object newInput)
  {
    // if (newInput instanceof InMemoryStore)
    // {
    // _store = (InMemoryStore) newInput;
    // }
    // else
    // {
    // _store = null;
    // }
  }

}
