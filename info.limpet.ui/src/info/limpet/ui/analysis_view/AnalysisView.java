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
package info.limpet.ui.analysis_view;

import info.limpet.IStoreItem;
import info.limpet.analysis.AnalysisLibrary;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class AnalysisView extends CoreAnalysisView
{

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.AnalysisView";

  private transient TableViewer viewer;

  public AnalysisView()
  {
    super(ID, "Analysis");
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    return true;
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
  {
    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(ArrayContentProvider.getInstance());
    viewer.setLabelProvider(new LabelProvider());
    viewer.setInput(null);
    viewer.getTable().setHeaderVisible(true);

    makeActions();
    contributeToActionBars();

    // define the two columns
    final TableViewerColumn colTitle = new TableViewerColumn(viewer, SWT.NONE);
    colTitle.getColumn().setWidth(150);
    colTitle.getColumn().setText("Title");
    colTitle.setLabelProvider(new ColumnLabelProvider()
    {
      @SuppressWarnings("unchecked")
      @Override
      public String getText(final Object element)
      {
        final ArrayList<String> p = (ArrayList<String>) element;
        return p.get(0);
      }
    });

    final TableViewerColumn colValue = new TableViewerColumn(viewer, SWT.NONE);
    colValue.getColumn().setWidth(200);
    colValue.getColumn().setText("Value");
    colValue.setLabelProvider(new ColumnLabelProvider()
    {
      @SuppressWarnings("unchecked")
      @Override
      public String getText(final Object element)
      {
        final ArrayList<String> p = (ArrayList<String>) element;
        return p.get(1);
      }
    });

    setupListener();
  }

  @Override
  protected void doDisplay(final List<IStoreItem> res)
  {
    if (viewer.getControl().isDisposed())
    {
      return;
    }

    // clear the output
    final ArrayList<ArrayList<String>> resList =
        new ArrayList<ArrayList<String>>();

    final AnalysisLibrary ana = new AnalysisLibrary()
    {

      @Override
      protected void presentResults(final List<String> titles,
          final List<String> values)
      {
        // produce two column list
        final Iterator<String> tIter = titles.iterator();
        final Iterator<String> vIter = values.iterator();
        while (tIter.hasNext())
        {
          final ArrayList<String> thisRow = new ArrayList<String>();
          thisRow.add(tIter.next());
          thisRow.add(vIter.next());

          resList.add(thisRow);
        }
      }
    };
    ana.analyse(res);
    viewer.setInput(resList);
  }

  @Override
  protected String getTextForClipboard()
  {
    final StringBuffer output = new StringBuffer();
    @SuppressWarnings("unchecked")
    final ArrayList<ArrayList<String>> list =
        (ArrayList<ArrayList<String>>) viewer.getInput();
    final String separator = System.getProperty("line.separator");
    final Iterator<ArrayList<String>> iter = list.iterator();
    while (iter.hasNext())
    {
      final ArrayList<java.lang.String> arrayList = iter.next();
      output.append(arrayList.get(0));
      output.append(", ");
      output.append(arrayList.get(1));
      output.append(separator);
    }
    return output.toString();
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }
}
