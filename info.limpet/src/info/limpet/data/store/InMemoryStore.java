package info.limpet.data.store;

import info.limpet.ICollection;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InMemoryStore implements IStore
{

	List<ICollection> _store = new ArrayList<ICollection>();
	

	@Override
	public void add(List<ICollection> results)
	{
		_store.addAll(results);
	}

	public int size()
	{
		return _store.size() ;
	}


	@Override
	public ICollection get(String name)
	{
		ICollection res = null;
		Iterator<ICollection> iter = _store.iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();
			if(name.equals(iCollection.getName()))
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
