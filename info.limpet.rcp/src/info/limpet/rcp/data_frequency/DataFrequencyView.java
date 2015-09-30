package info.limpet.rcp.data_frequency;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.analysis.ObjectFrequencyBins;
import info.limpet.analysis.ObjectFrequencyBins.BinnedData;
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
import org.swtchart.IBarSeries;
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
public class DataFrequencyView extends CoreAnalysisView
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.DataFrequencyView";

	private Chart chart;

	/**
	 * The constructor.
	 */
	public DataFrequencyView()
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
		chart.getAxisSet().getXAxis(0).getTitle().setText("Value");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Frequency");
		chart.getTitle().setVisible(false);

		// adjust the axis range
		chart.getAxisSet().adjustRange();

		// register as selection listener
		setupListener();
	}

	@Override
	public void display(List<ICollection> res)
	{

		// they're all the same type - check the first one
		Iterator<ICollection> iter = res.iterator();

		ICollection first = iter.next();

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

	private void showObject(List<ICollection> res)
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
			BinnedData bins = null;
			IObjectCollection<?> thisQ = (IObjectCollection<?>) iCollection;
			bins = ObjectFrequencyBins.doBins(thisQ);

			String seriesName = iCollection.getName();
			IBarSeries newSeries = (IBarSeries) chart.getSeriesSet().createSeries(
					SeriesType.BAR, seriesName);
			newSeries.setBarColor(PlottingHelpers.colorFor(seriesName));

			String[] xData = new String[bins.size()];
			double[] yData = new double[bins.size()];

			// put the data into series
			int ctr = 0;
			Iterator<ObjectFrequencyBins.Bin> iter2 = bins.iterator();
			while (iter2.hasNext())
			{
				ObjectFrequencyBins.Bin bin = (ObjectFrequencyBins.Bin) iter2.next();
				xData[ctr] = (String) bin.indexVal;
				yData[ctr++] = bin.freqVal;
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

	private void showQuantity(List<ICollection> res)
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
		return (tests.allQuantity(res) || tests.allNonQuantity(res));
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

}