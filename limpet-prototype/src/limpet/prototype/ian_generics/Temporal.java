package limpet.prototype.ian_generics;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;

abstract public class Temporal<T extends Object> extends CoreCollection{

	private ArrayList<TemporalObservation<T>> _values= new ArrayList<TemporalObservation<T>>();

	public Temporal(String name)
	{
		super(name);
	}
	
	public static class TemporalObservation<T>
	{
		private long _time;
		private T _observation;
		public TemporalObservation(long time, T observation)
		{
			_time = time;
			_observation = observation;
		}
		public long getTime()
		{
			return _time;
		}
		public T getObservation()
		{
			return _observation;
		}
	}

	public void add(long time, T observation)
	{
		// do some checking.
		// 1. this time should be equal to or newer than the last item
		if(size()>0)
		{
			if(_values.get(_values.size()-1)._time > time)
			{
				throw new RuntimeException("Temporal quantities must arrive in time order");
			}
		}
		_values.add(new TemporalObservation<T>(time, observation));
	}
	

	public static class QuantityType<Q extends Quantity<?>> extends Temporal<Quantity<?>>
	{
		public QuantityType(String name) {
			super(name);
		}

		@Override
		public boolean isQuantity() {
			return true;
		}		
	}

	public static class ObjectType extends Temporal<Object>
	{
		public ObjectType(String name) {
			super(name);
		}

		@Override
		public boolean isQuantity() {
			return false;
		}		
	}

	@Override
	abstract public boolean isQuantity(); 
		
	public Collection<TemporalObservation<T>> getMeasurements()
	{
		return _values;
	}

	@Override
	public int size() {
		return _values.size();
	}

	@Override
	public boolean isTemporal() {
		return true;
	}
	
}
