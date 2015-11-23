package info.limpet.data.impl;

import info.limpet.ICommand;
import info.limpet.ITemporalObjectCollection;
import info.limpet.data.impl.helpers.TimeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class TemporalObjectCollection<T extends Object> extends
		ObjectCollection<T> implements ITemporalObjectCollection<T>
{

	ArrayList<Long> times = new ArrayList<Long>();
	transient TimeHelper tSupport;

	public TemporalObjectCollection(String name)
	{
		this(name, null);
	}

	public TemporalObjectCollection(String name, ICommand<?> precedent)
	{
		super(name, precedent);
		
		initTime();
	}
	
	@Override
	public void clearQuiet()
	{
		times.clear();
		
		// let the parent clear the objects/values
		super.clearQuiet();
	}

	
	
	@Override
	public void clear()
	{
		times.clear();
		
		// let the parent clear the objects/values
		super.clear();
	}

	protected void initTime()
	{
		if(tSupport == null)
		{
			tSupport = new TimeHelper(times);
		}
	}

	@Override
	public List<Long> getTimes()
	{
		return times;
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
		times.add(time);
		super.add(object);
	}

	@Override
	public long start()
	{
		initTime();
		return tSupport.start();
	}

	@Override
	public long finish()
	{
		initTime();
		return tSupport.finish();
	}

	@Override
	public long duration()
	{
		initTime();
		return tSupport.duration();
	}

	@Override
	public double rate()
	{
		initTime();
		return tSupport.rate();
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
					return times.get(thisCtr);
				}

				@Override
				public T getObservation()
				{
					return values.get(thisCtr);
				}
			};
		}

		@Override
		public void remove()
		{
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
