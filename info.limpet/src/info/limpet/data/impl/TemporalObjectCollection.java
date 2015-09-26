package info.limpet.data.impl;

import info.limpet.ICommand;
import info.limpet.ITemporalObjectCollection;
import info.limpet.data.impl.helpers.TimeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class TemporalObjectCollection<T extends Object> extends
		ObjectCollection<T> implements ITemporalObjectCollection<T>
{

	ArrayList<Long> _times = new ArrayList<Long>();
	TimeHelper _tSupport;

	public TemporalObjectCollection(String name)
	{
		this(name, null);
	}

	public TemporalObjectCollection(String name, ICommand<?> precedent)
	{
		super(name, precedent);
		_tSupport = new TimeHelper(_times);
	}

	@Override
	public Collection<Long> getTimes()
	{
		return _times;
	}

	@Override
	public void add(T value)
	{
		throw new UnsupportedOperationException(
				"Use add(time,object) for a time series");
	}

	@Override
	public void add(long time, T object)
	{
		_times.add(time);
		super.add(object);
	}

	@Override
	public long start()
	{
		return _tSupport.start();
	}

	@Override
	public long finish()
	{
		return _tSupport.finish();
	}

	@Override
	public long duration()
	{
		return _tSupport.duration();
	}

	@Override
	public double rate()
	{
		return _tSupport.rate();
	}

	public Iterator<Doublet<T>> iterator()
	{
		return new MyIterator();
	}

	private class MyIterator implements Iterator<Doublet<T>>
	{

		int ctr = 0;

		@Override
		public boolean hasNext()
		{
			return ctr < size();
		}

		@Override
		public Doublet<T> next()
		{

			final int thisCtr = ctr;
			ctr++;
			return new Doublet<T>()
			{

				@Override
				public long getTime()
				{
					return _times.get(thisCtr);
				}

				@Override
				public T getObservation()
				{
					return _values.get(thisCtr);
				}
			};
		}

		@Override
		public void remove()
		{
			// TODO: make a decision. We probably don't want to implement this
			throw new UnsupportedOperationException("Method not implemented");
		}

	}


	@Override
	public boolean isQuantity()
	{
		return false;
	}

	@Override
	public boolean isTemporal()
	{
		return true;
	}

}
