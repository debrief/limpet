/*******************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet.data.persistence.xml;

import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.AddQuantityOperation.AddQuantityValues;
import info.limpet.data.operations.MultiplyQuantityOperation.MultiplyQuantityValues;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;

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
		xstream.alias("Temporal.Location", info.limpet.data.impl.samples.TemporalLocation.class);

		xstream.alias("NonTemporal.Length_m", info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M.class);
		xstream.alias("NonTemporal.Speed_msec", info.limpet.data.impl.samples.StockTypes.NonTemporal.Speed_MSec.class);
		xstream.alias("ObjectCollection", ObjectCollection.class);
		xstream.alias("TemporalObjectCollection", TemporalObjectCollection.class);
		xstream.alias("QuantityCollection", QuantityCollection.class);
		xstream.alias("TemporalQuantityCollection", TemporalQuantityCollection.class);

		xstream.alias("Folder", StoreGroup.class);
		

		xstream.alias("Point", org.geotools.geometry.iso.primitive.PointImpl.class);
		xstream.alias("GeographicBoundingBox", org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl.class);
		xstream.alias("LocalName", org.geotools.util.LocalName.class);
		xstream.alias("DefaultCoordinateSystemAxis", org.geotools.referencing.cs.DefaultCoordinateSystemAxis.class);
		xstream.alias("SimpleInternationalString", org.geotools.util.SimpleInternationalString.class);
		xstream.alias("ResponsibleParty", org.geotools.metadata.iso.citation.ResponsiblePartyImpl.class);
		xstream.alias("NamedIdentifier", org.geotools.referencing.NamedIdentifier.class);
		xstream.alias("Identifier", org.geotools.metadata.iso.IdentifierImpl.class);

		// tidier names for operations
		xstream.alias("AddQuantityValues", AddQuantityValues.class);
		xstream.alias("MultiplyQuantityValues", MultiplyQuantityValues.class);
		
		// TODO: KUMAR: create equivalent alias operations (as above) for other defined operations 
		

		// and force some objects to be represnted as attributes, rather than child objects
		xstream.useAttributeFor(ObjectCollection.class, "name");
		xstream.useAttributeFor(AbstractCommand.class, "title");
		xstream.useAttributeFor(AbstractCommand.class, "canUndo");
		xstream.useAttributeFor(AbstractCommand.class, "canRedo");
		xstream.useAttributeFor(AbstractCommand.class, "dynamic");

		xstream.addImplicitCollection(InMemoryStore.class, "_store");

		// setup converter
		xstream.registerConverter(new LimpetCollectionConverter(xstream.getMapper()), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new TimesCollectionConverter(xstream.getMapper()), XStream.PRIORITY_NORMAL);
		xstream.registerConverter(new PointConverter(), XStream.PRIORITY_NORMAL);
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
	
	public IStore fromXML(String xml)
	{
		return (IStore) xstream.fromXML(xml);
	}
	
	public String toXML(IStore store)
	{
		return xstream.toXML(store);
	}

}
