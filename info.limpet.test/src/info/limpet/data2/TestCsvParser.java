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

import static javax.measure.unit.NonSI.BAR;
import static javax.measure.unit.SI.CENTI;
import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocument;
import info.limpet.persistence.CsvParser;

import java.io.File;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.VolumetricDensity;
import javax.measure.unit.SI;

import org.junit.Test;

import junit.framework.TestCase;

public class TestCsvParser extends TestCase
{

  @Test
  public void testIntervals() throws Exception
  {
    File file = getDataFile("data/intervals.csv");
    assertTrue(file.isFile());
    List<IStoreItem> items = new CsvParser().parse(file.getAbsolutePath());
    assertTrue(items.size() == 1);
    IDocument<?> firstColl = (IDocument<?>) items.get(0);
    assertEquals("correct num rows", 69, firstColl.size());
  }

  @Test
  public void testNonTimeIndex() throws Exception
  {
    File file = getDataFile("non_time/BalloonAscentData.csv");
    assertTrue(file.isFile());
    final String absPath = file.getAbsolutePath();
    List<IStoreItem> items = new CsvParser().parse(absPath);
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 9, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(0);
    assertEquals("correct num rows", 185, firstColl.size());

    // check we have the correct data types
    int ctr = 0;
    NumberDocument thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units", SI.SECOND, thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Time", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",MILLI(BAR).asType(
        Pressure.class), thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Pressure", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",SI.KELVIN, thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Air Temperature", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",SI.KELVIN, thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Dewpoint", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",GRAM
        .divide(CENTI(METER).pow(3)).asType(VolumetricDensity.class), thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Water density", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",Dimensionless.UNIT, thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Humidity", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",METRE.divide(SECOND).asType(
        Velocity.class), thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Wind speed", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    thisN = (NumberDocument) group.get(ctr++);
    assertTrue("correct data type", thisN instanceof NumberDocument);
    assertEquals("correct units",SampleData.DEGREE_ANGLE
        .asType(Angle.class), thisN.getUnits());
    assertEquals("correct name", "BalloonAscentData-Wind direction", thisN.getName());
    assertEquals("correct index units", SI.METER, thisN.getIndexUnits());

    StringDocument thisS = (StringDocument) group.get(8);
    assertTrue("correct data type", thisS instanceof StringDocument);
    assertEquals("correct index units", SI.METER, thisS.getIndexUnits());
  }

  @Test
  public void testFrequencies() throws Exception
  {
    File file = getDataFile("data/frequences.csv");
    assertTrue(file.isFile());
    final String absolutePath = file.getAbsolutePath();
    List<IStoreItem> items = new CsvParser().parse(absolutePath);
    assertTrue(items.size() == 1);
    IDocument<?> firstColl = (IDocument<?>) items.get(0);
    assertEquals("correct num rows", 11, firstColl.size());
  }

  @Test
  public void testMultiColumn() throws Exception
  {
    File file = getDataFile("data/track_one.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 6, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(0);
    assertEquals("correct num rows", 69, firstColl.size());
  }

  @Test
  public void testMultiColumnUSA() throws Exception
  {
    File file = getDataFile("americas_cup/usa.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(0);
    assertEquals("correct num rows", 1708, firstColl.size());
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
