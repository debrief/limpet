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
package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.IStoreItem;
import info.limpet.data.csv.CsvParser;
import info.limpet.data.store.StoreGroup;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

public class TestCsvParser extends TestCase
{

	public void testIntervals() throws Exception
	{
		File file = getDataFile("data/intervals.csv");
		assertTrue(file.isFile());
		List<IStoreItem> items = new CsvParser().parse(file.getAbsolutePath());
		assertTrue(items.size() == 1);
		ICollection firstColl = (ICollection) items.get(0);
		assertEquals("correct num rows", 69, firstColl.getValuesCount());
	}

	public void testFrequencs() throws Exception
	{
		File file = getDataFile("data/frequences.csv");
		assertTrue(file.isFile());
		List<IStoreItem> items = new CsvParser().parse(file.getAbsolutePath());
		assertTrue(items.size() == 1);
		ICollection firstColl = (ICollection) items.get(0);
		assertEquals("correct num rows", 11, firstColl.getValuesCount());
	}

	public void testMultiColumn() throws Exception
	{
		File file = getDataFile("data/track_one.csv");
		assertTrue(file.isFile());
		CsvParser parser = new CsvParser();
		List<IStoreItem> items = parser.parse(file.getAbsolutePath());
		assertEquals("correct group", 1, items.size());
		StoreGroup group = (StoreGroup) items.get(0);
		assertEquals("correct num collections", 6, group.size());
		ICollection firstColl = (ICollection) group.get(0);
		assertEquals("correct num rows", 69, firstColl.getValuesCount());
	}

	public void testMultiColumnUSA() throws Exception
	{
		File file = getDataFile("americas_cup/usa.csv");
		assertTrue(file.isFile());
		CsvParser parser = new CsvParser();
		List<IStoreItem> items = parser.parse(file.getAbsolutePath());
		assertEquals("correct group", 1, items.size());
		StoreGroup group = (StoreGroup) items.get(0);
		assertEquals("correct num collections", 3, group.size());
		ICollection firstColl = (ICollection) group.get(0);
		assertEquals("correct num rows", 1708, firstColl.getValuesCount());
	}
	
	public static File getDataFile(String name)
	{
		File file = new File(getFileName(name));
		return file;
	}

	public static String getFileName(String name)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("..");
		builder.append(File.separator);
		builder.append("info.limpet.sample_data");
		builder.append(File.separator);
		builder.append(name);
		return builder.toString();
	}

}
