package info.limpet.data.impl;

import java.util.ArrayList;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.data.impl.helpers.QuantityHelper;



public class QuantityCollection<T extends Quantity> extends
		ObjectCollection<Measurable<T>> implements IQuantityCollection<T>
{

	Unit<T> _units;
	QuantityHelper<T> _qHelper;

	public QuantityCollection(String name, Unit<T> units)
	{
		this(name, null, units);
	}

	public QuantityCollection(String name, ICommand<?> precedent, Unit<T> units)
	{
		super(name, precedent);
		_units = units;
		_qHelper = new QuantityHelper<T>((ArrayList<Measurable<T>>) _values, units);
	}
	
	@Override
	public void setRange(QuantityRange<T> range)
	{
		_qHelper.setRange(range);
	}
	
	@Override
	public QuantityRange<T> getRange()
	{
		return _qHelper.getRange();
	}

	@Override
	public void add(Number value)
	{
		_qHelper.add(value);
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
	public void add(Measurable<T> value)
	{
		_qHelper.add(value);
	}

	@Override
	public Measurable<T> min()
	{
		return _qHelper.min();
	}

	@Override
	public Measurable<T> max()
	{
		return _qHelper.max();
	}

	@Override
	public Measurable<T> mean()
	{
		return _qHelper.mean();
	}

	@Override
	public Measurable<T> variance()
	{
		return _qHelper.variance();
	}

	@Override
	public Measurable<T> sd()
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

	@Override
	public void replaceSingleton(double newValue)
	{
		_qHelper.replace(newValue);
	}

}
