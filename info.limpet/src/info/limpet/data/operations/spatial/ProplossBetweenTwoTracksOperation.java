package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.AcousticStrength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class ProplossBetweenTwoTracksOperation extends TwoTrackOperation
{

	private final class DistanceBetweenOperation extends DistanceOperation
	{
		private DistanceBetweenOperation(String outputName,
				List<IStoreItem> selection, IStore store, String title,
				String description, IBaseTemporalCollection timeProvider, IContext context)
		{
			super(outputName, selection, store, title, description, timeProvider, context);
		}

		protected IQuantityCollection<?> getOutputCollection(String title,
				boolean isTemporal)
		{
			final IQuantityCollection<?> res;
			if (isTemporal)
			{
				res = new StockTypes.Temporal.AcousticStrength("Acoustic loss between "
						+ title, this);
			}
			else
			{
				res = new StockTypes.NonTemporal.AcousticStrength(
						"Acoustic loss between " + title, this);
			}
			return res;
		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB, Long time)
		{
			final Unit<Dimensionless> outUnits = NonSI.DECIBEL;

			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			double thisDistMetres = calc.getOrthodromicDistance();
			
			// ok, we've got to do 20 log R
			double thisLoss = 20d * Math.log(thisDistMetres);
			final Measure<Double, Dimensionless> thisRes = Measure.valueOf(thisLoss,
					outUnits);

			if (time != null)
			{
				// get the output dataset
				AcousticStrength target2 = (Temporal.AcousticStrength) getOutputs()
						.get(0);
				target2.add(time, thisRes);
			}
			else
			{
				// get the output dataset
				NonTemporal.AcousticStrength target2 = (NonTemporal.AcousticStrength) getOutputs()
						.get(0);
				target2.add(thisRes);
			}
		}
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
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

				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination, "Propagation loss between tracks (interpolated)",
						"Propagation loss between two tracks", timeProvider, context);

				res.add(newC);
			}

			if (aTests.allEqualLengthOrSingleton(selection))
			{
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination,
						"Propagation loss between tracks (indexed)",
						"Propagation loss between two tracks", null, context);

				res.add(newC);
			}
		}
		return res;
	}
}
