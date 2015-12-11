package info.limpet.data.impl.samples;

import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.operations.spatial.GeoSupport;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.iso.coordinate.DirectPositionImpl;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.opengis.geometry.Geometry;

public class TemporalLocation extends TemporalObjectCollection<Geometry>
		implements StockTypes.ILocations
{
	public TemporalLocation(String name)
	{
		super(name);
	}

	@Override
	public List<Geometry> getLocations()
	{
		return super.getValues();
	}

	public TemporalLocation()
	{
		this(null);
	}

	private Geometry linearInterp(Collection<Long> times, List<Geometry> values,
			long time)
	{
		final Geometry res;

		// ok, find the values either side
		DirectPositionImpl beforeVal, afterVal;
		int beforeIndex = -1, afterIndex = -1;
		long beforeTime = 0, afterTime = 0;

		Iterator<Long> tIter = times.iterator();
		int ctr = 0;
		while (tIter.hasNext())
		{
			Long thisT = (Long) tIter.next();
			if (thisT <= time)
			{
				beforeIndex = ctr;
				beforeTime = thisT;
			}
			if (thisT >= time)
			{
				afterIndex = ctr;
				afterTime = thisT;
				break;
			}

			ctr++;
		}

		if (beforeIndex >= 0 && afterIndex == 0)
		{
			res = values.get(beforeIndex);
		}
		else if (beforeIndex >= 0 && afterIndex >= 0)
		{
			if (beforeIndex == afterIndex)
			{
				// special case - it falls on one of our values
				res = values.get(beforeIndex);
			}
			else
			{

				beforeVal = ((PointImpl) values.get(beforeIndex)).getDirectPosition();
				afterVal = ((PointImpl) values.get(afterIndex)).getDirectPosition();

				double latY0 = beforeVal.getX();
				double latY1 = afterVal.getX();

				double longY0 = beforeVal.getY();
				double longY1 = afterVal.getY();
				
				double x0 = beforeTime;
				double x1 = afterTime;
				double x = time;

				double newResLat = latY0 + (latY1 - latY0) * (x - x0) / (x1 - x0);
				double newResLong = longY0 + (longY1 - longY0) * (x - x0) / (x1 - x0);

				// ok, we can do the calc
				res = GeoSupport.getBuilder().createPoint(newResLat, newResLong);
			}
		}
		else
		{
			res = null;
		}

		return res;
	}

	public Geometry interpolateValue(long time, InterpMethod interpMethod)
	{
		final Geometry res;
		switch (interpMethod)
		{
		case Linear:
			res = linearInterp(this.getTimes(), this.getValues(), time);
			break;
		default:
			res = null;
		}
		return res;
	}

}