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
import info.limpet.ui.Activator;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet.ui.xy_plot.Helper2D.HContainer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.jface.action.Action;
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
import org.eclipse.ui.IActionBars;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class TabularView extends CoreAnalysisView
{

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.TabularView";
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private Action switchAxes;
  private TableViewer table;
  private Text title;

  public TabularView()
  {
    super(ID, "Tabular view");
  }

  @Override
  protected void makeActions()
  {
    super.makeActions();

    switchAxes = new Action("Switch axes", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        if (switchAxes.isChecked())
        {
          // chart.setOrientation(SWT.VERTICAL);
        }
        else
        {
          // chart.setOrientation(SWT.HORIZONTAL);
        }
      }
    };
    switchAxes.setText("Switch axes");
    switchAxes.setToolTipText("Switch X and Y axes");
    switchAxes.setImageDescriptor(Activator
        .getImageDescriptor("icons/angle.png"));

  }

  @Override
  protected void contributeToActionBars()
  {
    super.contributeToActionBars();
    IActionBars bars = getViewSite().getActionBars();
    bars.getToolBarManager().add(switchAxes);
    bars.getMenuManager().add(switchAxes);
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent)
  {

    // Create the composite to hold the controls
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    // Create the combo to hold the team names
    title = new Text(composite, SWT.BORDER);
    title.setText("document name");

    // Create the table viewer
    table =
        new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

    final Table tableCtrl = table.getTable();
    tableCtrl.setHeaderVisible(true);
    tableCtrl.setLinesVisible(true);

    // sort out the content provider
    MyContentProvider provider = new MyContentProvider();
    table.setContentProvider(provider);
    IBaseLabelProvider labeller = new MyLabelProvider();
    table.setLabelProvider(labeller);

    tableCtrl.setLayoutData(new GridData(GridData.FILL_BOTH));

    // define layout for the viewer
    // GridData gridData = new GridData();
    // gridData.verticalAlignment = GridData.FILL;
    // gridData.horizontalSpan = 1;
    // gridData.grabExcessHorizontalSpace = true;
    // gridData.grabExcessVerticalSpace = true;
    // gridData.horizontalAlignment = GridData.FILL;
    // table.getControl().setLayoutData(gridData);

    TableColumn tc = new TableColumn(tableCtrl, SWT.LEFT);
    tc.setText("Angle");

    tc = new TableColumn(tableCtrl, SWT.LEFT);
    tc.setText("0 degs");

    composite.pack();
    // Pack the columns
    for (int i = 0, n = tableCtrl.getColumnCount(); i < n; i++)
    {
      tableCtrl.getColumn(i).pack();
    }
    makeActions();
    contributeToActionBars();
    // register as selection listener
    setupListener();

  }

  private static class MyLabelProvider implements ITableLabelProvider
  {

    private DecimalFormat _formatter;

    public MyLabelProvider()
    {
      _formatter = new DecimalFormat("000.0");
    }
    
    @Override
    public void addListener(ILabelProviderListener listener)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public boolean isLabelProperty(Object element, String property)
    {
      return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener)
    {
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex)
    {
      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
      @SuppressWarnings("unchecked")
      List<Double> items = (List<Double>) element;
      final String res;

      Double thisD = items.get(columnIndex);

      if (columnIndex == 0)
      {
        res = thisD.intValue() + ":";
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
  }

  private static class MyContentProvider extends ArrayContentProvider
  {

    @Override
    public Object[] getElements(Object inputElement)
    {
      if (inputElement instanceof HContainer)
      {
        HContainer nd = (HContainer) inputElement;
        Object[] data = new Object[nd.rowTitles.length];

        for (int i = 0; i < nd.rowTitles.length; i++)
        {
          List<Double> thisR = new ArrayList<Double>();
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

  @Override
  public void display(List<IStoreItem> res)
  {
    if (res.size() == 0)
    {
      // ok, clear the table
      clearTable();
    }
    else
    {
      // check they're all one dim
      if (aTests.allTwoDim(res) && res.size() == 1)
      {
        // ok, it's a single two-dim dataset
        showTwoDim(res.get(0));
      }
      else
      {
        clearTable();
      }
    }
  }

  private void clearTable()
  {
    // clear the columns
    Table ctrl = table.getTable();
    TableColumn[] cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.dispose();
    }
    
    table.setInput(null);
  }

  private void showTwoDim(IStoreItem item)
  {
    NumberDocument thisQ = (NumberDocument) item;

    clearTable();

    String seriesName = thisQ.getName();

    title.setText(seriesName);

    final NumberDocument nd = (NumberDocument) item;

    // sort out the columns
    HContainer data = Helper2D.convert((DoubleDataset) nd.getDataset());

    double[] aIndices = data.colTitles;

    // clear the columns
    Table ctrl = table.getTable();
    TableColumn[] cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.dispose();
    }

    // ok. the columns
    TableColumn header = new TableColumn(ctrl, SWT.RIGHT);
    header.setText("Angle");
    header.pack();
    for (double a : aIndices)
    {
      TableColumn tc = new TableColumn(ctrl, SWT.LEFT);
      tc.setText("" + (int) a);
      tc.pack();
    }

    // finally get the data displayed
    table.setInput(data);
    
    // lastly, pack the columns
    cols = ctrl.getColumns();
    for (final TableColumn thisC : cols)
    {
      thisC.pack();
    }    
    
    title.pack();
  }

  @Override
  public void setFocus()
  {
    table.getTable().setFocus();
  }

  @Override
  protected boolean appliesToMe(List<IStoreItem> res,
      CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean suitableIndex =
        tests.allIndexed(res) || tests.allNonIndexed(res);
    return allCollections && suitableIndex && (allQuantity || allNonQuantity);
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

}
