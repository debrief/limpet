package limpet.prototype.store;

import java.util.List;

import limpet.prototype.generics.dinko.interfaces.ICollection;

public interface IStore
{
	void addAlongside(ICollection target, List<ICollection> results);
	void add(List<ICollection> results);
}
