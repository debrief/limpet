package info.limpet.data.impl;

import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.helpers.QuantityHelper;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

import tec.units.ri.quantity.Quantities;


public class TemporalQuantityCollection<T extends Quantity<T>> extends
		TemporalObjectCollection<Quantity<T>> implements
		ITemporalQuantityCollection<T>
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
	public void add(long time, Number value)
	{
		super.add(time, Quantities.getQuantity(value, getUnits()));
	}

	
	@Override
	public void add(Number value)
	{
		throw new UnsupportedOperationException("Please use add(time, value) for time series datasets");
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

}
