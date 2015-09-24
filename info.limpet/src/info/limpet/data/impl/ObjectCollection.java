package info.limpet.data.impl;

import info.limpet.ICommand;
import info.limpet.IObjectCollection;

import java.util.ArrayList;
import java.util.List;



public class ObjectCollection<T extends Object> implements IObjectCollection<T>
{

	ArrayList<T> _values = new ArrayList<T>();
	private String _myName;
	private final ICommand _precedent;
	private final List<ICommand> _dependents;
	
	public ObjectCollection(String name)
	{
		this(name, null);
	}

	public ObjectCollection(String name, ICommand precedent)
	{
		_myName = name;
		_precedent = precedent;
		_dependents = new ArrayList<ICommand>();
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

	@Override
	public ICommand getPrecedent()
	{
		return _precedent;
	}

	@Override
	public List<ICommand> getDependents()
	{
		return _dependents;
	}

	@Override
	public void addDependent(ICommand command)
	{
		_dependents.add(command);
	}
	
	
}
