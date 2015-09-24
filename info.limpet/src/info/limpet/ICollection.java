package info.limpet;

import java.util.List;

public interface ICollection
{
	public String getName();
	public int size();
	public boolean isQuantity();
	public boolean isTemporal();
	public abstract List<ICommand> getDependents();
	public abstract ICommand getPrecedent();
	public void addDependent(ICommand addQuantityValues);

}