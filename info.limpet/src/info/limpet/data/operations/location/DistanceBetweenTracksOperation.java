package info.limpet.data.operations.location;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
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

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class DistanceBetweenTracksOperation implements
		IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String SUM_OF_INPUT_SERIES = "Sum of input series";

	final protected String outputName;

	public DistanceBetweenTracksOperation(String name)
	{
		outputName = name;
	}

	public DistanceBetweenTracksOperation()
	{
		this(SUM_OF_INPUT_SERIES);
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new AddQuantityValues(
					outputName, selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean nonEmpty = aTests.nonEmpty(selection);
		boolean equalLength = aTests.allEqualLengthOrSingleton(selection);
		boolean onlyTwo = aTests.exactNumber(selection, 2);
		
		return (nonEmpty && equalLength && onlyTwo && aTests.allLocation(selection));
	}

	public class AddQuantityValues extends
			AbstractCommand<ICollection>
	{
		public AddQuantityValues(String outputName,
				List<ICollection> selection, IStore store)
		{
			super("Distance between tracks", "Calculate distance between two tracks", outputName,
					store, false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			ICollection target = new StockTypes.NonTemporal.Length_M("Distance between two tracks");

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			Iterator<ICollection> iter = _inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().addAll(res);
		}

		@Override
		protected void recalculate()
		{
			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			// update the results
			performCalc(_outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<ICollection> outputs)
		{
			Length_M target = (Length_M) outputs.iterator().next();

			ICollection track1 = _inputs.get(0);
			ICollection track2 = _inputs.get(1);
			
			// find one wiht more than one item
			final Location primary;
			final Location secondary;
			if(track1.size() > 1)
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
			final GeodeticCalculator calc= GeoSupport.getCalculator();
			
			for (int j = 0; j < primary.size(); j++)
			{
				final Point locA, locB;
				
				locA = (Point) primary.getValues().get(j);
				
				if(secondary.size()>1)
				{
					locB = (Point) secondary.getValues().get(j);
				}
				else
				{
					locB = (Point) secondary.getValues().get(0);
				}
				
				//	now find the range between them
				calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA.getCentroid().getOrdinate(1));
				calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0), locB.getCentroid().getOrdinate(1));				
				double thisDist = calc.getOrthodromicDistance();				
				target.add(Measure.valueOf(thisDist, target.getUnits()));
			}
		}
	}

}
