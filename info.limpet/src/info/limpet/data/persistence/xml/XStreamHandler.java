package info.limpet.data.persistence.xml;

import info.limpet.IStore;
import info.limpet.data.store.InMemoryStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.thoughtworks.xstream.XStream;

public class XStreamHandler
{

	private static final XStream xstream;
	
	static 
	{
		xstream = new XStream();
		xstream.alias("store", InMemoryStore.class);
		xstream.setMode(XStream.ID_REFERENCES);
	}

	public IStore load(String fileName)
	{
		IStore store = (IStore) xstream.fromXML(new File(fileName));
		return store;
	}

	public void save(IStore store, String fileName) throws FileNotFoundException, IOException
	{
		save(store, new File(fileName));
	}

	private void save(IStore store, File file) throws FileNotFoundException, IOException
	{
		try (OutputStream out = new FileOutputStream(file))
		{
			xstream.toXML(store, out);
		}
	}

	public IStore load(IFile iFile) throws CoreException
	{
		File file = getFile(iFile);
		IStore store = (IStore) xstream.fromXML(file);
		return store;
	}

	private File getFile(IFile iFile) throws CoreException
	{
		URI uri = iFile.getLocationURI();
		if(iFile.isLinked())
		{
			uri = iFile.getRawLocationURI();
		}
		File file = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
		return file;
	}

	public void save(IStore store, IFile iFile) throws CoreException, FileNotFoundException, IOException
	{
		File file = getFile(iFile);
		save(store, file);
	}

}
