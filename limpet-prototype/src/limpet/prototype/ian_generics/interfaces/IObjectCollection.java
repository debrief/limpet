package limpet.prototype.ian_generics.interfaces;

import java.util.Collection;

public interface IObjectCollection<T extends Object> {
	public void add(T object);
	
	public Collection<T> getValues();
}
