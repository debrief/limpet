package info.limpet.data.operations.spatial;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.coordinate.Position;

public class GeoSupport
{
	public static GeometryBuilder getBuilder()
	{
		return new GeometryBuilder(DefaultGeographicCRS.WGS84);
	}

	public static GeodeticCalculator getCalculator()
	{
		return new GeodeticCalculator(DefaultGeographicCRS.WGS84);
	}

	public static Position createPosition()
	{
		return new DirectPosition2D(-4, 55.8);
	}
}
