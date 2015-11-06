package info.limpet.rcp.xy_plot;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes.Temporal.Location;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.PlottingHelpers;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.geometry.Geometry;
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
	public void display(List<IStoreItem> res)
	{
		if (res.size() == 0)
		{
			chart.setVisible(false);
		}
		else
		{

			// they're all the same type - check the first one
			Iterator<IStoreItem> iter = res.iterator();

			ICollection first = (ICollection) iter.next();

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
				// exception - show locations
				if (aTests.allLocation(res))
				{
					showLocations(res);
					chart.setVisible(true);
				}
				else
					chart.setVisible(false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void showQuantity(List<IStoreItem> res)
	{
		Iterator<IStoreItem> iter = res.iterator();

		clearGraph();

		Unit<Quantity> existingUnits = null;
		
		while (iter.hasNext())
		{
			ICollection coll = (ICollection) iter.next();
			if (coll.isQuantity())
			{
				if (coll.size() >= 1)
				{
					if (coll.size() < maxSize)
					{

						IQuantityCollection<Quantity> thisQ = (IQuantityCollection<Quantity>) coll;

						final Unit<Quantity> theseUnits = thisQ.getUnits();
						String seriesName = thisQ.getName() + " (" + theseUnits + ")";
						ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
								.createSeries(SeriesType.LINE, seriesName);
						
						// if it's a singleton, show the symbol
						final PlotSymbolType theSym;
						if(thisQ.size() == 1)
						{
							theSym = PlotSymbolType.CIRCLE;
						}
						else
						{
							theSym = PlotSymbolType.NONE;
						}
						
						newSeries.setSymbolType(theSym);
						newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));
						newSeries.setSymbolColor(PlottingHelpers.colorFor(seriesName));

						double[] yData = new double[thisQ.size()];

						Iterator<?> values = thisQ.getValues().iterator();
						int ctr = 0;
						while (values.hasNext())
						{
							Measurable<Quantity> tQ = (Measurable<Quantity>) values.next();
							yData[ctr++] = tQ.doubleValue(thisQ.getUnits());
						}

						// ok, do we have existing data?
						if((existingUnits != null) && !(existingUnits.equals(theseUnits)))
						{
							// 	create second Y axis
							int axisId = chart.getAxisSet().createYAxis();								

							// set the properties of second Y axis
							IAxis yAxis2 = chart.getAxisSet().getYAxis(axisId);
							yAxis2.getTitle().setText(theseUnits.toString());
							yAxis2.setPosition(Position.Secondary);
							newSeries.setYAxisId(axisId);
						}
						else
						{
							chart.getAxisSet().getYAxes()[0].getTitle().setText(theseUnits.toString());
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
		}
	}

	@SuppressWarnings("unchecked")
	private void showTemporalQuantity(List<IStoreItem> res)
	{
		Iterator<IStoreItem> iter = res.iterator();

		clearGraph();
		
		Unit<Quantity> existingUnits = null;

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

							final Unit<Quantity> theseUnits = thisQ.getUnits();

							String seriesName = thisQ.getName() + " (" + theseUnits
									+ ")";
							ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
									.createSeries(SeriesType.LINE, seriesName);
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
							
							// ok, do we have existing data?
							if((existingUnits != null) && !(existingUnits.equals(theseUnits)))
							{
								// 	create second Y axis
								int axisId = chart.getAxisSet().createYAxis();								

								// set the properties of second Y axis
								IAxis yAxis2 = chart.getAxisSet().getYAxis(axisId);
								yAxis2.getTitle().setText(theseUnits.toString());
								yAxis2.setPosition(Position.Secondary);
								newSeries.setYAxisId(axisId);
							}
							else
							{
								chart.getAxisSet().getYAxes()[0].getTitle().setText(theseUnits.toString());
								existingUnits = theseUnits;
							}


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
			ICollection coll = (ICollection) iter.next();
			if (!coll.isQuantity())
			{
				if (coll.size() > 1)
				{
					if (coll.size() < maxSize)
					{
						final List<Geometry> values;

						if (coll.isTemporal())
						{
							info.limpet.data.impl.samples.StockTypes.Temporal.Location loc = (Location) coll;
							values = loc.getValues();
						}
						else
						{
							info.limpet.data.impl.samples.StockTypes.NonTemporal.Location loc = (info.limpet.data.impl.samples.StockTypes.NonTemporal.Location) coll;
							values = loc.getValues();
						}

						String seriesName = coll.getName();
						ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
								.createSeries(SeriesType.LINE, seriesName);
						newSeries.setSymbolType(PlotSymbolType.NONE);
						newSeries.setLineColor(PlottingHelpers.colorFor(seriesName));

						double[] xData = new double[values.size()];
						double[] yData = new double[values.size()];

						Iterator<Geometry> vIter = values.iterator();

						int ctr = 0;
						while (vIter.hasNext())
						{
							Geometry geom = vIter.next();
							xData[ctr] = geom.getRepresentativePoint().getOrdinate(0);
							yData[ctr++] = geom.getRepresentativePoint().getOrdinate(1);
						}

						newSeries.setXSeries(xData);
						newSeries.setYSeries(yData);

						newSeries.setSymbolType(PlotSymbolType.CROSS);

						// adjust the axis range
						chart.getAxisSet().adjustRange();

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
	protected boolean appliesToMe(List<IStoreItem> res,
			CollectionComplianceTests tests)
	{
		return (tests.allCollections(res) && tests.allQuantity(res) || tests
				.allNonQuantity(res));
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

}