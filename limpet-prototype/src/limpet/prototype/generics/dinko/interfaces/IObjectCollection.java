package limpet.prototype.generics.dinko.interfaces;

import java.util.List;

public interface IObjectCollection<T extends Object> extends ICollection
{
	public List<T> getValues();
	public void add(T value);
}
