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
package info.limpet.data2;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.StoreGroup;
import info.limpet.persistence.FileParser;
import info.limpet.persistence.rep.RepParser;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TestRepParser extends TestCase
{

  public static File getDataFile(final String name)
  {
    final File file = new File(getFileName(name));
    return file;
  }

  public static String getFileName(final String name)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("..");
    builder.append(File.separator);
    builder.append("info.limpet.sample_data");
    builder.append(File.separator);
    builder.append(name);
    return builder.toString();
  }
  

  @SuppressWarnings("unchecked")
  @Test
  public void testParseSensor() throws Exception
  {
    final File file = getDataFile("data/turn.dsf");
    assertTrue(file.isFile());
    final FileParser parser = new RepParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    items = (List<IStoreItem>) items.get(0);
//    assertEquals("correct num tracks", 4, items.size());
//    final StoreGroup trackOne =  (StoreGroup) items.get(2);
//    assertEquals("correct name", "OWNSHIP", trackOne.getName());
//    assertEquals("correct num collections", 4, trackOne.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testParseMultiTrack() throws Exception
  {
    final File file = getDataFile("data/turn.rep");
    assertTrue(file.isFile());
    final FileParser parser = new RepParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    items = (List<IStoreItem>) items.get(0);
    assertEquals("correct num tracks", 4, items.size());
    final StoreGroup trackOne =  (StoreGroup) items.get(2);
    assertEquals("correct name", "OWNSHIP", trackOne.getName());
    assertEquals("correct num collections", 4, trackOne.size());
    IDocument<?> firstColl = (IDocument<?>) trackOne.get(0);
    assertEquals("correct name", "OWNSHIP-location", firstColl.getName());
    assertEquals("correct num rows", 529, firstColl.size());
    IDocument<?> secondColl = (IDocument<?>) trackOne.get(1);
    assertEquals("correct name", "OWNSHIP-speed", secondColl.getName());
    assertEquals("correct num rows", 529, firstColl.size());

    LocationDocument trackTwo = (LocationDocument) items.get(1);
    assertEquals("correct name", "Singleton Track B-location", trackTwo.getName());
    assertEquals("correct num points", 1, trackTwo.size());

  }
}
