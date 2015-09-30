package info.limpet.data.store;

import info.limpet.ICollection;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryStore implements IStore
{

	List<ICollection> _store = new ArrayList<ICollection>();

	private List<StoreChangeListener> _listeners = new ArrayList<StoreChangeListener>();

	public static interface StoreChangeListener
	{
		public void changed();
	}

	public void addChangeListener(StoreChangeListener listener)
	{
		_listeners.add(listener);
	}

	public void removeChangeListener(StoreChangeListener listener)
	{
		_listeners.remove(listener);
	}

	protected void fireModified()
	{
		Iterator<StoreChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			InMemoryStore.StoreChangeListener listener = (InMemoryStore.StoreChangeListener) iter
					.next();
			listener.changed();
		}
	}
	
	@Override
	public void add(List<ICollection> results)
	{
		_store.addAll(results);
		
		fireModified();
	}

	public int size()
	{
		return _store.size();
	}

	@Override
	public ICollection get(String name)
	{
		ICollection res = null;
		Iterator<ICollection> iter = _store.iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();
			if (name.equals(iCollection.getName()))
			{
				res = iCollection;
				break;
			}
		}
		return res;
	}

	public Iterator<ICollection> iterator()
	{
		return _store.iterator();
	}

	public void clear()
	{
		_store.clear();
	}

}
