package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.ian_generics.IQuantityCollection;

public class QuantityCollection<T extends Quantity<?>> extends CoreCollection implements IQuantityCollection<T>
{

	private final ArrayList<T> _values = new ArrayList<T>();
	private final Unit<?> _myUnits;
	
	private T _min = null;
	private T _max = null;
	private T _mean;
	private T _sd;

	public QuantityCollection(String name, Unit<?> units)
	{
		super(name);
		_myUnits = units;
	}

	public Collection<T> getValues()
	{
		return _values;
	}

	public void add(T quantity)
	{
		if (_myUnits != quantity.getUnit())
		{
			throw new RuntimeException("New data value in wrong units");
		}

		_values.add(quantity);
		
		if(_min == null)
		{
			// ok, store the first value
			_min = _max = quantity;
		}
		else
		{
			double doubleVal = quantity.getValue().doubleValue();
			
			_min = ( _min.getValue().doubleValue() < doubleVal ) ? _min : quantity;
			_max = ( _max.getValue().doubleValue() > doubleVal ) ? _max : quantity;
		}
		
		clearRunningTotal();
	}

	private void clearRunningTotal()
	{
		_mean = null;
		_sd = null;
	}

	@Override
	public boolean isQuantity()
	{
		return true;
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

	@Override
	public T min()
	{
		return _min;
	}

	@Override
	public T max()
	{
		return _max;
	}

	@Override
	public T mean()
	{
		if(_mean == null)
		{
			calcStats();
		}
		return _mean;
	}

	@Override
	public T sd()
	{
		if(_sd == null)
		{
			calcStats();
		}
		return _sd;
	}

	private void calcStats()
	{
		// loop through the values, calc mean & SD
		// TODO:  Somehow we need to produce a T. Should be ok, we know the doubleValue and the units for this dataset 
	}


}
