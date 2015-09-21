package limpet.prototype.ian_generics;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;

abstract public class TemporalCollection<T extends TemporalObservation<?>> implements ICollection{

	private String _name;
	private ArrayList<T> _values= new ArrayList<T>();

	public TemporalCollection(String name)
	{
		_name = name;
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
	//	_values.add(new T(time, observation));
	}
	
	public static class TemporalQuantity extends TemporalObservation<Quantity<?>>
	{
		public TemporalQuantity(long time, Quantity<?> observation) {
			super(time, observation);
		}		
	}

	public static class TemporalObject extends TemporalObservation<Object>
	{
		public TemporalObject(long time, Object observation) {
			super(time, observation);
		}		
	}

	public static class TemporalQuantityCollection extends TemporalCollection<TemporalQuantity>
	{
		public TemporalQuantityCollection(String name) {
			super(name);
		}

		@Override
		public boolean isQuantity() {
			return true;
		}		
	}

	public static class TemporalObjectCollection extends TemporalCollection<TemporalObject>
	{
		public TemporalObjectCollection(String name) {
			super(name);
		}

		@Override
		public boolean isQuantity() {
			return false;
		}		
	}

	@Override
	abstract public boolean isQuantity(); 
		
	public Collection<T> getMeasurements()
	{
		return _values;
	}

	@Override
	public String name() {
		return _name;
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
