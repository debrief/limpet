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
package info.limpet.ui.operations;

import info.limpet.IContext;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet2.ICommand;
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.operations.AbstractCommand;
import info.limpet2.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowInNamedView implements IOperation
{
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  private final String theId;
  private final String _title;

  public ShowInNamedView(String title, String id)
  {
    _title = title;
    theId = id;
  }

  protected CollectionComplianceTests getTests()
  {
    return aTests;
  }

  public List<ICommand> actionsFor(
      List<IStoreItem> selection, IStoreGroup destination, IContext context)
  {
    List<ICommand> res =
        new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      ICommand newC =
          new ShowInViewOperation(_title, selection, theId, context);
      res.add(newC);
    }

    return res;
  }

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    return aTests.allCollections(selection) && aTests.nonEmpty(selection);
  }

  public static class ShowInViewOperation extends AbstractCommand
  {

    private final String _id;

    public ShowInViewOperation(String title, List<IStoreItem> selection,
        String id, IContext context)
    {
      super(title, "Show selection in specified view", null, false, false,
          selection, context);
      _id = id;
    }

//    @Override
//    protected String getOutputName()
//    {
//      return null;
//    }

    @Override
    public void execute()
    {
      String secId = getInputs().toString();

      // create a new instance of the specified view
      IWorkbenchWindow window =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      IWorkbenchPage page = window.getActivePage();

      try
      {
        page.showView(_id, secId, IWorkbenchPage.VIEW_ACTIVATE);
      }
      catch (PartInitException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // try to recover the view
      IViewReference viewRef = page.findViewReference(_id, secId);
      if (viewRef != null)
      {
        IViewPart theView = viewRef.getView(true);

        // double check it's what we're after
        if (theView instanceof CoreAnalysisView)
        {
          CoreAnalysisView cv = (CoreAnalysisView) theView;

          // set follow selection to off
          cv.follow(getInputs());
        }
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

  }

}
