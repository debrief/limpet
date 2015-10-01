package info.limpet.rcp.time_frequency;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.PlottingHelpers;
import info.limpet.rcp.core_view.CoreAnalysisView;

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

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class TimeFrequencyView extends CoreAnalysisView
{

	private static final int MAX_SIZE = 1000;

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
		super(ID);
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
		chart = new Chart(parent, SWT.NONE);

		// set titles
		chart.getAxisSet().getXAxis(0).getTitle().setText("Time");
		chart.getAxisSet().getYAxis(0).getTitle().setText("# of Measurements");
		chart.getTitle().setVisible(false);

		// adjust the axis range
		chart.getAxisSet().adjustRange();

		// register as selection listener
		setupListener();
	}

	@Override
	public void display(List<ICollection> res)
	{
		// sort out what type of data this is.
		showData(res);
	}

	private void showData(List<ICollection> res)
	{
		Iterator<ICollection> iter = res.iterator();

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
			QuantityFrequencyBins.BinnedData bins = null;
			if (iCollection.isQuantity())
			{
				if (iCollection.size() > 1)
				{
					if (iCollection.size() <= MAX_SIZE)
					{
						IQuantityCollection<?> thisQ = (IQuantityCollection<?>) iCollection;
						bins = QuantityFrequencyBins.doBins(thisQ);

						String seriesName = iCollection.getName() + " (" + thisQ.getUnits()
								+ ")";
						ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
								.createSeries(SeriesType.LINE, seriesName);
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
							xData[ctr] = bin.lowerVal;
							yData[ctr++] = bin.freqVal;
							xData[ctr] = bin.upperVal;
							yData[ctr++] = bin.freqVal;
						}

						newSeries.setXSeries(xData);
						newSeries.setYSeries(yData);

						// adjust the axis range
						chart.getAxisSet().adjustRange();
						IAxis xAxis = chart.getAxisSet().getXAxis(0);
						xAxis.enableCategory(false);

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
	protected boolean appliesToMe(List<ICollection> res,
			CollectionComplianceTests tests)
	{
		// are all the items of the same type?
		return (tests.nonEmpty(res) && tests.allTemporal(res));
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

}