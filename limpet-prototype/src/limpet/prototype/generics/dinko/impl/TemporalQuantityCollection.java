package limpet.prototype.generics.dinko.impl;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.generics.dinko.interfaces.IBaseQuantityCollection;
import limpet.prototype.generics.dinko.interfaces.ITemporalQuantityCollection;


public class TemporalQuantityCollection<T extends Quantity<T>> extends TemporalObjectCollection<Quantity<T>> implements ITemporalQuantityCollection<T>, IBaseQuantityCollection<T>
{

	private Unit<T> _myUnits;
	private QuantityHelper<T> _qHelper;

	public TemporalQuantityCollection(String string, Unit<T> units)
	{
		super(string);
		_myUnits = units;
		_qHelper = new QuantityHelper<>(_values, units);
	}

	@Override
	public void add(long time, Quantity<T> object)
	{
		if (_myUnits != object.getUnit())
		{
			throw new RuntimeException("New data value in wrong units");
		}
		
		super.add(time, object);
	}
	
	@Override
	public Quantity<T> min()
	{
		return _qHelper.min();
	}

	@Override
	public Quantity<T> max()
	{
		return _qHelper.max();
	}

	@Override
	public Quantity<T> mean()
	{
		return _qHelper.mean();
	}

	@Override
	public Quantity<T> variance()
	{
		return _qHelper.variance();
	}

	@Override
	public Quantity<T> sd()
	{
		return _qHelper.sd();
	}
	
}
