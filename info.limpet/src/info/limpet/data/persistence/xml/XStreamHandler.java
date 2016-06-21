/*****************************************************************************
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
 *****************************************************************************/
package info.limpet.data.persistence.xml;

import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.arithmetic.AddQuantityOperation.AddQuantityValues;
import info.limpet.data.operations.arithmetic.MultiplyQuantityOperation.MultiplyQuantityValues;
import info.limpet.data.store.StoreGroup;

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

  private static final XStream XSTREAM;

  static
  {
    XSTREAM = new XStream();
    XSTREAM.alias("store", StoreGroup.class);
    XSTREAM.alias("TransformedUnit", TransformedUnit.class);
    XSTREAM.alias("ProductUnit", ProductUnit.class);
    XSTREAM.alias("AlternateUnit", AlternateUnit.class);
    XSTREAM.alias("MultiplyConverter", MultiplyConverter.class);
    XSTREAM.alias("BaseUnit", BaseUnit.class);
    XSTREAM.alias("Temporal.Speed_MSec",
        info.limpet.data.impl.samples.StockTypes.Temporal.SpeedMSec.class);
    XSTREAM.alias("Temporal.Angle_Degs",
        info.limpet.data.impl.samples.StockTypes.Temporal.AngleDegrees.class);
    XSTREAM.alias("Temporal.Angle_Rads",
        info.limpet.data.impl.samples.StockTypes.Temporal.AngleRadians.class);
    XSTREAM.alias("Temporal.Elapsed_Time",
        info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTimeSec.class);
    XSTREAM.alias("Temporal.Location",
        info.limpet.data.impl.samples.TemporalLocation.class);

    XSTREAM.alias("NonTemporal.Length_m",
        info.limpet.data.impl.samples.StockTypes.NonTemporal.LengthM.class);
    XSTREAM.alias("NonTemporal.Speed_msec",
        info.limpet.data.impl.samples.StockTypes.NonTemporal.SpeedMSec.class);
    XSTREAM.alias("ObjectCollection", ObjectCollection.class);
    XSTREAM.alias("TemporalObjectCollection", TemporalObjectCollection.class);
    XSTREAM.alias("QuantityCollection", QuantityCollection.class);
    XSTREAM.alias("TemporalQuantityCollection",
        TemporalQuantityCollection.class);

    XSTREAM.alias("Folder", StoreGroup.class);

    // tidier names for operations
    XSTREAM.alias("AddQuantityValues", AddQuantityValues.class);
    XSTREAM.alias("MultiplyQuantityValues", MultiplyQuantityValues.class);

    // TODO: KUMAR: create equivalent alias operations (as above) for other defined operations

    // and force some objects to be represnted as attributes, rather than child objects
    XSTREAM.useAttributeFor(ObjectCollection.class, "name");
    XSTREAM.useAttributeFor(AbstractCommand.class, "title");
    XSTREAM.useAttributeFor(AbstractCommand.class, "canUndo");
    XSTREAM.useAttributeFor(AbstractCommand.class, "canRedo");
    XSTREAM.useAttributeFor(AbstractCommand.class, "dynamic");

    // setup converter
    XSTREAM.registerConverter(
        new LimpetCollectionConverter(XSTREAM.getMapper()),
        XStream.PRIORITY_NORMAL);
    XSTREAM.registerConverter(
        new TimesCollectionConverter(XSTREAM.getMapper()),
        XStream.PRIORITY_NORMAL);
    XSTREAM.registerConverter(new PointConverter(), XStream.PRIORITY_NORMAL);
    XSTREAM.setMode(XStream.ID_REFERENCES);
  }

  public IStore load(String fileName)
  {
    IStore store = (IStore) XSTREAM.fromXML(new File(fileName));
    return store;
  }

  public void save(IStore store, String fileName) throws FileNotFoundException,
      IOException
  {
    save(store, new File(fileName));
  }

  private void save(IStore store, File file) throws FileNotFoundException,
      IOException
  {
    try (OutputStream out = new FileOutputStream(file))
    {
      XSTREAM.toXML(store, out);
    }
  }

  public IStore load(IFile iFile) throws CoreException
  {
    File file = getFile(iFile);
    IStore store = (IStore) XSTREAM.fromXML(file);
    return store;
  }

  private File getFile(IFile iFile) throws CoreException
  {
    URI uri = iFile.getLocationURI();
    if (iFile.isLinked())
    {
      uri = iFile.getRawLocationURI();
    }
    File file = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
    return file;
  }

  public void save(IStore store, IFile iFile) throws CoreException,
      FileNotFoundException, IOException
  {
    File file = getFile(iFile);
    save(store, file);
  }

  public IStore fromXML(String xml)
  {
    return (IStore) XSTREAM.fromXML(xml);
  }

  public String toXML(IStore store)
  {
    return XSTREAM.toXML(store);
  }

}
