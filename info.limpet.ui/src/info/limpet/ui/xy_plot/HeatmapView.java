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

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.Activator;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.unit.Unit;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.january.MetadataException;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure.IROIListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class HeatmapView extends CoreAnalysisView
{

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.HeatMapView";
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private Chart chart;

  private Action switchAxes;

  public HeatmapView()
  {
    super(ID, "Heatmap view");
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
          chart.setOrientation(SWT.VERTICAL);
        }
        else
        {
          chart.setOrientation(SWT.HORIZONTAL);
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
    makeActions();
    contributeToActionBars();
//
//    // create a chart
//    chart = new InteractiveChart(parent, SWT.NONE);
//
//    // set titles
//    chart.getAxisSet().getXAxis(0).getTitle().setText("Value");
//    chart.getAxisSet().getYAxis(0).getTitle().setText("Value");
//    chart.getTitle().setVisible(false);
//
//    // adjust the axis range
//    chart.getAxisSet().adjustRange();
//
//    chart.getLegend().setPosition(SWT.BOTTOM);
//
//    // register as selection listener
//    setupListener();
//
//    Canvas can = new Canvas(parent, SWT.NONE);
//    
    doTest(parent);

  //  IntensityGraphExample.main(new String[]{""});
    
  }
  int count = 0;
 
  private void doTest(Composite parent)
  {
    final int DataHeight = 1024;
    final int DataWidth = 1280;

    //Create Intensity Graph
    final IntensityGraphFigure intensityGraph = new IntensityGraphFigure();
    
    //Create Simulation Data
    final short[] simuData = new short[DataWidth * DataHeight * 2];
    final short[] data = new short[DataWidth * DataHeight];
    int seed = count++;
    for (int i = 0; i < DataHeight; i++) {
      for (int j = 0; j < DataWidth; j++) {
        int x = j - DataWidth;
        int y = i - DataHeight;
        int p = (int) Math.sqrt(x * x + y * y);
        simuData[i * DataWidth + j] = (short) (Math.sin(p * 2 * Math.PI
            / DataWidth + seed * Math.PI / 100) * 100);
      }
    }

    //Configure
    intensityGraph.setMax(100);
    intensityGraph.setMin(-100);
    intensityGraph.setDataHeight(DataHeight);
    intensityGraph.setDataWidth(DataWidth);
    intensityGraph.setColorMap(new ColorMap(PredefinedColorMap.JET, true,true));
    intensityGraph.addROI("ROI 1",  new IROIListener() {
      
      @Override
      public void roiUpdated(int xIndex, int yIndex, int width, int height) {
        System.out.println("Region of Interest: (" + xIndex + ", " + yIndex 
            +", " + width +", " + height +")");
      }
    }, null);
    
    final Shell shell = new Shell();
    shell.setSize(300, 250);
    shell.open();
    
    // use LightweightSystem to create the bridge between SWT and draw2D
    final LightweightSystem lws = new LightweightSystem(shell);
    
    lws.setContents(intensityGraph);

    // Update the graph in another thread.
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
        new Runnable() {

          @Override
          public void run() {
            System.arraycopy(simuData, count % DataWidth, data, 0,
                DataWidth * DataHeight);

            Display.getDefault().asyncExec(new Runnable() {

              public void run() {
                count++;
                intensityGraph.setDataArray(simuData);
              }
            });
          }
        }, 100, 10, TimeUnit.MILLISECONDS);

    Display display = Display.getDefault();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    future.cancel(true);
    scheduler.shutdown();
  }
  

  @Override
  public void display(List<IStoreItem> res)
  {
    if (res.size() == 0)
    {
      chart.setVisible(false);
    }
    else
    {
      // check they're all one dim
      if(aTests.allTwoDim(res) && res.size() == 1)
      {
        // ok, it's a single two-dim dataset
        showTwoDim(res.get(0));
      }
    }
  }

  private void showTwoDim(IStoreItem item)
  {
    NumberDocument thisQ = (NumberDocument) item;

    clearGraph();

    String seriesName = thisQ.getName();
    final ILineSeries newSeries =
        (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
            seriesName);

    final PlotSymbolType theSym;
    // if it's a singleton, show the symbol
    // markers
    if (thisQ.size() > 100 || thisQ.size() == 1)
    {
      theSym = PlotSymbolType.NONE;
    }
    else
    {
      theSym = PlotSymbolType.CIRCLE;
    }

    newSeries.setSymbolType(theSym);
    newSeries.setLineStyle(LineStyle.NONE);
    newSeries.setSymbolColor(PlottingHelpers.colorFor(seriesName));

    // ok, show this 2d dataset
    NumberDocument nd = (NumberDocument) item;

    try
    {
      // sort out the axes
      final List<AxesMetadata> amList =
          nd.getDataset().getMetadata(AxesMetadata.class);
      AxesMetadata am = amList.get(0);
      ILazyDataset[] axes = am.getAxes();
      if (axes.length == 2)
      {
        DoubleDataset aOne = (DoubleDataset) axes[0];
        DoubleDataset aTwo = (DoubleDataset) axes[1];

        double[] aIndices = aOne.getData();
        double[] bIndices = aTwo.getData();

        // loop through the data
        List<Double> xValues = new ArrayList<Double>();
        List<Double> yValues = new ArrayList<Double>();

        final DoubleDataset dataset = (DoubleDataset) nd.getDataset();

        // process the data
        for (int i = 0; i < aIndices.length; i++)
        {
          for (int j = 0; j < bIndices.length; j++)
          {
            Double thisVal = dataset.get(i, j);
            if (!thisVal.equals(Double.NaN))
            {
              xValues.add(aIndices[i]);
              yValues.add(bIndices[j]);
            }
          }
        }

        final double[] xArr = toArray(xValues);
        final double[] yArr = toArray(yValues);

        newSeries.setXSeries(xArr);
        newSeries.setYSeries(yArr);

        chart.getAxisSet().getYAxes()[0].getTitle().setText(
            "" + nd.getIndexUnits());
        chart.getAxisSet().getXAxes()[0].getTitle().setText(
            "" + nd.getIndexUnits());

        // adjust the axis range
        chart.getAxisSet().adjustRange();

      }
    }
    catch (MetadataException e)
    {
      e.printStackTrace();
      // ok, just drop out
    }
  }

  private double[] toArray(List<Double> xData)
  {
    double[] res = new double[xData.size()];
    for (int i = 0; i < xData.size(); i++)
    {
      res[i] = xData.get(i);
    }
    return res;
  }

  private String seriesNameFor(NumberDocument thisQ, final Unit<?> theseUnits)
  {
    String seriesName = thisQ.getName() + " (" + theseUnits + ")";
    return seriesName;
  }

  private void clearGraph()
  {
    // clear the graph
    ISeries[] series = chart.getSeriesSet().getSeries();
    for (int i = 0; i < series.length; i++)
    {
      ISeries iSeries = series[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());

      // and clear any series
      IAxis[] yA = chart.getAxisSet().getYAxes();
      for (int j = 1; j < yA.length; j++)
      {
        IAxis iAxis = yA[j];
        chart.getAxisSet().deleteYAxis(iAxis.getId());
      }
    }
  }

  private void clearChart()
  {
    // clear the graph
    ISeries[] series = chart.getSeriesSet().getSeries();
    for (int i = 0; i < series.length; i++)
    {
      ISeries iSeries = series[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }
  }

  @Override
  public void setFocus()
  {
    chart.setFocus();
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

  @Override
  protected void datasetDataChanged(IStoreItem subject)
  {
    final String name;
    IDocument<?> coll = (IDocument<?>) subject;
    if (coll.isQuantity())
    {
      NumberDocument cq = (NumberDocument) coll;
      Unit<?> units = cq.getUnits();
      name = seriesNameFor(cq, units);
    }
    else
    {
      name = coll.getName();
    }

    ISeries match = chart.getSeriesSet().getSeries(name);
    if (match != null)
    {
      chart.getSeriesSet().deleteSeries(name);
    }
    else
    {
      clearChart();
    }
  }

}
