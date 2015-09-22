package limpet.prototype.generics.iscrapped.nterfaces;

import java.util.Collection;

public interface IObjectCollection<T extends Object> {
	public void add(T object);
	
	public Collection<T> getValues();
}
