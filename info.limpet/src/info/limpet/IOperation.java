package info.limpet;


import java.util.Collection;
import java.util.List;


public interface IOperation
{
	public Collection<ICommand> actionsFor(List<ICollection> selection, IStore destination);
}
