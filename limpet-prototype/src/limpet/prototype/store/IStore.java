package limpet.prototype.store;

import limpet.prototype.generics.dinko.interfaces.ICollection;

public interface IStore
{
	void addAlongside(ICollection target, ICollection[] results);
	void add(ICollection[] results);
}
