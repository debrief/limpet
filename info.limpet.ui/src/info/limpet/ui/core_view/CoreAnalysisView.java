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
import info.limpet.ui.EventStack;

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

  private transient Action newView;
  private transient Action copyToClipboard;
  private transient Action followSelection;
  private transient ISelectionListener selListener;
  private transient final CollectionComplianceTests aTests;
  private transient final List<IStoreItem> curList =
      new ArrayList<IStoreItem>();
  private transient IChangeListener changeListener;
  private final String _myId;
  private final String _myTitle;
  private ISelection _curSelection;

  /**
   * ensure only the most recent UI update is called
   * 
   */
  protected EventStack _eventStack = new EventStack(150);

  public CoreAnalysisView(final String myId, final String myTitle)
  {
    super();

    _myId = myId;
    _myTitle = myTitle;

    aTests = new CollectionComplianceTests();
    changeListener = new IChangeListener()
    {

      @Override
      public void collectionDeleted(final IStoreItem subject)
      {
        // hmm, we should probably stop listening to that collection
        curList.remove(subject);

        // and update the UI
        display(curList);
      }

      @Override
      public void dataChanged(final IStoreItem subject)
      {
        datasetDataChanged(subject);
        display(curList);
      }

      @Override
      public void metadataChanged(final IStoreItem subject)
      {
        display(curList);
      }
    };
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

  private void clearChangeListeners()
  {
    if (curList.size() > 0)
    {
      final Iterator<IStoreItem> iter = curList.iterator();
      while (iter.hasNext())
      {
        final IStoreItem iC = iter.next();
        iC.removeTransientChangeListener(changeListener);
      }

      // and forget about them all
      curList.clear();
    }
  }

  protected void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected void copyToClipboard()
  {
    final Display display = Display.getCurrent();
    final Clipboard clipboard = new Clipboard(display);
    final String output = getTextForClipboard();

    clipboard.setContents(new Object[]
    {output}, new Transfer[]
    {TextTransfer.getInstance()});
    clipboard.dispose();
  }

  protected void createChangeListeners(final List<IStoreItem> res)
  {
    // store the new list
    curList.addAll(res);

    // now listen to the new list
    final Iterator<IStoreItem> iter = curList.iterator();
    while (iter.hasNext())
    {
      final IStoreItem iC = iter.next();
      iC.addTransientChangeListener(changeListener);
    }
  }

  protected void createNewView()
  {
    // create a new instance of the specified view
    final IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    final IWorkbenchPage page = window.getActivePage();

    try
    {
      final String millis = "" + System.currentTimeMillis();
      page.showView(_myId, millis, IWorkbenchPage.VIEW_ACTIVATE);
    }
    catch (final PartInitException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * the specified item has changed, and should be re-analysed
   * 
   * @param subject
   *          the item that has changed
   */
  protected void datasetDataChanged(final IStoreItem subject)
  {

  }

  /**
   * display this dataset
   * 
   * @param res
   */
  public void display(final List<IStoreItem> res)
  {
    // create the runnable that stores the update
    final Runnable runme = new Runnable()
    {
      @Override
      public void run()
      {
        doDisplay(res);
      }
    };

    // put the event on the top of the stack
    _eventStack.addEvent(runme);
  }

  @Override
  public void dispose()
  {
    // stop listening for data changes
    clearChangeListeners();

    // and stop listening for selection changes
    getSite().getWorkbenchWindow().getSelectionService()
        .removeSelectionListener(selListener);

    super.dispose();
  }

  /**
   * show this set of collections
   * 
   * @param res
   */
  protected abstract void doDisplay(List<IStoreItem> res);

  protected void fillContextMenu(final IMenuManager manager)
  {
    manager.add(copyToClipboard);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected void fillLocalPullDown(final IMenuManager manager)
  {
    manager.add(newView);
    manager.add(copyToClipboard);
    manager.add(followSelection);
    manager.add(new Separator());
  }

  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(copyToClipboard);
    manager.add(followSelection);
  }

  /**
   * external accessor, since we switch off following when a view has been created specifically to
   * view a particular selection
   * 
   * @param val
   */
  public void follow(final List<IStoreItem> data)
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

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  public List<IStoreItem> getData()
  {
    return curList;
  }

  protected abstract String getTextForClipboard();

  protected void makeActions()
  {
    newView = new Action()
    {
      @Override
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
      @Override
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
      @Override
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

  protected void newSelection(final ISelection selection)
  {
    if (selection == _curSelection)
    {
      return;
    }
    else
    {
      _curSelection = selection;
    }

    final List<IStoreItem> res = new ArrayList<IStoreItem>();
    if (selection instanceof StructuredSelection)
    {
      final StructuredSelection str = (StructuredSelection) selection;

      // check if it/they are suitable
      final Iterator<?> iter = str.iterator();
      while (iter.hasNext())
      {
        final Object object = iter.next();
        if (object instanceof IAdaptable)
        {
          final IAdaptable ad = (IAdaptable) object;
          final IStoreItem coll = (IStoreItem) ad.getAdapter(IStoreItem.class);
          if (coll != null)
          {
            res.add(coll);
          }
        }
        else
        {
          // can we adapt it?
          final ArrayList<IAdapterFactory> adapters =
              Activator.getDefault().getAdapters();
          if (adapters != null)
          {
            final Iterator<IAdapterFactory> aIter = adapters.iterator();
            while (aIter.hasNext())
            {
              final IAdapterFactory iAdapterFactory = aIter.next();
              final Object match =
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

    // have we found any, and are they suitable for us?
    if ((res.size()) > 0 && appliesToMe(res, getATests()))
    {
      // ok, stop listening to the old list
      clearChangeListeners();

      // and start listening to the new ones
      createChangeListeners(res);

      // ok, display them
      display(res);
    }
    else
    {
      // ok, nothing to display - clear the graph
      // display(new ArrayList<IStoreItem>());
    }
  }

  protected void setupListener()
  {
    // register as selection listener
    selListener = new ISelectionListener()
    {
      @Override
      public void selectionChanged(final IWorkbenchPart part,
          final ISelection selection)
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

}
