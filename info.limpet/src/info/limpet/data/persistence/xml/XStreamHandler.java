package info.limpet.data.persistence.xml;

import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.store.InMemoryStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.measure.converter.MultiplyConverter;
import javax.measure.unit.AlternateUnit;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.TransformedUnit;

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
		xstream.alias("TransformedUnit", TransformedUnit.class);
		xstream.alias("ProductUnit", ProductUnit.class);
		xstream.alias("AlternateUnit", AlternateUnit.class);
		xstream.alias("MultiplyConverter", MultiplyConverter.class);
		xstream.alias("BaseUnit", BaseUnit.class);
		xstream.alias("Temporal.Speed_MSec", info.limpet.data.impl.samples.StockTypes.Temporal.Speed_MSec.class);
		xstream.alias("Temporal.Angle_Degs", info.limpet.data.impl.samples.StockTypes.Temporal.Angle_Degrees.class);
		xstream.alias("Temporal.Angle_Rads", info.limpet.data.impl.samples.StockTypes.Temporal.Angle_Radians.class);
		xstream.alias("Temporal.Elapsed_Time", info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTime_Sec.class);
		xstream.alias("Temporal.Location", info.limpet.data.impl.samples.StockTypes.Temporal.Location.class);

		xstream.alias("NonTemporal.Length_m", info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M.class);
		xstream.alias("NonTemporal.Speed_msec", info.limpet.data.impl.samples.StockTypes.NonTemporal.Speed_MSec.class);
		xstream.alias("ObjectCollection", ObjectCollection.class);
		xstream.alias("TemporalObjectCollection", TemporalObjectCollection.class);
		xstream.alias("QuantityCollection", QuantityCollection.class);
		xstream.alias("TemporalQuantityCollection", TemporalQuantityCollection.class);

		
		xstream.useAttributeFor(ObjectCollection.class, "name");
		xstream.useAttributeFor(AbstractCommand.class, "title");
		xstream.useAttributeFor(AbstractCommand.class, "canUndo");
		xstream.useAttributeFor(AbstractCommand.class, "canRedo");
		xstream.useAttributeFor(AbstractCommand.class, "dynamic");
		xstream.useAttributeFor(AbstractCommand.class, "outputName");

		xstream.addImplicitCollection(InMemoryStore.class, "_store");
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
