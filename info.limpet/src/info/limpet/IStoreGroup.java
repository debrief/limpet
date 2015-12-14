package info.limpet;

import info.limpet.IStore.IStoreItem;

public interface IStoreGroup extends IChangeListener
{

	public boolean hasChildren();

	public boolean add(IStoreItem item);
	
	public boolean remove(Object item);
	
	public void addChangeListener(IChangeListener listener);

	public void removeChangeListener(IChangeListener listener);
	
}
