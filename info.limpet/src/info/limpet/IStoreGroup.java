package info.limpet;

import java.util.Collection;

import info.limpet.IStore.IStoreItem;

public interface IStoreGroup extends IStoreItem, Collection<IStoreItem>, IChangeListener
{
  
}
