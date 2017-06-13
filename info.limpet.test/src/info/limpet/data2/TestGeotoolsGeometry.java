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

import static javax.measure.unit.SI.METRE;
import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.operations.spatial.DopplerShiftBetweenTracksOperation;
import info.limpet.operations.spatial.DopplerShiftBetweenTracksOperation.DopplerShiftOperation.TrackProvider;
import info.limpet.operations.spatial.GenerateCourseAndSpeedOperation;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;
import info.limpet.operations.spatial.ProplossBetweenTwoTracksOperation;
import info.limpet.operations.spatial.TwoTrackOperation;
import info.limpet.persistence.CsvParser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.junit.Assert;

public class TestGeotoolsGeometry extends TestCase
{

  private IContext context = new MockContext();

  public void testCreateTemporalObjectCollection()
  {
    LocationDocumentBuilder ld =
        new LocationDocumentBuilder("test", null, null);
    ld.add(new Point2D.Double(12d, 14d));
    LocationDocument locations = ld.toDocument();
    assertNotNull(locations);
  }

  public void testGenerateSingleCourse() throws IOException
  {
    File file = TestCsvParser.getDataFile("americas_cup/usa.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(2);
    assertEquals("correct num rows", 1708, firstColl.size());

    IDocument<?> track = firstColl;
    GenerateCourseAndSpeedOperation genny =
        new GenerateCourseAndSpeedOperation();
    List<IStoreItem> sel = new ArrayList<IStoreItem>();
    sel.add(track);

    StoreGroup store = new StoreGroup("Outputs");

    Collection<ICommand> ops = genny.actionsFor(sel, store, context);
    assertNotNull("created command", ops);
    assertEquals("created operations", 2, ops.size());
    ICommand firstOp = ops.iterator().next();
    assertEquals("store empty", 0, store.size());
    firstOp.execute();
    assertEquals("new coll created", 1, store.size());
    NumberDocument newColl = (NumberDocument) firstOp.getOutputs().get(0);
    assertEquals("correct size", firstColl.size() - 1, newColl.size());
    assertNotNull("knows about parent", newColl.getPrecedent());

  }

  public void testGenerateMultipleCourse() throws IOException
  {
    File file = TestCsvParser.getDataFile("americas_cup/usa.csv");
    assertTrue(file.isFile());
    File file2 = TestCsvParser.getDataFile("americas_cup/nzl.csv");
    assertTrue(file2.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(2);
    assertEquals("correct num rows", 1708, firstColl.size());

    List<IStoreItem> items2 = parser.parse(file2.getAbsolutePath());
    assertEquals("correct group", 1, items2.size());
    StoreGroup group2 = (StoreGroup) items2.get(0);
    assertEquals("correct num collections", 3, group2.size());
    IDocument<?> secondColl = (IDocument<?>) group2.get(2);
    assertEquals("correct num rows", 1708, secondColl.size());

    LocationDocument track1 = (LocationDocument) firstColl;
    LocationDocument track2 = (LocationDocument) secondColl;
    GenerateCourseAndSpeedOperation genny =
        new GenerateCourseAndSpeedOperation();
    List<IStoreItem> sel = new ArrayList<IStoreItem>();
    sel.add(track1);
    sel.add(track2);

    StoreGroup store = new StoreGroup("outputs");

    List<ICommand> ops = (List<ICommand>) genny.actionsFor(sel, store, context);
    assertNotNull("created command", ops);
    assertEquals("created operatoins", 2, ops.size());
    ICommand courseOp = ops.get(0);
    assertEquals("store empty", 0, store.size());
    courseOp.execute();
    assertEquals("new colls created", 2, store.size());
    IDocument<?> newColl = (IDocument<?>) courseOp.getOutputs().get(0);
    assertEquals("correct size", firstColl.size() - 1, newColl.size());
    ICommand speedOp = ops.get(1);
    assertEquals("store empty", 2, store.size());
    speedOp.execute();
    assertEquals("new colls created", 4, store.size());
    newColl = courseOp.getOutputs().get(0);
    assertEquals("correct size", firstColl.size() - 1, newColl.size());

  }

  public void testBuilder()
  {
    final LocationDocumentBuilder track1 =
        new LocationDocumentBuilder("some location data", null, null);

    IGeoCalculator calc = GeoSupport.getCalculator();
    Point2D pos1 = calc.createPoint(-4, 55.8);
    Point2D pos2 = calc.calculatePoint(pos1, Math.toRadians(54), 0.003);

    track1.add(pos1);
    track1.add(pos2);

    LocationDocument doc = track1.toDocument();

    assertEquals("track has points", 2, doc.size());

  }

  public void testCreatePoint()
  {
    IGeoCalculator builder = GeoSupport.getCalculator();
    Point2D point = builder.createPoint(48.44, -123.37);
    Assert.assertNotNull(point);

    Point2D point2 = builder.createPoint(48.44, -123.37);
    Assert.assertNotNull(point2);
  }

  public void testRangeCalc()
  {
    IGeoCalculator builder = GeoSupport.getCalculator();

    Point2D p1 = builder.createPoint(0, 80);
    Point2D p2 = builder.createPoint(0, 81);
    Point2D p3 = builder.createPoint(1, 80);

    final double dest1 = builder.getDistanceBetween(p1, p2);
    final double dest2 = builder.getDistanceBetween(p1, p3);

    assertEquals("range 1 right", 111663, dest1, 10);
    assertEquals("range 2 right", 19393, dest2, 10);
  }

  public void testBearingCalc()
  {

    IGeoCalculator builder = GeoSupport.getCalculator();

    Point2D p1 = builder.createPoint(1, 0);
    Point2D p2 = builder.createPoint(2, 1);

    assertEquals("correct result", 45, builder.getAngleBetween(p1, p2), 0.2);
  }

  public void testGenerateInterpLocation()
  {
    LocationDocumentBuilder locB =
        new LocationDocumentBuilder("track 1", null, SampleData.MILLIS);
    locB.add(0000, new Point2D.Double(0d, 0d));
    locB.add(1000, new Point2D.Double(1d, 2d));
    locB.add(2000, new Point2D.Double(2d, 4d));
    locB.add(3000, new Point2D.Double(3d, 6d));

    NumberDocumentBuilder numB =
        new NumberDocumentBuilder("times", null, null, SampleData.MILLIS);
    numB.add(800, 10d);
    numB.add(1200, 10d);
    numB.add(1300, 10d);
    numB.add(1400, 10d);
    numB.add(1500, 10d);
    numB.add(2600, 10d);

    LocationDocument track = locB.toDocument();
    Document<?> times = numB.toDocument();
    LocationDocument aa = new GetLocationsHelper().getTestLocations(track, times);
    assertNotNull("doc created", aa);
  }
  
  public static class GetLocationsHelper extends AbstractCommand
  {

//    String title, String description,
//    IStoreGroup store, boolean canUndo, boolean canRedo,
//    List<IStoreItem> inputs, IContext context
    
    public GetLocationsHelper()
    {
      super(null, null, null, false, false, null, null);
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
    }
    
    public LocationDocument getTestLocations(LocationDocument track, Document<?> times)
    {
      return locationsFor(track, times);
    }
  }

  public void testLocationInterp()
  {
    LocationDocumentBuilder loc1b =
        new LocationDocumentBuilder("loc1", null, SI.MILLI(SI.SECOND));
    IGeoCalculator builder = GeoSupport.getCalculator();
    loc1b.add(1000, builder.createPoint(2, 3));
    loc1b.add(2000, builder.createPoint(3, 4));

    LocationDocument loc1 = loc1b.toDocument();

    Point2D geo1 = loc1.locationAt(1500);
    assertEquals("correct value", 2.5, geo1.getX());
    assertEquals("correct value", 3.5, geo1.getY());

    geo1 = loc1.locationAt(1700);
    assertEquals("correct value", 2.7, geo1.getX());
    assertEquals("correct value", 3.7, geo1.getY());
  }

  public void testInterpolatedLocationCalcNonTemporal()
  {
    LocationDocumentBuilder loc1 =
        new LocationDocumentBuilder("loc1", null, null);
    LocationDocumentBuilder loc2 =
        new LocationDocumentBuilder("loc2", null, null);
    NumberDocumentBuilder len1 =
        new NumberDocumentBuilder("dummy2", METRE.asType(Length.class), null,
            null);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(loc1.toDocument());

    IStoreGroup store = new StoreGroup("Store");
    Collection<ICommand> ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    final NumberDocument len1D = len1.toDocument();
    selection.add(len1D);
    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    selection.remove(len1D);
    selection.add(loc2.toDocument());
    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    // ok, try adding some data
    IGeoCalculator builder = GeoSupport.getCalculator();
    loc1.add(builder.createPoint(4, 3));
    loc1.add(builder.createPoint(1, 3));
    loc2.add(builder.createPoint(3, 4));
    loc2.add(builder.createPoint(2, 4));

    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(loc2.toDocument());

    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("does work collection", 1, ops.size());

    loc2.add(builder.createPoint(2, 1));

    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("can't work, since we can't interpolate", 1, ops.size());

    // check output is empty
    assertEquals("store empty", 0, store.size());

    ops.iterator().next().execute();

    assertEquals("store not empty", 1, store.size());
  }

  public void testInterpolatedLocationCalcTemporal()
  {
    LocationDocumentBuilder loc1 =
        new LocationDocumentBuilder("loc1", null, SampleData.MILLIS);
    LocationDocumentBuilder loc2 =
        new LocationDocumentBuilder("loc2", null, SampleData.MILLIS);
    NumberDocumentBuilder len1 =
        new NumberDocumentBuilder("dummy2", METRE.asType(Length.class), null,
            SampleData.MILLIS);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(loc1.toDocument());

    IStoreGroup store = new StoreGroup("data");

    List<ICommand> ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    final NumberDocument len1D = len1.toDocument();
    selection.add(len1D);
    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    selection.remove(len1D);
    selection.add(loc2.toDocument());
    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    // ok, try adding some data
    IGeoCalculator builder = GeoSupport.getCalculator();

    loc1.add(1000, builder.createPoint(1, 3));
    loc1.add(2000, builder.createPoint(2, 3));
    loc1.add(3000, builder.createPoint(3, 3));
    loc1.add(4000, builder.createPoint(4, 3));
    loc1.add(5000, builder.createPoint(5, 3));

    loc2.add(1100, builder.createPoint(2.2, 4));
    loc2.add(1200, builder.createPoint(2.4, 4));
    loc2.add(1300, builder.createPoint(2.6, 4));

    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(loc2.toDocument());

    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("does work collection", 1, ops.size());

    // check output is empty
    assertEquals("store empty", 0, store.size());

    ops.get(0).execute();

    assertEquals("store not empty", 1, store.size());

    IDocument<?> output = ops.get(0).getOutputs().get(0);
    assertNotNull("output produced", output);
    assertEquals("correct items", 3, output.size());
    assertNotNull("has indices", output.getIndex());

    System.out.println(output.toString());

    // ok, add a couple more entries, so it could be indexed or interpolated
    loc2.add(1400, builder.createPoint(1.4, 4));
    loc2.add(1500, builder.createPoint(1.5, 4));

    // rebuild the selection
    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(loc2.toDocument());

    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("does work collection", 2, ops.size());

    // check output is empty
    store.clear();
    assertEquals("store empty", 0, store.size());

    ops.iterator().next().execute();

    assertEquals("store not empty", 1, store.size());

    output = ops.iterator().next().getOutputs().get(0);
    assertNotNull("output produced", output);
    assertEquals("correct items", 5, output.size());
    assertNotNull("has indices", output.getIndex());

    // ok, let's check how it works for an indexed dataset
    // check output is empty
    store.clear();
    assertEquals("store empty", 0, store.size());

    ICommand newOp = ops.get(1);
    newOp.execute();

    assertEquals("store not empty", 1, store.size());

    output = newOp.getOutputs().get(0);
    assertNotNull("output produced", output);
    assertEquals("correct items", 5, output.size());
    assertNull("does not have indices", output.getIndex());

  }

  public void testProplossCalc()
  {
    LocationDocumentBuilder loc1 = new LocationDocumentBuilder("loc1", null, SI.MILLI(SI.SECOND));
    LocationDocumentBuilder loc2 = new LocationDocumentBuilder("loc2", null, SI.MILLI(SI.SECOND));
    LocationDocumentBuilder loc3 = new LocationDocumentBuilder("loc2", null, null);
    NumberDocumentBuilder len1 = new NumberDocumentBuilder("dummy2", SI.METER, null, null);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(loc1.toDocument());

    IStoreGroup store = new StoreGroup("data");

    List<ICommand> ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    selection.add(len1.toDocument());
    ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    selection.remove(len1);
    selection.add(loc2.toDocument());
    ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection", 0, ops.size());

    // ok, try adding some data
    IGeoCalculator builder = GeoSupport.getCalculator();

    loc1.add(1000, builder.createPoint(4, 3));
    loc1.add(2000, builder.createPoint(3, 4));
    loc2.add(1000, builder.createPoint(5, 3));
    loc2.add(1500, builder.createPoint(4, 3));
    loc3.add(builder.createPoint(2, 2));
    
    // ok, regenerate the list
    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(loc2.toDocument());
    selection.add(len1.toDocument());
    

    ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("not empty collection", 2, ops.size());

    // make hte series different lengths
    loc2.add(2000, builder.createPoint(3, 4));
    
    // regenerate the selection
    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(loc2.toDocument());
    selection.add(len1.toDocument());

    ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("not empty collection", 1, ops.size());

    // check how it runs
    ICommand thisOp = ops.iterator().next();
    thisOp.execute();
    IStoreItem thisOut = thisOp.getOutputs().iterator().next();
    assertNotNull(thisOut);
    assertTrue("correct type", thisOut instanceof NumberDocument);
    NumberDocument iQ = (NumberDocument) thisOut;
    assertEquals("correct length", 3, iQ.size());

    // try with a singleton
    selection.clear();
    selection.add(loc1.toDocument());
    selection.add(len1.toDocument());
    selection.add(loc3.toDocument());

    ops =
        new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("not empty collection", 2, ops.size());

    // check how it runs
    thisOp = ops.iterator().next();
    thisOp.execute();
    thisOut = thisOp.getOutputs().iterator().next();
    assertNotNull(thisOut);
    assertTrue("correct type", thisOut instanceof NumberDocument);
    iQ = (NumberDocument) thisOut;
    assertEquals("correct length", 2, iQ.size());

  }

  public void testLocationCalc()
  {
     LocationDocumentBuilder loc1 = new LocationDocumentBuilder("loc1", null, SI.MILLI(SI.SECOND));
     LocationDocumentBuilder loc2 = new LocationDocumentBuilder("loc2", null, SI.MILLI(SI.SECOND));
     NumberDocumentBuilder len1b = new NumberDocumentBuilder("dummy", METRE.asType(Length.class), null,
     null);
    
     List<IStoreItem> selection = new ArrayList<IStoreItem>();
     selection.add(loc1.toDocument());
    
     StoreGroup store = new StoreGroup("Results");

     Collection<ICommand> ops =
     new DistanceBetweenTracksOperation().actionsFor(selection, store,
     context);
     assertEquals("empty collection", 0, ops.size());
     
     NumberDocument len1 = len1b.toDocument();
    
     selection.add(len1);
     ops =
     new DistanceBetweenTracksOperation().actionsFor(selection, store,
     context);
     assertEquals("empty collection", 0, ops.size());
    
     
     
     selection.remove(len1);
     selection.add(loc2.toDocument());
     ops =
     new DistanceBetweenTracksOperation().actionsFor(selection, store,
     context);
     assertEquals("empty collection", 0, ops.size());
    
     // ok, try adding some data
     IGeoCalculator builder = GeoSupport.getCalculator();
    
     loc1.add(1000, builder.createPoint(4, 3));
     loc2.add(2000, builder.createPoint(3, 4));
     
     // put in the new documents
     selection.clear();
     selection.add(loc1.toDocument());
     selection.add(loc2.toDocument());
    
     ops =
     new DistanceBetweenTracksOperation().actionsFor(selection, store,
     context);
     assertEquals("not empty collection", 1, ops.size());
  }

  public void testFindingDopplerTracks()
  {
    final List<IStoreItem> items = new ArrayList<IStoreItem>();
    final DopplerShiftBetweenTracksOperation doppler =
        new DopplerShiftBetweenTracksOperation();
    final StoreGroup store = new StoreGroup("Data");
    final CollectionComplianceTests tests = new CollectionComplianceTests();

    IContext mockContext = new MockContext();
    List<TrackProvider> matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            null, items, tests);
    assertEquals("empty", 0, matches.size());

    // create a good track
    IStoreGroup tmpStore = new SampleData().getData(20);
    IStoreGroup cTrack = (IStoreGroup) tmpStore.get(SampleData.COMPOSITE_ONE);
    assertNotNull("not found track", cTrack);
    items.add(cTrack);
    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            null, items, tests);
    assertEquals("not empty", 1, matches.size());

    // ignore that track
    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            (IStoreGroup) cTrack, items, tests);
    assertEquals("empty", 0, matches.size());

    // ok, add a singleton location
    LocationDocument loc1 =
        (LocationDocument) tmpStore.get(SampleData.SINGLETON_LOC_1);
    assertNotNull("not found track", loc1);
    items.add(loc1);
    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            (IStoreGroup) cTrack, items, tests);
    assertEquals("not empty", 1, matches.size());

    IStoreItem loc2 = tmpStore.get(SampleData.SINGLETON_LOC_2);
    items.add(loc2);
    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            (IStoreGroup) cTrack, items, tests);
    assertEquals("not empty", 2, matches.size());

    // ok - they work at the top level, see if they work
    // in a child group
    items.remove(loc1);
    items.remove(loc2);

    // check it's empty
    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            (IStoreGroup) cTrack, items, tests);
    assertEquals("empty", 0, matches.size());

    IStoreGroup sensors = new StoreGroup("Sensor");
    sensors.add(loc1);
    sensors.add(loc2);
    items.add(sensors);

    matches =
        DopplerShiftBetweenTracksOperation.DopplerShiftOperation.getTracks(
            (IStoreGroup) cTrack, items, tests);
    assertEquals("not empty", 2, matches.size());

    // ok, move up a level
    Collection<ICommand> ops = doppler.actionsFor(items, store, mockContext);
    assertEquals("single action", 0, ops.size());

    // ok, give it a top-level sounds speed
    final IStoreItem soundSpeed = tmpStore.get(SampleData.SPEED_ONE);
    items.add(soundSpeed);

    ops = doppler.actionsFor(items, store, mockContext);
    assertEquals("single action", 1, ops.size());

    assertEquals("Loc doens't yet have deps", 0, loc1.getDependents().size());

    // ok, we have two static sensors, ets them
    ICommand firstOp = ops.iterator().next();
    firstOp.execute();
    List<Document<?>> outputs = firstOp.getOutputs();
    assertEquals("two output datasets", 2, outputs.size());

    assertEquals("Loc now has deps", 1, loc1.getDependents().size());

    // hmm, ensure we only get updates on tracks that have changed
    final List<String> messages = new ArrayList<String>();
    final IChangeListener listener = new IChangeListener()
    {

      @Override
      public void metadataChanged(IStoreItem subject)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        messages.add("" + subject.getName());
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
        // TODO Auto-generated method stub
      }
    };

    Iterator<Document<?>> iter = outputs.iterator();
    while (iter.hasNext())
    {
      IStoreItem iStoreItem = (IStoreItem) iter.next();
      iStoreItem.addChangeListener(listener);
    }

    assertEquals("no updates yet", 0, messages.size());

    // ok, make a change to loc1
    String locName = loc1.getName();
    loc1.clearQuiet();
    LocationDocumentBuilder lb =
        new LocationDocumentBuilder(locName, null, null);
    lb.add(new Point2D.Double(22, 33));
    loc1.setDataset(lb.toDocument().getDataset());

    assertEquals("no updates yet", 0, messages.size());

    loc1.fireDataChanged();

    assertEquals("two updates (since we update all outputs)", 2, messages
        .size());

    // restart
    messages.clear();

    // get the freq
    tmpStore.get(SampleData.FREQ_ONE).fireDataChanged();
    assertEquals("both outputs updated", 2, messages.size());

    // restart
    messages.clear();

    // get the freq
    soundSpeed.fireDataChanged();
    assertEquals("both outputs updated", 2, messages.size());

    // and what if we make the sensors group look like a track?
    sensors.remove(loc2);
    sensors.add(tmpStore.get(SampleData.FREQ_ONE));
    sensors.add(soundSpeed);

    ops = doppler.actionsFor(items, store, mockContext);
    assertEquals("single action", 2, ops.size());

  }
  //
  // @SuppressWarnings("unused")
  // public void testDoppler()
  // {
  // // TODO: reinstate me!
  //
  // final ArrayList<IStoreItem> items = new ArrayList<IStoreItem>();
  // final DopplerShiftBetweenTracksOperation doppler =
  // new DopplerShiftBetweenTracksOperation();
  // final StoreGroup store = new StoreGroup();
  // final CollectionComplianceTests tests = new CollectionComplianceTests();
  //
  // // create datasets
  // TemporalLocation loc1 = new TemporalLocation("loc 1");
  // TemporalLocation loc2 = new TemporalLocation("loc 2");
  // NonTemporal.Location loc3 = new NonTemporal.Location("loc 3");
  // NonTemporal.Location loc4 = new NonTemporal.Location("loc 4");
  //
  // Temporal.AngleDegrees angD1 = new Temporal.AngleDegrees("ang D 1", null);
  // Temporal.AngleRadians angR2 = new Temporal.AngleRadians("ang R 2", null);
  // NonTemporal.AngleRadians angR3 =
  // new NonTemporal.AngleRadians("ang R 3", null);
  // NonTemporal.AngleDegrees angD4 =
  // new NonTemporal.AngleDegrees("ang D 4", null);
  //
  // Temporal.SpeedKts spdK1 = new Temporal.SpeedKts("speed kts 1", null);
  // Temporal.SpeedMSec spdM2 = new Temporal.SpeedMSec("speed M 2", null);
  // NonTemporal.SpeedKts spdK3 = new NonTemporal.SpeedKts("speed kts 1", null);
  // NonTemporal.SpeedMSec spdM4 =
  // new NonTemporal.SpeedMSec("speed kts 1", null);
  //
  // Temporal.FrequencyHz freq1 = new Temporal.FrequencyHz("freq 1", null);
  // NonTemporal.FrequencyHz freq2 = new NonTemporal.FrequencyHz("freq 2", null);
  //
  // Temporal.SpeedMSec sspdM1 = new Temporal.SpeedMSec("sound speed M 1", null);
  // NonTemporal.SpeedKts sspdK2 =
  // new NonTemporal.SpeedKts("sound speed kts 2", null);
  //
  // IGeoCalculator builder = GeoSupport.getCalculator();
  //
  // // populate the datasets
  // for (int i = 10000; i <= 90000; i += 5000)
  // {
  // double j = Math.toRadians(i / 1000d);
  //
  // loc1.add(i, builder.createPoint(2 + Math.cos(5 * j) * 5, 4 + Math
  // .sin(6 * j) * 5));
  // if (i % 2000 == 0)
  // {
  // loc2.add(i, builder.createPoint(4 - Math.cos(3 * j) * 2, 9 - Math
  // .sin(4 * j) * 3));
  // }
  //
  // if (i % 2000 == 0)
  // {
  // angD1.add(i, 55 + Math.sin(j) * 4);
  // }
  // if (i % 3000 == 0)
  // {
  // angR2.add(i, Math.toRadians(45 + Math.cos(j) * 3));
  // }
  //
  // if (i % 4000 == 0)
  // {
  // spdK1.add(i, 5 + Math.sin(j) * 2);
  // }
  // if (i % 6000 == 0)
  // {
  // spdM2.add(i, 6 + Math.sin(j) * 2);
  // }
  //
  // if (i % 3000 == 0)
  // {
  // freq1.add(i, 55 + Math.sin(j) * 4);
  // }
  //
  // if (i % 4000 == 0)
  // {
  // sspdM1.add(i, 950 + Math.sin(j) * 4);
  // }
  //
  // }
  //
  // loc3.add(builder.createPoint(4, 9));
  // loc4.add(builder.createPoint(6, 12));
  //
  // angR3.add(Math.toRadians(155));
  // angD4.add(255);
  //
  // freq2.add(55.5);
  //
  // sspdK2.add(400);
  // spdK3.add(4);
  //
  // // check we've got roughly the right amount of data
  // assertEquals("correct items", 17, loc1.getValuesCount());
  // assertEquals("correct items", 9, loc2.getValuesCount());
  // assertEquals("correct items", 9, angD1.getValuesCount());
  // assertEquals("correct items", 6, angR2.getValuesCount());
  // assertEquals("correct items", 4, spdK1.getValuesCount());
  // assertEquals("correct items", 3, spdM2.getValuesCount());
  // assertEquals("correct items", 6, freq1.getValuesCount());
  //
  // // create some incomplete input data
  // StoreGroup track1 = new StoreGroup("Track 1");
  // StoreGroup track2 = new StoreGroup("Track 2");
  // items.add(track1);
  // items.add(track2);
  //
  // assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());
  //
  // track1.add(loc1);
  //
  // assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());
  //
  // track1.add(angD1);
  //
  // assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());
  //
  // assertFalse("valid track", tests.getNumberOfTracks(items) == 1);
  //
  // track1.add(spdK1);
  //
  // assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());
  //
  // assertTrue("valid track", tests.getNumberOfTracks(items) == 1);
  //
  // // now for track two
  // track2.add(loc2);
  // track2.add(angR2);
  //
  // assertFalse("valid track", tests.getNumberOfTracks(items) == 2);
  //
  // track2.add(spdK3);
  //
  // assertTrue("valid track", tests.getNumberOfTracks(items) == 2);
  //
  // assertEquals("still empty", 0, doppler.actionsFor(items, store, context)
  // .size());
  //
  // assertEquals("has freq", null, tests.collectionWith(items, Frequency.UNIT
  // .getDimension(), true));
  //
  // // give one a freq
  // track1.add(freq1);
  //
  // assertEquals("still empty", 0, doppler.actionsFor(items, store, context)
  // .size());
  //
  // assertEquals("has freq", null, tests.collectionWith(items, Frequency.UNIT
  // .getDimension(), false));
  // assertNotNull("has freq", tests.collectionWith(items, Frequency.UNIT
  // .getDimension(), true));
  // assertNotNull("has freq", tests.collectionWith(track1, Frequency.UNIT
  // .getDimension(), true));
  // assertEquals("has freq", null, tests.collectionWith(track2, Frequency.UNIT
  // .getDimension(), true));
  //
  // // and now complete dataset (with temporal location)
  //
  // // add the missing sound speed
  // items.add(sspdK2);
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // // and now complete dataset (with one non temporal location)
  //
  // track1.remove(loc1);
  // track1.add(loc3);
  //
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // // try to remove the course/speed for static track = check we still get it
  // // offered.
  // track1.remove(spdK1);
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // track1.remove(angD1);
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // // see if it runs
  // ICommand<IStoreItem> ops =
  // doppler.actionsFor(items, store, context).iterator().next();
  // ops.execute();
  // IStoreItem tmpOut = ops.getOutputs().iterator().next();
  // assertNotNull("received output", tmpOut);
  //
  // // and put them back
  // track1.add(sspdK2);
  // track1.add(angD1);
  //
  // // and now complete dataset (with two non temporal locations)
  // track2.remove(loc2);
  // track2.add(loc4);
  //
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // // back to original type
  // track1.remove(loc3);
  // track1.add(loc1);
  // track2.remove(loc4);
  // track2.add(loc2);
  //
  // assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
  // .size());
  //
  // // try giving track 2 a frewquency
  // track2.add(freq2);
  //
  // assertEquals("actions for both tracks", 2, doppler.actionsFor(items, store,
  // context).size());
  //
  // // and remove that freq
  // track2.remove(freq2);
  //
  // assertEquals("actions for just one track", 1, doppler.actionsFor(items,
  // store, context).size());
  //
  // // quick extra test
  // track1.remove(loc1);
  //
  // assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());
  //
  // // quick extra test
  // track1.add(loc1);
  //
  // assertEquals("empty", 1, doppler.actionsFor(items, store, context).size());
  //
  // // ok, now check how the doppler handler organises its data
  // DSOperation op1 =
  // (DSOperation) doppler.actionsFor(items, store, context)
  // .iterator().next();
  //
  // assertNotNull("found operation", op1);
  //
  // op1.organiseData();
  // HashMap<String, ICollection> map = op1.getDataMap();
  // assertEquals("all items", 5, map.size());
  //
  // // ok, let's try undo redo
  // assertEquals("correct size store", store.size(), 1);
  //
  // op1.execute();
  //
  // assertEquals("new correct size store", store.size(), 2);
  //
  // op1.undo();
  //
  // assertEquals("new correct size store", store.size(), 1);
  //
  // op1.redo();
  //
  // assertEquals("new correct size store", store.size(), 2);
  //
  // op1.undo();
  //
  // assertEquals("new correct size store", store.size(), 1);
  //
  // op1.redo();
  //
  // assertEquals("new correct size store", store.size(), 2);
  //
  // }
  //
  // public void testGetOptimalTimes()
  // {
  // CollectionComplianceTests aTests = new CollectionComplianceTests();
  // Collection<ICollection> items = new ArrayList<ICollection>();
  //
  // SpeedKts speed1 = new Temporal.SpeedKts("spd1", null);
  // SpeedKts speed2 = new Temporal.SpeedKts("spd2", null);
  // SpeedKts speed3 = new Temporal.SpeedKts("spd3", null);
  //
  // speed1.add(100, 5);
  // speed1.add(120, 5);
  // speed1.add(140, 5);
  // speed1.add(160, 5);
  // speed1.add(180, 5);
  //
  // speed2.add(130, 5);
  // speed2.add(140, 5);
  // speed2.add(141, 5);
  // speed2.add(142, 5);
  // speed2.add(143, 5);
  // speed2.add(145, 5);
  // speed2.add(150, 5);
  // speed2.add(160, 5);
  // speed2.add(230, 5);
  //
  // speed3.add(90, 5);
  // speed3.add(120, 5);
  // speed3.add(160, 5);
  //
  // TimePeriod period = new TimePeriod(120, 180);
  // IBaseTemporalCollection common = aTests.getOptimalTimes(period, items);
  // assertEquals("duh, empty set", null, common);
  //
  // items.add(speed1);
  //
  // period = aTests.getBoundingTime(items);
  //
  // assertEquals("correct period", 100, period.getStartTime());
  // assertEquals("correct period", 180, period.getEndTime());
  //
  // common = aTests.getOptimalTimes(period, items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct choice", common, speed1);
  //
  // items.add(speed2);
  //
  // common = aTests.getOptimalTimes(period, items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct choice", common, speed2);
  //
  // items.add(speed3);
  //
  // common = aTests.getOptimalTimes(period, items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("still correct choice", common, speed2);
  //
  // // step back, test it without the period
  // common = aTests.getOptimalTimes(null, items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct choice", common, speed2);
  //
  // }
  //
  // public void testGetCommonTimePeriod()
  // {
  // CollectionComplianceTests aTests = new CollectionComplianceTests();
  // Collection<ICollection> items = new ArrayList<ICollection>();
  //
  // SpeedKts speed1 = new Temporal.SpeedKts("spd1", null);
  // SpeedKts speed2 = new Temporal.SpeedKts("spd2", null);
  // SpeedKts speed3 = new Temporal.SpeedKts("spd3", null);
  //
  // speed1.add(100, 5);
  // speed1.add(120, 5);
  // speed1.add(140, 5);
  // speed1.add(160, 5);
  // speed1.add(180, 5);
  //
  // speed2.add(130, 5);
  // speed2.add(230, 5);
  //
  // speed3.add(90, 5);
  // speed3.add(120, 5);
  // speed3.add(160, 5);
  //
  // TimePeriod common = aTests.getBoundingTime(items);
  // assertEquals("duh, empty set", null, common);
  //
  // // ok, now add the items to hte collection
  // items.add(speed1);
  //
  // common = aTests.getBoundingTime(items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct times", speed1.start(), common.getStartTime());
  // assertEquals("correct times", speed1.finish(), common.getEndTime());
  //
  // items.add(speed2);
  //
  // common = aTests.getBoundingTime(items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct times", speed2.start(), common.getStartTime());
  // assertEquals("correct times", speed1.finish(), common.getEndTime());
  //
  // items.add(speed3);
  //
  // common = aTests.getBoundingTime(items);
  // assertNotNull("duh, empty set", common);
  // assertEquals("correct times", speed2.start(), common.getStartTime());
  // assertEquals("correct times", speed3.finish(), common.getEndTime());
  // }
  //
  // public void testDopplerInterpolation()
  // {
  // final CollectionComplianceTests aTests = new CollectionComplianceTests();
  //
  // Temporal.SpeedKts sKts = new Temporal.SpeedKts("Speed knots", null);
  // sKts.add(1000, 10);
  // sKts.add(2000, 20);
  // sKts.add(4000, 30);
  //
  // double val = aTests.valueAt(sKts, 1500L, sKts.getUnits());
  // assertEquals("correct value", 15.0, val);
  //
  // val = aTests.valueAt(sKts, 3000L, sKts.getUnits());
  // assertEquals("correct value", 25.0, val);
  //
  // // try converting to m_sec
  // val = aTests.valueAt(sKts, 1500L, new Temporal.SpeedMSec().getUnits());
  // assertEquals("correct value", 7.72, val, 0.01);
  //
  // // try converting to m_sec
  // try
  // {
  // val = aTests.valueAt(sKts, 1500L, new Temporal.AngleDegrees().getUnits());
  // }
  // catch (ConversionException ce)
  // {
  // assertNotNull("exception thrown", ce);
  // }
  //
  // }
}
