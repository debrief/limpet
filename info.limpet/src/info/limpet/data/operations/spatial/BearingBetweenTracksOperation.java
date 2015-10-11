package info.limpet.data.operations.spatial;

import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Angle_Degrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;

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
			ICommand<IStoreItem> newC = new DistanceOperation(null, selection,
					destination, "Bearing between tracks",
					"Calculate bearing between two tracks")
			{

				protected IQuantityCollection<?> getOutputCollection(String title)
				{
					return new StockTypes.NonTemporal.Angle_Degrees("Bearing between "
							+ title);
				}

				protected void calcAndStore(final GeodeticCalculator calc,
						final Point locA, final Point locB)
				{
					// get the output dataset
					Angle_Degrees target = (Angle_Degrees) getOutputs().get(0);

					// now find the range between them
					calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
							locA.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
							locB.getCentroid().getOrdinate(1));
					double thisDist = calc.getAzimuth();
					target.add(Measure.valueOf(thisDist, target.getUnits()));
				}
			};

			res.add(newC);
		}

		return res;
	}

}
