package limpet.prototype.ian_generics.dinko;

import java.util.ArrayList;
import java.util.Collection;

public class TemporalObjectCollection <T extends Object> extends ObjectCollection<T> implements ITemporalObjectCollection<T>
{

	ArrayList<Long> _times = new ArrayList<Long>();
	
	public TemporalObjectCollection(String name)
	{
		super(name);
	}

	@Override
	public Collection<Long> getTimes()
	{
		return _times;
	}
	
	@Override
	public void add(T value)
	{
		throw new UnsupportedOperationException("Use add(time,object) for a time series");
	}

	@Override
	public void add(long time, T object)
	{
		_times.add(time);
		super.add(object);
	}

}
