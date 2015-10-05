package info.limpet.data.persistence.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;

import info.limpet.IStore;
import info.limpet.data.store.InMemoryStore;

public class XStreamHandler implements IPersistenceHandler
{

	private static final XStream xstream;
	
	static 
	{
		xstream = new XStream();
		xstream.alias("store", InMemoryStore.class);
	}

	@Override
	public IStore load(String fileName)
	{
		IStore store = (IStore) xstream.fromXML(new File(fileName));
		return store;
	}

	@Override
	public void save(IStore store, String fileName)
	{
		try (OutputStream out = new FileOutputStream(new File(fileName)))
		{
			xstream.toXML(store, out);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
