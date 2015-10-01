package info.limpet.data;

import org.opengis.geometry.Geometry;

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

}
