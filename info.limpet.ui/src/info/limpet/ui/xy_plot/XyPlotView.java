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

import info.limpet.Document;
import info.limpet.IStoreItem;
import info.limpet.LocationDocument;
import info.limpet.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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

  public XyPlotView()
  {
    super(ID, "XY plot view");
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
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
      List<Document> docList = aTests.getDocumentsIn(res);
      
      // they're all the same type - check the first one
      Iterator<Document> iter = docList.iterator();

      Document first = iter.next();

      // sort out what type of data this is.
      if (first.isQuantity())
      {
        if (aTests.allIndexedOrSingleton(res))
        {
          showTemporalQuantity(res);
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

  private void showQuantity(List<IStoreItem> res)
  {
    Iterator<IStoreItem> iter = res.iterator();

    clearGraph();

    Unit<?> existingUnits = null;

    // get the longest collection length (used for plotting singletons)
    int longestColl = aTests.getLongestCollectionLength(res);

    while (iter.hasNext())
    {
      Document coll = (Document) iter.next();
      if (coll.isQuantity() && coll.size() >= 1
          && coll.size() < MAX_SIZE)
      {

        NumberDocument thisQ =
            (NumberDocument) coll;

        final Unit<?> theseUnits = thisQ.getUnits();
        String seriesName = seriesNameFor(thisQ, theseUnits);

        // do we need to create this series
        ISeries match = chart.getSeriesSet().getSeries(seriesName);
        if (match != null)
        {
          continue;
        }

        ILineSeries newSeries =
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

        final double[] yData;

        if (coll.size() == 1)
        {
          // singleton = insert it's value at every point
          yData = new double[longestColl];
          Number thisValue = thisQ.getValue(0);
          double dVal = thisValue.doubleValue();
          for (int i = 0; i < longestColl; i++)
          {
            yData[i] = dVal;
          }
        }
        else
        {
          yData = new double[thisQ.size()];
          Iterator<Double> values = thisQ.getIterator();
          int ctr = 0;
          while (values.hasNext())
          {
            yData[ctr++] = values.next();
          }
        }

        // ok, do we have existing data?
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
          existingUnits = theseUnits;
        }

        // newSeries.setXSeries(xData);
        newSeries.setYSeries(yData);

        chart.getAxisSet().getXAxis(0).getTitle().setText("Count");

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        chart.redraw();
      }
    }
  }

  private String seriesNameFor(NumberDocument thisQ,
      final Unit<?> theseUnits)
  {
    String seriesName = thisQ.getName() + " (" + theseUnits + ")";
    return seriesName;
  }

  private void showTemporalQuantity(List<IStoreItem> res)
  {
    clearGraph();

    Unit<?> existingUnits = null;

    List<Document> docList = aTests.getDocumentsIn(res);
    
    // get the outer time period (used for plotting singletons)
    List<Document> safeColl = new ArrayList<Document>();
    safeColl.addAll(docList);
    TimePeriod outerPeriod = aTests.getBoundingRange(res);

    for(Document coll: docList)
    {
      if (coll.isQuantity() && coll.size() >= 1
          && coll.size() < MAX_SIZE)
      {
        NumberDocument thisQ =
            (NumberDocument) coll;

        final Unit<?> theseUnits = thisQ.getUnits();

        String seriesName = seriesNameFor(thisQ, theseUnits);

        // do we need to create this series
        ISeries match = chart.getSeriesSet().getSeries(seriesName);
        if (match != null)
        {
          continue;
        }

        ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

        final Date[] xTimeData;
        final double[] yData;

        if (coll.isIndexed())
        {
          xTimeData = new Date[thisQ.size()];
          yData = new double[thisQ.size()];

          // must be temporal
          Iterator<Double> times = coll.getIndices();
          Iterator<Double> values = thisQ.getIterator();
          
          int ctr = 0;
          while (values.hasNext())
          {
            double t = times.next();
            xTimeData[ctr] = new Date((long) t);
            yData[ctr++] = values.next();
          }
        }
        else
        {
          // non temporal, include it as a marker line
          // must be non temporal
          xTimeData = new Date[2];
          yData = new double[2];

          // get the singleton value
          Double theValue = thisQ.getIterator().next();

          // create the marker line
          xTimeData[0] = new Date((long) outerPeriod.getStartTime());
          yData[0] = theValue;
          xTimeData[1] = new Date((long) outerPeriod.getEndTime());
          yData[1] = theValue;

        }

        newSeries.setXDateSeries(xTimeData);
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
          existingUnits = theseUnits;
        }

        // if it's a monster line, or just a singleton value, we won't plot
        // markers
        if (thisQ.size() > 90 || thisQ.size() == 1)
        {
          newSeries.setSymbolType(PlotSymbolType.NONE);
        }
        else
        {
          newSeries.setSymbolType(PlotSymbolType.CROSS);
        }

        chart.getAxisSet().getXAxis(0).getTitle().setText("Time");

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        chart.redraw();
      }
    }
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
    Iterator<IStoreItem> iter = res.iterator();

    // clear the graph
    ISeries[] series = chart.getSeriesSet().getSeries();
    for (int i = 0; i < series.length; i++)
    {
      ISeries iSeries = series[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    while (iter.hasNext())
    {
      Document coll = (Document) iter.next();
      if (!coll.isQuantity() && coll.size() >= 1
          && coll.size() < MAX_SIZE)
      {
        String seriesName = coll.getName();
        ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setSymbolType(PlotSymbolType.NONE);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

        double[] xData = new double[coll.size()];
        double[] yData = new double[coll.size()];

        LocationDocument loc = (LocationDocument) coll;
        Iterator<Point2D> lIter = loc.getLocationIterator();
        int ctr = 0;
        while(lIter.hasNext())
        {
          Point2D geom = lIter.next();
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

        chart.redraw();

      }
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
    return tests.allCollections(res) && tests.allQuantity(res)
        || tests.allNonQuantity(res);
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
    Document coll = (Document) subject;
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
