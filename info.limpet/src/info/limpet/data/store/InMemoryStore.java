package info.limpet.data.store;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryStore implements IStore, IChangeListener
{

	List<ICollection> _store = new ArrayList<ICollection>();

	private transient List<StoreChangeListener> _listeners = new ArrayList<StoreChangeListener>();

	private Object readResolve() {
    _listeners = new ArrayList<StoreChangeListener>();
    return this;
  }
	
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
	public void addAll(List<ICollection> results)
	{
		// add the items individually, so we can register as a listener
		Iterator<ICollection> iter = results.iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();
			add(iCollection);
		}

		fireModified();
	}

	@Override
	public void add(ICollection results)
	{
		_store.add(results);
		
		// register as a listener with the results object
		results.addChangeListener(this);

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
		// stop listening to the collections individually
		// - defer the clear until the end,
		// so we don't get concurrent modification
		Iterator<ICollection> iter = _store.iterator();
		while (iter.hasNext())
		{
			ICollection iC = (ICollection) iter.next();
			iC.removeChangeListener(this);
		}
		
		_store.clear();
		fireModified();
	}

	public void remove(ICollection collection)
	{
		_store.remove(collection);
		
		// stop listening to this one
		collection.removeChangeListener(this);

		fireModified();
	}

	@Override
	public void dataChanged(ICollection subject)
	{
		fireModified();
	}

	@Override
	public void collectionDeleted(ICollection subject)
	{
	}

}
