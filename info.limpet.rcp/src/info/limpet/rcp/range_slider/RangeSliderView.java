package info.limpet.rcp.range_slider;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.List;

import javax.measure.Quantity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

import tec.units.ri.quantity.QuantityRange;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class RangeSliderView extends CoreAnalysisView
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.RangeSliderView";

	private Slider slider;

	private Label minL;

	private Label val;

	private Label maxL;

	private Label label;

	/**
	 * The constructor.
	 */
	public RangeSliderView()
	{
		super(ID, "Range Slider");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		makeActions();
		contributeToActionBars();
		
		// ok, do the layout
		Composite holder = new Composite(parent, SWT.NONE);
		RowLayout gl = new RowLayout(SWT.VERTICAL);
		gl.pack = false;
		holder.setLayout(gl);

		// top row
		Composite headerRow = new Composite(holder, SWT.NONE);
		RowLayout headerL = new RowLayout(SWT.HORIZONTAL);
		headerL.pack = false;
		headerRow.setLayout(headerL);
		label = new Label(headerRow, SWT.NONE);
		label.setText(" == pending  == ");

		Composite topRow = new Composite(holder, SWT.NONE);
		RowLayout trL = new RowLayout(SWT.HORIZONTAL);
		trL.pack = false;
		topRow.setLayout(trL);
		minL = new Label(topRow, SWT.NONE);
		minL.setText("   ==   ");
		val = new Label(topRow, SWT.NONE);
		val.setText("   ==   ");
		maxL = new Label(topRow, SWT.NONE);
		maxL.setText("   ==   ");

		Composite bottomRow = new Composite(holder, SWT.NONE);
		RowLayout brL = new RowLayout(SWT.HORIZONTAL);
		brL.pack = false;
		bottomRow.setLayout(brL);
		
		slider = new Slider(bottomRow, SWT.NONE);
		slider.addListener(SWT.Selection, new Listener()
		{

			@Override
			public void handleEvent(Event event)
			{
				int curVal = slider.getSelection();
				val.setText("" + curVal);
				setValue(curVal);
			}
		});

		// register as selection listener
		setupListener();
	}

	protected void setValue(double val)
	{
		if (getData() != null)
		{
			IQuantityCollection<?> qc = (IQuantityCollection<?>) getData().get(0);
			qc.replaceSingleton(val);
			qc.fireChanged();
		}
	}

	@Override
	public void display(List<ICollection> res)
	{
		showData(res);
	}

	private void showData(List<ICollection> res)
	{

		IQuantityCollection<?> qc = (IQuantityCollection<?>) getData().get(0);
		QuantityRange rng = qc.getRange();
		int curVal = qc.getValues().iterator().next().getValue().intValue();

		label.setText(getData().get(0).getName());
		
		Object min = rng.getMinimum();
		Object max = rng.getMaximum();

		// if(min instanceof DoubleQuantity)
		// {
		//
		// slider.setMinimum(0);
		int minVal = ((Quantity<?>) min).getValue().intValue();
		int maxVal = ((Quantity<?>) max).getValue().intValue();
		slider.setMinimum(minVal);
		slider.setMaximum(maxVal);
		slider.setSelection(curVal);
		
		minL.setText("" + minVal);
		maxL.setText("" + maxVal);
		
		this.getViewSite().getShell().redraw();
		//
		// }
		//
	}

	@Override
	public void setFocus()
	{
		slider.setFocus();
	}

	@Override
	protected boolean appliesToMe(final List<ICollection> selection,
			final CollectionComplianceTests tests)
	{
		boolean res = false;

		if (selection.size() == 1)
		{
			ICollection coll = selection.iterator().next();
			if (coll.isQuantity())
			{
				if (coll.size() == 1)
				{
					IQuantityCollection<?> qc = (IQuantityCollection<?>) coll;
					QuantityRange<?> range = qc.getRange();
					res = (range != null);
				}
			}
		}
		return res;
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

}