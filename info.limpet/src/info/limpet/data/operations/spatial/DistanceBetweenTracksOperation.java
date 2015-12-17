package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M;
import info.limpet.data.impl.samples.StockTypes.Temporal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class DistanceBetweenTracksOperation extends TwoTrackOperation
{

	private final class DistanceBetweenOperation extends DistanceOperation
	{

		private DistanceBetweenOperation(String outputName,
				List<IStoreItem> selection, IStore store, String title,
				String description)
		{
			this(outputName, selection, store, title, description, null);
		}

		public DistanceBetweenOperation(String outputName,
				List<IStoreItem> selection, IStore store, String title,
				String description, IBaseTemporalCollection timeProvider)
		{
			super(outputName, selection, store, title, description, timeProvider);
		}

		protected IQuantityCollection<?> getOutputCollection(String title,
				boolean isTemporal)
		{
			final IQuantityCollection<?> res;
			if (isTemporal)
			{
				res = new StockTypes.Temporal.Length_M("Distance between " + title, this);
			}
			else
			{
				res = new StockTypes.NonTemporal.Length_M("Distance between " + title, this);

			}

			return res;

		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB, Long time)
		{
			final Unit<Length> outUnits;
			if(time != null)
			{
				// get the output dataset
				Temporal.Length_M target2 = (Temporal.Length_M) getOutputs().get(0);
				outUnits = target2.getUnits();
			}
			else
			{
				// get the output dataset
				Length_M target2 = (Length_M) getOutputs().get(0);
				outUnits = target2.getUnits();
			}


			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			double thisDist = calc.getOrthodromicDistance();
			final Measure<Double, Length> thisRes = Measure.valueOf(thisDist, outUnits);

			if(time != null)
			{
				// get the output dataset
				Temporal.Length_M target2 = (Temporal.Length_M) getOutputs().get(0);
				target2.add(time, thisRes);
			}
			else
			{
				// get the output dataset
				Length_M target2 = (Length_M) getOutputs().get(0);
				target2.add(thisRes);
			}
		}
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			// ok, are we doing a tempoarl opeartion?
			if (aTests.suitableForTimeInterpolation(selection))
			{
				// hmm, find the time provider
				final IBaseTemporalCollection timeProvider = aTests
						.getLongestTemporalCollections(selection);

				// ok, provide an interpolated action
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination, "Distance between tracks (interpolated)",
						"Calculate distance between two tracks (interpolated)",
						timeProvider);
				res.add(newC);
			}

			if (aTests.allEqualLengthOrSingleton(selection))
			{
				// ok, provide an indexed action
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination, "Distance between tracks (indexed)",
						"Calculate distance between two tracks (indexed)");
				res.add(newC);
			}
		}

		return res;
	}
}
