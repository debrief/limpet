package limpet.prototype.ian_generics.dinko;

import java.util.Collection;

public interface IObjectCollection<T extends Object>
{
	public Collection<T> getValues();
	public void add(T value);
}
