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
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalObjectCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.store.StoreGroup;
import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.view.StackedChartsView;
import info.limpet.ui.range_slider.RangeSliderView;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

      List<IStoreItem> children = sg;
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
          final StackedChartsView cv = (StackedChartsView) theView;

          // create the charts set model
          ChartSet model = createModelFor(this.getInputs());

          if (model != null)
          {
            // set follow selection to off
            // cv.follow(getInputs());
            cv.setModel(model);
            
            // ok, also try to find the top level store
            if(getInputs().size() > 0)
            {
              IStoreItem first = this.getInputs().get(0);
              final IStoreGroup top = RangeSliderView.findTopParent(first);
              if (top != null)
              {
                final PropertyChangeListener timeListener  = new PropertyChangeListener()
                {
                  
                  @Override
                  public void propertyChange(PropertyChangeEvent evt)
                  {
                    Date newTime = (Date) evt.getNewValue();
                    cv.updateTime(newTime);
                  }
                };
                // ok, register as listener
                top.addTimeChangeListener(timeListener);
                
                // we also need to drop it when the view is closing
                cv.addRunOnCloseCallback(new Runnable(){

                  @Override
                  public void run()
                  {
                    // ok, forget about it
                    top.removeTimeChangeListener(timeListener);
                  }});
              }
            }
            
//            // take a copy of the model
//            URI resourceURI = URI.createFileURI("/home/ian/tacticalOverview.stackedcharts");
//            Resource resource = new ResourceSetImpl().createResource(resourceURI);
//            System.out.println("saving to:" + resourceURI.toFileString());
//            resource.getContents().add(model);
//            try
//            {
//              resource.save(null);
//            }
//            catch (IOException e)
//            {
//              e.printStackTrace();
//            }
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
    
    // ok, make it a time axis
    timeAxis.setAxisType(factory.createDateAxis());
    res.setSharedAxis(timeAxis);

    // ok, start looking deeper
    StoreGroup sg = (StoreGroup) selection.get(0);

    // create our state charts
    Chart crsePlot = factory.createChart();
    crsePlot.setName("Course");
    DependentAxis crseAxis = factory.createDependentAxis();
    AngleAxis crseAxisType = factory.createAngleAxis();
    crseAxisType.setMinVal(0);
    crseAxisType.setMaxVal(360);
    crseAxis.setAxisType(crseAxisType);
    crseAxis.setName("Course (Degs)");
    crsePlot.getMinAxes().add(crseAxis);
    res.getCharts().add(crsePlot);
    
    Chart speedDepthPlot = factory.createChart();
    speedDepthPlot.setName("Speed/Depth");
    res.getCharts().add(speedDepthPlot);
    DependentAxis speedAxis = factory.createDependentAxis();
    speedAxis.setAxisType(factory.createNumberAxis());
    speedAxis.setName("Speed (Kts)");
    speedDepthPlot.getMinAxes().add(speedAxis);
    DependentAxis depthAxis = factory.createDependentAxis();
    depthAxis.setName("Depth (m)");
    depthAxis.setAxisType(factory.createNumberAxis());
    speedDepthPlot.getMaxAxes().add(depthAxis);
    
    // create our range plot
    Chart rangePlot = factory.createChart();
    rangePlot.setName("Range");
    DependentAxis rangeAxis = factory.createDependentAxis();
    rangeAxis.setAxisType(factory.createNumberAxis());
    rangeAxis.setName("Range (m)");
    rangePlot.getMinAxes().add(rangeAxis);
    res.getCharts().add(rangePlot);
    
    List<IStoreItem> children = sg;
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
          createStateChart(factory, crseAxis, speedAxis, depthAxis, primary, "Primary State", res, Color.blue);
        }
        else if (thisG.getName().equals("Secondary"))
        {
          StoreGroup primary = (StoreGroup) thisG;
          createStateChart(factory, crseAxis, speedAxis, depthAxis, primary, "Secondary State", res, Color.red);
        }
        else if (thisG.getName().equals("Relative"))
        {
          StoreGroup relative = (StoreGroup) thisG;
          if (relative.size() == 5)
          {
            // ok, create the primary chart
            Chart relativeChart = factory.createChart();
            relativeChart.setName("Relative");
            res.getCharts().add(relativeChart);

            DependentAxis brgAxis = factory.createDependentAxis();
            brgAxis.setName("Bearing");
            brgAxis.setAxisType(factory.createNumberAxis());
            relativeChart.getMinAxes().add(brgAxis);

            DependentAxis brgRateAxis = factory.createDependentAxis();
            brgRateAxis.setName("Bearing Rate");
            brgRateAxis.setAxisType(factory.createNumberAxis());
            relativeChart.getMinAxes().add(brgRateAxis);

            // now sort out the data series
            Iterator<IStoreItem> dIter = relative.iterator();
            while (dIter.hasNext())
            {
              IStoreItem iStoreItem = (IStoreItem) dIter.next();
              if (iStoreItem.getName().equals("Range"))
              {
                createDataset(factory, iStoreItem, "Range", rangeAxis, res,
                		null, MarkerStyle.NONE, false);
              }
              else if (iStoreItem.getName().equals("Bearing"))
              {
                createDataset(factory, iStoreItem, "Bearing", brgAxis, res,
                		null, MarkerStyle.NONE, true);
              }
              else if (iStoreItem.getName().equals("Rel Brg"))
              {
                createDataset(factory, iStoreItem, "Rel Brg", brgAxis, res,
                		null, MarkerStyle.NONE, true);
              }
              else if (iStoreItem.getName().equals("ATB"))
              {
                createDataset(factory, iStoreItem, "ATB", brgAxis, res,
                		null, MarkerStyle.NONE, true);
              }
              else if (iStoreItem.getName().equals("Brg Rate"))
              {
                createDataset(factory, iStoreItem, "Brg Rate", brgRateAxis, res,
                		null, MarkerStyle.NONE, true);
              }
            }
          }
        }
        else if (thisG.getName().equals("Sensor"))
        {
          StoreGroup sensor = (StoreGroup) thisG;
          if (sensor.size() == 1)
          {
            ScatterSet scatter = factory.createScatterSet();
            @SuppressWarnings("unchecked")
            ITemporalObjectCollection<Object> cuts = (ITemporalObjectCollection<Object>) sensor.get(0);

            Iterator<Long> times = cuts.getTimes().iterator();
            while (times.hasNext())
            {
              Long long1 = (Long) times.next();
              Datum item = factory.createDatum();
              item.setVal(long1);
              scatter.getDatums().add(item);
            }
            
            SelectiveAnnotation sel = factory.createSelectiveAnnotation();
            sel.setAnnotation(scatter);
            sel.getAppearsIn().add(res.getCharts().get(res.getCharts().size()-1));
            timeAxis.getAnnotations().add(sel);
          }
        }
      }
    }

    return res;
  }

  protected static void createStateChart(StackedchartsFactory factory,
      DependentAxis crseAxis, DependentAxis speedAxis, DependentAxis depthAxis, StoreGroup group, String chartName, ChartSet set, java.awt.Color color)
  {
    if (group.size() == 3)
    {
      // now sort out the data series
      Iterator<IStoreItem> dIter = group.iterator();
      while (dIter.hasNext())
      {
        IStoreItem iStoreItem = (IStoreItem) dIter.next();
        final String thisName = iStoreItem.getName();
        if (thisName.contains("Course"))
        {
          createDataset(factory, iStoreItem, thisName, crseAxis, set, 
          		color, MarkerStyle.NONE, false);
        }
        else if (thisName.contains("Speed"))
        {
          createDataset(factory, iStoreItem, thisName, speedAxis, set,
          		color.brighter().brighter(), MarkerStyle.CROSS, true);
        }
        else if (thisName.contains("Depth"))
        {
          createDataset(factory, iStoreItem, thisName, depthAxis, set,
          		color.darker().darker(), MarkerStyle.DIAMOND, true);
        }
      }
    }
  }

  protected static void createDataset(StackedchartsFactory factory,
      IStoreItem iStoreItem, String name, DependentAxis axis, 
      ChartSet chartSet, java.awt.Color color, MarkerStyle marker,
      boolean showInLegend)
  {
    ITemporalQuantityCollection<?> it =
        (ITemporalQuantityCollection<?>) iStoreItem;
    List<Long> times = it.getTimes();
    List<?> values = it.getValues();

    Dataset newD = factory.createDataset();
    newD.setName(name);
    
    PlainStyling styling = factory.createPlainStyling();
    styling.setColor(color);
    styling.setMarkerStyle(marker);
    styling.setIncludeInLegend(showInLegend);
    styling.setLineThickness(2d);
		newD.setStyling(styling);
    
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
  }

}
