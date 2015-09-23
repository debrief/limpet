package info.limpet.data.impl.helpers;

import info.limpet.IBaseQuantityCollection;

import java.util.ArrayList;
import java.util.Iterator;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import tec.units.ri.quantity.DefaultQuantityFactory;

public class QuantityHelper<T extends Quantity<T>> implements IBaseQuantityCollection<T>
{
	private ArrayList<Quantity<T>> _values;
	
	private Quantity<T> _min = null;
	private Quantity<T> _max = null;
	private Quantity<T> _mean;
	private Quantity<T> _variance;
	private Quantity<T> _sd;

	private Unit<T> _myUnits;

	public QuantityHelper(ArrayList<Quantity<T>> values, Unit<T> units)
	{		
		_values = values;
		_myUnits = units;
	}

	public void add(Quantity<T> quantity)
	{
		if (_myUnits != quantity.getUnit())
		{
			throw new RuntimeException("New data value in wrong units");
		}

		_values.add(quantity);

		if (_min == null)
		{
			// ok, store the first value
			_min = _max = quantity;
		}
		else
		{
			double doubleVal = quantity.getValue().doubleValue();

			_min = (_min.getValue().doubleValue() < doubleVal) ? _min : quantity;
			_max = (_max.getValue().doubleValue() > doubleVal) ? _max : quantity;
		}

		clearRunningTotal();
	}

	private void clearRunningTotal()
	{
		_mean = null;
		_sd = null;
	}

	@Override
	public Quantity<T> min()
	{
		return _min;
	}

	@Override
	public Quantity<T> max()
	{
		return _max;
	}

	@Override
	public Quantity<T> mean()
	{
		if (_mean == null)
		{
			calcStats();
		}
		return _mean;
	}

	@Override
	public Quantity<T> sd()
	{
		if (_sd == null)
		{
			calcStats();
		}
		return _sd;
	}
	
	@Override
	public Quantity<T> variance()
	{
		if (_variance == null)
		{
			calcStats();
		}
		return _variance;
	}


	@SuppressWarnings("unchecked")
	private void calcStats()
	{
		if (_values.size() > 0)
		{
			Iterator<Quantity<T>> iter = _values.iterator();
			double runningSum = 0;
			while (iter.hasNext())
			{
				Quantity<T> quantity = (Quantity<T>) iter.next();
				runningSum += quantity.getValue().doubleValue();
			}

			final double mean = runningSum / _values.size();

			_mean = (Quantity<T>) DefaultQuantityFactory.getInstance(Speed.class)
					.create(mean, (Unit<Speed>) _myUnits);
			// TODO: DINKO - fix the previous horrible kludge!

			iter = _values.iterator();
			runningSum = 0;
			while (iter.hasNext())
			{
				Quantity<T> quantity = (Quantity<T>) iter.next();				
				double a = quantity.getValue().doubleValue();
				runningSum += (mean - a) * (mean - a);
			}

			final double variance = runningSum / _values.size();
			_variance = (Quantity<T>) DefaultQuantityFactory.getInstance(Speed.class)
					.create(variance, (Unit<Speed>) _myUnits);
			// TODO: DINKO - fix the previous horrible kludge!


			final double sd = Math.sqrt(variance);
			_sd = (Quantity<T>) DefaultQuantityFactory.getInstance(Speed.class)
					.create(sd, (Unit<Speed>) _myUnits);
			// TODO: DINKO - fix the previous horrible kludge!

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
}
