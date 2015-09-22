package limpet.prototype.ian_generics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import limpet.prototype.ian_generics.impl.support.TemporalSupport;
import limpet.prototype.ian_generics.interfaces.ITemporalObjectCollection;

public class TemporalObjectCollection<T extends Object> extends
		ObjectCollection<T> implements ITemporalObjectCollection<T>
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

	@Override
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
	

	private class MyIterator implements Iterator<Doublet<T>>
	{

		int ctr = 0;
		
		public MyIterator()
		{
			
		}

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
			return new Doublet<T>(){

				@Override
				public long getTime()
				{
					return _times.get(thisCtr);
				}

				@Override
				public T getObject()
				{
					return  _values.get(thisCtr);
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
	public Iterator<Doublet<T>> iterator()
	{
		return new MyIterator() ;
	}
	
}