package limpet.prototype.operations;

import limpet.prototype.commands.ICommand;
import limpet.prototype.generics.dinko.interfaces.ICollection;
import limpet.prototype.store.IStore;

public interface IOperation
{
	public ICommand[] actionsFor(ICollection[] selection, IStore destination);
}
