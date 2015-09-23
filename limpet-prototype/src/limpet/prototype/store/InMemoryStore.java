package limpet.prototype.store;

import java.util.ArrayList;
import java.util.List;

import limpet.prototype.generics.dinko.interfaces.ICollection;

public class InMemoryStore implements IStore
{

	List<ICollection> alongsideStore = new ArrayList<ICollection>();
	List<ICollection> rootStore = new ArrayList<ICollection>();
	
	@Override
	public void addAlongside(ICollection target, List<ICollection> results)
	{
		alongsideStore.addAll(results);
	}

	@Override
	public void add(List<ICollection> results)
	{
		rootStore.addAll(results);
	}

	public int alongsideSize()
	{
		return alongsideStore.size() ;
	}

	public int rootSize()
	{
		return rootStore.size() ;
	}

}
