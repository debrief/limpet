package info.limpet.data.impl;

import info.limpet.IChangeListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListenerHelper
{

	private List<IChangeListener> _listeners;

	public ListenerHelper()
	{
		_listeners = new ArrayList<IChangeListener>();
	}

	public void add(IChangeListener listener)
	{
		_listeners.add(listener);
	}

	public void fireChange()
	{
		Iterator<IChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			IChangeListener iL = (IChangeListener) iter.next();
			iL.dataChanged();
		}
	}

	public void remove(IChangeListener listener)
	{
		_listeners.remove(listener);
	}

	
}
