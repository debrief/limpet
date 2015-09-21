package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;

import limpet.prototype.ian_generics.ITemporalCollection;

public class TemporalObjectCollection<T extends Object> extends
		ObjectCollection<T> implements ITemporalCollection
{
	private final ArrayList<Long> _times = new ArrayList<Long>();
	TemporalSupport _temporalSupport;


	public TemporalObjectCollection(String name)
	{
		super(name);
		_temporalSupport = new TemporalSupport(_times);
	}

	@Override
	public void add(T observation)
	{
		throw new RuntimeException("This is a time series, data must be added with a timestamp");
	}

	public void add(long time, T observation)
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
		super.add(observation);
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