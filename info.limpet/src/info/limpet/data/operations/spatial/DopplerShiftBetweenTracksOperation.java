package info.limpet.data.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.Frequency_Hz;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;

public class DopplerShiftBetweenTracksOperation implements
		IOperation<IStoreItem>
{

	public static class DopplerShiftOperation extends AbstractCommand<IStoreItem>
	{

		private static final String RX = "RX_";
		private static final String TX = "TX_";
		private HashMap<String, ICollection> data;
		private final IStoreGroup _tx;
		private final IStoreGroup _rx;

		public DopplerShiftOperation(String outputName, IStoreGroup tx,
				IStoreGroup rx, IStore store, String title, String description,
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

		/** find the best collection to use as a time-base. Which collection has the most values within
		 * the specified time period?
		 * 
		 * @param period 
		 * @param items
		 * @return most suited collection
		 */
		public IBaseTemporalCollection getOptimalTimes(TimePeriod period, Collection<ICollection> items)
		{
			IBaseTemporalCollection res = null;
			long resScore = 0;

			Iterator<ICollection> iter = items.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				if (iCollection.isTemporal())
				{
					IBaseTemporalCollection timeC = (IBaseTemporalCollection) iCollection;
					Iterator<Long> times = timeC.getTimes().iterator();
					int score = 0;
					while (times.hasNext())
					{
						long long1 = (long) times.next();
						if(period.contains(long1))
						{
							score++;
						}
					}
					
					if((res == null) || (score > resScore))
					{
						res = timeC;
						resScore = score;
					}
				}
			}

			return res;
		}

		public static class TimePeriod
		{
			public long startTime;
			public long endTime;

			public TimePeriod(final long tStart, final long tEnd)
			{
				startTime = tStart;
				endTime = tEnd;
			}

			public boolean invalid()
			{
				return endTime < startTime;
			}

			public boolean contains(long time)
			{
				return ((startTime <= time) && (endTime >= time));
			}
		}

		public TimePeriod getBoundingTime(final Collection<ICollection> items)
		{
			TimePeriod res = null;

			Iterator<ICollection> iter = items.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				if (iCollection.isTemporal())
				{
					IBaseTemporalCollection timeC = (IBaseTemporalCollection) iCollection;
					if (res == null)
					{
						res = new TimePeriod(timeC.start(), timeC.finish());
					}
					else
					{
						res.startTime = Math.max(res.startTime, timeC.start());
						res.endTime = Math.min(res.endTime, timeC.finish());
					}
				}
			}

			return res;
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
			TimePeriod period = getBoundingTime(data.values());

			// check it's valid
			if (period.invalid())
			{
				System.err.println("Insufficient coverage for datasets");
				return;
			}

			// ok, let's start by finding our time sync
			IBaseTemporalCollection times = getOptimalTimes(period, data.values());

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
					Geometry txLoc = locationFor(data.get(TX + "LOC"), thisTime);
					Geometry rxLoc = locationFor(data.get(RX + "LOC"), thisTime);

					double txCourseRads = valueAt(data.get(TX + "COURSE"), thisTime,
							SI.RADIAN);
					double rxCourseRads = valueAt(data.get(RX + "COURSE"), thisTime,
							SI.RADIAN);

					double txSpeedMSec = valueAt(data.get(TX + "SPEED"), thisTime,
							SI.METERS_PER_SECOND);
					double rxSpeedMSec = valueAt(data.get(RX + "SPEED"), thisTime,
							SI.METERS_PER_SECOND);

					double freq = valueAt(data.get(TX + "FREQ"), thisTime, SI.HERTZ);

					double soundSpeed = valueAt(data.get("SOUND_SPEED"), thisTime,
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
							rxCourseRads, txSpeedMSec, rxSpeedMSec,
							angleRads, freq);

					output.add(thisTime, shifted);
				}
			}
		}

		@SuppressWarnings("unchecked")
		public double valueAt(ICollection iCollection, long thisTime,
				Unit<?> requiredUnits)
		{
			Measurable<Quantity> res;
			if (iCollection.isQuantity())
			{
				IQuantityCollection<?> iQ = (IQuantityCollection<?>) iCollection;

				if (iCollection.isTemporal())
				{
					TemporalQuantityCollection<?> tQ = (TemporalQuantityCollection<?>) iCollection;
					res = (Measurable<Quantity>) tQ.interpolateValue(thisTime,
							InterpMethod.Linear);
				}
				else
				{
					IQuantityCollection<?> qC = (IQuantityCollection<?>) iCollection;
					res = (Measurable<Quantity>) qC.getValues().iterator().next();
				}

				if (res != null)
				{
					UnitConverter converter = iQ.getUnits().getConverterTo(requiredUnits);
					Unit<?> sourceUnits = iQ.getUnits();
					double doubleValue = res.doubleValue((Unit<Quantity>) sourceUnits);
					double result = converter.convert(doubleValue);
					return result;
				}
				else
				{
					return 0;
				}
			}
			else
			{
				throw new RuntimeException(
						"Tried to get value of non quantity data type");
			}
		}

		private Geometry locationFor(ICollection iCollection, Long thisTime)
		{
			Geometry res;
			if (iCollection.isTemporal())
			{
				TemporalLocation tLoc = (TemporalLocation) iCollection;
				res = tLoc.interpolateValue(thisTime, InterpMethod.Linear);
			}
			else
			{
				NonTemporal.Location tLoc = (info.limpet.data.impl.samples.StockTypes.NonTemporal.Location) iCollection;
				res = tLoc.getValues().iterator().next();
			}
			return res;
		}

	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

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
			IStoreGroup groupA = (IStoreGroup) selection.get(0);
			IStoreGroup groupB = (IStoreGroup) selection.get(1);

			ICommand<IStoreItem> newC = new DopplerShiftOperation(null, groupA,
					groupB, destination, "Doppler between tracks (from "
							+ groupA.getName() + ")", "Calculate doppler between two tracks",
					selection);

			res.add(newC);
			newC = new DopplerShiftOperation(null, groupB, groupA, destination,
					"Doppler between tracks (from " + groupB.getName() + ")",
					"Calculate doppler between two tracks", selection);
			res.add(newC);
		}

		return res;
	}
}