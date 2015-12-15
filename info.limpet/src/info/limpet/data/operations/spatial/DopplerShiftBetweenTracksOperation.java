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
import info.limpet.data.store.InMemoryStore;
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
	public static class DopplerShiftOperation extends AbstractCommand<IStoreItem>
	{

		private static final String SOUND_SPEED = "SOUND_SPEED";
		private static final String LOC = "LOC";
		private static final String SPEED = "SPEED";
		private static final String COURSE = "COURSE";
		private static final String FREQ = "FREQ";
		private static final String RX = "RX_";
		private static final String TX = "TX_";

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
		private double calcPredictedFreqSI(final double SpeedOfSound,
				final double osHeadingRads, final double tgtHeadingRads,
				final double osSpeed, final double tgtSpeed, final double bearing,
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

		/** let the class organise a tidy set of data, to collate the assorted datasets
		 * 
		 */
		private transient HashMap<String, ICollection> _data;
		
		/** nominated transmitted
		 * 
		 */
		private final StoreGroup _tx;

		/** nominated receiver
		 * 
		 */
		private final StoreGroup _rx;

		public DopplerShiftOperation(final String outputName, final StoreGroup tx,
				final StoreGroup rx, final IStore store, final String title,
				final String description, final List<IStoreItem> selection)
		{
			super(title, description, outputName, store, true, true, selection);
			_tx = tx;
			_rx = rx;
		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB)
		{
			// get the output dataset
			final Length_M target = (Length_M) getOutputs().get(0);

			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			final double thisDist = calc.getOrthodromicDistance();
			target.add(Measure.valueOf(thisDist, target.getUnits()));
		}

		@Override
		public void execute()
		{
			// store the data in an accessible way
			organiseData();

			// get the unit
			final List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// put the names into a string
			final String title = _tx.getName() + " and " + _rx.getName();

			// ok, generate the new series
			final IQuantityCollection<?> target = getOutputCollection(title);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			final Iterator<ICollection> iter = _data.values().iterator();
			while (iter.hasNext())
			{
				final ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			final List<IStoreItem> res = new ArrayList<IStoreItem>();
			res.add(target);
			getStore().addAll(res);
		}
		
		

		@Override
		public void undo()
		{
			// ok, remove the calculated dataset
			IStoreItem results = getOutputs().iterator().next();
			IStore store = getStore();
			if(store instanceof InMemoryStore)
			{
				InMemoryStore im = (InMemoryStore) store;
				im.remove(results);
			}
		}

		@Override
		public void redo()
		{
			IStoreItem results = getOutputs().iterator().next();
			IStore store = getStore();
			if(store instanceof InMemoryStore)
			{
				InMemoryStore im = (InMemoryStore) store;
				im.add(results);
			}
		}


		public HashMap<String, ICollection> getDataMap()
		{
			return _data;
		}

		protected IQuantityCollection<?> getOutputCollection(final String title)
		{
			return new StockTypes.Temporal.Frequency_Hz("Doppler shift between "
					+ title);
		}

		public void organiseData()
		{
			// ok, we need to collate the data
			_data = new HashMap<String, ICollection>();

			final CollectionComplianceTests tests = new CollectionComplianceTests();

			// ok, transmitter data
			_data.put(TX + FREQ,
					tests.someHave(_tx, Frequency.UNIT.getDimension(), true));
			_data.put(TX + COURSE,
					tests.someHave(_tx, SI.RADIAN.getDimension(), true));
			_data.put(TX + SPEED,
					tests.someHave(_tx, METRE.divide(SECOND).getDimension(), true));
			_data.put(TX + LOC, tests.someHaveLocation(_tx));

			// and the receiver
			_data.put(RX + COURSE,
					tests.someHave(_rx, SI.RADIAN.getDimension(), true));
			_data.put(RX + SPEED,
					tests.someHave(_rx, METRE.divide(SECOND).getDimension(), true));
			_data.put(RX + LOC, tests.someHaveLocation(_rx));

			// and the sound speed
			_data.put(SOUND_SPEED, tests.someHave(getInputs(), METRE.divide(SECOND)
					.getDimension(), false));
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(final List<IStoreItem> outputs)
		{

			// and the bounding period
			final TimePeriod period = aTests.getBoundingTime(_data.values());

			// check it's valid
			if (period.invalid())
			{
				System.err.println("Insufficient coverage for datasets");
				return;
			}

			// ok, let's start by finding our time sync
			final IBaseTemporalCollection times = aTests.getOptimalTimes(period,
					_data.values());

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
			final Iterator<Long> tIter = times.getTimes().iterator();
			while (tIter.hasNext())
			{
				final long thisTime = tIter.next();

				if ((thisTime >= period.startTime) && (thisTime <= period.endTime))
				{
					// ok, now collate our data
					final Geometry txLoc = aTests.locationFor(_data.get(TX + LOC),
							thisTime);
					final Geometry rxLoc = aTests.locationFor(_data.get(RX + LOC),
							thisTime);

					final double txCourseRads = aTests.valueAt(_data.get(TX + COURSE),
							thisTime, SI.RADIAN);
					final double rxCourseRads = aTests.valueAt(_data.get(RX + COURSE),
							thisTime, SI.RADIAN);

					final double txSpeedMSec = aTests.valueAt(_data.get(TX + SPEED),
							thisTime, SI.METERS_PER_SECOND);
					final double rxSpeedMSec = aTests.valueAt(_data.get(RX + SPEED),
							thisTime, SI.METERS_PER_SECOND);

					final double freq = aTests.valueAt(_data.get(TX + FREQ), thisTime,
							SI.HERTZ);

					final double soundSpeed = aTests.valueAt(_data.get(SOUND_SPEED),
							thisTime, SI.METERS_PER_SECOND);

					// now find the bearing between them
					calc.setStartingGeographicPoint(txLoc.getCentroid().getOrdinate(0),
							txLoc.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(
							rxLoc.getCentroid().getOrdinate(0), rxLoc.getCentroid()
									.getOrdinate(1));
					double angleDegs = calc.getAzimuth();
					if (angleDegs < 0)
						angleDegs += 360;

					final double angleRads = Math.toRadians(angleDegs);

					// ok, and the calculation
					final double shifted = calcPredictedFreqSI(soundSpeed, txCourseRads,
							rxCourseRads, txSpeedMSec, rxSpeedMSec, angleRads, freq);

					output.add(thisTime, shifted);
				}
			}
		}

		@Override
		protected void recalculate()
		{
			// clear out the lists, first
			final Iterator<IStoreItem> iter = getOutputs().iterator();
			while (iter.hasNext())
			{
				final IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			// update the results
			performCalc(getOutputs());
		}

	}

	final private static CollectionComplianceTests aTests = new CollectionComplianceTests();

	@Override
	public Collection<ICommand<IStoreItem>> actionsFor(
			final List<IStoreItem> selection, final IStore destination)
	{
		final Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			final StoreGroup groupA = (StoreGroup) selection.get(0);
			final StoreGroup groupB = (StoreGroup) selection.get(1);

			// do we have freq for groupA
			if (aTests.someHave(groupA, Frequency.UNIT.getDimension(), true) != null)
			{
				final ICommand<IStoreItem> newC = new DopplerShiftOperation(null, groupA, groupB, destination,
						"Doppler between tracks (from " + groupA.getName() + ")",
						"Calculate doppler between two tracks", selection);
				res.add(newC);
			}

			if (aTests.someHave(groupB, Frequency.UNIT.getDimension(), true) != null)
			{
				final ICommand<IStoreItem> newC = new DopplerShiftOperation(null, groupB, groupA, destination,
						"Doppler between tracks (from " + groupB.getName() + ")",
						"Calculate doppler between two tracks", selection);
				res.add(newC);
			}
		}

		return res;
	}

	protected boolean appliesTo(final List<IStoreItem> selection)
	{
		// ok, check we have two collections
		final boolean allGroups = aTests.numberOfGroups(selection, 2);
		final boolean allTracks = aTests.numberOfTracks(selection, 2);
		final boolean someHaveFreq = aTests.someHave(selection,
				Frequency.UNIT.getDimension(), true) != null;
		final boolean topLevelSpeed = aTests.someHave(selection,
				METRE.divide(SECOND).getDimension(), true) != null;

		return (aTests.exactNumber(selection, 3) && allGroups && allTracks
				&& someHaveFreq && topLevelSpeed);
	}
}