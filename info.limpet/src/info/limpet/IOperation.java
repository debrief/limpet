package info.limpet;


import java.util.Collection;
import java.util.List;


public interface IOperation<T extends ICollection>
{
	public Collection<ICommand<T>> actionsFor(List<T> selection, IStore destination);
}
