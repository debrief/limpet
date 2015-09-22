package limpet.prototype.generics.scrapped.impl;

import java.util.ArrayList;
import java.util.Collection;

import limpet.prototype.generics.iscrapped.nterfaces.IObjectCollection;
import limpet.prototype.generics.scrapped.impl.support.CoreCollection;

public class ObjectCollection<T extends Object> extends CoreCollection implements IObjectCollection<T>
{

	protected final ArrayList<T> _values = new ArrayList<T>();

	public ObjectCollection(String name)
	{
		super(name);
	}

	public Collection<T> getValues()
	{
		return _values;
	}

	public void add(T observation)
	{
		_values.add(observation);
	}

	@Override
	final public boolean isQuantity()
	{
		return false;
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
