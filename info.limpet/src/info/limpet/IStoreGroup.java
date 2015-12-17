package info.limpet;

import java.util.Collection;
import java.util.Spliterator;

import info.limpet.IStore.IStoreItem;

public interface IStoreGroup extends IStoreItem, Collection<IStoreItem>, IChangeListener
{
  /* Note: clarify which splititerator we want to use, to overcome compiler warning)
   *
   */
  public Spliterator<IStoreItem> spliterator();
  
}
