package info.limpet.data.operations.spatial;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.Angle_Degrees;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;

public class GenerateCourseAndSpeedOperation implements IOperation<IStoreItem>
{

	protected static abstract class DistanceOperation extends
			AbstractCommand<IStoreItem>
	{

		public DistanceOperation(String outputName, List<IStoreItem> selection,
				IStore store, String title, String description)
		{
			super(title, description, outputName, store, false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// ok, generate the new series
			for (int i = 0; i < getInputs().size(); i++)
			{
				IQuantityCollection<?> target = getOutputCollection(getInputs().get(i)
						.getName());
				
				outputs.add(target);
				// store the output
				super.addOutput(target);
			}

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
			getStore().addAll(outputs);
		}

		abstract protected IQuantityCollection<?> getOutputCollection(
				String trackList);

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
			// get a calculator to use
			final GeodeticCalculator calc = GeoSupport.getCalculator();

			// do some clearing first
			
			
			Iterator<IStoreItem> iter = inputs.iterator();
			Iterator<IStoreItem> oIter = outputs.iterator();
			while (iter.hasNext())
			{
				Temporal.Location thisTrack = (Temporal.Location) iter.next();
				IStoreItem thisOut = oIter.next();

				// ok, walk through it
				Iterator<Geometry> pITer = thisTrack.getLocations().iterator();
				Iterator<Long> tIter = thisTrack.getTimes().iterator();

				// remember the last value
				long lastTime = 0;
				Point lastLocation = null;

				while (pITer.hasNext())
				{
					Point geometry = (Point) pITer.next();
					long thisTime = tIter.next();

					if (lastLocation != null)
					{
						calcAndStore(thisOut, calc, lastTime, lastLocation, thisTime,
								geometry);
					}

					// and remember the values
					lastLocation = geometry;
					lastTime = thisTime;

				}
			}
		}

		abstract protected void calcAndStore(IStoreItem thisOut,
				final GeodeticCalculator calc, final long timeA, final Point locA,
				final long timeB, final Point locB);
	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	protected boolean appliesTo(List<IStoreItem> selection)
	{
		boolean nonEmpty = aTests.nonEmpty(selection);
		boolean allTemporal = aTests.allTemporal(selection);

		return (nonEmpty && allTemporal && aTests.allNonQuantity(selection) && aTests
				.allLocation(selection));
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{

			int len = selection.size();
			final String title;
			if (len > 1)
			{
				title = "Generate course for track";
			}
			else
			{
				title = "Generate course for tracks";
			}

			ICommand<IStoreItem> genCourse = new DistanceOperation(null, selection,
					destination, "Generate calculated course", title)
			{

				protected IQuantityCollection<?> getOutputCollection(String title)
				{
					return new StockTypes.Temporal.Angle_Degrees("Generated course for "
							+ title, this);
				}

				protected void calcAndStore(IStoreItem output,
						final GeodeticCalculator calc, long lastTime, final Point locA,
						long thisTime, final Point locB)
				{
					// get the output dataset
					Temporal.Angle_Degrees target = (Angle_Degrees) output;

					// now find the range between them
					calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
							locA.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
							locB.getCentroid().getOrdinate(1));
					double angleDegs = calc.getAzimuth();
					if (angleDegs < 0)
						angleDegs += 360;

					target.add(thisTime, Measure.valueOf(angleDegs, target.getUnits()));
				}
			};
			ICommand<IStoreItem> genSpeed = new DistanceOperation(null, selection,
					destination, "Generate calculated speed", title)
			{

				protected IQuantityCollection<?> getOutputCollection(String title)
				{
					return new StockTypes.Temporal.Speed_MSec("Generated speed for "
							+ title, this);
				}

				protected void calcAndStore(IStoreItem output,
						final GeodeticCalculator calc, long lastTime, final Point locA,
						long thisTime, final Point locB)
				{
					// get the output dataset
					Temporal.Speed_MSec target = (Temporal.Speed_MSec) output;

					// now find the range between them
					calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
							locA.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
							locB.getCentroid().getOrdinate(1));
					double thisDist = calc.getOrthodromicDistance();
					double calcTime = thisTime - lastTime;
					double thisSpeed = thisDist / (calcTime / 1000d);
					target.add(thisTime, Measure.valueOf(thisSpeed, target.getUnits()));
				}
			};

			res.add(genCourse);
			res.add(genSpeed);
		}

		return res;
	}

}