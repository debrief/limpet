package info.limpet.rcp.xy_plot;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.PlottingHelpers;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.IAxis;
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

	private final int maxSize = 10000;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.XyPlotView";
	protected CollectionComplianceTests aTests = new CollectionComplianceTests();

	private Chart chart;

	public XyPlotView()
	{
		super(ID, "XY plot view");
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
		chart.getAxisSet().getXAxis(0).getTitle().setText("Value");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Value");
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
			if (aTests.allTemporal(res))
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
			chart.setVisible(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void showQuantity(List<ICollection> res)
	{
		Iterator<ICollection> iter = res.iterator();

		// clear the graph
		ISeries[] series = chart.getSeriesSet().getSeries();
		for (int i = 0; i < series.length; i++)
		{
			ISeries iSeries = series[i];
			chart.getSeriesSet().deleteSeries(iSeries.getId());
		}

		while (iter.hasNext())
		{
			ICollection coll = (ICollection) iter.next();
			if (coll.isQuantity())
			{
				if (coll.size() > 1)
				{
					if (coll.size() < maxSize)
					{

						IQuantityCollection<Quantity> thisQ = (IQuantityCollection<Quantity>) coll;

						String seriesName = thisQ.getName() + " (" + thisQ.getUnits() + ")";
						ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
								.createSeries(SeriesType.LINE, seriesName);
						newSeries.setSymbolType(PlotSymbolType.NONE);
						newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

						double[] yData = new double[thisQ.size()];

						Iterator<?> values = thisQ.getValues().iterator();
						int ctr = 0;
						while (values.hasNext())
						{
							Measurable<Quantity> tQ = (Measurable<Quantity>) values.next();
							yData[ctr++] = tQ.doubleValue(thisQ.getUnits());
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
		}
	}

	@SuppressWarnings("unchecked")
	private void showTemporalQuantity(List<ICollection> res)
	{
		Iterator<ICollection> iter = res.iterator();

		// clear the graph
		ISeries[] series = chart.getSeriesSet().getSeries();
		for (int i = 0; i < series.length; i++)
		{
			ISeries iSeries = series[i];
			chart.getSeriesSet().deleteSeries(iSeries.getId());
		}

		while (iter.hasNext())
		{
			ICollection coll = (ICollection) iter.next();
			if (coll.isQuantity())
			{
				if (coll.size() > 1)
				{
					if (coll.size() < maxSize)
					{
						if (coll.isTemporal())
						{
							ITemporalQuantityCollection<Quantity> thisQ = (ITemporalQuantityCollection<Quantity>) coll;

							String seriesName = thisQ.getName() + " (" + thisQ.getUnits()
									+ ")";
							ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
									.createSeries(SeriesType.LINE, seriesName);
							newSeries.setSymbolType(PlotSymbolType.NONE);
							newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

							Date[] xData = new Date[thisQ.size()];
							double[] yData = new double[thisQ.size()];

							Iterator<?> values = thisQ.getValues().iterator();
							Iterator<Long> times = thisQ.getTimes().iterator();
							int ctr = 0;
							while (values.hasNext())
							{
								Measurable<Quantity> tQ = (Measurable<Quantity>) values.next();
								long t = times.next();
								xData[ctr] = new Date(t);
								yData[ctr++] = tQ.doubleValue(thisQ.getUnits());
							}

							newSeries.setXDateSeries(xData);
							newSeries.setYSeries(yData);

							newSeries.setSymbolType(PlotSymbolType.CROSS);

							chart.getAxisSet().getXAxis(0).getTitle().setText("Time");

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
		return (tests.allQuantity(res) || tests.allNonQuantity(res));
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

}