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
package info.limpet.ui.core_view;

import info.limpet.IChangeListener;
import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.Activator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public abstract class CoreAnalysisView extends ViewPart
{

  private Action newView;
  private Action copyToClipboard;
  private Action followSelection;
  private ISelectionListener selListener;
  private final CollectionComplianceTests aTests;
  private final List<IStoreItem> curList = new ArrayList<IStoreItem>();
  private IChangeListener changeListener;
  private final String _myId;
  private final String _myTitle;

  public CoreAnalysisView(String myId, String myTitle)
  {
    super();

    _myId = myId;
    _myTitle = myTitle;

    aTests = new CollectionComplianceTests();
    changeListener = new IChangeListener()
    {

      @Override
      public void dataChanged(IStoreItem subject)
      {
        datasetDataChanged(subject);
        display(curList);
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
        // hmm, we should probably stop listening to that collection
        curList.remove(subject);

        // and update the UI
        display(curList);
      }

      @Override
      public void metadataChanged(IStoreItem subject)
      {
        display(curList);
      }
    };
  }

  /**
   * the specified item has changed, and should be re-analysed
   * 
   * @param subject
   *          the item that has changed
   */
  protected void datasetDataChanged(IStoreItem subject)
  {

  }

  /**
   * external accessor, since we switch off following when a view has been created specifically to
   * view a particular selection
   * 
   * @param val
   */
  public void follow(List<IStoreItem> data)
  {
    followSelection.setChecked(false);
    followSelection.setEnabled(false);
    followSelection
        .setDescription("Disabled - view focussed on particular dataset");

    display(data);

    // also set the title
    if (data.size() == 1)
    {
      this.setPartName(_myTitle + " - " + data.get(0).getName());
    }
    else
    {
      this.setPartName(_myTitle + " - multiple datasets");
    }

  }

  protected void newSelection(ISelection selection)
  {
    List<IStoreItem> res = new ArrayList<IStoreItem>();
    if (selection instanceof StructuredSelection)
    {
      StructuredSelection str = (StructuredSelection) selection;

      // check if it/they are suitable
      Iterator<?> iter = str.iterator();
      while (iter.hasNext())
      {
        Object object = (Object) iter.next();
        if (object instanceof IAdaptable)
        {
          IAdaptable ad = (IAdaptable) object;
          IStoreItem coll = (IStoreItem) ad.getAdapter(IStoreItem.class);
          if (coll != null)
          {
            res.add(coll);
          }
        }
        else
        {
          // can we adapt it?
          ArrayList<IAdapterFactory> adapters =
              Activator.getDefault().getAdapters();
          if (adapters != null)
          {
            Iterator<IAdapterFactory> aIter = adapters.iterator();
            while (aIter.hasNext())
            {
              IAdapterFactory iAdapterFactory = (IAdapterFactory) aIter.next();
              Object match =
                  iAdapterFactory.getAdapter(object, IStoreItem.class);
              if (match != null)
              {
                res.add((IStoreItem) match);
                break;
              }

            }
          }
        }
      }
    }

    // ok, stop listening to the old list
    clearChangeListeners();

    // have we found any, and are they suitable for us?
    if ((res.size()) > 0 && appliesToMe(res, getATests()))
    {
      // store the new list
      curList.addAll(res);

      // now listen to the new list
      Iterator<IStoreItem> iter = curList.iterator();
      while (iter.hasNext())
      {
        IStoreItem iC = iter.next();
        iC.addChangeListener(changeListener);
      }

      // ok, display them
      display(res);
    }
    else
    {
      // ok, nothing to display - clear the graph
      display(new ArrayList<IStoreItem>());
    }
  }

  private void clearChangeListeners()
  {
    if (curList.size() > 0)
    {
      Iterator<IStoreItem> iter = curList.iterator();
      while (iter.hasNext())
      {
        IStoreItem iC = iter.next();
        iC.removeChangeListener(changeListener);
      }

      // and forget about them all
      curList.clear();
    }
  }

  public List<IStoreItem> getData()
  {
    return curList;
  }

  /**
   * determine if this set of collections are suitable for displaying
   * 
   * @param res
   * @param aTests2
   * @return
   */
  protected abstract boolean appliesToMe(List<IStoreItem> res,
      CollectionComplianceTests aTests2);

  /**
   * show this set of collections
   * 
   * @param res
   */
  public abstract void display(List<IStoreItem> res);

  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(newView);
    manager.add(copyToClipboard);
    manager.add(followSelection);
    manager.add(new Separator());
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(copyToClipboard);
    manager.add(followSelection);
  }

  protected void copyToClipboard()
  {
    Display display = Display.getCurrent();
    Clipboard clipboard = new Clipboard(display);
    String output = getTextForClipboard();

    clipboard.setContents(new Object[]
    {output}, new Transfer[]
    {TextTransfer.getInstance()});
    clipboard.dispose();
  }

  protected void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected void setupListener()
  {
    // register as selection listener
    selListener = new ISelectionListener()
    {
      public void selectionChanged(IWorkbenchPart part, ISelection selection)
      {
        // are we following the selection?
        if (followSelection.isChecked())
        {
          newSelection(selection);
        }
      }
    };
    getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(
        selListener);
  }

  public void dispose()
  {
    // stop listening for data changes
    clearChangeListeners();

    // and stop listening for selection changes
    getSite().getWorkbenchWindow().getSelectionService()
        .removeSelectionListener(selListener);

    super.dispose();
  }

  protected abstract String getTextForClipboard();

  protected void makeActions()
  {
    newView = new Action()
    {
      public void run()
      {
        createNewView();
      }
    };
    newView.setText("New instance of " + _myTitle);
    newView.setToolTipText("Create a fresh instance of this view");
    newView.setImageDescriptor(Activator
        .getImageDescriptor("icons/newView.png"));

    copyToClipboard = new Action()
    {
      public void run()
      {
        copyToClipboard();
      }
    };
    copyToClipboard.setText("Copy to clipboard");
    copyToClipboard.setToolTipText("Copy analysis to clipboard");
    copyToClipboard.setImageDescriptor(PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

    followSelection = new Action("Follow selection", SWT.TOGGLE)
    {
      public void run()
      {
        // don't worry, we can ignore the events
      }
    };
    followSelection.setChecked(true);
    followSelection.setToolTipText("Link with selection");
    followSelection.setImageDescriptor(PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

  }

  protected void createNewView()
  {
    // create a new instance of the specified view
    IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();

    try
    {
      String millis = "" + System.currentTimeMillis();
      page.showView(_myId, millis, IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (PartInitException e)
    {
      e.printStackTrace();
    }
  }

  protected void fillContextMenu(IMenuManager manager)
  {
    manager.add(copyToClipboard);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
