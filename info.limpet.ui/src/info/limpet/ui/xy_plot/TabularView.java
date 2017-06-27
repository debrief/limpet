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
package info.limpet.ui.xy_plot;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.xy_plot.Helper2D.HContainer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class TabularView extends CommonGridView
{

  private static class MyContentProvider extends ArrayContentProvider
  {

    @Override
    public Object[] getElements(final Object inputElement)
    {
      if (inputElement instanceof HContainer)
      {
        final HContainer nd = (HContainer) inputElement;
        final Object[] data = new Object[nd.rowTitles.length];

        for (int i = 0; i < nd.rowTitles.length; i++)
        {
          final List<Double> thisR = new ArrayList<Double>();
          // start off with the row title
          thisR.add(nd.rowTitles[i]);

          for (int j = 0; j < nd.colTitles.length; j++)
          {
            thisR.add(nd.values[i][j]);
          }
          data[i] = thisR;
        }

        return data;
      }

      return null;
    }
  }

  private class MyLabelProvider implements ITableLabelProvider
  {

    @Override
    public void addListener(final ILabelProviderListener listener)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex)
    {
      return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex)
    {
      @SuppressWarnings("unchecked")
      final List<Double> items = (List<Double>) element;
      final String res;

      final Double thisD = items.get(columnIndex);

      if (columnIndex == 0)
      {
        res = _formatter.format(thisD) + ":";
      }
      else
      {
        if ((thisD != null) && !thisD.equals(Double.NaN))
        {
          res = _formatter.format(thisD);
        }
        else
        {
          res = " ";
        }
      }
      return res;
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property)
    {
      return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener)
    {
    }
  }

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.TabularView";

  private TableViewer table;

  private final DecimalFormat _formatter;

  public TabularView()
  {
    super(ID, "Tabular view");
    _formatter = new DecimalFormat("0.00");

  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean suitableIndex =
        tests.allIndexed(res) || tests.allNonIndexed(res);
    return allCollections && suitableIndex && (allQuantity || allNonQuantity);
  }

  @Override
  protected void clearChart()
  {
    // clear the columns
    final Table ctrl = table.getTable();
    final TableColumn[] cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.dispose();
    }

    table.setInput(null);
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
  {

    // Create the composite to hold the controls
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    // Create the combo to hold the team names
    titleLbl = new Text(composite, SWT.NONE);
    titleLbl.setText(" ");

    // Create the table viewer
    table = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);

    final Table tableCtrl = table.getTable();
    tableCtrl.setHeaderVisible(true);
    tableCtrl.setLinesVisible(true);

    // sort out the content provider
    final MyContentProvider provider = new MyContentProvider();
    table.setContentProvider(provider);
    final IBaseLabelProvider labeller = new MyLabelProvider();
    table.setLabelProvider(labeller);

    tableCtrl.setLayoutData(new GridData(GridData.FILL_BOTH));

    makeActions();
    contributeToActionBars();

    // register as selection listener
    setupListener();
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  @Override
  public void setFocus()
  {
    table.getTable().setFocus();
  }

  @Override
  protected void show(final IStoreItem item)
  {
    final NumberDocument thisQ = (NumberDocument) item;

    clearChart();

    final NumberDocument nd = (NumberDocument) item;

    final String seriesName = thisQ.getName();

    titleLbl.setText(seriesName + " (" + nd.getUnits().toString() + ")");

    // sort out the columns
    final HContainer data = Helper2D.convert((DoubleDataset) nd.getDataset());

    final double[] aIndices = data.colTitles;

    // clear the columns
    final Table ctrl = table.getTable();
    TableColumn[] cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.dispose();
    }

    // ok. the columns
    final TableColumn header = new TableColumn(ctrl, SWT.RIGHT);
    header.setText(nd.getIndexUnits().toString());

    for (final double a : aIndices)
    {
      final TableColumn tc = new TableColumn(ctrl, SWT.RIGHT);
      tc.setText("" + _formatter.format(a));
    }

    // finally get the data displayed
    table.setInput(data);

    // lastly, pack the columns (now that we've loaded the data)
    cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.pack();
    }

    titleLbl.pack();
  }
}
