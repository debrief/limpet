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

import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.persistence.FileParser;
import info.limpet.persistence.csv.CsvParser;
import info.limpet.persistence.rep.RepParser;
import info.limpet.ui.Activator;
import info.limpet.ui.editors.LimpetDragListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

public class DataManagerDropAdapter extends ViewerDropAdapter
{

  private IStoreGroup _store;

  public DataManagerDropAdapter(Viewer viewer, IStoreGroup store)
  {
    super(viewer);
    this._store = store;
  }

  @Override
  public boolean performDrop(Object data)
  {
    if (!(data instanceof String[]) && !(data instanceof List[])
        && !(data instanceof String))
    {
      return false;
    }
    if (data instanceof String[])
    {
      String[] fileNames = (String[]) data;
      return filesDropped(fileNames);
    }
    if (data instanceof String)
    {
      // ok, it's some data from the control itself.

      final boolean validTarget;
      final Object target = getCurrentTarget();
      if (target == getSelectedObject())
      {
        return false;
      }
      if (target == null)
      {
        validTarget = true;
      }
      else if (target instanceof GroupWrapper)
      {
        validTarget = true;
      }
      else
      {
        validTarget = false;
      }

      if (!validTarget)
      {
        return false;
      }
      else
      {
        String str = (String) data;
        String[] items = str.split(LimpetDragListener.SEPARATOR);
        for (int i = 0; i < items.length; i++)
        {
          String thisS = items[i];

          // find the item
          IStoreItem item = _store.get(UUID.fromString(thisS));

          if (item == null)
          {
            Activator.logError(Status.ERROR,
                "Failed to find matching UUID, from another editor?", null);
            return false;
          }

          IStoreGroup hisParent = item.getParent();
          if (hisParent != null)
          {
            // remove the item from the current parent
            hisParent.remove(item);
          }
          else
          {
            if (_store instanceof IStoreGroup)
            {
              IStoreGroup mem = (IStoreGroup) _store;
              mem.remove(item);
            }
          }

          // sort out the object
          if (target instanceof GroupWrapper)
          {
            GroupWrapper group = (GroupWrapper) target;
            // and add to the new one
            group.getGroup().add(item);
          }
          else
          {
            // ok, put it at the top level
            _store.add(item);
          }
        }
        changed();
        return true;
      }

    }
    // else if (data instanceof List[])
    // {
    // Object target = getCurrentTarget();
    // if (target == getSelectedObject())
    // {
    // return false;
    // }
    // if (target instanceof GroupWrapper)
    // {
    // for (@SuppressWarnings("rawtypes")
    // List list : (List[]) data)
    // {
    // for (Object object : list)
    // {
    // if (object instanceof GroupWrapper)
    // {
    // GroupWrapper groupWrapper = (GroupWrapper) object;
    // List<IStoreItem> l = new ArrayList<IStoreItem>();
    // StoreGroup storeGroup = new StoreGroup(groupWrapper.toString());
    // l.add(storeGroup);
    // storeGroup.addAll(groupWrapper.getGroup());
    // ((GroupWrapper) target).getGroup().addAll(l);
    // }
    // else if (object instanceof CollectionWrapper)
    // {
    // CollectionWrapper collectionWrapper = (CollectionWrapper) object;
    // ((GroupWrapper) target).getGroup().add(
    // collectionWrapper.getCollection());
    // }
    // }
    // }
    // changed();
    // return true;
    // }
    // }
    return false;
  }

  @Override
  public boolean validateDrop(Object target, int operation,
      TransferData transferType)
  {
    boolean validTarget = target == null || target instanceof GroupWrapper;

    return FileTransfer.getInstance().isSupportedType(transferType)
        || TextTransfer.getInstance().isSupportedType(transferType)
        || validTarget;
  }

  private boolean filesDropped(String[] fileNames)
  {
    if (fileNames != null)
    {
      for (int i = 0; i < fileNames.length; i++)
      {
        String fileName = fileNames[i];
        if (fileName != null && fileName.toLowerCase().endsWith(".csv"))
        {
          try
          {
            parseCsv(fileName);
          }
          catch (IOException e)
          {
            MessageDialog.openWarning(getViewer().getControl().getShell(),
                "Warning", "Cannot drop '" + fileName
                    + "'. See log for more details");
            Activator.log(e);
            return false;
          }
        }
        else if (fileName != null && fileName.endsWith(".lap"))
        {
          // defer that
          try
          {
            parseLap(fileName);
          }
          catch (IOException e)
          {
            MessageDialog.openWarning(getViewer().getControl().getShell(),
                "Warning", "Cannot drop '" + fileName
                    + "'. See log for more details");
            Activator.log(e);
            return false;
          }
        }
        else if (fileName != null && fileName.toLowerCase().endsWith(".rep"))
        {
          try
          {
            parseRep(fileName);
          }
          catch (IOException e)
          {
            MessageDialog.openWarning(getViewer().getControl().getShell(),
                "Warning", "Cannot drop '" + fileName
                    + "'. See log for more details");
            Activator.log(e);
            return false;
          }
        }
        else if (fileName != null && fileName.toLowerCase().endsWith(".dsf"))
        {
          try
          {
            // ok, re-use the REP parser
            parseRep(fileName);
          }
          catch (IOException e)
          {
            MessageDialog.openWarning(getViewer().getControl().getShell(),
                "Warning", "Cannot drop '" + fileName
                    + "'. See log for more details");
            Activator.log(e);
            return false;
          }
        }
      }
    }
    return true;
  }

  private void parseThis(final FileParser parser, final String fileName)
      throws IOException
  {
    List<IStoreItem> collections = parser.parse(fileName);
    Object target = getCurrentTarget();
    if (target instanceof GroupWrapper)
    {
      ((GroupWrapper) target).getGroup().addAll(collections);
    }
    else
    {
      _store.addAll(collections);
    }
    changed();
  }

  private void parseCsv(String fileName) throws IOException
  {
    parseThis(new CsvParser(), fileName);
  }

  private void parseRep(String fileName) throws IOException
  {
    parseThis(new RepParser(), fileName);
  }

  private void changed()
  {
    getViewer().refresh();
  }

  private void parseLap(String fileName) throws IOException
  {
    Object target = getCurrentTarget();
    IStoreGroup store = new XStreamHandler().load(fileName);
    if (store != null)
    {
      final List<IStoreItem> list = new ArrayList<IStoreItem>();
      final Iterator<IStoreItem> iter = ((IStoreGroup) store).iterator();
      while (iter.hasNext())
      {
        final IStoreItem item = iter.next();
        list.add(item);
      }
      if (target instanceof GroupWrapper)
      {
        GroupWrapper groupWrapper = (GroupWrapper) target;
        groupWrapper.getGroup().addAll(list);
      }
      else
      {
        _store.addAll(list);
      }
      changed();
    }
  }

}
