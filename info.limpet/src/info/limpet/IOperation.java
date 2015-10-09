package info.limpet;


import info.limpet.IStore.IStoreItem;

import java.util.Collection;
import java.util.List;


public interface IOperation<T extends IStoreItem>
{
	public Collection<ICommand<T>> actionsFor(List<T> selection, IStore destination);
}
