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
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.view.StackedChartsView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowInStackedChartsOverview implements IOperation<IStoreItem>
{
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  private final String _title;

  public ShowInStackedChartsOverview(String title)
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
          new ShowInStackedChartsOperation(_title, selection, context);
      res.add(newC);
    }

    return res;
  }

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = aTests.nonEmpty(selection);
    boolean allData = aTests.allCollections(selection);
    boolean allQuantity = aTests.allQuantity(selection);
    boolean allTemporal = aTests.allTemporal(selection);

    return (nonEmpty && allData && allQuantity && allTemporal);
  }

  public static class ShowInStackedChartsOperation extends
      AbstractCommand<IStoreItem>
  {
    public ShowInStackedChartsOperation(String title,
        List<IStoreItem> selection, IContext context)
    {
      super(title, "Show selection in Stacked Charts", null, false, false,
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

            // // take a copy of the model
            // URI resourceURI = URI.createFileURI("/home/ian/tacticalOverview.stackedcharts");
            // Resource resource = new ResourceSetImpl().createResource(resourceURI);
            // System.out.println("saving to:" + resourceURI.toFileString());
            // resource.getContents().add(model);
            // try
            // {
            // resource.save(null);
            // }
            // catch (IOException e)
            // {
            // e.printStackTrace();
            // }
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
    IndependentAxis timeAxis = factory.createIndependentAxis();
    timeAxis.setAxisType(factory.createDateAxis());
    res.setSharedAxis(timeAxis);

    Chart currentChart = factory.createChart();
    res.getCharts().add(currentChart);

    Map<String, DependentAxis> axes = new HashMap<String, DependentAxis>();
    Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      IStoreItem iStoreItem = (IStoreItem) sIter.next();
      TemporalQuantityCollection<?> tq =
          (TemporalQuantityCollection<?>) iStoreItem;

      // do we have a suitable axis?
      String units = tq.getUnits().toString();
      DependentAxis match = axes.get(units);
      if (match == null)
      {
        match = factory.createDependentAxis();
        match.setName(units);
        match.setAxisType(factory.createNumberAxis());
        axes.put(units, match);

        // are we due to create a new chart?
        if (currentChart.getMinAxes().size() + currentChart.getMaxAxes().size() >= 4)
        {
          currentChart = factory.createChart();
          res.getCharts().add(currentChart);
        }

        final EList<DependentAxis> target;
        if (currentChart.getMinAxes().size() <= currentChart.getMaxAxes()
            .size())
        {
          target = currentChart.getMinAxes();
        }
        else
        {
          target = currentChart.getMaxAxes();
        }
        target.add(match);

      }

      // ok, create the dataset
      Dataset dataset = factory.createDataset();
      dataset.setName(tq.getName() + "(" + tq.getUnits() + ")");
      match.getDatasets().add(dataset);

      // and now work through the data
      List<?> values = tq.getValues();
      List<Long> times = tq.getTimes();
      Iterator<?> vIter = values.iterator();
      Iterator<Long> tIter = times.iterator();

      while (tIter.hasNext())
      {
        Long long1 = (Long) tIter.next();

        @SuppressWarnings("unchecked")
        Measurable<Quantity> quantity = (Measurable<Quantity>) vIter.next();
        @SuppressWarnings("unchecked")
        double value = quantity.doubleValue((Unit<Quantity>) tq.getUnits());

        DataItem di = factory.createDataItem();
        di.setDependentVal(value);
        di.setIndependentVal(long1);
        dataset.getMeasurements().add(di);
      }

    }

    return res;
  }

}
