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
package info.limpet.ui.time_frequency;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.analysis.TimeFrequencyBins;
import info.limpet.analysis.TimeFrequencyBins.Bin;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.PlottingHelpers;
import info.limpet.ui.core_view.CoreAnalysisView;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.IAxis;
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
public class TimeFrequencyView extends CoreAnalysisView
{

  private static final int MAX_SIZE = 2000;

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.TimeFrequencyView";

  private Chart chart;

  /**
   * The constructor.
   */
  public TimeFrequencyView()
  {
    super(ID, "Time frequency");
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
    chart.getAxisSet().getXAxis(0).getTitle().setText("Time");
    chart.getAxisSet().getYAxis(0).getTitle().setText("Frequency");
    chart.getTitle().setVisible(false);

    // adjust the axis range
    chart.getAxisSet().adjustRange();

    // register as selection listener
    setupListener();
  }

  @Override
  protected void doDisplay(final List<IStoreItem> res)
  {
    if (getATests().allEqualIndexed(res))
    {
      // sort out what type of data this is.
      showData(res);
      chart.setVisible(true);
    }
    else
    {
      chart.setVisible(false);
    }
  }

  private void showData(List<IStoreItem> res)
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
      IDocument<?> iCollection = (IDocument<?>) iter.next();
      TimeFrequencyBins.BinnedData bins = null;
      if (iCollection.isIndexed() && iCollection.size() > 1
          && iCollection.size() <= MAX_SIZE)
      {
        bins = TimeFrequencyBins.doBins(iCollection);

        String seriesName = iCollection.getName();
        ILineSeries newSeries =
            (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE,
                seriesName);
        newSeries.setSymbolType(PlotSymbolType.NONE);
        newSeries.enableArea(true);
        newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

        Date[] xData = new Date[bins.size() * 2];
        double[] yData = new double[bins.size() * 2];

        // put the data into series
        int ctr = 0;
        Iterator<Bin> iter2 = bins.iterator();
        while (iter2.hasNext())
        {
          Bin bin = (TimeFrequencyBins.Bin) iter2.next();
          xData[ctr] = new Date(bin.getLowerVal());
          yData[ctr++] = bin.getFreqVal();
          xData[ctr] = new Date(bin.getUpperVal());
          yData[ctr++] = bin.getFreqVal();
        }

        newSeries.setXDateSeries(xData);
        newSeries.setYSeries(yData);

        newSeries.enableStack(true);
        newSeries.enableArea(true);

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
