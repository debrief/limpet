package info.limpet;


import java.util.List;


public interface IStore
{
	void addAlongside(ICollection target, List<ICollection> results);
	void add(List<ICollection> results);
}
