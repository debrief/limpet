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
package info.limpet.ui.data_frequency;

import info.limpet.IDocument;
import info.limpet.IObjectDocument;
import info.limpet.IStoreItem;
import info.limpet.analysis.ObjectFrequencyBins;
import info.limpet.analysis.ObjectFrequencyBins.BinnedData;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;
import org.swtchart.ext.InteractiveChart;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class DataFrequencyView extends CoreAnalysisView
{

  private static final int MAX_SIZE = 2000;

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.DataFrequencyView";

  private Chart chart;

  /**
   * The constructor.
   */
  public DataFrequencyView()
  {
    super(ID, "Data frequency");
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
    chart.getAxisSet().getYAxis(0).getTitle().setText("Frequency");
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
      return;
    }

    // they're all the same type - check the first one
    Iterator<IStoreItem> iter = res.iterator();

    IDocument first = (IDocument) iter.next();

    // sort out what type of data this is.
    if (first.isQuantity())
    {
      showQuantity(res);
    }
    else
    {
      showObject(res);
    }
  }

  private void showObject(List<IStoreItem> res)
  {
    Iterator<IStoreItem> iter = res.iterator();

    // clear the graph
    ISeries[] coll = chart.getSeriesSet().getSeries();
    for (int i = 0; i < coll.length; i++)
    {
      ISeries iSeries = coll[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    while (iter.hasNext())
    {
      IDocument iCollection = (IDocument) iter.next();
      if (iCollection.size() <= MAX_SIZE)
      {
        BinnedData bins = null;
        IObjectDocument objDoc = (IObjectDocument) iCollection;
        bins = ObjectFrequencyBins.doBins(objDoc);

        String seriesName = iCollection.getName();
        IBarSeries newSeries =
            (IBarSeries) chart.getSeriesSet().createSeries(SeriesType.BAR,
                seriesName);
        newSeries.setBarColor(PlottingHelpers.colorFor(seriesName));

        String[] xData = new String[bins.size()];
        double[] yData = new double[bins.size()];

        // put the data into series
        int ctr = 0;
        Iterator<ObjectFrequencyBins.Bin> iter2 = bins.iterator();
        while (iter2.hasNext())
        {
          ObjectFrequencyBins.Bin bin = (ObjectFrequencyBins.Bin) iter2.next();
          xData[ctr] = (String) bin.getIndexVal();
          yData[ctr++] = bin.getFreqVal();
        }

        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.setCategorySeries(xData);
        xAxis.enableCategory(true);

        // newSeries.set(xData);
        newSeries.setYSeries(yData);

        // adjust the axis range
        chart.getAxisSet().adjustRange();

        chart.redraw();
      }
    }
  }

  private void showQuantity(List<IStoreItem> res)
  {
    Iterator<IStoreItem> iter = res.iterator();

    // clear the graph
    ISeries[] coll = chart.getSeriesSet().getSeries();
    for (int i = 0; i < coll.length; i++)
    {
      ISeries iSeries = coll[i];
      chart.getSeriesSet().deleteSeries(iSeries.getId());
    }

    while (iter.hasNext())
    {
      NumberDocument iCollection = (NumberDocument) iter.next();
      QuantityFrequencyBins.BinnedData bins = null;
      if (iCollection.isQuantity() && iCollection.size() > 1
          && iCollection.size() <= MAX_SIZE)
      {
        bins = QuantityFrequencyBins.doBins(iCollection);

        String seriesName =
            iCollection.getName() + " (" + iCollection.getUnits() + ")";
        ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setSymbolType(PlotSymbolType.NONE);
        newSeries.enableArea(true);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

        double[] xData = new double[bins.size() * 2];
        double[] yData = new double[bins.size() * 2];

        // put the data into series
        int ctr = 0;
        Iterator<Bin> iter2 = bins.iterator();
        while (iter2.hasNext())
        {
          Bin bin = (QuantityFrequencyBins.Bin) iter2.next();
          xData[ctr] = bin.getLowerVal();
          yData[ctr++] = bin.getFreqVal();
          xData[ctr] = bin.getUpperVal();
          yData[ctr++] = bin.getFreqVal();
        }

        newSeries.setXSeries(xData);
        newSeries.setYSeries(yData);

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        IAxis xAxis = chart.getAxisSet().getXAxis(0);
        xAxis.enableCategory(false);

        // set the y axis min to be zero
        Range yRange = chart.getAxisSet().getYAxis(0).getRange();
        chart.getAxisSet().getYAxis(0).setRange(new Range(0, yRange.upper));

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
  protected boolean appliesToMe(List<IStoreItem> selection,
      CollectionComplianceTests tests)
  {
    final boolean res;
    if (tests.allQuantity(selection))
    {
      // ok, all quantities - that's easy
      res = true;
    }
    else if (tests.allNonQuantity(selection))
    {
      if (tests.allNonLocation(selection))
      {
        // none of them are locations - that's ok
        res = true;
      }
      else
      {
        // hmm, locations - scary. say no
        res = false;
      }
    }
    else
    {
      // mixed sorts, let's not bother
      res = false;
    }

    chart.setVisible(res);

    return res;
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

}
