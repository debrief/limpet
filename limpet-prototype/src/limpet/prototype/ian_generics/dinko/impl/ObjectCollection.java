package limpet.prototype.ian_generics.dinko.impl;

import java.util.ArrayList;
import java.util.Collection;

import limpet.prototype.ian_generics.dinko.interfaces.IObjectCollection;


public class ObjectCollection<T extends Object> implements IObjectCollection<T>
{

	ArrayList<T> _values = new ArrayList<T>();
	private String _myName;
	
	public ObjectCollection(String name)
	{
		_myName = name;
	}

	@Override
	public Collection<T> getValues()
	{
		return _values;
	}

	@Override
	public void add(T value)
	{
		_values.add(value);
	}

	public int size()
	{
		return _values.size();
	}
	
}
