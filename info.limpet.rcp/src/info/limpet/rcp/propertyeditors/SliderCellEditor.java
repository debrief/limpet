package info.limpet.rcp.propertyeditors;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

public class SliderCellEditor extends CellEditor
{

	Composite _myControl = null;
	protected Label _myLabel;
	protected Slider _theSlider;
	private int minValue;
	private int maxValue;

	public SliderCellEditor(final Composite parent, int minValue, int maxValue)
	{
		super(parent, SWT.NONE);
		this.minValue = minValue;
    this.maxValue = maxValue;
	}

	/**
	 * @return
	 */
	public LayoutData getLayoutData()
	{
		final CellEditor.LayoutData res = super.getLayoutData();
		res.grabHorizontal = true;
		return res;
	}

	protected Control createControl(final Composite parent)
	{
		final Font font = parent.getFont();
		final Color bg = parent.getBackground();

		_myControl = new Composite(parent, getStyle());
		_myControl.setFont(font);
		_myControl.setBackground(bg);

		final GridLayout rl = new GridLayout();
		rl.marginWidth = 0;
		rl.marginHeight = 0;
		rl.numColumns = 8;

		_myControl.setLayout(rl);
		_myLabel = new Label(_myControl, SWT.NONE);
		_myLabel.setText("000");
		_myLabel.setBackground(bg);
		final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		_myLabel.setLayoutData(gd1);

		_theSlider = new Slider(_myControl, SWT.HORIZONTAL);
		final GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 7;
		_theSlider.setLayoutData(gd2);
		_theSlider.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(final SelectionEvent e)
			{
				_myLabel.setText(formatMe(_theSlider.getSelection()));
				_myLabel.update();
			}

			public void widgetDefaultSelected(final SelectionEvent e)
			{

			}
		});

		return _myControl;
	}

	protected Object doGetValue()
	{
		Object res = null;
		if (_theSlider != null)
		{
			res = new Integer(_theSlider.getSelection()).toString();
		}
		return res;
	}

	protected void doSetFocus()
	{
		_theSlider.setFocus();
	}

	DecimalFormat _df = null;

	protected String formatMe(final int value)
	{
		if (_df == null)
			_df = new DecimalFormat("000");

		return _df.format(value);
	}

	protected void doSetValue(final Object value)
	{
		final String curr = (String) value;
		if (_myLabel != null)
			_myLabel.setText(curr);
		if (_theSlider != null)
		{
			_theSlider.setMaximum(maxValue);
			_theSlider.setMinimum(minValue);
			_theSlider.setIncrement(1);
			_theSlider.setPageIncrement(5);
			_theSlider.setSelection(new Double(curr).intValue());
			_theSlider.setThumb(1);
		}
	}

}
