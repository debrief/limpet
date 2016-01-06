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

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.csv.CsvParser;
import info.limpet.data.impl.MockContext;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.SpeedKts;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.data.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation.DopplerShiftOperation;
import info.limpet.data.operations.spatial.GenerateCourseAndSpeedOperation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.operations.spatial.ProplossBetweenTwoTracksOperation;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.converter.ConversionException;
import javax.measure.quantity.Frequency;

import junit.framework.TestCase;

import org.geotools.factory.Hints;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.GeometryFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.referencing.operation.TransformException;

public class TestGeotoolsGeometry extends TestCase
{

	private IContext context = new MockContext();

	public void testCreateTemporalObjectCollection()
	{
		TemporalObjectCollection<Geometry> locations = new TemporalObjectCollection<Geometry>(
				"test");
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
		ICollection firstColl = (ICollection) group.get(2);
		assertEquals("correct num rows", 1708, firstColl.size());

		TemporalLocation track = (TemporalLocation) firstColl;
		GenerateCourseAndSpeedOperation genny = new GenerateCourseAndSpeedOperation();
		List<IStoreItem> sel = new ArrayList<IStoreItem>();
		sel.add(track);

		InMemoryStore store = new InMemoryStore();

		Collection<ICommand<IStoreItem>> ops = genny
				.actionsFor(sel, store, context);
		assertNotNull("created command", ops);
		assertEquals("created operatoins", 2, ops.size());
		ICommand<IStoreItem> firstOp = ops.iterator().next();
		assertEquals("store empty", 0, store.size());
		firstOp.execute();
		assertEquals("new coll created", 1, store.size());
		ICollection newColl = (ICollection) firstOp.getOutputs().get(0);
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
		ICollection firstColl = (ICollection) group.get(2);
		assertEquals("correct num rows", 1708, firstColl.size());

		List<IStoreItem> items2 = parser.parse(file2.getAbsolutePath());
		assertEquals("correct group", 1, items2.size());
		StoreGroup group2 = (StoreGroup) items2.get(0);
		assertEquals("correct num collections", 3, group2.size());
		ICollection secondColl = (ICollection) group2.get(2);
		assertEquals("correct num rows", 1708, secondColl.size());

		TemporalLocation track1 = (TemporalLocation) firstColl;
		TemporalLocation track2 = (TemporalLocation) secondColl;
		GenerateCourseAndSpeedOperation genny = new GenerateCourseAndSpeedOperation();
		List<IStoreItem> sel = new ArrayList<IStoreItem>();
		sel.add(track1);
		sel.add(track2);

		InMemoryStore store = new InMemoryStore();

		List<ICommand<IStoreItem>> ops = (List<ICommand<IStoreItem>>) genny
				.actionsFor(sel, store, context);
		assertNotNull("created command", ops);
		assertEquals("created operatoins", 2, ops.size());
		ICommand<IStoreItem> courseOp = ops.get(0);
		assertEquals("store empty", 0, store.size());
		courseOp.execute();
		assertEquals("new colls created", 2, store.size());
		ICollection newColl = (ICollection) courseOp.getOutputs().get(0);
		assertEquals("correct size", firstColl.size() - 1, newColl.size());
		ICommand<IStoreItem> speedOp = ops.get(1);
		assertEquals("store empty", 2, store.size());
		speedOp.execute();
		assertEquals("new colls created", 4, store.size());
		newColl = (ICollection) courseOp.getOutputs().get(0);
		assertEquals("correct size", firstColl.size() - 1, newColl.size());

	}

	public void testBuilder() throws TransformException
	{
		final Location track_1 = new StockTypes.NonTemporal.Location(
				"some location data");

		GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84);
		GeodeticCalculator geoCalc = new GeodeticCalculator(
				DefaultGeographicCRS.WGS84);
		DirectPosition pos_1 = new DirectPosition2D(-4, 55.8);
		geoCalc.setStartingGeographicPoint(pos_1.getOrdinate(0),
				pos_1.getOrdinate(1));
		geoCalc.setDirection(Math.toRadians(54), 0.003);
		pos_1 = geoCalc.getDestinationPosition();

		Point p1 = builder.createPoint(pos_1.getOrdinate(0), pos_1.getOrdinate(1));
		track_1.add(p1);

		assertEquals("track has point", 1, track_1.size());

	}

	public void testCreatePoint()
	{
		GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84);
		Point point = builder.createPoint(48.44, -123.37);
		Assert.assertNotNull(point);

		Hints hints = new Hints(Hints.CRS, DefaultGeographicCRS.WGS84);
		PrimitiveFactory primitiveFactory = GeometryFactoryFinder
				.getPrimitiveFactory(hints);
		Point point2 = primitiveFactory.createPoint(new double[]
		{ 48.44, -123.37 });
		Assert.assertNotNull(point2);
	}

	public void testRangeCalc()
	{

		GeodeticCalculator calc = GeoSupport.getCalculator();

		Point p1 = GeoSupport.getBuilder().createPoint(0, 80);
		Point p2 = GeoSupport.getBuilder().createPoint(0, 81);
		Point p3 = GeoSupport.getBuilder().createPoint(1, 80);

		calc.setStartingGeographicPoint(p1.getCentroid().getOrdinate(0), p1
				.getCentroid().getOrdinate(1));
		calc.setDestinationGeographicPoint(p2.getCentroid().getOrdinate(0), p2
				.getCentroid().getOrdinate(1));

		final double dest1 = calc.getOrthodromicDistance();

		calc.setDestinationGeographicPoint(p3.getCentroid().getOrdinate(0), p3
				.getCentroid().getOrdinate(1));
		final double dest2 = calc.getOrthodromicDistance();

		assertEquals("range 1 right", 111663, dest1, 10);
		assertEquals("range 2 right", 19393, dest2, 10);

	}

	public void testBearingCalc()
	{

		GeodeticCalculator calc = GeoSupport.getCalculator();

		Point p1 = GeoSupport.getBuilder().createPoint(1, 0);
		Point p2 = GeoSupport.getBuilder().createPoint(2, 1);

		calc.setStartingGeographicPoint(p1.getCentroid().getOrdinate(0), p1
				.getCentroid().getOrdinate(1));
		calc.setDestinationGeographicPoint(p2.getCentroid().getOrdinate(0), p2
				.getCentroid().getOrdinate(1));

		assertEquals("correct result", 45, calc.getAzimuth(), 0.2);

	}

	public void testLocationInterp()
	{
		TemporalLocation loc1 = new TemporalLocation("loc1");
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(1000, builder.createPoint(2, 3));
		loc1.add(2000, builder.createPoint(3, 4));

		Geometry geo1 = loc1.interpolateValue(1500, InterpMethod.Linear);
		assertEquals("correct value", 2.5, geo1.getRepresentativePoint()
				.getDirectPosition().getCoordinate()[0]);
		assertEquals("correct value", 3.5, geo1.getRepresentativePoint()
				.getDirectPosition().getCoordinate()[1]);

		geo1 = loc1.interpolateValue(1700, InterpMethod.Linear);
		assertEquals("correct value", 2.7, geo1.getRepresentativePoint()
				.getDirectPosition().getCoordinate()[0]);
		assertEquals("correct value", 3.7, geo1.getRepresentativePoint()
				.getDirectPosition().getCoordinate()[1]);

	}

	public void testInterpolatedLocationCalcNonTemporal()
	{
		Location loc1 = new Location("loc1");
		Location loc2 = new Location("loc2");
		Temporal.LengthM len1 = new Temporal.LengthM("dummy2", null);

		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(loc1);

		IStore store = new InMemoryStore();
		;
		Collection<ICommand<IStoreItem>> ops = new DistanceBetweenTracksOperation()
				.actionsFor(selection, store, context);
		assertEquals("empty collection", 0, ops.size());

		selection.add(len1);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		selection.remove(len1);
		selection.add(loc2);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		// ok, try adding some data
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(builder.createPoint(4, 3));
		loc1.add(builder.createPoint(1, 3));
		loc2.add(builder.createPoint(3, 4));
		loc2.add(builder.createPoint(2, 4));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("does work collection", 1, ops.size());

		loc2.add(builder.createPoint(2, 1));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("can't work, since we can't interpolate", 0, ops.size());
	}

	public void testInterpolatedLocationCalcTemporal()
	{
		TemporalLocation loc1 = new TemporalLocation("loc1");
		TemporalLocation loc2 = new TemporalLocation("loc2");
		Temporal.LengthM len1 = new Temporal.LengthM("dummy2", null);

		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(loc1);

		IStore store = new InMemoryStore();
		;
		Collection<ICommand<IStoreItem>> ops = new DistanceBetweenTracksOperation()
				.actionsFor(selection, store, context);
		assertEquals("empty collection", 0, ops.size());

		selection.add(len1);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		selection.remove(len1);
		selection.add(loc2);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		// ok, try adding some data
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(1000, builder.createPoint(4, 3));
		loc1.add(2000, builder.createPoint(1, 3));
		loc2.add(1000, builder.createPoint(3, 4));
		loc2.add(2000, builder.createPoint(2, 4));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("does work collection", 2, ops.size());

		loc2.add(3000, builder.createPoint(2, 1));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("can work, since we can interpolate", 1, ops.size());

		// ok, run it, and see how we get on
		Iterator<ICommand<IStoreItem>> opsIter = ops.iterator();
		ICommand<IStoreItem> operation = opsIter.next();
		operation.execute();

		Iterator<IStoreItem> oIter = operation.getOutputs().iterator();
		IStoreItem output = oIter.next();
		assertNotNull(output);
		assertTrue(output instanceof IQuantityCollection<?>);
		assertTrue("results are temporal",
				output instanceof ITemporalQuantityCollection<?>);

		IQuantityCollection<?> iq = (IQuantityCollection<?>) output;
		assertEquals("correct size", 2, iq.size());

	}

	public void testProplossCalc()
	{
		TemporalLocation loc1 = new TemporalLocation("loc1");
		TemporalLocation loc2 = new TemporalLocation("loc2");
		Location loc3 = new Location("loc2");
		Temporal.LengthM len1 = new Temporal.LengthM("dummy2", null);

		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(loc1);

		IStore store = new InMemoryStore();
		;
		Collection<ICommand<IStoreItem>> ops = new ProplossBetweenTwoTracksOperation()
				.actionsFor(selection, store, context);
		assertEquals("empty collection", 0, ops.size());

		selection.add(len1);
		ops = new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		selection.remove(len1);
		selection.add(loc2);
		ops = new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		// ok, try adding some data
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(1000, builder.createPoint(4, 3));
		loc1.add(2000, builder.createPoint(3, 4));
		loc2.add(1000, builder.createPoint(5, 3));
		loc2.add(1500, builder.createPoint(4, 3));

		loc3.add(builder.createPoint(2, 2));

		ops = new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("not empty collection", 2, ops.size());

		// make hte series different lengths
		loc2.add(2000, builder.createPoint(3, 4));

		ops = new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("not empty collection", 1, ops.size());

		// check how it runs
		ICommand<IStoreItem> thisOp = ops.iterator().next();
		thisOp.execute();
		IStoreItem thisOut = thisOp.getOutputs().iterator().next();
		assertNotNull(thisOut);
		assertTrue("correct type", thisOut instanceof IQuantityCollection);
		IQuantityCollection<?> iQ = (IQuantityCollection<?>) thisOut;
		assertEquals("correct length", 3, iQ.size());

		// try with a singleton
		selection.remove(loc2);
		selection.add(loc3);

		ops = new ProplossBetweenTwoTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("not empty collection", 2, ops.size());

		// check how it runs
		thisOp = ops.iterator().next();
		thisOp.execute();
		thisOut = thisOp.getOutputs().iterator().next();
		assertNotNull(thisOut);
		assertTrue("correct type", thisOut instanceof IQuantityCollection);
		iQ = (IQuantityCollection<?>) thisOut;
		assertEquals("correct length", 2, iQ.size());

	}

	public void testLocationCalc()
	{
		TemporalLocation loc1 = new TemporalLocation("loc1");
		TemporalLocation loc2 = new TemporalLocation("loc2");
		Temporal.LengthM len1 = new Temporal.LengthM("dummy2", null);

		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(loc1);

		IStore store = new InMemoryStore();
		;
		Collection<ICommand<IStoreItem>> ops = new DistanceBetweenTracksOperation()
				.actionsFor(selection, store, context);
		assertEquals("empty collection", 0, ops.size());

		selection.add(len1);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		selection.remove(len1);
		selection.add(loc2);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("empty collection", 0, ops.size());

		// ok, try adding some data
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(1000, builder.createPoint(4, 3));
		loc2.add(2000, builder.createPoint(3, 4));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store,
				context);
		assertEquals("not empty collection", 1, ops.size());
	}

	@SuppressWarnings("unused")
	public void testDoppler()
	{
		final ArrayList<IStoreItem> items = new ArrayList<IStoreItem>();
		final DopplerShiftBetweenTracksOperation doppler = new DopplerShiftBetweenTracksOperation();
		final InMemoryStore store = new InMemoryStore();
		final CollectionComplianceTests tests = new CollectionComplianceTests();

		// create datasets
		TemporalLocation loc1 = new TemporalLocation("loc 1");
		TemporalLocation loc2 = new TemporalLocation("loc 2");
		NonTemporal.Location loc3 = new NonTemporal.Location("loc 3");
		NonTemporal.Location loc4 = new NonTemporal.Location("loc 4");

		Temporal.AngleDegrees angD1 = new Temporal.AngleDegrees("ang D 1", null);
		Temporal.AngleRadians angR2 = new Temporal.AngleRadians("ang R 2", null);
		NonTemporal.AngleRadians angR3 = new NonTemporal.AngleRadians("ang R 3",
				null);
		NonTemporal.AngleDegrees angD4 = new NonTemporal.AngleDegrees("ang D 4",
				null);

		Temporal.SpeedKts spdK1 = new Temporal.SpeedKts("speed kts 1", null);
		Temporal.SpeedMSec spdM2 = new Temporal.SpeedMSec("speed M 2", null);
		NonTemporal.SpeedKts spdK3 = new NonTemporal.SpeedKts("speed kts 1", null);
		NonTemporal.SpeedMSec spdM4 = new NonTemporal.SpeedMSec("speed kts 1",
				null);

		Temporal.FrequencyHz freq1 = new Temporal.FrequencyHz("freq 1", null);
		NonTemporal.FrequencyHz freq2 = new NonTemporal.FrequencyHz("freq 2",
				null);

		Temporal.SpeedMSec sspdM1 = new Temporal.SpeedMSec("sound speed M 1",
				null);
		NonTemporal.SpeedKts sspdK2 = new NonTemporal.SpeedKts(
				"sound speed kts 2", null);

		// populate the datasets
		for (int i = 10000; i <= 90000; i += 5000)
		{
			double j = Math.toRadians(i / 1000);

			loc1.add(
					i,
					GeoSupport.getBuilder().createPoint(2 + Math.cos(5 * j) * 5,
							4 + Math.sin(6 * j) * 5));
			if (i % 2000 == 0)
				loc2.add(
						i,
						GeoSupport.getBuilder().createPoint(4 - Math.cos(3 * j) * 2,
								9 - Math.sin(4 * j) * 3));

			if (i % 2000 == 0)
				angD1.add(i, 55 + Math.sin(j) * 4);
			if (i % 3000 == 0)
				angR2.add(i, Math.toRadians(45 + Math.cos(j) * 3));

			if (i % 4000 == 0)
				spdK1.add(i, 5 + Math.sin(j) * 2);
			if (i % 6000 == 0)
				spdM2.add(i, 6 + Math.sin(j) * 2);

			if (i % 3000 == 0)
				freq1.add(i, 55 + Math.sin(j) * 4);

			if (i % 4000 == 0)
				sspdM1.add(i, 950 + Math.sin(j) * 4);

		}

		loc3.add(GeoSupport.getBuilder().createPoint(4, 9));
		loc4.add(GeoSupport.getBuilder().createPoint(6, 12));

		angR3.add(Math.toRadians(155));
		angD4.add(255);

		freq2.add(55.5);

		sspdK2.add(400);
		spdK3.add(4);

		// check we've got roughly the right amount of data
		assertEquals("correct items", 17, loc1.size());
		assertEquals("correct items", 9, loc2.size());
		assertEquals("correct items", 9, angD1.size());
		assertEquals("correct items", 6, angR2.size());
		assertEquals("correct items", 4, spdK1.size());
		assertEquals("correct items", 3, spdM2.size());
		assertEquals("correct items", 6, freq1.size());

		// create some incomplete input data
		StoreGroup track1 = new StoreGroup("Track 1");
		StoreGroup track2 = new StoreGroup("Track 2");
		items.add(track1);
		items.add(track2);

		assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());

		track1.add(loc1);

		assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());

		track1.add(angD1);

		assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());

		assertFalse("valid track", tests.hasNumberOfTracks(items, 1));

		track1.add(spdK1);

		assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());

		assertTrue("valid track", tests.hasNumberOfTracks(items, 1));

		// now for track two
		track2.add(loc2);
		track2.add(angR2);

		assertFalse("valid track", tests.hasNumberOfTracks(items, 2));

		track2.add(spdK3);

		assertTrue("valid track", tests.hasNumberOfTracks(items, 2));

		assertEquals("still empty", 0, doppler.actionsFor(items, store, context)
				.size());

		assertEquals("has freq", null,
				tests.collectionWith(items, Frequency.UNIT.getDimension(), true));

		// give one a freq
		track1.add(freq1);

		assertEquals("still empty", 0, doppler.actionsFor(items, store, context)
				.size());

		assertEquals("has freq", null,
				tests.collectionWith(items, Frequency.UNIT.getDimension(), false));
		assertNotNull("has freq",
				tests.collectionWith(items, Frequency.UNIT.getDimension(), true));
		assertNotNull("has freq",
				tests.collectionWith(track1, Frequency.UNIT.getDimension(), true));
		assertEquals("has freq", null,
				tests.collectionWith(track2, Frequency.UNIT.getDimension(), true));

		// and now complete dataset (with temporal location)

		// add the missing sound speed
		items.add(sspdK2);
		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		// and now complete dataset (with one non temporal location)

		track1.remove(loc1);
		track1.add(loc3);

		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		// try to remove the course/speed for static track = check we still get it
		// offered.
		track1.remove(spdK1);
		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		track1.remove(angD1);
		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		// see if it runs
		ICommand<IStoreItem> ops = doppler.actionsFor(items, store, context)
				.iterator().next();
		ops.execute();
		IStoreItem tmpOut = ops.getOutputs().iterator().next();
		assertNotNull("received output", tmpOut);

		// and put them back
		track1.add(sspdK2);
		track1.add(angD1);

		// and now complete dataset (with two non temporal locations)
		track2.remove(loc2);
		track2.add(loc4);

		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		// back to original type
		track1.remove(loc3);
		track1.add(loc1);
		track2.remove(loc4);
		track2.add(loc2);

		assertEquals("not empty", 1, doppler.actionsFor(items, store, context)
				.size());

		// try giving track 2 a frewquency
		track2.add(freq2);

		assertEquals("actions for both tracks", 2,
				doppler.actionsFor(items, store, context).size());

		// and remove that freq
		track2.remove(freq2);

		assertEquals("actions for just one track", 1,
				doppler.actionsFor(items, store, context).size());

		// quick extra test
		track1.remove(loc1);

		assertEquals("empty", 0, doppler.actionsFor(items, store, context).size());

		// quick extra test
		track1.add(loc1);

		assertEquals("empty", 1, doppler.actionsFor(items, store, context).size());

		// ok, now check how the doppler handler organises its data
		DopplerShiftOperation op1 = (DopplerShiftOperation) doppler
				.actionsFor(items, store, context).iterator().next();

		assertNotNull("found operation", op1);

		op1.organiseData();
		HashMap<String, ICollection> map = op1.getDataMap();
		assertEquals("all items", 8, map.size());

		// ok, let's try undo redo
		assertEquals("correct size store", store.size(), 1);

		op1.execute();

		assertEquals("new correct size store", store.size(), 2);

		op1.undo();

		assertEquals("new correct size store", store.size(), 1);

		op1.redo();

		assertEquals("new correct size store", store.size(), 2);

		op1.undo();

		assertEquals("new correct size store", store.size(), 1);

		op1.redo();

		assertEquals("new correct size store", store.size(), 2);

	}

	public void testGetOptimalTimes()
	{
		CollectionComplianceTests aTests = new CollectionComplianceTests();
		Collection<ICollection> items = new ArrayList<ICollection>();

		SpeedKts speed1 = new Temporal.SpeedKts("spd1", null);
		SpeedKts speed2 = new Temporal.SpeedKts("spd2", null);
		SpeedKts speed3 = new Temporal.SpeedKts("spd3", null);

		speed1.add(100, 5);
		speed1.add(120, 5);
		speed1.add(140, 5);
		speed1.add(160, 5);
		speed1.add(180, 5);

		speed2.add(130, 5);
		speed2.add(140, 5);
		speed2.add(141, 5);
		speed2.add(142, 5);
		speed2.add(143, 5);
		speed2.add(145, 5);
		speed2.add(150, 5);
		speed2.add(160, 5);
		speed2.add(230, 5);

		speed3.add(90, 5);
		speed3.add(120, 5);
		speed3.add(160, 5);

		TimePeriod period = new TimePeriod(120, 180);
		IBaseTemporalCollection common = aTests.getOptimalTimes(period, items);
		assertEquals("duh, empty set", null, common);

		items.add(speed1);

		period = aTests.getBoundingTime(items);

		assertEquals("correct period", 100, period.getStartTime());
		assertEquals("correct period", 180, period.getEndTime());

		common = aTests.getOptimalTimes(period, items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct choice", common, speed1);

		items.add(speed2);

		common = aTests.getOptimalTimes(period, items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct choice", common, speed2);

		items.add(speed3);

		common = aTests.getOptimalTimes(period, items);
		assertNotNull("duh, empty set", common);
		assertEquals("still correct choice", common, speed2);

		// step back, test it without the period
		common = aTests.getOptimalTimes(null, items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct choice", common, speed2);

	}

	public void testGetCommonTimePeriod()
	{
		CollectionComplianceTests aTests = new CollectionComplianceTests();
		Collection<ICollection> items = new ArrayList<ICollection>();

		SpeedKts speed1 = new Temporal.SpeedKts("spd1", null);
		SpeedKts speed2 = new Temporal.SpeedKts("spd2", null);
		SpeedKts speed3 = new Temporal.SpeedKts("spd3", null);

		speed1.add(100, 5);
		speed1.add(120, 5);
		speed1.add(140, 5);
		speed1.add(160, 5);
		speed1.add(180, 5);

		speed2.add(130, 5);
		speed2.add(230, 5);

		speed3.add(90, 5);
		speed3.add(120, 5);
		speed3.add(160, 5);

		TimePeriod common = aTests.getBoundingTime(items);
		assertEquals("duh, empty set", null, common);

		// ok, now add the items to hte collection
		items.add(speed1);

		common = aTests.getBoundingTime(items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct times", speed1.start(), common.getStartTime());
		assertEquals("correct times", speed1.finish(), common.getEndTime());

		items.add(speed2);

		common = aTests.getBoundingTime(items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct times", speed2.start(), common.getStartTime());
		assertEquals("correct times", speed1.finish(), common.getEndTime());

		items.add(speed3);

		common = aTests.getBoundingTime(items);
		assertNotNull("duh, empty set", common);
		assertEquals("correct times", speed2.start(), common.getStartTime());
		assertEquals("correct times", speed3.finish(), common.getEndTime());
	}

	public void testDopplerInterpolation()
	{
		final CollectionComplianceTests aTests = new CollectionComplianceTests();

		Temporal.SpeedKts sKts = new Temporal.SpeedKts("Speed knots", null);
		sKts.add(1000, 10);
		sKts.add(2000, 20);
		sKts.add(4000, 30);

		double val = aTests.valueAt(sKts, 1500L, sKts.getUnits());
		assertEquals("correct value", 15.0, val);

		val = aTests.valueAt(sKts, 3000L, sKts.getUnits());
		assertEquals("correct value", 25.0, val);

		// try converting to m_sec
		val = aTests.valueAt(sKts, 1500L, new Temporal.SpeedMSec().getUnits());
		assertEquals("correct value", 7.72, val, 0.01);

		// try converting to m_sec
		try
		{
			val = aTests
					.valueAt(sKts, 1500L, new Temporal.AngleDegrees().getUnits());
		}
		catch (ConversionException ce)
		{
			assertNotNull("exception thrown", ce);
		}

	}
}
