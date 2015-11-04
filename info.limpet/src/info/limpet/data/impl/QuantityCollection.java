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

	Unit<T> units;
	
	private transient QuantityHelper<T> _qHelper;

	// TODO: we store range at this level, and in the Q Helper
	// this is required for persistence reasons, since the
	// Q Helper is transient
	private QuantityRange<T> _range;
	
	public QuantityCollection(String name, Unit<T> units)
	{
		this(name, null, units);
	}

	public QuantityCollection(String name, ICommand<?> precedent, Unit<T> units)
	{
		super(name, precedent);
		this.units = units;
		initQHelper();
	}
	
	protected void initQHelper()
	{
		if(_qHelper == null)
		{
			_qHelper = new QuantityHelper<T>((ArrayList<Measurable<T>>) values, units);
		}
	}
	
	@Override
	public void setRange(QuantityRange<T> range)
	{
		initQHelper();
		_range = range;
		_qHelper.setRange(range);
		
		// tell anyone that wants to know
		super.fireMetadataChanged();
	}
	
	@Override
	public QuantityRange<T> getRange()
	{
		initQHelper();
		return _range;
	}

	@Override
	public void add(Number value)
	{
		initQHelper();
		_qHelper.add(value);
	}

	@Override
	public Dimension getDimension()
	{
		initQHelper();
		return _qHelper.getDimension();
	}
	
	@Override
	public Unit<T> getUnits()
	{
		initQHelper();
		return _qHelper.getUnits();
	}
	
	@Override
	public void add(Measurable<T> value)
	{
		initQHelper();
		_qHelper.add(value);
	}

	@Override
	public Measurable<T> min()
	{
		initQHelper();
		return _qHelper.min();
	}

	@Override
	public Measurable<T> max()
	{
		initQHelper();
		return _qHelper.max();
	}

	@Override
	public Measurable<T> mean()
	{
		initQHelper();
		return _qHelper.mean();
	}

	@Override
	public Measurable<T> variance()
	{
		initQHelper();
		return _qHelper.variance();
	}

	@Override
	public Measurable<T> sd()
	{
		initQHelper();
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
