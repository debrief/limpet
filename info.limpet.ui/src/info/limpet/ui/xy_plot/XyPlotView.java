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
import info.limpet.impl.Document;
import info.limpet.impl.DoubleListDocument;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.ui.Activator;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet.ui.heatmap.Helper2D;
import info.limpet.ui.heatmap.Helper2D.HContainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.january.MetadataException;
import org.eclipse.january.metadata.AxesMetadata;
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
import org.swtchart.ITitle;
import org.swtchart.LineStyle;
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
  private Action fitToWindow;

  public XyPlotView()
  {
    super(ID, "XY plot view");
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean allLocation = tests.allLocation(res);
    final boolean suitableIndex =
        tests.allEqualIndexed(res) || tests.allNonIndexed(res) || allLocation;
    return allCollections && suitableIndex && (allQuantity || allNonQuantity);
  }

  private void clearChart()
  {
    // clear the graph
    final ISeries[] series = chart.getSeriesSet().getSeries();
    for (final ISeries iSeries : series)
    {
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    /**
     * we keep the zero axis, it's the first y axis.
     * 
     */
    final int AXIS_TO_KEEP = 0;

    // clear the secondary x axes
    final IAxis[] yAxes = chart.getAxisSet().getYAxes();
    for (final IAxis axis : yAxes)
    {
      // delete all axes except the first
      final int thisId = axis.getId();
      if (thisId != AXIS_TO_KEEP)
      {
        chart.getAxisSet().deleteYAxis(thisId);
      }
    }
  }

  private void clearGraph()
  {
    // clear the graph
    final ISeries[] series = chart.getSeriesSet().getSeries();
    for (int i = 0; i < series.length; i++)
    {
      final ISeries iSeries = series[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());

      // and clear any series
      final IAxis[] yA = chart.getAxisSet().getYAxes();
      for (int j = 1; j < yA.length; j++)
      {
        final IAxis iAxis = yA[j];
        chart.getAxisSet().deleteYAxis(iAxis.getId());
      }
    }
  }

  @Override
  protected void contributeToActionBars()
  {
    super.contributeToActionBars();
    final IActionBars bars = getViewSite().getActionBars();
    bars.getToolBarManager().add(switchAxes);
    bars.getToolBarManager().add(fitToWindow);
    bars.getMenuManager().add(switchAxes);
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
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
  protected void datasetDataChanged(final IStoreItem subject)
  {
    final String name;
    final IDocument<?> coll = (IDocument<?>) subject;
    if (coll.isQuantity())
    {
      final NumberDocument cq = (NumberDocument) coll;
      final Unit<?> units = cq.getUnits();
      name = seriesNameFor(cq, units);
    }
    else
    {
      name = coll.getName();
    }

    final ISeries match = chart.getSeriesSet().getSeries(name);
    if (match != null)
    {
      chart.getSeriesSet().deleteSeries(name);
    }
    else
    {
      clearChart();
    }
  }

  @Override
  protected void doDisplay(final List<IStoreItem> res)
  {
    if (res.size() == 0)
    {
      chart.setVisible(false);
    }
    else
    {
      // check they're all one dim
      if (aTests.allOneDim(res))
      {
        showOneDim(res);
      }
      else if (aTests.allTwoDim(res) && res.size() == 1)
      {
        // ok, it's a single two-dim dataset
        showTwoDim(res.get(0));
      }
    }
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  /**
   * produce the graph's title text
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

    fitToWindow = new Action("Fit to window", SWT.COMMAND)
    {
      @Override
      public void run()
      {
        // work through the axes to reset the range
        IAxis[] xAxes = chart.getAxisSet().getAxes();
        for (IAxis axis : xAxes)
        {
          // ok, do fit
          axis.adjustRange();
        }
        // and re-plot it
        chart.redraw();
      }
    };
    fitToWindow.setText("Fit to window");
    fitToWindow.setToolTipText("Resize plot to view all data");
    fitToWindow.setImageDescriptor(Activator
        .getImageDescriptor("icons/fit_to_win.png"));

  }

  private void putOnAxis(final Chart chart, final ILineSeries newSeries,
      final Unit<?> newUnits)
  {
    final String unitStr = newUnits.toString();

    // special case. at start we just have an empty y axis
    final IAxis[] yAxes = chart.getAxisSet().getYAxes();
    if (yAxes.length == 1 && yAxes[0].getTitle().getText() == "")
    {
      // ok, we're only just opened. Use this one
      yAxes[0].getTitle().setText(unitStr);
      newSeries.setYAxisId(yAxes[0].getId());
    }
    else
    {
      int leftCount = 0;
      int rightCount = 0;

      // clear the axis id, we're going to rely on it
      final int INVALID_ID = -10000;
      newSeries.setYAxisId(INVALID_ID);

      // ok, work through the axes
      for (final IAxis t : yAxes)
      {
        if (t.getTitle().getText().equals(unitStr))
        {
          // ok, this will do
          newSeries.setYAxisId(t.getId());
        }
        else
        {
          // just keep track of the count
          switch (t.getPosition())
          {
          case Primary:
            leftCount++;
            break;
          case Secondary:
          default:
            rightCount++;
            break;
          }
        }
      }

      // did we store it?
      if (newSeries.getYAxisId() == INVALID_ID)
      {
        final Position toUse;
        // choose the side with the fewest, or the x if none.
        if (leftCount == 0)
        {
          toUse = Position.Primary;
        }
        else if (leftCount > rightCount)
        {
          toUse = Position.Secondary;
        }
        else
        {
          toUse = Position.Primary;
        }

        // create the axis
        final int newAxisId = chart.getAxisSet().createYAxis();
        final IAxis newAxis = chart.getAxisSet().getYAxis(newAxisId);
        newAxis.getTitle().setText(unitStr);
        newAxis.setPosition(toUse);

        // and tell the series to use it
        newSeries.setYAxisId(newAxisId);
      }
    }
  }

  private String seriesNameFor(final NumberDocument thisQ,
      final Unit<?> theseUnits)
  {
    final String seriesName = thisQ.getName() + " (" + theseUnits + ")";
    return seriesName;
  }

  @Override
  public void setFocus()
  {
    chart.setFocus();
  }

  private void showIndexedQuantity(final List<IStoreItem> res)
  {
    clearGraph();

    Unit<?> existingUnits = null;

    final List<IDocument<?>> docList = aTests.getDocumentsIn(res);

    // get the outer time period (used for plotting singletons)
    final List<IDocument<?>> safeColl = new ArrayList<IDocument<?>>();
    safeColl.addAll(docList);
    final TimePeriod outerPeriod = aTests.getBoundingRange(res);

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
        final IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        chart.redraw();
      }
    }
  }

  private void showLocations(final List<IStoreItem> res)
  {
    clearChart();

    // now loop through
    boolean chartUpdated = false;
    for (final IStoreItem document : res)
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
    if (chartUpdated)
    {
      chart.redraw();
    }
  }

  private void showOneDim(final List<IStoreItem> res)
  {
    // transform them to a lsit of documents
    final List<IDocument<?>> docList = aTests.getDocumentsIn(res);

    // they're all the same type - check the first one
    final Iterator<IDocument<?>> iter = docList.iterator();

    final IDocument<?> first = iter.next();

    // do a bit of y axis tidying. We rely on the first
    // axis having blank text to know when we're overwriting
    // the initial empty (template) dataset
    chart.getAxisSet().getYAxes()[0].getTitle().setText("");

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

  private void showQuantity(final List<IStoreItem> items)
  {
    clearGraph();

    Unit<?> existingUnits = null;

    // get the longest collection length (used for plotting singletons)
    final int longestColl = aTests.getLongestCollectionLength(items);

    boolean chartUpdated = false;

    for (final IStoreItem item : items)
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

        existingUnits =
            showThisNumberDocument(existingUnits, longestColl, coll, thisQ,
                theseUnits, seriesName);

        chartUpdated = true;
      }
    }

    if (chartUpdated)
    {
      chart.redraw();
    }
  }

  private Unit<?> showThisNumberDocument(final Unit<?> existingUnits,
      final int longestColl, final IDocument<?> coll,
      final NumberDocument thisQ, final Unit<?> theseUnits,
      final String seriesName)
  {
    final Unit<?> NewUnits = existingUnits;

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

    // loop through the axes, see if we have suitable
    putOnAxis(chart, newSeries, theseUnits);
    //
    // // ok, do we have existing data?
    // if (existingUnits != null && !existingUnits.equals(theseUnits))
    // {
    // // create second Y axis
    // final int axisId = chart.getAxisSet().createYAxis();
    //
    // // set the properties of second Y axis
    // final IAxis yAxis2 = chart.getAxisSet().getYAxis(axisId);
    // yAxis2.getTitle().setText(theseUnits.toString());
    // yAxis2.setPosition(Position.Secondary);
    // newSeries.setYAxisId(axisId);
    // }
    // else
    // {
    // chart.getAxisSet().getYAxes()[0].getTitle()
    // .setText(theseUnits.toString());
    // NewUnits = theseUnits;
    // }

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
    final IAxis xAxis = chart.getAxisSet().getXAxis(0);
    xAxis.enableCategory(false);

    return NewUnits;
  }

  protected static interface TwoDimHelper
  {
    List<AxesMetadata> getAxes(IStoreItem item) throws MetadataException;

    Unit<?> getIndexUnits(IStoreItem item);

  }

  private void showTwoDim(final IStoreItem item)
  {
    final TwoDimHelper helper;
    if (item instanceof NumberDocument)
    {
      helper = new TwoDimHelper()
      {

        @Override
        public List<AxesMetadata> getAxes(IStoreItem item)
            throws MetadataException
        {
          NumberDocument nd = (NumberDocument) item;
          return nd.getDataset().getMetadata(AxesMetadata.class);
        }

        @Override
        public Unit<?> getIndexUnits(IStoreItem item)
        {
          NumberDocument nd = (NumberDocument) item;
          return nd.getIndexUnits();
        }

      };
    }
    else if (item instanceof DoubleListDocument)
    {
      helper = new TwoDimHelper()
      {

        @Override
        public List<AxesMetadata> getAxes(IStoreItem item)
            throws MetadataException
        {
          DoubleListDocument nd = (DoubleListDocument) item;
          return nd.getDataset().getMetadata(AxesMetadata.class);
        }

        @Override
        public Unit<?> getIndexUnits(IStoreItem item)
        {
          DoubleListDocument nd = (DoubleListDocument) item;
          return nd.getIndexUnits();
        }
      };
    }
    else
    {
      throw new IllegalArgumentException("Unexpected item type");
    }

    clearGraph();

    Document<?> doc = (Document<?>) item;

    final String seriesName = item.getName();
    final ILineSeries newSeries =
        (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
            seriesName);

    final PlotSymbolType theSym;
    // if it's a singleton, show the symbol
    // markers
    if (doc.size() > 500 || doc.size() == 1)
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

    List<IStoreItem> items = new ArrayList<IStoreItem>();
    items.add(doc);
    HContainer cont = Helper2D.convertToMean(items);

    // loop through the data
    final List<Double> xValues = new ArrayList<Double>();
    final List<Double> yValues = new ArrayList<Double>();

    // process the data
    double[] rows = cont.rowTitles;
    double[] cols = cont.colTitles;
    double[][] values = cont.values;
    for (int i = 0; i < rows.length; i++)
    {
      for (int j = 0; j < cols.length; j++)
      {
        final Double thisVal = values[i][j];
        if (!thisVal.equals(Double.NaN))
        {
          xValues.add(rows[i]);
          yValues.add(cols[j]);
        }
      }
    }

    final double[] xArr = toArray(xValues);
    final double[] yArr = toArray(yValues);

    newSeries.setXSeries(xArr);
    newSeries.setYSeries(yArr);

    chart.getAxisSet().getYAxes()[0].getTitle().setText(
        "" + helper.getIndexUnits(item));
    chart.getAxisSet().getXAxes()[0].getTitle().setText(
        "" + helper.getIndexUnits(item));

    // adjust the axis range
    chart.getAxisSet().adjustRange();
  }

  private Unit<?> storeIndexedData(final Unit<?> existingUnits,
      final TimePeriod outerPeriod, final IDocument<?> coll,
      final NumberDocument thisQ, final Unit<?> theseUnits,
      final Unit<?> indexUnits, final ILineSeries newSeries)
  {

    final Unit<?> newUnits = existingUnits;

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

      storeListOfIndexedData(coll, thisQ, indexUnits, xTimeData, xData, yData,
          isTemporal);
    }
    else
    {
      // non temporal, include it as a marker line
      // must be non temporal
      xTimeData = new Date[2];
      xData = null;
      yData = new double[2];

      // get the singleton value
      final Double theValue = thisQ.getIterator().next();

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

    // loop through the axes, see if we have suitable
    putOnAxis(chart, newSeries, theseUnits);

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

    // and the x axis title
    final String xTitleStr = getTitleFor(xTimeData, indexUnits);
    final ITitle xTitle = chart.getAxisSet().getXAxis(0).getTitle();
    if (!xTitleStr.equals(xTitle.getText()))
    {
      xTitle.setText(xTitleStr);
    }

    return newUnits;
  }

  private void storeListOfIndexedData(final IDocument<?> coll,
      final NumberDocument thisQ, final Unit<?> indexUnits,
      final Date[] xTimeData, final double[] xData, final double[] yData,
      final boolean isTemporal)
  {
    // must be temporal
    final Iterator<Double> index = coll.getIndexIterator();
    final Iterator<Double> values = thisQ.getIterator();

    int ctr = 0;
    final Unit<Duration> millis = SI.SECOND.divide(1000);
    while (values.hasNext())
    {
      final double t = index.next();
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
          final UnitConverter converter = indexUnits.getConverterTo(millis);
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

  private double[] toArray(final List<Double> xData)
  {
    final double[] res = new double[xData.size()];
    for (int i = 0; i < xData.size(); i++)
    {
      res[i] = xData.get(i);
    }
    return res;
  }

}
