package info.limpet.data.operations.spatial;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Frequency;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class DopplerShiftBetweenTracksOperation implements IOperation<IStoreItem>
{

	public static class DopplerShiftOperation extends
			AbstractCommand<IStoreItem>
	{

		public DopplerShiftOperation(String outputName, List<IStoreItem> selection,
				IStore store, String title, String description)
		{
			super(title, description, outputName, store, false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// put the names into a string
			ICollection input0 = (ICollection) super.getInputs().get(0);
			ICollection input1 = (ICollection) super.getInputs().get(1);
			String title = input0.getName() + " and "
					+ input1.getName();

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
			return new StockTypes.NonTemporal.Length_M("Distance between " + title);
		}

		protected void calcAndStore(final GeodeticCalculator calc, final Point locA, 
				final Point locB)
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
		boolean allGroups = aTests.allGroups(selection);
		boolean allTracks = aTests.allTracks(selection);
		boolean allHaveFreq = aTests.allHave(selection, Frequency.UNIT.getDimension());
		return (allGroups && allTracks && allHaveFreq);
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			ICommand<IStoreItem> newC = new DopplerShiftOperation(null, selection, destination, "Doppler between tracks", "Calculate doppler between two tracks");

			res.add(newC);
		}

		return res;
	}
}