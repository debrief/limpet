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
import info.limpet.IDocument;
import info.limpet.IGroupWrapper;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.view.StackedChartsView;
import info.limpet.stackedcharts.ui.view.StackedChartsView.ControllableDate;
import info.limpet.ui.Activator;
import info.limpet.ui.data_provider.data.DocumentWrapper;
import info.limpet.ui.range_slider.RangeSliderView;
import info.limpet.ui.stacked.LimpetStackedChartsAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowInStackedChartsOverview implements IOperation
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

  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      ICommand newC =
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
    boolean allTemporal = aTests.allEqualIndexed(selection);

    return (nonEmpty && allData && allQuantity && allTemporal);
  }

  public static class ShowInStackedChartsOperation extends AbstractCommand
  {
    public ShowInStackedChartsOperation(String title,
        List<IStoreItem> selection, IContext context)
    {
      super(title, "Show selection in Stacked Charts", null, false, false,
          selection, context);
    }

    // @Override
    // protected String getOutputName()
    // {
    // return null;
    // }

    @Override
    public void execute()
    {
      String secId = getInputs().toString();
      String viewId = StackedChartsView.ID;

      // create a new instance of the specified view
      StackedChartsView chartView = null;

      if (PlatformUI.isWorkbenchRunning())
      {
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
          return;
        }

        // try to recover the view
        IViewReference viewRef = page.findViewReference(viewId, secId);
        if (viewRef != null)
        {
          final IViewPart theView = viewRef.getView(true);
          if (theView != null && theView instanceof StackedChartsView)
          {
            chartView = (StackedChartsView) theView;
          }
          else
          {
            Activator.getDefault().getLog().log(
                new Status(Status.ERROR,
                    "Failed to create Stacked Charts View", null));
            return;
          }
        }
      }

      // did we find the chart?
      if (chartView != null)
      {
        // create the charts set model
        ChartSet model = createModelFor(this.getInputs());

        if (model != null)
        {
          // set follow selection to off
          // cv.follow(getInputs());
          chartView.setModel(model);
        }

        final StackedChartsView finalView = chartView;

        // also, see if we can listen to changes in it
        final IStoreGroup group =
            RangeSliderView.findTopParent(this.getInputs().get(0));
        if (group != null)
        {
          final PropertyChangeListener listener = new PropertyChangeListener()
          {

            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
              // ok, update the time now
              Date newTime = (Date) evt.getNewValue();
              finalView.updateTime(newTime);
            }
          };
          group.addTimeChangeListener(listener);

          Runnable closer = new Runnable()
          {
            @Override
            public void run()
            {
              group.removeTimeChangeListener(listener);
            }
          };
          chartView.addRunOnCloseCallback(closer);

          ControllableDate timeC = new ControllableDate()
          {

            @Override
            public void setDate(Date date)
            {
              group.setTime(date);
            }

            @Override
            public Date getDate()
            {
              return group.getTime();
            }
          };
          chartView.setDateSupport(timeC);

        }

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

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

  }

  protected static NumberDocument getFirstCollectionFor(
      List<IStoreItem> selection)
  {
    NumberDocument res = null;

    if (selection != null)
    {
      IStoreItem first = selection.iterator().next();
      if (first instanceof IGroupWrapper)
      {
        IGroupWrapper group = (IGroupWrapper) first;
        IStoreItem nextItem = group.getGroup().iterator().next();
        while (nextItem instanceof IStoreGroup)
        {
          IStoreGroup storeGroup = (IStoreGroup) nextItem;
          nextItem = storeGroup.iterator().next();
        }
        if (nextItem != null)
        {
          IDocument<?> coll = (IDocument<?>) nextItem;
          if (coll.isQuantity() && coll.isIndexed())
          {
            res = (NumberDocument) coll;
          }
        }
      }
      else if (first instanceof DocumentWrapper)
      {
        DocumentWrapper cw = (DocumentWrapper) first;
        IDocument<?> collection = cw.getDocument();
        if (collection.isQuantity() && collection.isIndexed())
          res = (NumberDocument) collection;
      }
      else if (first instanceof NumberDocument)
      {
        res = (NumberDocument) first;
      }
    }

    return res;
  }

  public static ChartSet createModelFor(List<IStoreItem> selection)
  {
    // ok, use the limpet adapter to get the data
    ChartSet res = null;

    // our data generator
    LimpetStackedChartsAdapter adapter = new LimpetStackedChartsAdapter();

    if (adapter.canConvertToDataset(selection))
    {
      List<Dataset> datasets = adapter.convertToDataset(selection);
      if (datasets != null)
      {
        StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;

        Chart thisChart = null;

        for (Object thisO : datasets)
        {
          if (thisO instanceof Dataset)
          {
            Dataset dataset = (Dataset) thisO;
            // set the independent axis
            final String theseUnits = dataset.getUnits();

            if (res == null)
            {
              // ok, we need a chart-set
              res = factory.createChartSet();
              res.setOrientation(Orientation.VERTICAL);

              // get the first item, so we can determine the independent axis
              NumberDocument firstItem = getFirstCollectionFor(selection);

              if (firstItem.isIndexed())
              {
                IndependentAxis ia = factory.createIndependentAxis();

                // see what the index should look like
                Unit<?> units = firstItem.getIndexUnits();
                if (units != null && units.getDimension() != null
                    && units.getDimension().equals(SI.SECOND.getDimension()))
                {
                  ia.setAxisType(factory.createDateAxis());
                }
                else
                {
                  ia.setAxisType(factory.createNumberAxis());
                }

                res.setSharedAxis(ia);
              }
              else
              {
                System.err
                    .println("FAILED TO CREATE CHARTSET - WE DON'T HAVE TIME AS INDEPENDENT AXIS");
              }
            }

            // ok, plot this dataset
            if (thisChart == null)
            {
              thisChart = factory.createChart();
              res.getCharts().add(thisChart);
            }

            DependentAxis dependent = findAxisFor(res, theseUnits);
            if (dependent == null)
            {
              dependent = factory.createDependentAxis();

              // double-check if this is angular data
              final NumberAxis numAxis;
              if ("\u00b0".equals(dataset.getUnits())
                  || "Degs".equals(dataset.getUnits()))
              {
                final AngleAxis angleAxis = factory.createAngleAxis();
                angleAxis.setMinVal(0);
                angleAxis.setMaxVal(360);
                numAxis = angleAxis;
              }
              else
              {
                numAxis = factory.createNumberAxis();
              }

              numAxis.setUnits(dataset.getUnits());
              dependent.setAxisType(numAxis);
              dependent.setName(theseUnits);
              thisChart.getMinAxes().add(dependent);
            }

            dependent.getDatasets().add(dataset);
          }
        }
      }
    }

    return res;
  }

  private static DependentAxis findAxisFor(ChartSet charts, String units)
  {
    DependentAxis res = null;

    for (Chart chart : charts.getCharts())
    {
      for (DependentAxis da : chart.getMinAxes())
      {
        AxisType thisType = da.getAxisType();
        if (thisType instanceof NumberAxis)
        {
          NumberAxis na = (NumberAxis) thisType;
          if (na.getUnits() != null && na.getUnits().equals(units))
          {
            return da;
          }
        }
      }
      for (DependentAxis da : chart.getMaxAxes())
      {
        AxisType thisType = da.getAxisType();
        if (thisType instanceof NumberAxis)
        {
          NumberAxis na = (NumberAxis) thisType;
          if (na.getUnits() != null && na.getUnits().equals(units))
          {
            return da;
          }
        }
      }
    }

    return res;
  }
}
