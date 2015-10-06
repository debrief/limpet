package info.limpet.data.operations.location;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
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

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new DistanceOperation(null, selection,
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
					Angle_Degrees target = (Angle_Degrees) _outputs.get(0);

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
