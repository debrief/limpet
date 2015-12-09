package info.limpet.data.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M;
import info.limpet.data.impl.samples.StockTypes.Temporal.Location;
import info.limpet.data.operations.CollectionComplianceTests;
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
import org.opengis.geometry.primitive.Point;

public class DopplerShiftBetweenTracksOperation implements
		IOperation<IStoreItem>
{

	public static class DopplerShiftOperation extends AbstractCommand<IStoreItem>
	{

		private static final String RX = "RX_";
		private static final String TX = "TX_";
		private HashMap<String, ICollection> data;
		private final StoreGroup _tx;
		private final StoreGroup _rx;

		public DopplerShiftOperation(String outputName, StoreGroup tx,
				StoreGroup rx, IStore store, String title, String description, List<IStoreItem> selection)
		{
			super(title, description, outputName, store, false, false, selection);
			_tx = tx;
			_rx = rx;
		}

		@Override
		public void execute()
		{
			// ok, we need to collate the data
			data = new HashMap<String, ICollection>();

			// ok, transmitter data
			data.put(TX + "FREQ", CollectionComplianceTests.someHave(_tx, Frequency.UNIT.getDimension(), true));
			data.put(TX + "COURSE", CollectionComplianceTests.someHave(_tx, SI.RADIAN.getDimension() , true));
			data.put(TX + "SPEED", CollectionComplianceTests.someHave(_tx, METRE.divide(SECOND).getDimension(), true));
			data.put(TX + "LOC", CollectionComplianceTests.someHaveLocation(_tx));

			// and the receiver
			data.put(RX + "COURSE", CollectionComplianceTests.someHave(_rx, SI.RADIAN.getDimension() , true));
			data.put(RX + "SPEED", CollectionComplianceTests.someHave(_rx, METRE.divide(SECOND).getDimension(), true));
			data.put(RX + "LOC", CollectionComplianceTests.someHaveLocation(_rx));

			// and the sound speed
			data.put("SOUND_SPEED", CollectionComplianceTests.someHave(getInputs(), METRE.divide(SECOND).getDimension(), false));

			
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
			Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<IStoreItem> res = new ArrayList<IStoreItem>();
			res.add(target);
			getStore().addAll(res);
		}

		protected IQuantityCollection<?> getOutputCollection(String title)
		{
			return new StockTypes.NonTemporal.Length_M("Doppler shift between "
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
			
			ICollection track1 = (ICollection) getInputs().get(0);
			ICollection track2 = (ICollection) getInputs().get(1);

			// find one wiht more than one item
			final Location primary;
			final Location secondary;
			if (track1.size() > 1)
			{
				primary = (Location) track1;
				secondary = (Location) track2;
			}
			else
			{
				primary = (Location) track2;
				secondary = (Location) track1;
			}

			// get a calculator to use
			final GeodeticCalculator calc = GeoSupport.getCalculator();

			for (int j = 0; j < primary.size(); j++)
			{
				final Point locA, locB;

				locA = (Point) primary.getValues().get(j);

				if (secondary.size() > 1)
				{
					locB = (Point) secondary.getValues().get(j);
				}
				else
				{
					locB = (Point) secondary.getValues().get(0);
				}

				calcAndStore(calc, locA, locB);
			}
		}

	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	protected boolean appliesTo(List<IStoreItem> selection)
	{
		// ok, check we have two collections
		boolean allGroups = aTests.numberOfGroups(selection, 2);
		boolean allTracks = aTests.numberOfTracks(selection, 2);
		boolean someHaveFreq = CollectionComplianceTests.someHave(selection,
				Frequency.UNIT.getDimension(),true) != null;
		boolean topLevelSpeed = CollectionComplianceTests.someHave(selection,
				METRE.divide(SECOND).getDimension(),true) != null;
		
		return (aTests.exactNumber(selection, 3) && allGroups && allTracks && someHaveFreq && topLevelSpeed);
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			StoreGroup groupA = (StoreGroup) selection.get(0);
			StoreGroup groupB = (StoreGroup) selection.get(1);

			ICommand<IStoreItem> newC = new DopplerShiftOperation(null, groupA,
					groupB, destination, "Doppler between tracks (from "
							+ groupA.getName() + ")", "Calculate doppler between two tracks", selection);

			res.add(newC);
			newC = new DopplerShiftOperation(null, groupB, groupA, destination,
					"Doppler between tracks (from " + groupB.getName() + ")",
					"Calculate doppler between two tracks", selection);
			res.add(newC);
		}

		return res;
	}
}