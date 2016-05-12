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

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.store.InMemoryStore.StoreGroup;
import info.limpet.stackedcharts.core.view.StackedChartsView;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowInTacticalOverview implements IOperation<IStoreItem>
{
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  private final String _title;

  public ShowInTacticalOverview(String title)
  {
    _title = title;
  }

  protected CollectionComplianceTests getTests()
  {
    return aTests;
  }

  public Collection<ICommand<IStoreItem>> actionsFor(
      List<IStoreItem> selection, IStore destination, IContext context)
  {
    Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    {
      ICommand<IStoreItem> newC =
          new ShowInTacticalOverviewOperation(_title, selection, context);
      res.add(newC);
    }

    return res;
  }

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean res = true;

    boolean nonEmpty = aTests.nonEmpty(selection);
    boolean isFolder = aTests.allGroups(selection);
    boolean onlyOne = aTests.exactNumber(selection, 1);
    if (nonEmpty && isFolder && onlyOne)
    {
      // ok, start looking deeper
      StoreGroup sg = (StoreGroup) selection.get(0);

      List<IStoreItem> children = sg.children();
      Iterator<IStoreItem> iter = children.iterator();
      while (iter.hasNext())
      {
        IStoreItem thisG = (IStoreItem) iter.next();

        // check it's a group
        if (thisG instanceof StoreGroup)
        {
          if (thisG.getName().equals("Primary"))
          {
            StoreGroup primary = (StoreGroup) thisG;
            if (primary.size() != 3)
            {
              res = false;
              break;
            }
          }
          else if (thisG.getName().equals("Secondary"))
          {
            StoreGroup secondary = (StoreGroup) thisG;
            if (secondary.size() != 3)
            {
              res = false;
              break;
            }
          }
          else if (thisG.getName().equals("Relative"))
          {
            StoreGroup relative = (StoreGroup) thisG;
            if (relative.size() != 5)
            {
              res = false;
              break;
            }
          }
          else if (thisG.getName().equals("Sensor"))
          {
            StoreGroup sensor = (StoreGroup) thisG;
            if (sensor.size() != 1)
            {
              res = false;
              break;
            }
          }
          else
          {
            res = false;
            break;
          }
        }
        else
        {
          res = false;
          break;
        }
      }

    }
    else
    {
      res = false;
    }
    return res;
  }

  public static class ShowInTacticalOverviewOperation extends
      AbstractCommand<IStoreItem>
  {
    public ShowInTacticalOverviewOperation(String title,
        List<IStoreItem> selection, IContext context)
    {
      super(title, "Show selection in Tactical Overview", null, false, false,
          selection, context);
    }

    @Override
    protected String getOutputName()
    {
      return null;
    }

    @Override
    public void execute()
    {
      String secId = getInputs().toString();

      String viewId = StackedChartsView.ID;

      // create a new instance of the specified view
      IWorkbenchWindow window =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      IWorkbenchPage page = window.getActivePage();

      try
      {
        page.showView(viewId, secId, IWorkbenchPage.VIEW_ACTIVATE);
      }
      catch (PartInitException e)
      {
        e.printStackTrace();
      }

      // try to recover the view
      IViewReference viewRef = page.findViewReference(viewId, secId);
      if (viewRef != null)
      {
        IViewPart theView = viewRef.getView(true);

        // double check it's what we're after
        if (theView instanceof StackedChartsView)
        {
          StackedChartsView cv = (StackedChartsView) theView;

          // create the charts set model
          ChartSet model = createModelFor(this.getInputs());

          if (model != null)
          {
            // set follow selection to off
            // cv.follow(getInputs());
            cv.setModel(model);
          }

        }
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

  }

  public static ChartSet createModelFor(List<IStoreItem> selection)
  {
    StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

    ChartSet res = factory.createChartSet();
    res.setOrientation(Orientation.VERTICAL);

    // ok, start looking deeper
    StoreGroup sg = (StoreGroup) selection.get(0);

    List<IStoreItem> children = sg.children();
    Iterator<IStoreItem> iter = children.iterator();
    while (iter.hasNext())
    {
      IStoreItem thisG = (IStoreItem) iter.next();

      // check it's a group
      if (thisG instanceof StoreGroup)
      {
        if (thisG.getName().equals("Primary"))
        {
          StoreGroup primary = (StoreGroup) thisG;
          createStateChart(factory, primary, "Primary State", res);
        }
        else if (thisG.getName().equals("Secondary"))
        {
          StoreGroup primary = (StoreGroup) thisG;
          createStateChart(factory, primary, "Secondary State", res);
        }
        else if (thisG.getName().equals("Relative"))
        {
          StoreGroup relative = (StoreGroup) thisG;
          if (relative.size() == 5)
          {
            // ok, create the primary chart
            Chart relativeChart = factory.createChart();
            res.getCharts().add(relativeChart);
            relativeChart.setName("Relative");

            // and the axes
            DependentAxis rangeAxis = factory.createDependentAxis();
            rangeAxis.setName("Range");
            relativeChart.getMinAxes().add(rangeAxis);
            DependentAxis brgAxis = factory.createDependentAxis();
            brgAxis.setName("Bearing");
            relativeChart.getMinAxes().add(brgAxis);

            DependentAxis relBrgAxis = factory.createDependentAxis();
            relBrgAxis.setName("Rel Bearing");
            relativeChart.getMinAxes().add(relBrgAxis);

            DependentAxis atbAxis = factory.createDependentAxis();
            atbAxis.setName("ATB");
            relativeChart.getMinAxes().add(atbAxis);

            
            DependentAxis brgRateAxis = factory.createDependentAxis();
            brgRateAxis.setName("Bearing Rate");
            relativeChart.getMinAxes().add(brgRateAxis);

            // now sort out the data series
            Iterator<IStoreItem> dIter = relative.children().iterator();
            while (dIter.hasNext())
            {
              IStoreItem iStoreItem = (IStoreItem) dIter.next();
              if (iStoreItem.getName().equals("Range"))
              {
                createDataset(factory, iStoreItem, "Range", rangeAxis, res);
              }
              else if (iStoreItem.getName().equals("Bearing"))
              {
                createDataset(factory, iStoreItem, "Bearing", brgAxis, res);
              }
              else if (iStoreItem.getName().equals("Rel Brg"))
              {
                createDataset(factory, iStoreItem, "Rel Brg", relBrgAxis, res);
              }
              else if (iStoreItem.getName().equals("ATB"))
              {
                createDataset(factory, iStoreItem, "ATB", atbAxis, res);
              }
              else if (iStoreItem.getName().equals("Brg Rate"))
              {
                createDataset(factory, iStoreItem, "Brg Rate", brgRateAxis, res);
              }
            }
          }
        }
        else if (thisG.getName().equals("Sensor"))
        {
          StoreGroup sensor = (StoreGroup) thisG;
          if (sensor.size() != 1)
          {
            // and lastly the sensor chart
          }
        }
      }
    }

    return res;
  }

  protected static void createStateChart(StackedchartsFactory factory,
      StoreGroup group, String chartName, ChartSet set)
  {
    if (group.size() == 3)
    {
      // ok, create the primary chart
      Chart chart = factory.createChart();
      set.getCharts().add(chart);
      
      chart.setName(chartName);

      // and the axes
      DependentAxis courseAxis = factory.createDependentAxis();
      chart.getMinAxes().add(courseAxis);
      courseAxis.setName("Course");
      DependentAxis speedAxis = factory.createDependentAxis();
      speedAxis.setName("Speed");
      chart.getMaxAxes().add(speedAxis);
      DependentAxis depthAxis = factory.createDependentAxis();
      depthAxis.setName("Depth");
      chart.getMinAxes().add(depthAxis);

      // now sort out the data series
      Iterator<IStoreItem> dIter = group.children().iterator();
      while (dIter.hasNext())
      {
        IStoreItem iStoreItem = (IStoreItem) dIter.next();
        final String thisName = iStoreItem.getName();
        if (thisName.contains("Course"))
        {
          createDataset(factory, iStoreItem, thisName, courseAxis, set);
        }
        else if (thisName.contains("Speed"))
        {
          createDataset(factory, iStoreItem, thisName, speedAxis, set);
        }
        else if (thisName.contains("Depth"))
        {
          createDataset(factory, iStoreItem, thisName, depthAxis, set);
        }
      }
    }
  }

  protected static void createDataset(StackedchartsFactory factory,
      IStoreItem iStoreItem, String name, DependentAxis axis, ChartSet chartSet)
  {
    ITemporalQuantityCollection<?> it =
        (ITemporalQuantityCollection<?>) iStoreItem;
    List<Long> times = it.getTimes();
    List<?> values = it.getValues();

    Dataset newD = factory.createDataset();
    newD.setName(name);

    for (int i = 0; i < times.size(); i++)
    {
      double time = times.get(i);
      
      @SuppressWarnings("unchecked")
      Measurable<Quantity> quantity = (Measurable<Quantity>) values.get(i);
      @SuppressWarnings("unchecked")
      double value = quantity.doubleValue((Unit<Quantity>) it.getUnits());
      DataItem newI = factory.createDataItem();
      newI.setDependentVal(value);
      newI.setIndependentVal(time);
      newD.getMeasurements().add(newI);
    }

    // and it to an axis
    axis.getDatasets().add(newD);
    
    // and add the dataset to the central chartset
    chartSet.getDatasets().add(newD);
  }

}
