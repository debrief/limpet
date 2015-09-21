package limpet.prototype.ian_generics.temp_vs_non_temp;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;

abstract public class NonTemporal<T extends Object> extends CoreCollection
{

	private ArrayList<T> _values = new ArrayList<T>();

	public NonTemporal(String name)
	{
		super(name);
	}

	public void add(T observation)
	{
		_values.add(observation);
	}

	public static class QuantityType extends NonTemporal<Quantity<?>>
	{
		public QuantityType(String name)
		{
			super(name);
		}

		@Override
		public boolean isQuantity()
		{
			return true;
		}
	}

	public static class ObjectType extends NonTemporal<Object>
	{
		public ObjectType(String name)
		{
			super(name);
		}

		@Override
		public boolean isQuantity()
		{
			return false;
		}
	}

	@Override
	abstract public boolean isQuantity();

	public Collection<T> getObservations()
	{
		return _values;
	}

	@Override
	public int size()
	{
		return _values.size();
	}

	@Override
	public boolean isTemporal()
	{
		return false;
	}

}
