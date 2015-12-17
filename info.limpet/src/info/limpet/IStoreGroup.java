package info.limpet;

import java.util.Collection;
import java.util.Spliterator;

import info.limpet.IStore.IStoreItem;

public interface IStoreGroup extends IStoreItem, Collection<IStoreItem>, IChangeListener
{
  
}
