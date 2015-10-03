package info.limpet.data;

import org.geotools.factory.Hints;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.GeometryFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.PrimitiveFactory;

import info.limpet.data.impl.TemporalObjectCollection;
import junit.framework.TestCase;

public class TestGeotoolsGeometry extends TestCase
{

	public void testCreateTemporalObjectCollection()
	{
		TemporalObjectCollection<Geometry> locations = new TemporalObjectCollection<Geometry>(
				"test");
		assertNotNull(locations);
	}
	
	public void testCreatePoint()
	{
		GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
		Point point = builder.createPoint( 48.44, -123.37 );
		Assert.assertNotNull(point);
		
		Hints hints = new Hints( Hints.CRS, DefaultGeographicCRS.WGS84 );
		PrimitiveFactory primitiveFactory = GeometryFactoryFinder.getPrimitiveFactory( hints );
		Point point2 = primitiveFactory.createPoint(  new double[]{48.44, -123.37} );
		Assert.assertNotNull(point2);
	}

}
