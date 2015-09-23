package info.limpet.data.impl;

import info.limpet.IObjectCollection;

import java.util.ArrayList;
import java.util.List;



public class ObjectCollection<T extends Object> implements IObjectCollection<T>
{

	ArrayList<T> _values = new ArrayList<T>();
	private String _myName;
	
	public ObjectCollection(String name)
	{
		_myName = name;
	}

	@Override
	public List<T> getValues()
	{
		return _values;
	}

	@Override
	public void add(T value)
	{
		_values.add(value);
	}

	@Override
	public int size()
	{
		return _values.size();
	}

	@Override
	public String getName()
	{
		return _myName;
	}

	@Override
	public boolean isQuantity()
	{
		return false;
	}

	@Override
	public boolean isTemporal()
	{
		return false;
	}
	
	
}
