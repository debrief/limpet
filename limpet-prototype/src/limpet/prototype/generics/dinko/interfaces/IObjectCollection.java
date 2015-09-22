package limpet.prototype.generics.dinko.interfaces;

import java.util.Collection;

public interface IObjectCollection<T extends Object>
{
	public Collection<T> getValues();
	public void add(T value);
	public String getName();
}
