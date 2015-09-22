package limpet.prototype.ian_generics.dinko.impl;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.ian_generics.dinko.interfaces.ITemporalQuantityCollection;


public class TemporalQuantityCollection<T extends Quantity<T>> extends TemporalObjectCollection<Quantity<T>> implements ITemporalQuantityCollection<T>
{

	private Unit<?> _myUnits;

	public TemporalQuantityCollection(String string, Unit<?> units)
	{
		super(string);
		_myUnits = units;
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
	
	
}
