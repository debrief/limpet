package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.ian_generics.ITemporalQuantityCollection;

public class TemporalQuantityCollection<T extends Quantity<T>> extends
		QuantityCollection<T> implements ITemporalQuantityCollection<T>
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
		throw new RuntimeException(
				"This is a time series, data must be added with a timestamp");
	}

	@Override
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

		// time is positive
		if (time < 0)
		{
			throw new RuntimeException("Cannot handle negative time");
		}

		_times.add(time);
		super.add(quantity);
	}

	@Override
	public Collection<Long> getTimes()
	{
		return _temporalSupport.getTimes();
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

	@Override
	public Quantity<T> valueAt(long time, InterpolationMethod method)
	{
		// TODO: implement this
		throw new UnsupportedOperationException("Method not implemented");
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
					// TODO Auto-generated method stub
					return _times.get(thisCtr);
				}

				@Override
				public Quantity<T> getObservation()
				{
					// TODO Auto-generated method stub
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
	public Iterator<Doublet<T>> getObservations()
	{
		return new MyIterator() ;
	}
}