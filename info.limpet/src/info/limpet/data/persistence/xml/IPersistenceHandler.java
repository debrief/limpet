package info.limpet.data.persistence.xml;

import info.limpet.IStore;

public interface IPersistenceHandler
{

	IStore load(String fileName);

	void save(IStore store, String fileName);

}
