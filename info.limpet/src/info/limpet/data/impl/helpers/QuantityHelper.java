package info.limpet.data.impl.helpers;

import info.limpet.IBaseQuantityCollection;
import info.limpet.QuantityRange;

import java.util.ArrayList;
import java.util.Iterator;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

public class QuantityHelper<T extends Quantity> implements IBaseQuantityCollection<T>
{
	private ArrayList<Measurable<T>> _values;
	
	private Measurable<T> _min = null;
	private Measurable<T> _max = null;
	private Measurable<T> _mean;
	private Measurable<T> _variance;
	private Measurable<T> _sd;

	private Unit<T> _myUnits;

	private QuantityRange<T> _range;

	public QuantityHelper(ArrayList<Measurable<T>> values, Unit<T> units)
	{		
		_values = values;
		_myUnits = units;
	}
	
	public void add(Number value)
	{
		Measurable<T> newVal = Measure.valueOf(value.doubleValue(), getUnits());
		_values.add(newVal);
	}

	public void add(Measurable<T> quantity)
	{

		_values.add(quantity);

		if (_min == null)
		{
			// ok, store the first value
			_min = _max = quantity;
		}
		else
		{
			double doubleVal = quantity.doubleValue(getUnits());

			_min = (_min.doubleValue(getUnits()) < doubleVal) ? _min : quantity;
			_max = (_max.doubleValue(getUnits()) > doubleVal) ? _max : quantity;
		}

		clearRunningTotal();
	}

	private void clearRunningTotal()
	{
		_mean = null;
		_sd = null;
	}

	@Override
	public Measurable<T> min()
	{
		return _min;
	}

	@Override
	public Measurable<T> max()
	{
		return _max;
	}

	@Override
	public Measurable<T> mean()
	{
		if (_mean == null)
		{
			calcStats();
		}
		return _mean;
	}

	@Override
	public Measurable<T> sd()
	{
		if (_sd == null)
		{
			calcStats();
		}
		return _sd;
	}
	
	@Override
	public Measurable<T> variance()
	{
		if (_variance == null)
		{
			calcStats();
		}
		return _variance;
	}


	private void calcStats()
	{
		if (_values.size() > 0)
		{
			Iterator<Measurable<T>> iter = _values.iterator();
			double runningSum = 0;
			while (iter.hasNext())
			{
				Measurable<T> quantity = (Measurable<T>) iter.next();
				runningSum += quantity.doubleValue(getUnits());
			}

			final double mean = runningSum / _values.size();

			_mean =  Measure.valueOf(mean,  _myUnits);

			iter = _values.iterator();
			runningSum = 0;
			while (iter.hasNext())
			{
				Measurable<T> quantity = iter.next();				
				double a = quantity.doubleValue(_myUnits);
				runningSum += (mean - a) * (mean - a);
			}

			final double variance = runningSum / _values.size();
			_variance = Measure.valueOf(variance, _myUnits);

			final double sd = Math.sqrt(variance);
			_sd = Measure.valueOf(sd, _myUnits);
		}
	}

	@Override
	public Dimension getDimension()
	{
		return _myUnits.getDimension();
	}

	@Override
	public Unit<T> getUnits()
	{
		return _myUnits;
	}

	public void replace(double newValue)
	{
		if(_values.size() != 1)
		{
			throw new RuntimeException("We only call this on singletons");
		}

		// create a new value
		Measurable<T> newVal = Measure.valueOf(newValue, getUnits());
		
		// drop the existing value
		_values.clear();
		
		// and insert the new value
		_values.add(newVal);
	}

	public void setRange(QuantityRange<T> range)
	{
		_range = range;
	}
	
	public QuantityRange<T> getRange()
	{
		return _range;
	}
}
