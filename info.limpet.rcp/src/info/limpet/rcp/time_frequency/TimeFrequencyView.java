package info.limpet.rcp.time_frequency;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.analysis.TimeFrequencyBins;
import info.limpet.analysis.TimeFrequencyBins.Bin;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.PlottingHelpers;
import info.limpet.rcp.core_view.CoreAnalysisView;

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
import org.swtchart.ext.InteractiveChart;
import org.swtchart.Range;

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
	public static final String ID = "info.limpet.rcp.TimeFrequencyView";

	private Chart chart;

	/**
	 * The constructor.
	 */
	public TimeFrequencyView()
	{
		super(ID, "Time frequency");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
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
	public void display(List<IStoreItem> res)
	{
		if (aTests.allTemporal(res))
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
			ICollection iCollection = (ICollection) iter.next();
			TimeFrequencyBins.BinnedData bins = null;
			if (iCollection.isTemporal())
			{
				if (iCollection.size() > 1)
				{
					if (iCollection.size() <= MAX_SIZE)
					{
						IBaseTemporalCollection thisQ = (IBaseTemporalCollection) iCollection;
						bins = TimeFrequencyBins.doBins(iCollection, thisQ);

						String seriesName = iCollection.getName();
						ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
								.createSeries(SeriesType.LINE, seriesName);
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
							xData[ctr] = new Date(bin.lowerVal);
							yData[ctr++] = bin.freqVal;
							xData[ctr] = new Date(bin.upperVal);
							yData[ctr++] = bin.freqVal;
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
			//ok, all quantities - that's easy
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