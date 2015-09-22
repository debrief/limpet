package limpet.prototype.ian_generics.dinko;

import javax.measure.Quantity;
import javax.measure.Unit;

public class QuantityCollection<T extends Quantity<T>> extends ObjectCollection<Quantity<T>> implements IQuantityCollection<T>
{

	Unit<?> _units;
	
	public QuantityCollection(String name, Unit<?> units)
	{
		super(name);
		_units = units;
	}

	@Override
	public Quantity<T> min()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<T> max()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<T> mean()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<T> variance()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quantity<T> sd()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
