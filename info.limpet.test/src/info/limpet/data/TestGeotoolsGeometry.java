package info.limpet.data;

import org.geotools.factory.Hints;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.GeometryFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Assert;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.referencing.operation.TransformException;

import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import junit.framework.TestCase;

public class TestGeotoolsGeometry extends TestCase
{

	public void testCreateTemporalObjectCollection()
	{
		TemporalObjectCollection<Geometry> locations = new TemporalObjectCollection<Geometry>(
				"test");
		assertNotNull(locations);
	}
	
	public void testBuilder() throws TransformException
	{
		final Location track_1 = new StockTypes.NonTemporal.Location("some location data");
		
		GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
		GeodeticCalculator geoCalc = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
		DirectPosition pos_1 = new DirectPosition2D(-4,  55.8);
		geoCalc.setStartingGeographicPoint(pos_1.getOrdinate(0), pos_1.getOrdinate(1));
		geoCalc.setDirection(Math.toRadians(54), 0.003);
		pos_1 = geoCalc.getDestinationPosition();
		
		Point p1 = builder.createPoint(pos_1.getOrdinate(0), pos_1.getOrdinate(1));
		track_1.add(p1);
		
		assertEquals("track has point", 1, track_1.size());

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
