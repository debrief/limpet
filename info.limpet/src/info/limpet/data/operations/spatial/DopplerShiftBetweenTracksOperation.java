package info.limpet.data.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.Frequency_Hz;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Frequency;
import javax.measure.unit.SI;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;

public class DopplerShiftBetweenTracksOperation implements
		IOperation<IStoreItem>
{
	final private static CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static class DopplerShiftOperation extends AbstractCommand<IStoreItem>
	{

		private static final String RX = "RX_";
		private static final String TX = "TX_";
		private transient HashMap<String, ICollection> data;
		private final StoreGroup _tx;
		private final StoreGroup _rx;

		public DopplerShiftOperation(String outputName, StoreGroup tx,
				StoreGroup rx, IStore store, String title, String description,
				List<IStoreItem> selection)
		{
			super(title, description, outputName, store, false, false, selection);
			_tx = tx;
			_rx = rx;
		}

		public HashMap<String, ICollection> getDataMap()
		{
			return data;
		}

		@Override
		public void execute()
		{
			// store the data in an accessible way
			organiseData();

			// get the unit
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// put the names into a string
			String title = _tx.getName() + " and " + _rx.getName();

			// ok, generate the new series
			IQuantityCollection<?> target = getOutputCollection(title);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			Iterator<ICollection> iter = data.values().iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<IStoreItem> res = new ArrayList<IStoreItem>();
			res.add(target);
			getStore().addAll(res);
		}

		public void organiseData()
		{
			// ok, we need to collate the data
			data = new HashMap<String, ICollection>();

			final CollectionComplianceTests tests = new CollectionComplianceTests();

			// ok, transmitter data
			data.put(TX + "FREQ",
					tests.someHave(_tx, Frequency.UNIT.getDimension(), true));
			data.put(TX + "COURSE",
					tests.someHave(_tx, SI.RADIAN.getDimension(), true));
			data.put(TX + "SPEED",
					tests.someHave(_tx, METRE.divide(SECOND).getDimension(), true));
			data.put(TX + "LOC", tests.someHaveLocation(_tx));

			// and the receiver
			data.put(RX + "COURSE",
					tests.someHave(_rx, SI.RADIAN.getDimension(), true));
			data.put(RX + "SPEED",
					tests.someHave(_rx, METRE.divide(SECOND).getDimension(), true));
			data.put(RX + "LOC", tests.someHaveLocation(_rx));

			// and the sound speed
			data.put("SOUND_SPEED", tests.someHave(getInputs(), METRE.divide(SECOND)
					.getDimension(), false));
		}

		protected IQuantityCollection<?> getOutputCollection(String title)
		{
			return new StockTypes.Temporal.Frequency_Hz("Doppler shift between "
					+ title);
		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB)
		{
			// get the output dataset
			Length_M target = (Length_M) getOutputs().get(0);

			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			double thisDist = calc.getOrthodromicDistance();
			target.add(Measure.valueOf(thisDist, target.getUnits()));
		}

		@Override
		protected void recalculate()
		{
			// clear out the lists, first
			Iterator<IStoreItem> iter = getOutputs().iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			// update the results
			performCalc(getOutputs());
		}

		/**
		 * 
		 * @param SpeedOfSound
		 * @param osHeadingRads
		 * @param tgtHeadingRads
		 * @param osSpeed
		 * @param tgtSpeed
		 * @param bearing
		 * @param fNought
		 * @return
		 */
		public static double calcPredictedFreqSI(final double SpeedOfSound,
				final double osHeadingRads, final double tgtHeadingRads,
				final double osSpeed, final double tgtSpeed, double bearing,
				final double fNought)
		{
			final double relB = bearing - osHeadingRads;

			// note - contrary to some publications TSL uses the
			// angle along the bearing, not the angle back down the bearing (ATB).
			final double AngleOffTheOtherB = tgtHeadingRads - bearing;

			final double OSL = Math.cos(relB) * osSpeed;
			final double TSL = Math.cos(AngleOffTheOtherB) * tgtSpeed;

			final double freq = fNought * (SpeedOfSound + OSL) / (SpeedOfSound + TSL);

			return freq;
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<IStoreItem> outputs)
		{

			// and the bounding period
			TimePeriod period = aTests.getBoundingTime(data.values());

			// check it's valid
			if (period.invalid())
			{
				System.err.println("Insufficient coverage for datasets");
				return;
			}

			// ok, let's start by finding our time sync
			IBaseTemporalCollection times = aTests.getOptimalTimes(period,
					data.values());

			// check we were able to find some times
			if (times == null)
			{
				System.err.println("Unable to find time source dataset");
				return;
			}

			// get the output dataset
			final Temporal.Frequency_Hz output = (Frequency_Hz) outputs.iterator()
					.next();

			final GeodeticCalculator calc = GeoSupport.getCalculator();

			// and now we can start looping through
			Iterator<Long> tIter = times.getTimes().iterator();
			while (tIter.hasNext())
			{
				long thisTime = (long) tIter.next();

				if ((thisTime >= period.startTime) && (thisTime <= period.endTime))
				{
					// ok, now collate our data
					Geometry txLoc = aTests.locationFor(data.get(TX + "LOC"), thisTime);
					Geometry rxLoc = aTests.locationFor(data.get(RX + "LOC"), thisTime);

					double txCourseRads = aTests.valueAt(data.get(TX + "COURSE"),
							thisTime, SI.RADIAN);
					double rxCourseRads = aTests.valueAt(data.get(RX + "COURSE"),
							thisTime, SI.RADIAN);

					double txSpeedMSec = aTests.valueAt(data.get(TX + "SPEED"), thisTime,
							SI.METERS_PER_SECOND);
					double rxSpeedMSec = aTests.valueAt(data.get(RX + "SPEED"), thisTime,
							SI.METERS_PER_SECOND);

					double freq = aTests.valueAt(data.get(TX + "FREQ"), thisTime,
							SI.HERTZ);

					double soundSpeed = aTests.valueAt(data.get("SOUND_SPEED"), thisTime,
							SI.METERS_PER_SECOND);

					// now find the bearing between them
					calc.setStartingGeographicPoint(txLoc.getCentroid().getOrdinate(0),
							txLoc.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(
							rxLoc.getCentroid().getOrdinate(0), rxLoc.getCentroid()
									.getOrdinate(1));
					double angleDegs = calc.getAzimuth();
					if (angleDegs < 0)
						angleDegs += 360;

					double angleRads = Math.toRadians(angleDegs);

					// ok, and the calculation
					double shifted = calcPredictedFreqSI(soundSpeed, txCourseRads,
							rxCourseRads, txSpeedMSec, rxSpeedMSec, angleRads, freq);

					output.add(thisTime, shifted);
				}
			}
		}

	}

	protected boolean appliesTo(List<IStoreItem> selection)
	{
		// ok, check we have two collections
		boolean allGroups = aTests.numberOfGroups(selection, 2);
		boolean allTracks = aTests.numberOfTracks(selection, 2);
		boolean someHaveFreq = aTests.someHave(selection,
				Frequency.UNIT.getDimension(), true) != null;
		boolean topLevelSpeed = aTests.someHave(selection, METRE.divide(SECOND)
				.getDimension(), true) != null;

		return (aTests.exactNumber(selection, 3) && allGroups && allTracks
				&& someHaveFreq && topLevelSpeed);
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			StoreGroup groupA = (StoreGroup) selection.get(0);
			StoreGroup groupB = (StoreGroup) selection.get(1);

			ICommand<IStoreItem> newC;

			// do we have freq for groupA
			if (aTests.someHave(groupA, Frequency.UNIT.getDimension(), true) != null)
			{
				newC = new DopplerShiftOperation(null, groupA, groupB, destination,
						"Doppler between tracks (from " + groupA.getName() + ")",
						"Calculate doppler between two tracks", selection);
				res.add(newC);
			}

			if (aTests.someHave(groupB, Frequency.UNIT.getDimension(), true) != null)
			{
				newC = new DopplerShiftOperation(null, groupB, groupA, destination,
						"Doppler between tracks (from " + groupB.getName() + ")",
						"Calculate doppler between two tracks", selection);
				res.add(newC);
			}
		}

		return res;
	}
}