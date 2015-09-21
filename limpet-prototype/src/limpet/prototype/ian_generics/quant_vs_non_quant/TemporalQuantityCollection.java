package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.ian_generics.ITemporalCollection;

public class TemporalQuantityCollection<T extends Quantity<T>> extends
		QuantityCollection<Quantity<T>> implements ITemporalCollection
{

	private ArrayList<Long> _times = new ArrayList<Long>();

	// helper class
	TemporalSupport _temporalSupport;

	public TemporalQuantityCollection(String name, Unit<?> units)
	{
		super(name, units);
		_temporalSupport = new TemporalSupport(_times);
	}

	
	
	@Override
	public void add(Quantity<T> quantity)
	{
		throw new RuntimeException("This is a time series, data must be added with a timestamp");
	}

	public void add(long time, Quantity<T> quantity)
	{
		// do some checking.
		// 1. this time should be equal to or newer than the last item
		if (size() > 0)
		{
			if (_times.get(_times.size() - 1) > time)
			{
				throw new RuntimeException(
						"Temporal quantities must arrive in time order");
			}
		}

		_times.add(time);
		super.add(quantity);
	}

	public Collection<Long> getTimes()
	{
		return _times;
	}

	@Override
	public boolean isTemporal()
	{
		return true;
	}

	@Override
	public long start()
	{
		return _temporalSupport.start();
	}

	@Override
	public long finish()
	{
		return _temporalSupport.finish();
	}

	@Override
	public long duration()
	{
		return _temporalSupport.duration();
	}

	@Override
	public double rate()
	{
		return _temporalSupport.rate();
	}
}