package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Angle_Degrees;
import info.limpet.data.impl.samples.StockTypes.Temporal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Angle;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class BearingBetweenTracksOperation extends TwoTrackOperation
{

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

				ICommand<IStoreItem> newC = new DistanceOperation(null, selection,
						destination, "Bearing between tracks (interpolated)",
						"Calculate bearing between two tracks (interpolated)", timeProvider)
				{

					protected IQuantityCollection<?> getOutputCollection(String title,
							boolean isTemporal)
					{
						final IQuantityCollection<?> output;
						if (isTemporal)
						{
							output = new StockTypes.Temporal.Angle_Degrees("Bearing between "
									+ title, null);

						}
						else
						{
							output = new StockTypes.NonTemporal.Angle_Degrees(
									"Bearing between " + title);
						}

						return output;
					}

					protected void calcAndStore(final GeodeticCalculator calc,
							final Point locA, final Point locB, Long time)
					{
						// get the output dataset
						Temporal.Angle_Degrees target2 = (Temporal.Angle_Degrees) getOutputs()
								.get(0);
						Unit<Angle> outUnits = target2.getUnits();

						// now find the range between them
						calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
								locA.getCentroid().getOrdinate(1));
						calc.setDestinationGeographicPoint(locB.getCentroid()
								.getOrdinate(0), locB.getCentroid().getOrdinate(1));
						double thisDist = calc.getAzimuth();
						Measure<Double, Angle> outVar = Measure.valueOf(thisDist, outUnits);

						target2.add(time, outVar);
					}
				};

				res.add(newC);
			}

			if (aTests.allEqualLengthOrSingleton(selection))
			{
				ICommand<IStoreItem> newC = new DistanceOperation(null, selection,
						destination, "Bearing between tracks (indexed)",
						"Calculate bearing between two tracks (indexed)", null)
				{

					protected IQuantityCollection<?> getOutputCollection(String title,
							boolean isTemporal)
					{
						final IQuantityCollection<?> output;
						if (isTemporal)
						{
							output = new StockTypes.Temporal.Angle_Degrees("Bearing between "
									+ title, null);

						}
						else
						{
							output = new StockTypes.NonTemporal.Angle_Degrees(
									"Bearing between " + title);
						}

						return output;
					}

					protected void calcAndStore(final GeodeticCalculator calc,
							final Point locA, final Point locB, Long time)
					{
						// get the output dataset
						Angle_Degrees target2 = (Angle_Degrees) getOutputs().get(0);
						Unit<Angle> outUnits = target2.getUnits();

						// now find the range between them
						calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
								locA.getCentroid().getOrdinate(1));
						calc.setDestinationGeographicPoint(locB.getCentroid()
								.getOrdinate(0), locB.getCentroid().getOrdinate(1));
						double thisDist = calc.getAzimuth();
						Measure<Double, Angle> outVar = Measure.valueOf(thisDist, outUnits);

						// get the output dataset
						target2.add(outVar);

					}
				};

				res.add(newC);
			}
		}

		return res;
	}

}
