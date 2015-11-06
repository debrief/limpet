package info.limpet.rcp.range_slider;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.QuantityRange;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.SimpleMovingAverageOperation.SimpleMovingAverageCommand;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

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

	private IQuantityCollection<Quantity> _currentColl;
	private SimpleMovingAverageCommand _command;

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
		GridLayout gl = new GridLayout(3, false);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		holder.setLayoutData(gd);
		holder.setLayout(gl);

		label = new Label(holder, SWT.NONE);
		gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		label.setText("     ====== pending  ====== ");

		slider = new Slider(holder, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		slider.setLayoutData(gd);
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
		minL = new Label(holder, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
		minL.setLayoutData(gd);
		minL.setText("   ==   ");

		val = new Label(holder, SWT.NONE);
		gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
		val.setLayoutData(gd);
		val.setText("   ==   ");

		maxL = new Label(holder, SWT.NONE);
		gd = new GridData(SWT.END, SWT.FILL, true, false);
		maxL.setLayoutData(gd);
		maxL.setText("   ==   ");

		// register as selection listener
		setupListener();
	}

	protected void setValue(double val)
	{
		if(_currentColl != null)
		{
			_currentColl.replaceSingleton(val);
			_currentColl.fireDataChanged();
		}
		else if(_command != null)
		{
			_command.setWindowSize((int)val);
			_command.recalculate();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void display(List<IStoreItem> res)
	{
		if(res.size() != 1)
			return;
		
		if(_currentColl != null)
		{
			// ok, stop listening
			_currentColl.removeChangeListener(this);
			
		}
		
		if(_command != null)
		{
			
		}
		
		
		IStoreItem first = res.get(0);
		if(first instanceof ICollection)
		{
			ICollection newColl = (ICollection) res.get(0);

			_currentColl = (IQuantityCollection<Quantity>) newColl;

			showData(_currentColl);
		}
		else if(first instanceof ICommand<?>)
		{

			if(first instanceof SimpleMovingAverageCommand)
			{
				SimpleMovingAverageCommand command = (SimpleMovingAverageCommand) first;

			// is this different?
			if (_command != command)
			{
				// are we showing anything?
				if (_command != null)
				{
					_command = null;
				}
			}

			_command = command;

			showData(_command);

			}
		}
		
	}

	private void showData(SimpleMovingAverageCommand command)
	{
		int curVal = (int)command.getWindowSize();

		label.setText(command.getName());

		slider.setMinimum((int) 1);
		slider.setMaximum((int) (50 + slider.getThumb()));
		// note: add the width of the thumb object to get the true max value
		slider.setSelection(curVal);

		minL.setText("" + 0);
		maxL.setText("" + 50);
		val.setText("" + curVal);

		label.getParent().getParent().layout(true, true);
		label.getParent().getParent().redraw();
	}

	@SuppressWarnings("unchecked")
	private void showData(IQuantityCollection<Quantity> qc)
	{

		QuantityRange<Quantity> rng = qc.getRange();
		Unit<Quantity> theUnits = qc.getUnits();
		int curVal = (int) qc.getValues().iterator().next()
				.doubleValue(theUnits);

		final String unitStr;
		if(theUnits != null && theUnits.toString().length()>0)
		{
			unitStr = theUnits.toString();
		}
		else
		{
			unitStr = "n/a";
		}
		
		label.setText(qc.getName() + " (" + unitStr	+ ")");

		Object min = rng.getMinimum();
		Object max = rng.getMaximum();

		long minVal = ((Measurable<Quantity>) min).longValue(qc.getUnits());
		long maxVal = ((Measurable<Quantity>) max).longValue(qc.getUnits());
		slider.setMinimum((int) minVal);
		slider.setMaximum((int) (maxVal + slider.getThumb()));
		// note: add the width of the thumb object to get the true max value
		slider.setSelection(curVal);

		minL.setText("" + minVal);
		maxL.setText("" + maxVal);
		val.setText("" + curVal);

		label.getParent().getParent().layout(true, true);
		label.getParent().getParent().redraw();
	}

	@Override
	public void setFocus()
	{
		slider.setFocus();
	}

	@Override
	protected boolean appliesToMe(final List<IStoreItem> selection,
			final CollectionComplianceTests tests)
	{
		boolean res = false;

		if (selection.size() == 1)
		{
			IStoreItem item = selection.iterator().next();
			if (item instanceof ICollection)
			{
				ICollection coll = (ICollection) item;

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
			else if (item instanceof ICommand)
			{
				ICommand<?> coll = (ICommand<?>) item;

				if(coll instanceof SimpleMovingAverageCommand)
				{
						res = true;
				}
			}
			
		}
		
		slider.setEnabled(res);
		
		return res;
	}

	@Override
	protected String getTextForClipboard()
	{
		return "Pending";
	}

	@Override
	public void dataChanged(IStoreItem subject)
	{
		// ok, re=do the update
		showData(_currentColl);
	}

	@Override
	public void collectionDeleted(IStoreItem subject)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void metadataChanged(IStoreItem subject)
	{
		// TODO: provide a more informed way of doing update
		dataChanged(subject);
	}

}