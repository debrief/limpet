package info.limpet.data;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.csv.CsvParser;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation.DopplerShiftOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation.DopplerShiftOperation.TimePeriod;
import info.limpet.data.operations.spatial.GenerateCourseAndSpeedOperation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

		Collection<ICommand<IStoreItem>> ops = genny.actionsFor(sel, store);
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
				.actionsFor(sel, store);
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

	public void testLocationCalc()
	{
		TemporalLocation loc1 = new TemporalLocation("loc1");
		TemporalLocation loc2 = new TemporalLocation("loc2");
		Temporal.Length_M len1 = new Temporal.Length_M("dummy2");

		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(loc1);

		IStore store = new InMemoryStore();
		;
		Collection<ICommand<IStoreItem>> ops = new DistanceBetweenTracksOperation()
				.actionsFor(selection, store);
		assertEquals("empty collection", 0, ops.size());

		selection.add(len1);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store);
		assertEquals("empty collection", 0, ops.size());

		selection.remove(len1);
		selection.add(loc2);
		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store);
		assertEquals("empty collection", 1, ops.size());

		// ok, try adding some data
		GeometryBuilder builder = GeoSupport.getBuilder();
		loc1.add(1000, builder.createPoint(4, 3));
		loc2.add(2000, builder.createPoint(3, 4));

		ops = new DistanceBetweenTracksOperation().actionsFor(selection, store);
		assertEquals("empty collection", 1, ops.size());
	}

	@SuppressWarnings("unused")
	public void testDoppler()
	{
		final ArrayList<IStoreItem> items = new ArrayList<IStoreItem>();
		final DopplerShiftBetweenTracksOperation doppler = new DopplerShiftBetweenTracksOperation();
		final IStore store = new InMemoryStore();
		final CollectionComplianceTests tests = new CollectionComplianceTests();

		// create datasets
		TemporalLocation loc1 = new TemporalLocation("loc 1");
		TemporalLocation loc2 = new TemporalLocation("loc 2");
		NonTemporal.Location loc3 = new NonTemporal.Location("loc 3");
		NonTemporal.Location loc4 = new NonTemporal.Location("loc 4");

		Temporal.Angle_Degrees angD1 = new Temporal.Angle_Degrees("ang D 1", null);
		Temporal.Angle_Radians angR2 = new Temporal.Angle_Radians("ang R 2");
		NonTemporal.Angle_Radians angR3 = new NonTemporal.Angle_Radians("ang R 3");
		NonTemporal.Angle_Degrees angD4 = new NonTemporal.Angle_Degrees("ang D 4");

		Temporal.Speed_Kts spdK1 = new Temporal.Speed_Kts("speed kts 1");
		Temporal.Speed_MSec spdM2 = new Temporal.Speed_MSec("speed M 2");
		NonTemporal.Speed_Kts spdK3 = new NonTemporal.Speed_Kts("speed kts 1");
		NonTemporal.Speed_MSec spdM4 = new NonTemporal.Speed_MSec("speed kts 1");

		Temporal.Frequency_Hz freq1 = new Temporal.Frequency_Hz("freq 1");
		NonTemporal.Frequency_Hz freq2 = new NonTemporal.Frequency_Hz("freq 2");

		Temporal.Speed_MSec sspdM1 = new Temporal.Speed_MSec("sound speed M 1");
		NonTemporal.Speed_Kts sspdK2 = new NonTemporal.Speed_Kts(
				"sound speed kts 2");

		// populate the datasets
		for (int i = 10000; i <= 90000; i += 5000)
		{
			loc1.add(
					i,
					GeoSupport.getBuilder().createPoint(2 + Math.cos(5 * i) * 5,
							4 + Math.sin(6 * i) * 5));
			if (i % 2000 == 0)
				loc2.add(
						i,
						GeoSupport.getBuilder().createPoint(4 - Math.cos(3 * i) * 2,
								9 - Math.sin(4 * i) * 3));

			if (i % 2000 == 0)
				angD1.add(i, 55 + Math.sin(i) * 4);
			if (i % 3000 == 0)
				angR2.add(i, Math.toRadians(45 + Math.cos(i) * 3));

			if (i % 4000 == 0)
				spdK1.add(i, 55 + Math.sin(i) * 4);
			if (i % 6000 == 0)
				spdM2.add(i, 55 + Math.sin(i) * 4);

			if (i % 3000 == 0)
				freq1.add(i, 55 + Math.sin(i) * 4);

			if (i % 4000 == 0)
				sspdM1.add(i, 950 + Math.sin(i) * 4);

		}

		loc3.add(GeoSupport.getBuilder().createPoint(4, 9));
		loc4.add(GeoSupport.getBuilder().createPoint(6, 12));

		angR3.add(Math.toRadians(155));
		angD4.add(255);

		freq2.add(55.5);

		sspdK2.add(400);

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
		StoreGroup track2 = new StoreGroup("Track 1");
		items.add(track1);
		items.add(track2);

		assertEquals("empty", 0, doppler.actionsFor(items, store).size());

		track1.add(loc1);

		assertEquals("empty", 0, doppler.actionsFor(items, store).size());

		track1.add(angD1);

		assertEquals("empty", 0, doppler.actionsFor(items, store).size());

		assertFalse("valid track", tests.numberOfTracks(items, 1));

		track1.add(spdK1);

		assertEquals("empty", 0, doppler.actionsFor(items, store).size());

		assertTrue("valid track", tests.numberOfTracks(items, 1));

		// now for track two
		track2.add(loc2);
		track2.add(angR2);

		assertFalse("valid track", tests.numberOfTracks(items, 2));

		track2.add(sspdK2);

		assertTrue("valid track", tests.numberOfTracks(items, 2));

		assertEquals("still empty", 0, doppler.actionsFor(items, store).size());

		assertEquals("has freq", null,
				tests.someHave(items, Frequency.UNIT.getDimension(), true));

		// give one a freq
		track1.add(freq1);

		assertEquals("still empty", 0, doppler.actionsFor(items, store).size());

		assertEquals("has freq", null,
				tests.someHave(items, Frequency.UNIT.getDimension(), false));
		assertNotNull("has freq",
				tests.someHave(items, Frequency.UNIT.getDimension(), true));
		assertNotNull("has freq",
				tests.someHave(track1, Frequency.UNIT.getDimension(), true));
		assertEquals("has freq", null,
				tests.someHave(track2, Frequency.UNIT.getDimension(), true));

		// and now complete dataset (with temporal location)

		// add the missing sound speed
		items.add(sspdK2);
		assertEquals("not empty", 2, doppler.actionsFor(items, store).size());

		// and now complete dataset (with one non temporal location)

		track1.remove(loc1);
		track1.add(loc3);

		assertEquals("not empty", 2, doppler.actionsFor(items, store).size());

		// and now complete dataset (with two non temporal locations)
		track2.remove(loc2);
		track2.add(loc4);

		assertEquals("not empty", 2, doppler.actionsFor(items, store).size());

		// back to original type
		track1.remove(loc3);
		track1.add(loc1);
		track2.remove(loc4);
		track2.add(loc2);

		assertEquals("not empty", 2, doppler.actionsFor(items, store).size());

		// quick extra test
		track1.remove(loc1);

		assertEquals("empty", 0, doppler.actionsFor(items, store).size());
		// quick extra test
		track1.add(loc1);

		assertEquals("empty", 2, doppler.actionsFor(items, store).size());

		// ok, now check how the doppler handler organises its data
		DopplerShiftOperation op1 = (DopplerShiftOperation) doppler
				.actionsFor(items, store).iterator().next();

		assertNotNull("found operation", op1);

		op1.organiseData();
		HashMap<String, ICollection> map = op1.getDataMap();
		assertEquals("all items", 8, map.size());

		IBaseTemporalCollection timing = op1.getOptimalTimes();
		assertNotNull("found times", timing);
		assertEquals("found most frequent", "loc 1",
				((ICollection) timing).getName());

		track1.remove(loc1);
		track1.add(loc3);
		op1 = (DopplerShiftOperation) doppler.actionsFor(items, store).iterator()
				.next();

		op1.organiseData();
		timing = op1.getOptimalTimes();
		assertNotNull("found times", timing);
//		assertEquals("found most frequent", "loc 2",
//				((ICollection) timing).getName());

		op1.execute();

		// check the bounding time
		TimePeriod period = op1.getBoundingTime();
		assertEquals("start", 20000, period.startTime);
		assertEquals("start", 80000, period.endTime);
		
	}

	public void testDopplerMeanTimes()
	{
		DopplerShiftOperation operation = new DopplerShiftOperation(null, null,
				null, null, null, null, null);
		ArrayList<Long> times = new ArrayList<Long>();
		times.add(1000L);
		times.add(3000L);

		long res = operation.calcMeanTimes(times);
		assertEquals("correct times", 2000, res);

		times.add(5500L);
		times.add(7600L);
		times.add(9700L);
		times.add(18000L);

		res = operation.calcMeanTimes(times);
		assertEquals("correct times", 3400, res);
	}

	public void testDopplerInterpolation()
	{
		DopplerShiftOperation operation = new DopplerShiftOperation(null, null,
				null, null, null, null, null);

		Temporal.Speed_Kts sKts = new Temporal.Speed_Kts("Speed knots");
		sKts.add(1000, 10);
		sKts.add(2000, 20);
		sKts.add(4000, 30);

		double val = operation.valueAt(sKts, 1500L, sKts.getUnits());
		assertEquals("correct value", 15.0, val);

		val = operation.valueAt(sKts, 3000L, sKts.getUnits());
		assertEquals("correct value", 25.0, val);

		// try converting to m_sec
		val = operation.valueAt(sKts, 1500L, new Temporal.Speed_MSec().getUnits());
		assertEquals("correct value", 7.72, val, 0.01);

		// try converting to m_sec
		try
		{
			val = operation.valueAt(sKts, 1500L,
					new Temporal.Angle_Degrees().getUnits());
		}
		catch (ConversionException ce)
		{
			assertNotNull("exception thrown", ce);
		}

	}
}
