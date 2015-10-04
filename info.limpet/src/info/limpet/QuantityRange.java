package info.limpet;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;

public class QuantityRange<T extends Quantity>
{

	private Measure<Double, T> _min;
	private Measure<Double, T> _max;

	public QuantityRange(Measure<Double, T> min,
			Measure<Double, T> max)
	{
		_min = min;
		_max = max;
	}

	public Measurable<T> getMinimum()
	{
		return _min;
	}

	public Measurable<T> getMaximum()
	{
		return _max;
	}


}
