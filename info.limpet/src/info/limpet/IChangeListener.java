package info.limpet;

import info.limpet.IStore.IStoreItem;

public interface IChangeListener
{
	public void dataChanged(IStoreItem subject);
	
	public void collectionDeleted(IStoreItem subject);
}
