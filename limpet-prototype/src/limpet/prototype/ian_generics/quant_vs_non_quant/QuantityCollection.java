package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;
import javax.measure.Unit;

public class QuantityCollection<T extends Quantity<?>> extends CoreCollection
{

	private final ArrayList<T> _values = new ArrayList<T>();
	private final Unit<?> _myUnits;

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

}
