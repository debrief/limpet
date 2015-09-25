package info.limpet.rcp.data_frequency;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.analysis.ObjectFrequencyBins;
import info.limpet.analysis.ObjectFrequencyBins.BinnedData;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class DataFrequencyView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.DataFrequencyView";

	private Action copyToClipboard;
	private ISelectionListener selListener;

	private Chart chart;
	final CollectionComplianceTests aTests;

	/**
	 * The constructor.
	 */
	public DataFrequencyView()
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
		chart.getAxisSet().getYAxis(0).getTitle().setText("Frequency");
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
				IQuantityCollection<?> thisQ = (IQuantityCollection<?>) iCollection;
				bins = QuantityFrequencyBins.doBins(thisQ);

				String seriesName = iCollection.getName() + " (" + thisQ.getUnits()
						+ ")";
				ILineSeries newSeries = (ILineSeries) chart.getSeriesSet()
						.createSeries(SeriesType.LINE, seriesName);
				newSeries.setSymbolType(PlotSymbolType.NONE);
				newSeries.enableArea(true);

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