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
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.ui.Activator;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ext.InteractiveChart;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class XyPlotView extends CoreAnalysisView
{

  private static final int MAX_SIZE = 10000;

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.XyPlotView";
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private Chart chart;

  private Action switchAxes;

  public XyPlotView()
  {
    super(ID, "XY plot view");
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

    // create a chart
    chart = new InteractiveChart(parent, SWT.NONE);

    // set titles
    chart.getAxisSet().getXAxis(0).getTitle().setText("Value");
    chart.getAxisSet().getYAxis(0).getTitle().setText("Value");
    chart.getTitle().setVisible(false);

    // adjust the axis range
    chart.getAxisSet().adjustRange();

    chart.getLegend().setPosition(SWT.BOTTOM);

    // register as selection listener
    setupListener();
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
      // transform them to a lsit of documents
      List<IDocument<?>> docList = aTests.getDocumentsIn(res);

      // they're all the same type - check the first one
      Iterator<IDocument<?>> iter = docList.iterator();

      IDocument<?> first = iter.next();

      // sort out what type of data this is.
      if (first.isQuantity())
      {
        if (aTests.allIndexedOrSingleton(res))
        {
          showIndexedQuantity(res);
        }
        else
        {
          showQuantity(res);
        }
        chart.setVisible(true);
      }
      else
      {
        // exception - show locations
        if (aTests.allLocation(res))
        {
          showLocations(res);
          chart.setVisible(true);
        }
        else
        {
          chart.setVisible(false);
        }
      }
    }
  }

  private void showQuantity(List<IStoreItem> items)
  {
    clearGraph();

    Unit<?> existingUnits = null;

    // get the longest collection length (used for plotting singletons)
    final int longestColl = aTests.getLongestCollectionLength(items);

    boolean chartUpdated = false;
    
    for(IStoreItem item: items)
    {      
      final IDocument<?> coll = (IDocument<?>) item;
      if (coll.isQuantity() && coll.size() >= 1 && coll.size() < MAX_SIZE)
      {

        final NumberDocument thisQ = (NumberDocument) coll;

        final Unit<?> theseUnits = thisQ.getUnits();
        final String seriesName = seriesNameFor(thisQ, theseUnits);

        // do we need to create this series
        final ISeries match = chart.getSeriesSet().getSeries(seriesName);
        if (match != null)
        {
          continue;
        }

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
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));
        newSeries.setSymbolColor(PlottingHelpers.colorFor(seriesName));

        // get the data, depending on if it's a singleton or not
        final double[] yData = getYData(longestColl, coll, thisQ);

        // ok, do we have existing data?
        if (existingUnits != null && !existingUnits.equals(theseUnits))
        {
          // create second Y axis
          final int axisId = chart.getAxisSet().createYAxis();

          // set the properties of second Y axis
          final IAxis yAxis2 = chart.getAxisSet().getYAxis(axisId);
          yAxis2.getTitle().setText(theseUnits.toString());
          yAxis2.setPosition(Position.Secondary);
          newSeries.setYAxisId(axisId);
        }
        else
        {
          chart.getAxisSet().getYAxes()[0].getTitle().setText(
              theseUnits.toString());
          existingUnits = theseUnits;
        }

        newSeries.setYSeries(yData);

        // if it's a monster line, we won't plot
        // markers
        if (thisQ.size() > 90)
        {
          newSeries.setSymbolType(PlotSymbolType.NONE);
          newSeries.setLineWidth(2);
        }
        else
        {
          newSeries.setSymbolType(PlotSymbolType.CROSS);
        }

        chart.getAxisSet().getXAxis(0).getTitle().setText("Count");

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        chartUpdated = true;
      }
    }
    
    if(chartUpdated)
    {
      chart.redraw();
    }
  }

  private double[] getYData(final int longestColl, final IDocument<?> coll,
      final NumberDocument thisQ)
  {
    final double[] yData;

    if (coll.size() == 1)
    {
      // singleton = insert it's value at every point
      yData = new double[longestColl];
      final Number thisValue = thisQ.getValueAt(0);
      final double dVal = thisValue.doubleValue();
      for (int i = 0; i < longestColl; i++)
      {
        yData[i] = dVal;
      }
    }
    else
    {
      yData = new double[thisQ.size()];
      final Iterator<Double> values = thisQ.getIterator();
      int ctr = 0;
      while (values.hasNext())
      {
        yData[ctr++] = values.next();
      }
    }
    return yData;
  }

  private String seriesNameFor(NumberDocument thisQ, final Unit<?> theseUnits)
  {
    String seriesName = thisQ.getName() + " (" + theseUnits + ")";
    return seriesName;
  }

  private void showIndexedQuantity(List<IStoreItem> res)
  {
    clearGraph();

    Unit<?> existingUnits = null;

    List<IDocument<?>> docList = aTests.getDocumentsIn(res);

    // get the outer time period (used for plotting singletons)
    List<IDocument<?>> safeColl = new ArrayList<IDocument<?>>();
    safeColl.addAll(docList);
    TimePeriod outerPeriod = aTests.getBoundingRange(res);

    for (final IDocument<?> coll : docList)
    {
      if (coll.isQuantity() && coll.size() >= 1 && coll.size() < MAX_SIZE)
      {
        final NumberDocument thisQ = (NumberDocument) coll;

        final Unit<?> theseUnits = thisQ.getUnits();

        final Unit<?> indexUnits;
        if (thisQ.isIndexed())
        {
          indexUnits = thisQ.getIndexUnits();
        }
        else
        {
          indexUnits = null;
        }

        final String seriesName = seriesNameFor(thisQ, theseUnits);

        // do we need to create this series
        final ISeries match = chart.getSeriesSet().getSeries(seriesName);
        if (match != null)
        {
          continue;
        }

        final ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

        // extract & store the data, but track any change to the units 
        existingUnits =
            storeIndexedData(existingUnits, outerPeriod, coll, thisQ,
                theseUnits, indexUnits, newSeries);
        
        // adjust the axis range
        chart.getAxisSet().adjustRange();
        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        chart.redraw();
      }
    }
  }

  private Unit<?> storeIndexedData(final Unit<?> existingUnits,
      final TimePeriod outerPeriod, final IDocument<?> coll,
      final NumberDocument thisQ, final Unit<?> theseUnits,
      final Unit<?> indexUnits, final ILineSeries newSeries)
  {
    
    Unit<?> newUnits = existingUnits;
    
    final Date[] xTimeData;
    final double[] xData;
    final double[] yData;

    if (coll.isIndexed())
    {
      // sort out the destination data type
      final boolean isTemporal =
          indexUnits != null && indexUnits.getDimension() != null
              && indexUnits.getDimension().equals(SI.SECOND.getDimension());
      if (isTemporal)
      {
        xTimeData = new Date[thisQ.size()];
        xData = null;
      }
      else
      {
        xData = new double[thisQ.size()];
        xTimeData = null;
      }

      yData = new double[thisQ.size()];

      // must be temporal
      Iterator<Double> index = coll.getIndex();
      Iterator<Double> values = thisQ.getIterator();

      int ctr = 0;
      final Unit<Duration> millis = SI.SECOND.divide(1000);
      while (values.hasNext())
      {
        double t = index.next();
        if (isTemporal)
        {
          final long value;
          if (indexUnits.equals(millis))
          {
            value = (long) t;
          }
          else
          {
            // do we need to convert to millis?
            UnitConverter converter = indexUnits.getConverterTo(millis);
            value = (long) converter.convert(t);
          }

          xTimeData[ctr] = new Date(value);
        }
        else
        {
          xData[ctr] = t;
        }
        yData[ctr++] = values.next();
      }
    }
    else
    {
      // non temporal, include it as a marker line
      // must be non temporal
      xTimeData = new Date[2];
      xData = null;
      yData = new double[2];

      // get the singleton value
      Double theValue = thisQ.getIterator().next();

      // create the marker line
      xTimeData[0] = new Date((long) outerPeriod.getStartTime());
      yData[0] = theValue;
      xTimeData[1] = new Date((long) outerPeriod.getEndTime());
      yData[1] = theValue;
    }

    if (xTimeData != null)
    {
      newSeries.setXDateSeries(xTimeData);
    }
    else if (xData != null)
    {
      newSeries.setXSeries(xData);
    }
    else
    {
      System.err.println("We haven't correctly collated data");
    }
    newSeries.setYSeries(yData);

    // ok, do we have existing data, in different units?
    if (existingUnits != null && !existingUnits.equals(theseUnits))
    {
      // create second Y axis
      int axisId = chart.getAxisSet().createYAxis();

      // set the properties of second Y axis
      IAxis yAxis2 = chart.getAxisSet().getYAxis(axisId);
      yAxis2.getTitle().setText(theseUnits.toString());
      yAxis2.setPosition(Position.Secondary);
      newSeries.setYAxisId(axisId);
    }
    else
    {
      chart.getAxisSet().getYAxes()[0].getTitle().setText(
          theseUnits.toString());
      newUnits = theseUnits;
    }

    // if it's a monster line, we won't plot
    // markers
    if (thisQ.size() > 90)
    {
      newSeries.setSymbolType(PlotSymbolType.NONE);
      newSeries.setLineWidth(2);
    }
    else
    {
      newSeries.setSymbolType(PlotSymbolType.CROSS);
    }

    final String xTitle = getTitleFor(xTimeData, indexUnits);
    chart.getAxisSet().getXAxis(0).getTitle().setText(xTitle);
    
    return newUnits;
  }

  /** produce the graph's title text
   * 
   * @param xTimeData
   * @param indexUnits
   * @return
   */
  private String getTitleFor(final Date[] xTimeData, final Unit<?> indexUnits)
  {
    String xTitle = null;

    if (xTimeData != null)
    {
      xTitle = "Time";
    }
    else
    {
      final String titlePrefix;
      final String theDim =
          indexUnits != null ? indexUnits.getDimension().toString() : "N/A";
      switch (theDim)
      {
      case "[L]":
        titlePrefix = "Length";
        break;
      case "[M]":
        titlePrefix = "Mass";
        break;
      case "[T]":
        titlePrefix = "Time";
        break;
      default:
        titlePrefix = theDim;
        break;
      }

      final String indexText =
          indexUnits != null ? " (" + indexUnits.toString() + ")" : "";
      xTitle = titlePrefix + indexText;

    }
    return xTitle;
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

  private void showLocations(List<IStoreItem> res)
  {
    // clear the graph
    ISeries[] series = chart.getSeriesSet().getSeries();
    for (int i = 0; i < series.length; i++)
    {
      ISeries iSeries = series[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    // now loop through
    boolean chartUpdated = false;
    for(final IStoreItem document: res)
    {
      final IDocument<?> coll = (IDocument<?>) document;
      if (!coll.isQuantity() && coll.size() >= 1 && coll.size() < MAX_SIZE)
      {
        final String seriesName = coll.getName();
        final ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setSymbolType(PlotSymbolType.NONE);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));
        newSeries.setSymbolColor(PlottingHelpers.colorFor(seriesName));

        final double[] xData = new double[coll.size()];
        final double[] yData = new double[coll.size()];

        final LocationDocument loc = (LocationDocument) coll;
        int ctr = 0;
        final Iterator<Point2D> lIter = loc.getLocationIterator();
        while (lIter.hasNext())
        {
          final Point2D geom = lIter.next();
          xData[ctr] = geom.getX();
          yData[ctr++] = geom.getY();
        }

        // clear the axis labels
        chart.getAxisSet().getXAxis(0).getTitle().setText("");
        chart.getAxisSet().getYAxis(0).getTitle().setText("");

        newSeries.setXSeries(xData);
        newSeries.setYSeries(yData);

        newSeries.setSymbolType(PlotSymbolType.CROSS);

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        chartUpdated = true;
      }
    }
    if(chartUpdated)
    {
      chart.redraw();
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
      // clear all of the series
      ISeries[] allSeries = chart.getSeriesSet().getSeries();
      for (int i = 0; i < allSeries.length; i++)
      {
        ISeries iSeries = allSeries[i];
        chart.getSeriesSet().deleteSeries(iSeries.getId());
      }
    }
  }

}
