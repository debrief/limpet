package limpet.prototype.operations;

import java.util.Collection;
import java.util.List;

import limpet.prototype.commands.ICommand;
import limpet.prototype.generics.dinko.interfaces.ICollection;
import limpet.prototype.store.IStore;

public interface IOperation
{
	public Collection<ICommand> actionsFor(List<ICollection> selection, IStore destination);
}
