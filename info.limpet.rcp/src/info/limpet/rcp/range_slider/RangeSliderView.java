package info.limpet.rcp.range_slider;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.List;

import javax.measure.Quantity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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
public class RangeSliderView extends CoreAnalysisView implements
		IChangeListener
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

	private IQuantityCollection<?> _current;

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
		FillLayout gl = new FillLayout();
		holder.setLayout(gl);

		Composite theG = new Composite(holder, SWT.NONE);
		RowLayout gr = new RowLayout(SWT.VERTICAL);
		theG.setLayout(gr);

		// top row
		Composite headerRow = new Composite(theG, SWT.NONE);
		FillLayout headerL = new FillLayout();
		headerRow.setLayout(headerL);
		label = new Label(headerRow, SWT.NONE);
		label.setText("     ====== pending  ====== ");
		label.setSize(300, 45);

		Composite sliderRow = new Composite(theG, SWT.NONE);
		RowLayout brL = new RowLayout(SWT.VERTICAL);
		brL.pack = true;
		sliderRow.setLayout(brL);
		slider = new Slider(sliderRow, SWT.NONE);
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
		slider.setSize(300, 46);

		Composite labelRow = new Composite(theG, SWT.NONE);
		RowLayout trL = new RowLayout(SWT.HORIZONTAL);
		trL.pack = false;
		labelRow.setLayout(trL);
		minL = new Label(labelRow, SWT.NONE);
		minL.setText("   ==   ");
		val = new Label(labelRow, SWT.NONE);
		val.setText("   ==   ");
		maxL = new Label(labelRow, SWT.NONE);
		maxL.setText("   ==   ");

		// register as selection listener
		setupListener();
	}

	protected void setValue(double val)
	{
		if (getData() != null)
		{
			if (getData().size() > 0)
			{
				IQuantityCollection<?> qc = (IQuantityCollection<?>) getData().get(0);
				qc.replaceSingleton(val);
				qc.fireChanged();
			}
		}
	}

	@Override
	public void display(List<ICollection> res)
	{
		ICollection newColl = res.get(0);

		// is this different?
		if (_current != newColl)
		{
			// are we showing anything?
			if (_current != null)
			{
				// ok, stop listening
				_current.removeChangeListener(this);

				_current = null;
			}
		}

		_current = (IQuantityCollection<?>) newColl;

		showData(_current);
	}

	private void showData(IQuantityCollection<?> qc)
	{

		QuantityRange<?> rng = qc.getRange();
		int curVal = qc.getValues().iterator().next().getValue().intValue();

		label.setText(getData().get(0).getName() + " (" + qc.getUnits().toString()
				+ ")");

		Object min = rng.getMinimum();
		Object max = rng.getMaximum();

		int minVal = ((Quantity<?>) min).getValue().intValue();
		int maxVal = ((Quantity<?>) max).getValue().intValue();
		slider.setMinimum(minVal);
		slider.setMaximum(maxVal + slider.getThumb());
		// note: add the width of the thumb object to get the true max value
		slider.setSelection(curVal);

		minL.setText("" + minVal);
		maxL.setText("" + maxVal);
		val.setText("" + curVal);

		this.getViewSite().getShell().redraw();
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

	@Override
	public void dataChanged(ICollection subject)
	{
		// ok, re=do the update
		showData(_current);
	}

	@Override
	public void collectionDeleted(ICollection subject)
	{
		// TODO Auto-generated method stub

	}

}