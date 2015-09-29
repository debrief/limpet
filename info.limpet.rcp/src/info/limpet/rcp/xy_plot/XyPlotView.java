package info.limpet.rcp.xy_plot;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.analysis.ObjectFrequencyBins;
import info.limpet.analysis.ObjectFrequencyBins.BinnedData;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
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
public class XyPlotView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.XyPlotView";

	private Action copyToClipboard;
	private ISelectionListener selListener;

	private Chart chart;
	final CollectionComplianceTests aTests;

	/**
	 * The constructor.
	 */
	public XyPlotView()
	{
		aTests = new CollectionComplianceTests();
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
		chart.getAxisSet().getYAxis(0).getTitle().setText("Pending");
		chart.getTitle().setVisible(false);

		// adjust the axis range
		chart.getAxisSet().adjustRange();

		// register as selection listener
		selListener = new ISelectionListener()
		{
			public void selectionChanged(IWorkbenchPart part, ISelection selection)
			{
				newSelection(selection);
			}
		};
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selListener);
	}

	protected void newSelection(ISelection selection)
	{
		List<ICollection> res = new ArrayList<ICollection>();
		if (selection instanceof StructuredSelection)
		{
			StructuredSelection str = (StructuredSelection) selection;

			// check if it/they are suitable
			Iterator<?> iter = str.iterator();
			while (iter.hasNext())
			{
				Object object = (Object) iter.next();
				if (object instanceof IAdaptable)
				{
					IAdaptable ad = (IAdaptable) object;
					ICollection coll = (ICollection) ad.getAdapter(ICollection.class);
					if (coll != null)
					{
						res.add(coll);
					}
				}
			}
		}

		// are there any valid items?
		if (res.size() > 0)
		{
			// are all the items of the same type?
			if (aTests.allQuantity(res) || aTests.allNonQuantity(res))
			{
				reportCollection(res);
			}
		}
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(selListener);

		super.dispose();
	}

	private void reportCollection(List<ICollection> res)
	{
		// they're all the same type - check the first one
		Iterator<ICollection> iter = res.iterator();

		ICollection first = iter.next();

		// sort out what type of data this is.
		if (first.isQuantity())
		{
			if (first.isTemporal())
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
					IQuantityCollection<?> thisQ = (IQuantityCollection<?>) coll;
					
					String seriesName = thisQ.getName() + " (" + thisQ.getUnits()
							+ ")";
					ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
							.createSeries(SeriesType.LINE, seriesName);
					newSeries.setSymbolType(PlotSymbolType.NONE);


					double[] yData = new double[thisQ.size()];

					Iterator<?> values = thisQ.getValues().iterator();
					int ctr = 0;
					while (values.hasNext())
					{
						Quantity tQ = (Quantity) values.next();
						yData[ctr++] = tQ.getValue().doubleValue();
					}
					

//					newSeries.setXSeries(xData);
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
					ITemporalQuantityCollection<?> thisQ = (ITemporalQuantityCollection<?>) coll;
					
					String seriesName = thisQ.getName() + " (" + thisQ.getUnits()
							+ ")";
					ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
							.createSeries(SeriesType.LINE, seriesName);
					newSeries.setSymbolType(PlotSymbolType.NONE);


					Date[] xData = new Date[thisQ.size()];
					double[] yData = new double[thisQ.size()];

					Iterator<?> values = thisQ.getValues().iterator();
					Iterator<Long> times = thisQ.getTimes().iterator();
					int ctr = 0;
					while (values.hasNext())
					{
						Quantity tQ = (Quantity) values.next();
						long t = times.next();
						xData[ctr] = new Date(t);
						yData[ctr++] = tQ.getValue().doubleValue();
					}				

					newSeries.setXDateSeries(xData);
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

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(copyToClipboard);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(copyToClipboard);
	}

	private void makeActions()
	{
		copyToClipboard = new Action()
		{
			public void run()
			{
				copyToClipboard();
			}
		};
		copyToClipboard.setText("Copy to clipboard");
		copyToClipboard.setToolTipText("Copy analysis to clipboard");
		copyToClipboard.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

	private void copyToClipboard()
	{
		// Display display = Display.getCurrent();
		// Clipboard clipboard = new Clipboard(display);
		// StringBuffer output = new StringBuffer();
		// @SuppressWarnings("unchecked")
		// ArrayList<ArrayList<String>> list = (ArrayList<ArrayList<String>>)
		// viewer.getInput();
		// String separator = System.getProperty( "line.separator" );
		// Iterator<ArrayList<String>> iter = list.iterator();
		// while (iter.hasNext())
		// {
		// ArrayList<java.lang.String> arrayList = (ArrayList<java.lang.String>)
		// iter
		// .next();
		// output.append(arrayList.get(0));
		// output.append(", ");
		// output.append(arrayList.get(1));
		// output.append(separator);
		// }
		//
		// clipboard.setContents(new Object[] { output.toString()},
		// new Transfer[] { TextTransfer.getInstance() });
		// clipboard.dispose();
	}

	@Override
	public void setFocus()
	{
		chart.setFocus();
	}

}