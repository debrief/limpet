package info.limpet.data.impl;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IObjectCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class ObjectCollection<T extends Object> implements IObjectCollection<T>
{

	ArrayList<T> _values = new ArrayList<T>();
	private String _name;
	private String _description = "";
	private final ICommand<?> _precedent;
	private final List<ICommand<?>> _dependents;
	private final ListenerHelper _changeSupport;
	
	public ObjectCollection(String name)
	{
		this(name, null);
	}

	public ObjectCollection(String name, ICommand<?> precedent)
	{
		_name = name;
		_precedent = precedent;
		_dependents = new ArrayList<ICommand<?>>();
		_changeSupport = new ListenerHelper();
	}	
	
	@Override
	public String getDescription()
	{
		return _description;
	}

	@Override
	public void setDescription(String description)
	{
		this._description = description;
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
		return _name;
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
	public ICommand<?> getPrecedent()
	{
		return _precedent;
	}

	@Override
	public List<ICommand<?>> getDependents()
	{
		return _dependents;
	}

	@Override
	public void addDependent(ICommand<?> command)
	{
		_dependents.add(command);
	}

	@Override
	public void setName(String name)
	{
		_name = name;
	}

	@Override
	public void addChangeListener(IChangeListener listener)
	{
		_changeSupport.add(listener);
	}
	
	@Override
	public void removeChangeListener(IChangeListener listener)
	{
		_changeSupport.remove(listener);
	}
	

	@Override
	public void fireChanged()
	{
		// tell any standard listeners
		_changeSupport.fireChange(this);
		
		// now tell the dependents
		Iterator<ICommand<?>> iter = _dependents.iterator();
		while (iter.hasNext())
		{
			ICommand<?> iC = (ICommand<?>) iter.next();
			iC.dataChanged(this);
		}
	}

	
}
