package info.limpet.data.impl;

import info.limpet.IChangeListener;
import info.limpet.IStore.IStoreItem;

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

	public void fireDataChange(IStoreItem subject)
	{
		Iterator<IChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			IChangeListener iL = (IChangeListener) iter.next();
			iL.dataChanged(subject);
		}
	}
	
	public void fireMetadataChange(IStoreItem subject)
	{
		Iterator<IChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			IChangeListener iL = (IChangeListener) iter.next();
			iL.metadataChanged(subject);
		}
	}
	
	public void beingDeleted(IStoreItem subject)
	{
		Iterator<IChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			IChangeListener iL = (IChangeListener) iter.next();
			iL.collectionDeleted(subject);
		}
	}

	public void remove(IChangeListener listener)
	{
		_listeners.remove(listener);
	}

	
}
