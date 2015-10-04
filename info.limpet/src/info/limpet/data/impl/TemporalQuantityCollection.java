package info.limpet.data.impl;

import java.util.ArrayList;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.data.impl.helpers.QuantityHelper;

//public class QuantityCollection<T extends Quantity> extends
//ObjectCollection<T> implements IQuantityCollection<T>

public class TemporalQuantityCollection<T extends Quantity> extends
		TemporalObjectCollection<Measurable<T>> implements
		ITemporalQuantityCollection<Measurable<T>>, IQuantityCollection<T>
{

	private Unit<T> _myUnits;
	private QuantityHelper<T> _qHelper;

	public TemporalQuantityCollection(String name, Unit<T> units)
	{
		this(name, null, units);
	}
	
	public TemporalQuantityCollection(String name, ICommand<?> precedent, Unit<T> units)
	{
		super(name);
		_myUnits = units;
		_qHelper = new QuantityHelper<T>((ArrayList<Measurable<T>>) _values, units);
	}

	@Override
	public void add(long time, Measurable<T> object)
	{
		if (_myUnits != object.getUnit())
		{
			throw new RuntimeException("New data value in wrong units");
		}

		super.add(time, object.doubleValue(getUnits()));
	}

	
	@Override
	public void add(long time, Number value)
	{
		super.add(time, Measure.valueOf(value.doubleValue(), getUnits()));
	}

	
	@Override
	public void add(Number value)
	{
		throw new UnsupportedOperationException("Please use add(time, value) for time series datasets");
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
		return true;
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
	public void replaceSingleton(double newValue)
	{
		_qHelper.replace(newValue);
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
}
