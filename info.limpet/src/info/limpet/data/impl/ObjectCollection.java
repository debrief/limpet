package info.limpet.data.impl;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IObjectCollection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectCollection<T extends Object> implements IObjectCollection<T>
{

	ArrayList<T> values = new ArrayList<T>();
	private String name;
	private String description = "";
	private final ICommand<?> precedent;
	private final List<ICommand<?>> dependents;

	// note: we make the change support listeners transient, since
	// they refer to UI elements that we don't persist
	private transient ListenerHelper _changeSupport;

	public ObjectCollection(String name)
	{
		this(name, null);
	}

	public ObjectCollection(String name, ICommand<?> precedent)
	{
		this.name = name;
		this.precedent = precedent;
		dependents = new ArrayList<ICommand<?>>();

		// setup helpers
		initListeners();
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
		
		// tell anyone that wants to know
		_changeSupport.fireMetadataChange(this);
	}

	@Override
	public List<T> getValues()
	{
		return values;
	}

	@Override
	public void add(T value)
	{
		values.add(value);
	}

	@Override
	public int size()
	{
		return values.size();
	}

	@Override
	public String getName()
	{
		return name;
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
		return precedent;
	}

	@Override
	public List<ICommand<?>> getDependents()
	{
		return dependents;
	}

	@Override
	public void addDependent(ICommand<?> command)
	{
		dependents.add(command);
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
		
		// tell anyone that wants to know
		_changeSupport.fireMetadataChange(this);
	}

	protected void initListeners()
	{
		if (_changeSupport == null)
		{
			_changeSupport = new ListenerHelper();
		}
	}

	@Override
	public void addChangeListener(IChangeListener listener)
	{
		initListeners();

		_changeSupport.add(listener);
	}

	@Override
	public void removeChangeListener(IChangeListener listener)
	{
		initListeners();

		_changeSupport.remove(listener);
	}

	@Override
	public void fireDataChanged()
	{
		if (_changeSupport != null)
		{
			// tell any standard listeners
			_changeSupport.fireDataChange(this);
		}

		// now tell the dependents
		Iterator<ICommand<?>> iter = dependents.iterator();
		while (iter.hasNext())
		{
			ICommand<?> iC = (ICommand<?>) iter.next();
			iC.dataChanged(this);
		}
	}

	@Override
	public Class<?> storedClass()
	{
		@SuppressWarnings("unchecked")
		final Class<? extends ObjectCollection<?>> thisClass = (Class<? extends ObjectCollection<?>>) getClass();
		final Type superType = thisClass.getGenericSuperclass();
		if (superType instanceof ParameterizedType)
		{
			final ParameterizedType parameterizedType = (ParameterizedType) superType;
			Type theItem = parameterizedType.getActualTypeArguments()[0];
			if(theItem instanceof Class<?>)
				return (Class<?>) theItem;
			else
				return null;
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean hasChildren()
	{
		return size() > 0;
	}

	@Override
	public void beingDeleted()
	{
		if (_changeSupport != null)
		{
			// tell any standard listeners
			_changeSupport.beingDeleted(this);
		}

		// now tell the dependents
		Iterator<ICommand<?>> iter = dependents.iterator();
		while (iter.hasNext())
		{
			ICommand<?> iC = (ICommand<?>) iter.next();
			iC.collectionDeleted(this);
		}

	}

	public void fireMetadataChanged()
	{
		if (_changeSupport != null)
		{
			// tell any standard listeners
			_changeSupport.fireMetadataChange(this);
		}		
	}

	@Override
	public void clear()
	{
		clearQuiet();
		fireDataChanged();
	}

	@Override
	public void clearQuiet()
	{
		values.clear();
	}

}
