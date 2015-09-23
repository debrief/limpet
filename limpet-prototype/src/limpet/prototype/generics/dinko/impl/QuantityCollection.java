package limpet.prototype.generics.dinko.impl;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.generics.dinko.impl.hlpers.QuantityHelper;
import limpet.prototype.generics.dinko.interfaces.IQuantityCollection;

public class QuantityCollection<T extends Quantity<T>> extends
		ObjectCollection<Quantity<T>> implements IQuantityCollection<T>
{

	Unit<T> _units;
	QuantityHelper<T> _qHelper;

	public QuantityCollection(String name, Unit<T> units)
	{
		super(name);
		_units = units;
		_qHelper = new QuantityHelper<>(_values, units);
	}

	@Override
	public Dimension getDimension()
	{
		return _qHelper.getDimension();
	}
	
	@Override
	public Unit<T> getUnits()
	{
		return _qHelper.getUnits();
	}
	
	@Override
	public void add(Quantity<T> value)
	{
		_qHelper.add(value);
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
	

	@Override
	public boolean isQuantity()
	{
		return true;
	}

	@Override
	public boolean isTemporal()
	{
		return false;
	}

}
